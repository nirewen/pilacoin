<script lang="ts">
    import LogCard from '$lib/components/cards/LogCard.svelte';

    type Modulo = {
        modulo: {
            id: number;
            nome: string;
            descricao: string;
            ativo: boolean;
        };
        nome: string;
    };

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
            <LogCard modulo={modulo.nome} />
        {/each}
    {:catch name}
        Não foi possível carregar os módulos
    {/await}
</div>
