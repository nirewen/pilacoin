<script lang="ts">
    import type { UsuarioJson } from '$lib';

    export let data: UsuarioJson[];
</script>

{#each data as usuario}
    <div class="flex gap-2 p-2">
        <img src="https://api.dicebear.com/7.x/identicon/svg?seed={usuario.nome}" alt={usuario.nome} class="w-6 h-6" />
        {usuario.nome}
    </div>
{/each}
