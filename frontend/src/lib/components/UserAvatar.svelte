<script lang="ts">
    export let nome: string;
    export let size = 6;
</script>

<img src="https://api.dicebear.com/7.x/identicon/svg?seed={nome}" alt={nome} class="w-{size} h-{size} rounded-sm" />
