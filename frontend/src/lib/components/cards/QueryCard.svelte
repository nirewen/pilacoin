<script lang="ts">
    import { queries, type QueryResponse } from '$lib';
    import { createQuery } from '@tanstack/svelte-query';
    import { writable } from 'svelte/store';

    import { IconReload } from '$lib/icons';

    import { cn } from '$lib/utils';
    import Card from './Card.svelte';

    export let query: string;
    export let title = query;

    const store = writable<QueryResponse<any>>();

    queries.set(query, store);

    function getQuery() {
        return fetch(`/api/query/${query}?self=true`)
            .then((res) => res.json())
            .then((result: QueryResponse<any>) => {
                const store = queries.get(query);

                if (store) store.set(result);

                return result;
            });
    }

    const queryStore = createQuery({
        queryKey: ['query', query],
        queryFn: () => getQuery(),
    });
</script>

<Card loading={$queryStore.isLoading}>
    <svelte:fragment slot="header">
        <header class="flex items-center gap-2">
            <div class="p-1 aspect-square empty:hidden">
                <slot name="icon" />
            </div>
            <div class="flex items-center flex-1 gap-4">
                <h2 class="mr-auto text-xl font-bold text-capitalize">{title}</h2>
                <span class="p-1 leading-4 rounded tabular-nums bg-neutral-800 pb-0.5 empty:hidden"
                    >{$queryStore.data?.result.length}</span
                >
            </div>
            <div class="flex items-center gap-2">
                <button class="p-1 text-sm text-white rounded-sm bg-neutral-800" on:click={() => $queryStore.refetch()}>
                    <IconReload
                        size={20}
                        class={cn({
                            'animate-spin': $queryStore.isFetching,
                        })}
                    />
                </button>
            </div>
        </header>
    </svelte:fragment>
    <div class="flex flex-col rounded-sm bg-neutral-800">
        {#if $queryStore.isError}
            <p>Não foi possível carregar os dados</p>
        {:else if $queryStore.isSuccess}
            {#if $queryStore.data.idQuery}
                <slot data={$store} />
            {:else}
                <div class="flex flex-col items-center justify-center flex-1">
                    <h3 class="text-xl font-bold">Nenhum resultado</h3>
                    <p class="text-sm text-center">Nenhum resultado foi encontrado para a query <code>{query}</code></p>
                </div>
            {/if}
        {/if}
    </div>
</Card>
