<script lang="ts">
    import type { Modulo } from '$lib';
    import LogCard from '$lib/components/cards/LogCard.svelte';

    async function getModulos() {
        const modulos = await fetch('/api/modulo')
            .then((res) => res.json())
            .then((data: Modulo[]) => data);

        return modulos;
    }
</script>

<div class="flex flex-wrap gap-2">
    {#await getModulos()}
        Carregando...
    {:then modulos}
        {#each modulos as modulo}
            <LogCard {modulo} />
        {/each}
    {:catch name}
        Não foi possível carregar os módulos
    {/await}
</div>
