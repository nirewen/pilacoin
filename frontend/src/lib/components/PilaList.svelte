<script lang="ts">
    import { quintInOut } from 'svelte/easing';
    import { slide } from 'svelte/transition';

    import type { PilaCoinJson } from '$lib';

    import PilaRow from './row/PilaRow.svelte';

    export let data: PilaCoinJson[];
</script>

{#each data.toReversed() as pilacoin (pilacoin.id)}
    {#if !pilacoin.selected}
        <div
            class="flex flex-col border-b-2 border-neutral-900 hover:bg-neutral-900"
            transition:slide={{ duration: 300, easing: quintInOut, axis: 'y' }}
        >
            <PilaRow {pilacoin} />
        </div>
    {/if}
{/each}
