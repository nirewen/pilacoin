<script lang="ts">
    import type { LogMessage, Modulo, ModuloSettings } from '$lib';
    import { IconMaximize, IconMinimize, IconSettings, IconTrash } from '$lib/icons';
    import { cn, debounced } from '$lib/utils';

    import { Slider } from '$lib/components/ui/slider';
    import { Switch } from '$lib/components/ui/switch';
    import { slide } from 'svelte/transition';
    import LogBox from '../LogBox.svelte';
    import Card from './Card.svelte';

    export let modulo: Modulo;

    $: ativo = modulo.settings.find((s) => s.name === 'active')?.value;

    let expanded = false;
    let open = false;
    let messages: LogMessage[] = [];

    const updateModulo = debounced((settings: ModuloSettings[]) => {
        fetch(`/api/modulo/${modulo.topic}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(settings),
        })
            .then((res) => {
                if (res.ok) {
                    return res.json();
                }
            })
            .then((data: Modulo) => {
                modulo = data;
            });
    });

    function clearLogs() {
        messages = [];
    }
</script>

<Card
    class={cn({
        'basis-full order-1': expanded,
    })}
>
    <svelte:fragment slot="header">
        <header class="flex flex-wrap items-center gap-2">
            <div class="flex-1">
                <h2 class="text-xl font-bold">{modulo.name}</h2>
            </div>
            <div class="flex items-center gap-2">
                {#if (ativo === undefined && messages.length > 0) || (messages.length > 0 && ativo)}
                    <button class="p-1 text-sm text-white rounded-sm bg-neutral-800" on:click={clearLogs}>
                        <IconTrash size={20} />
                    </button>
                {/if}

                {#if ativo || expanded}
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

                <button
                    class="p-1 text-sm text-white rounded-sm bg-neutral-800"
                    on:click={() => {
                        open = !open;
                    }}
                >
                    <IconSettings size={20} />
                </button>
            </div>
            {#if open}
                <ul class="flex flex-col gap-1 basis-full" transition:slide={{ duration: 200 }}>
                    {#each modulo.settings as setting}
                        <li class="flex items-center justify-between">
                            <span class="text-sm font-bold">{setting.name}</span>
                            {#if setting.kind === 'BOOLEAN'}
                                <Switch
                                    class="data-[checked]:dark:bg-green-500"
                                    bind:checked={setting.value}
                                    onCheckedChange={(value) => {
                                        setting.value = value;

                                        updateModulo(modulo.settings);
                                    }}
                                />
                            {:else if setting.kind === 'RANGE'}
                                <Slider
                                    class="mr-3 w-28"
                                    min={setting.min}
                                    max={setting.max}
                                    value={[setting.value]}
                                    onValueChange={(value) => {
                                        setting.value = value[0];

                                        updateModulo(modulo.settings);
                                    }}
                                />
                            {/if}
                        </li>
                    {/each}
                </ul>
            {/if}
        </header>
    </svelte:fragment>
    <svelte:fragment>
        {#if ativo !== undefined && !ativo}
            <div
                class="absolute inset-0 z-10 flex flex-col items-center justify-center h-full select-none backdrop-blur-sm"
            >
                <span class="font-bold">Módulo desativado</span>
                <small>Ative o módulo para ver os logs</small>
            </div>
        {/if}
        <LogBox topic={modulo.topic} bind:messages />
    </svelte:fragment>
</Card>
