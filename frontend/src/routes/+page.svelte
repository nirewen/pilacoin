<script lang="ts">
    import type { Modulo } from '$lib';
    import LogCard from '$lib/components/cards/LogCard.svelte';
    import QueryCard from '$lib/components/cards/QueryCard.svelte';

    async function getModulos() {
        const modulos = await fetch('/api/modulo')
            .then((res) => res.json())
            .then((data: Modulo[]) => data);

        return modulos;
    }
</script>

<div class="flex flex-wrap gap-2 overflow-hidden">
    {#await getModulos()}
        Carregando...
    {:then modulos}
        {#each modulos as { modulo }}
            <LogCard {...modulo} />
        {/each}
    {:catch name}
        Não foi possível carregar os módulos
    {/await}
</div>

<div class="flex flex-wrap gap-2">
    <LogCard nome="UserMessage" />
    <QueryCard query="usuarios" />
    <QueryCard query="pilas" />
</div>
