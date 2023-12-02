<script lang="ts">
    import type { UsuarioJson } from '$lib';
    import UsuarioRow from './row/UsuarioRow.svelte';

    export let data: UsuarioJson[];
</script>

{#each data as usuario}
    <UsuarioRow {usuario} />
{/each}
