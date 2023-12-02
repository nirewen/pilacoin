<script lang="ts">
    import { logEventSource } from '$lib';
    import { cn } from '$lib/util/cn';
    import { format } from 'date-fns';
    import { onMount } from 'svelte';

    export let modulo: string;

    type Message = {
        timestamp: number;
        title: string;
        message: string;
        extra: object;
        expanded?: boolean;
    };

    let expanded = false;
    let messages: Message[] = [];

    onMount(() => {
        logEventSource.addEventListener(modulo, (event) => {
            messages = [...messages, { ...JSON.parse(event.data), expanded: false }];
        });
    });
</script>

<div
    class={cn('flex flex-col gap-1 w-0 flex-1 basis-[24%] p-2 order-2 border rounded-md border-neutral-800 ', {
        'basis-full order-1 ': expanded,
    })}
>
    <div class="flex justify-between align-items-center">
        <h2 class="text-xl font-bold">{modulo}</h2>
        <button
            class="p-1 text-sm text-white rounded-md bg-neutral-700"
            on:click={() => {
                expanded = !expanded;
            }}
        >
            {expanded ? 'Collapse' : 'Expand'}
        </button>
    </div>
    <div
        class={cn('flex flex-col-reverse overflow-auto rounded-sm h-52 bg-neutral-800', {
            'h-80': expanded,
        })}
    >
        <div class="grid grid-flow-row">
            {#each messages as message, index}
                {@const expanded = message.expanded}
                <button
                    class="flex flex-col font-mono text-xs text-left cursor-pointer px-2 bg-[#1f1f1f] whitespace-nowrap even:bg-[#2f2f2f]"
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
                    {#if expanded}
                        <div class="p-2 bg-[#0f0f0f] rounded-md my-2">
                            <pre class="text-pink-500 whitespace-pre-wrap">{JSON.stringify(
                                    message.extra,
                                    null,
                                    2,
                                )}</pre>
                        </div>
                    {/if}
                </button>
            {/each}
        </div>
    </div>
</div>
