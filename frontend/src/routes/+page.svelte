<script lang="ts">
    import LogCard from '$lib/components/cards/LogCard.svelte';
</script>

<div class="flex flex-wrap gap-2">
    <LogCard modulo="PilaCoinMining" />
    <LogCard modulo="PilaCoinValidation" />
    <LogCard modulo="BlockDiscovery" />
    <LogCard modulo="BlockValidation" />
</div>
