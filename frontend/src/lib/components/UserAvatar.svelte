<script lang="ts">
    import { cn } from '$lib/utils';

    export let nome: string;
</script>

<img
    src="https://api.dicebear.com/7.x/identicon/svg?seed={nome}"
    alt={nome}
    class={cn('w-6 h-6 rounded-[2px] bg-neutral-900', $$props.class)}
/>
