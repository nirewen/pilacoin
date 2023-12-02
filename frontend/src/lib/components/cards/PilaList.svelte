<script lang="ts">
    import type { PilaCoinJson } from '$lib';

    export let data: PilaCoinJson[];
</script>

{#each data as pilacoin}
    <div class="flex">
        {pilacoin.nomeCriador}
    </div>
{/each}
