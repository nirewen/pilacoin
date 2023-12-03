<script lang="ts">
    import type { PilaCoinJson } from '$lib';
    import { quintInOut } from 'svelte/easing';
    import { slide } from 'svelte/transition';
    import PilaRow from './row/PilaRow.svelte';

    export let data: PilaCoinJson[];
</script>

{#each data as pilacoin (pilacoin.id)}
    {#if !pilacoin.selected}
        <div class="flex flex-col odd:bg-[#1f1f1f]" transition:slide={{ duration: 300, easing: quintInOut, axis: 'y' }}>
            <PilaRow {pilacoin} />
        </div>
    {/if}
{/each}
