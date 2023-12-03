<script lang="ts">
    import { quintInOut } from 'svelte/easing';
    import { slide } from 'svelte/transition';

    import type { UsuarioJson } from '$lib';

    import UsuarioRow from './row/UsuarioRow.svelte';

    export let data: UsuarioJson[];
</script>

{#each data as usuario (usuario.id)}
    {#if !usuario.selected}
        <div
            class="flex flex-col border-b-2 border-neutral-900 hover:bg-neutral-900"
            transition:slide={{ duration: 300, easing: quintInOut, axis: 'y' }}
        >
            <UsuarioRow {usuario} />
        </div>
    {/if}
{/each}
