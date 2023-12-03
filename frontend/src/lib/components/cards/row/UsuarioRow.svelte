<script lang="ts">
    import { queries, type UsuarioJson } from '$lib';
    import IconChevron from '$lib/components/icons/IconChevron.svelte';
    import IconX from '$lib/components/icons/IconX.svelte';

    export let usuario: UsuarioJson;
    export let large = false;

    const usuarios = queries.get('usuarios');

    function selectUsuario() {
        if (!usuario) return;

        let foundUsuario = $usuarios?.usuariosResult.find((u) => u.selected);

        if (foundUsuario) {
            foundUsuario.selected = false;
        }

        let newUsuario = $usuarios?.usuariosResult.find((u) => u.nome === usuario?.nome);

        if (newUsuario && newUsuario.nome !== foundUsuario?.nome) {
            newUsuario.selected = true;
        }

        $usuarios = $usuarios;
    }
</script>

<button type="button" class="flex justify-between p-2 focus:outline-none" on:click={selectUsuario}>
    <div class="flex flex-col min-w-0 gap-2">
        <span class="flex items-center gap-2">
            <img
                src="https://api.dicebear.com/7.x/identicon/svg?seed={usuario.nome}"
                alt={usuario.nome}
                class="w-6 h-6"
            />
            {usuario.nome}
        </span>
        {#if large}
            <span class="overflow-hidden font-mono text-sm text-ellipsis">{usuario.chavePublica}</span>
        {/if}
    </div>
    <div class="flex items-center h-full">
        <div class="grid place-items-center">
            {#if usuario.selected}
                <IconX />
            {:else}
                <IconChevron />
            {/if}
        </div>
    </div>
</button>
