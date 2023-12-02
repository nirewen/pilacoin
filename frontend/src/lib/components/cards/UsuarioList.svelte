<script lang="ts">
    import type { UsuarioJson } from '$lib';

    export let data: UsuarioJson[];
</script>

{#each data as usuario}
    <div class="flex">
        {usuario.nome}
    </div>
{/each}
