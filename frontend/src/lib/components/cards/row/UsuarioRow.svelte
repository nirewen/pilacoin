<script lang="ts">
    import { usuarioTransferencia, type UsuarioJson } from '$lib';
    import IconChevron from '$lib/components/icons/IconChevron.svelte';
    import IconX from '$lib/components/icons/IconX.svelte';

    export let usuario: UsuarioJson | null = null;
    export let large = false;

    $: selected = $usuarioTransferencia?.id === usuario?.id;

    function setUsuarioTransferencia() {
        if (!usuario) return;

        if (selected) {
            $usuarioTransferencia = null;

            return;
        }

        $usuarioTransferencia = usuario;
    }
</script>

{#if usuario}
    <button
        type="button"
        class="flex justify-between p-2 odd:bg-[#1f1f1f] focus:outline-none"
        on:click={setUsuarioTransferencia}
    >
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
                {#if selected}
                    <IconX />
                {:else}
                    <IconChevron />
                {/if}
            </div>
        </div>
    </button>
{:else}
    <button type="button" class="h-[68px] bg-[#2f2f2f] focus:outline-none"></button>
{/if}
