<script lang="ts">
    import type { QueryResponse } from '$lib';
    import { createQuery } from '@tanstack/svelte-query';
    import IconLoader from '../icons/IconLoader.svelte';
    import IconReload from '../icons/IconReload.svelte';
    import PilaList from './PilaList.svelte';
    import UsuarioList from './UsuarioList.svelte';

    export let query: string;

    function getQuery() {
        return fetch(`/api/query/${query}?self=true`)
            .then((res) => res.json())
            .then((result: QueryResponse) => result);
    }

    const queryStore = createQuery({
        queryKey: ['query', query],
        queryFn: () => getQuery(),
    });
</script>

<div class="flex flex-col gap-1 w-0 flex-1 basis-[24%] p-2 h-full order-2 border rounded-md border-neutral-800">
    <div class="flex items-center justify-between">
        <h2 class="text-xl font-bold text-capitalize">{query}</h2>
        <div class="flex items-center gap-2">
            <button class="p-1 text-sm text-white rounded-sm bg-neutral-800" on:click={() => $queryStore.refetch()}>
                <IconReload size={20} />
            </button>
        </div>
    </div>
    <div class="relative flex flex-col h-full overflow-x-hidden overflow-y-auto rounded-sm bg-neutral-800">
        {#if $queryStore.isLoading}
            <div class="absolute inset-0 grid rounded-md place-items-center backdrop-blur-md">
                <IconLoader class="animate-spin" size={36} />
            </div>
        {:else if $queryStore.isError}
            <p>Não foi possível carregar os dados</p>
        {:else if $queryStore.isSuccess}
            {#if $queryStore.data.idQuery}
                <div class="flex flex-col">
                    {#if query === 'usuarios'}
                        <UsuarioList data={$queryStore.data.usuariosResult} />
                    {/if}
                    {#if query === 'pilas'}
                        <PilaList data={$queryStore.data.pilasResult} />
                    {/if}
                </div>
            {:else}
                <div class="flex flex-col items-center justify-center flex-1">
                    <h3 class="text-xl font-bold">Nenhum resultado</h3>
                    <p class="text-sm text-center">Nenhum resultado foi encontrado para a query <code>{query}</code></p>
                </div>
            {/if}
        {/if}
    </div>
</div>
