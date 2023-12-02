<script lang="ts">
    import { IconMaximize, IconMinimize } from '@tabler/icons-svelte';
    import { format } from 'date-fns';
    import { onMount } from 'svelte';

    import { logEventSource, type Modulo } from '$lib';
    import { Switch } from '$lib/components/ui/switch';
    import { cn } from '$lib/util/cn';

    export let modulo: Modulo;

    type Message = {
        timestamp: number;
        title: string;
        message: string;
        extra: object;
        expanded?: boolean;
    };

    let expanded = false;
    let messages: Message[] = [];
    let checked = modulo.modulo.ativo;

    onMount(() => {
        logEventSource.addEventListener(modulo.nome, (event) => {
            messages = [...messages, { ...JSON.parse(event.data), expanded: false }];
        });
    });

    function toggleModulo() {
        fetch(`/api/modulo/${modulo.nome}/toggle`, {
            method: 'POST',
        })
            .then((res) => {
                if (res.ok) {
                    return res.json();
                }
            })
            .then((data) => {
                modulo.modulo = data;
                checked = modulo.modulo.ativo;
            });
    }
</script>

<div
    class={cn('flex flex-col gap-1 w-0 flex-1 basis-[24%] p-2 order-2 border rounded-md border-neutral-800 ', {
        'basis-full order-1 ': expanded,
    })}
>
    <div class="flex items-center justify-between">
        <h2 class="text-xl font-bold">{modulo.nome}</h2>
        <div class="flex items-center gap-2">
            <Switch class="data-[checked]:dark:bg-green-500" bind:checked on:click={toggleModulo} />
            <button
                class="p-1 text-sm text-white rounded-md bg-neutral-700"
                on:click={() => {
                    expanded = !expanded;
                }}
            >
                {#if expanded}
                    <IconMinimize />
                {:else}
                    <IconMaximize />
                {/if}
            </button>
        </div>
    </div>
    <div
        class={cn('relative flex flex-col-reverse overflow-auto rounded-sm h-52 bg-neutral-800', {
            'h-80': expanded,
        })}
    >
        {#if !checked}
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
