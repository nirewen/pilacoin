<script lang="ts">
    import { IconMaximize, IconMinimize, IconTrash } from '$lib/components/icons';
    import { format } from 'date-fns';
    import { onMount } from 'svelte';

    import { logEventSource } from '$lib';
    import { Switch } from '$lib/components/ui/switch';
    import { cn } from '$lib/utils';

    type Message = {
        timestamp: number;
        title: string;
        message: string;
        extra: object;
        expanded?: boolean;
    };

    export let nome: string;
    export let ativo: boolean | undefined = undefined;

    let expanded = false;
    let messages: Message[] = [];

    onMount(() => {
        logEventSource.addEventListener(nome, (event) => {
            messages = [...messages, { ...JSON.parse(event.data), expanded: false }];
        });
    });

    function toggleModulo() {
        fetch(`/api/modulo/${nome}/toggle`, {
            method: 'POST',
        })
            .then((res) => {
                if (res.ok) {
                    return res.json();
                }
            })
            .then((data) => {
                ativo = data.ativo;
            });
    }

    function clearLogs() {
        messages = [];
    }
</script>

<div
    class={cn('flex flex-col gap-1 w-0 flex-1 basis-[24%] p-2 h-full order-2 border rounded-md border-neutral-800 ', {
        'basis-full order-1 ': expanded,
    })}
>
    <div class="flex items-center justify-between">
        <h2 class="text-xl font-bold">{nome}</h2>
        <div class="flex items-center gap-2">
            {#if ativo !== undefined}
                <Switch class="data-[checked]:dark:bg-green-500" bind:checked={ativo} on:click={toggleModulo} />
            {/if}

            {#if (ativo === undefined && messages.length > 0) || (messages.length > 0 && ativo)}
                <button class="p-1 text-sm text-white rounded-sm bg-neutral-800" on:click={clearLogs}>
                    <IconTrash size={20} />
                </button>
            {/if}

            {#if ativo}
                <button
                    class="p-1 text-sm text-white rounded-sm bg-neutral-800"
                    on:click={() => {
                        expanded = !expanded;
                    }}
                >
                    {#if expanded}
                        <IconMinimize size={20} />
                    {:else}
                        <IconMaximize size={20} />
                    {/if}
                </button>
            {/if}
        </div>
    </div>
    <div
        class={cn('relative flex flex-col-reverse overflow-auto rounded-sm h-full bg-neutral-800', {
            'h-80': expanded,
        })}
    >
        {#if ativo !== undefined && !ativo}
            <div
                class="absolute inset-0 z-10 flex flex-col items-center justify-center h-full select-none backdrop-blur-sm"
            >
                <span class="font-bold">Módulo desativado</span>
                <small>Ative o módulo para ver os logs</small>
            </div>
        {/if}
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
