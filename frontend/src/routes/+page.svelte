<script lang="ts">
    import LogCard from '$lib/components/cards/LogCard.svelte';
</script>

<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-2">
    <LogCard modulo="PilaCoinMining" />
    <LogCard modulo="PilaCoinValidation" />
    <LogCard modulo="BlockDiscovery" />
    <LogCard modulo="BlockValidation" />
</div>
