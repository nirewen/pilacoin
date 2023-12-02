<script lang="ts">
    import type { PilaCoinJson } from '$lib';
    import PilaRow from './row/PilaRow.svelte';

    export let data: PilaCoinJson[];
</script>

{#each data as pilacoin}
    <PilaRow {pilacoin} />
{/each}
