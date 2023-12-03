<script lang="ts">
    import type { Modulo } from '$lib';
    import LogCard from '$lib/components/cards/LogCard.svelte';
    import QueryCard from '$lib/components/cards/QueryCard.svelte';
    import TransferenciaCard from '$lib/components/cards/TransferenciaCard.svelte';
    import { createQuery } from '@tanstack/svelte-query';

    import { pilacoinTransferencia, usuarioTransferencia } from '$lib';
    import IconLoader from '$lib/components/icons/IconLoader.svelte';

    async function getModulos() {
        return fetch('/api/modulo')
            .then((res) => res.json())
            .then((data: Modulo[]) => data);
    }

    const query = createQuery({
        queryKey: ['modulos'],
        queryFn: () => getModulos(),
    });
</script>

<div class="relative flex flex-wrap gap-2 overflow-hidden">
    {#if $query.isLoading}
        <div class="absolute inset-0 grid rounded-md place-items-center backdrop-blur-md">
            <IconLoader class="animate-spin" size={36} />
        </div>
        {#each { length: 4 } as item}
            <div
                class="flex flex-col gap-1 w-0 flex-1 basis-[24%] p-2 h-full order-2 border rounded-md border-neutral-700"
            />
        {/each}
    {:else if $query.isError}
        {#each { length: 4 } as item}
            <div
                class="w-0 flex-1 basis-[24%] p-2 h-full order-2 border rounded-md border-neutral-700 grid place-items-center"
            >
                Não foi possível carregar esse módulo.
            </div>
        {/each}
    {:else if $query.isSuccess}
        {#each $query.data as { modulo }}
            <LogCard {...modulo} />
        {/each}
    {/if}
</div>

<div class="flex flex-wrap gap-2">
    <LogCard nome="UserMessage" />
    <QueryCard query="pilas" />
    <QueryCard query="usuarios" />
    <TransferenciaCard usuario={$usuarioTransferencia} pilacoin={$pilacoinTransferencia} />
</div>
