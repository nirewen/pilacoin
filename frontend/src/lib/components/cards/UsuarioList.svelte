<script lang="ts">
    import type { UsuarioJson } from '$lib';
    import { quintInOut } from 'svelte/easing';
    import { slide } from 'svelte/transition';
    import UsuarioRow from './row/UsuarioRow.svelte';

    export let data: UsuarioJson[];
</script>

{#each data as usuario (usuario.id)}
    {#if !usuario.selected}
        <div class="flex flex-col" transition:slide={{ duration: 300, easing: quintInOut, axis: 'y' }}>
            <UsuarioRow {usuario} />
        </div>
    {/if}
{/each}
