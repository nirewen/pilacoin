<script lang="ts">
    import { IconLoader } from '$lib/icons';
    import { cn } from '$lib/utils';

    export let loading: boolean = false;
</script>

<div
    class={cn(
        'flex flex-col gap-1 w-0 flex-1 basis-[24%] p-2 h-full order-2 border rounded-md border-neutral-800',
        $$props.class,
    )}
>
    <div class="flex flex-col">
        <slot name="header" />
    </div>
    <div class="relative flex flex-col h-full overflow-x-hidden overflow-y-auto rounded-sm bg-neutral-800">
        {#if loading}
            <div class="absolute inset-0 grid rounded-md place-items-center backdrop-blur-md">
                <IconLoader class="animate-spin" size={36} />
            </div>
        {:else}
            <slot />
        {/if}
    </div>
</div>
