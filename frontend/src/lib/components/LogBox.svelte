<script lang="ts">
    import { format } from 'date-fns';
    import { onMount } from 'svelte';

    import type { LogMessage } from '$lib';
    import { cn, logEventSource } from '$lib/utils';

    export let topic: string;
    export let messages: LogMessage[] = [];

    onMount(() => {
        const handler = (event: MessageEvent<any>) => {
            messages = [...messages, { ...JSON.parse(event.data), expanded: false }];
        };

        logEventSource.addEventListener(topic, handler);

        () => logEventSource.removeEventListener(topic, handler);
    });
</script>

<div class="relative flex flex-col-reverse h-full overflow-auto rounded-sm bg-neutral-800">
    <div class="grid grid-flow-row">
        {#each messages as message, index}
            {@const expanded = message.expanded}
            <button
                class={cn(
                    'flex flex-col font-mono text-xs text-left px-2 bg-[#1f1f1f] cursor-default whitespace-nowrap even:bg-[#2f2f2f] focus:outline-none',
                    {
                        'cursor-pointer': message.extra,
                    },
                )}
                on:click={() => (message.expanded = !message.expanded)}
            >
                <span>
                    <span class="text-blue-300">
                        {format(new Date(message.timestamp), 'HH:mm:ss')}
                    </span>
                    <span class="text-green-400">{message.title}</span>
                    {#if message.message}
                        <span class="text-yellow-400">{message.message}</span>
                    {/if}
                </span>
                {#if expanded && message.extra}
                    <div class="p-2 bg-[#0f0f0f] rounded-md my-2">
                        <pre class="text-pink-500 whitespace-pre-wrap">{JSON.stringify(message.extra, null, 2)}</pre>
                    </div>
                {/if}
            </button>
        {/each}
    </div>
</div>
