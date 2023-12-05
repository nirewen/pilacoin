<script lang="ts">
    import { format } from 'date-fns';
    import { onMount } from 'svelte';

    import type { LogMessage } from '$lib';
    import { cn, logEventSource } from '$lib/utils';

    export let topic: string;
    export let messages: LogMessage[] = [];

    onMount(() => {
        const handler = (event: MessageEvent<any>) => {
            messages = [...messages, { ...JSON.parse(event.data), expanded: false }].slice(-100);
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
                    'flex flex-col font-mono text-xs text-left px-2 bg-[#1f1f1f] cursor-default whitespace-nowrap even:bg-[#1a1a1a] focus:outline-none',
                    {
                        'cursor-pointer': message.extra,
                    },
                )}
                on:click={() => (message.expanded = !message.expanded)}
            >
                <span>
                    <span class="text-blue-300" title={format(new Date(message.timestamp), 'yyyy-MM-dd HH:mm')}>
                        {format(new Date(message.timestamp), 'HH:mm:ss')}
                    </span>
                    <span class="text-green-400">{message.title}</span>
                    {#if message.message}
                        <span
                            class={cn({
                                'text-slate-400': message.level === 'INFO',
                                'text-red-400': message.level === 'ERROR',
                            })}>{message.message}</span
                        >
                    {/if}
                </span>
                {#if expanded && message.extra}
                    <div class="p-2 bg-[#0f0f0f] rounded-md my-2">
                        <pre class="text-pink-400 whitespace-pre-wrap">{JSON.stringify(message.extra, null, 2)}</pre>
                    </div>
                {/if}
            </button>
        {/each}
    </div>
</div>
