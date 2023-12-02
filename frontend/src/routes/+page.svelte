<script lang="ts">
    import type { Modulo } from '$lib';
    import LogCard from '$lib/components/cards/LogCard.svelte';
    import QueryCard from '$lib/components/cards/QueryCard.svelte';
    import TransferenciaCard from '$lib/components/cards/TransferenciaCard.svelte';
    import { createQuery } from '@tanstack/svelte-query';

    import { pilacoinTransferencia, usuarioTransferencia } from '$lib';

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

<div class="flex flex-wrap gap-2 overflow-hidden">
    {#if $query.isLoading}
        <p>Carregando...</p>
    {:else if $query.isError}
        <p>Não foi possível carregar os módulos</p>
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
