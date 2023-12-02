<script lang="ts">
    import type { QueryResponse } from '$lib';
    import PilaList from './PilaList.svelte';
    import UsuarioList from './UsuarioList.svelte';

    export let query: string;

    function getQuery() {
        return fetch(`/api/query/${query}?self=true`)
            .then((res) => res.json())
            .then((result: QueryResponse) => result);
    }
</script>

<div class="flex flex-col gap-1 w-0 flex-1 basis-[24%] p-2 h-full order-2 border rounded-md border-neutral-800">
    <div class="flex items-center justify-between">
        <h2 class="text-xl font-bold text-capitalize">{query}</h2>
    </div>
    <div class="flex flex-col h-full overflow-auto rounded-sm bg-neutral-800">
        {#await getQuery()}
            Carregando...
        {:then data}
            {#if data.idQuery}
                <div class="grid grid-flow-row">
                    {#if query === 'usuarios'}
                        <UsuarioList data={data.usuariosResult} />
                    {/if}
                    {#if query === 'pilas'}
                        <PilaList data={data.pilasResult} />
                    {/if}
                </div>
            {:else}
                <div class="flex flex-col items-center justify-center flex-1">
                    <h3 class="text-xl font-bold">Nenhum resultado</h3>
                    <p class="text-sm text-center">Nenhum resultado foi encontrado para a query <code>{query}</code></p>
                </div>
            {/if}
        {:catch error}
            {error.message}
        {/await}
    </div>
</div>
