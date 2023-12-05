<script lang="ts">
    import type { BooleanSetting, ConstantSetting, LogMessage, Modulo, ModuloSettings, RangeSetting } from '$lib';
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
        if (JSON.stringify(modulo.settings) === JSON.stringify(settings)) {
            return;
        }

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
        'basis-full order-0': expanded,
    })}
    style="order: {modulo.settings.find((s) => s.name === 'order')?.value}"
>
    <svelte:fragment slot="header">
        <header class="flex flex-wrap items-center gap-2">
            <div class="p-1 aspect-square empty:hidden">
                <slot name="icon" />
            </div>
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

                {#if modulo.settings.length}
                    <button
                        class="p-1 text-sm text-white rounded-sm bg-neutral-800"
                        on:click={() => {
                            open = !open;
                        }}
                    >
                        <IconSettings size={20} />
                    </button>
                {/if}
            </div>
            {#if open}
                <ul class="flex flex-col gap-1 basis-full" transition:slide={{ duration: 200 }}>
                    {#each modulo.settings as setting}
                        <li class="flex items-center justify-between">
                            <div class="flex-1">
                                <span class="p-1 font-mono text-sm bg-neutral-900">{setting.name}</span>
                            </div>
                            {#if setting.kind === 'BOOLEAN'}
                                <Switch
                                    class="data-[checked]:dark:bg-green-500"
                                    checked={setting.value}
                                    onCheckedChange={(value) => {
                                        updateModulo(modulo.settings.map(s => {
                                            if (s.name === setting.name) {
                                                return {
                                                    ...s,
                                                    value,
                                                } as BooleanSetting;
                                            }

                                            return s;
                                        }));
                                    }}
                                />
                            {:else if setting.kind === 'RANGE'}
                                <span class="p-1 pb-[2px] mr-4 font-mono leading-4 rounded-sm bg-neutral-800">
                                    {setting.value.value}
                                </span>
                                <Slider
                                    class="mr-3 w-[50%]"
                                    min={setting.value.min}
                                    max={setting.value.max}
                                    value={[setting.value.value]}
                                    onValueChange={(value) => {
                                        updateModulo(modulo.settings.map(s => {
                                            if (s.name === setting.name) {
                                                return {
                                                    ...s,
                                                    value: {
                                                        ...(s as RangeSetting).value,
                                                        value: value[0],
                                                    },
                                                } as RangeSetting;
                                            }

                                            return s;
                                        }));
                                    }}
                                />
                            {:else if setting.kind === 'CONSTANT'}
                                <button
                                    class="px-2 bg-neutral-800 rounded-s-sm"
                                    on:click={() => {
                                    updateModulo(modulo.settings.map(s => {
                                            if (s.name === setting.name) {
                                                return {
                                                    ...s,
                                                    value: (setting as ConstantSetting).value - 1,
                                                } as ConstantSetting;
                                            }

                                            return s;
                                        }));
                                }}
                                    >&minus;</button
                                >
                                <span class="p-1 pb-[3px] leading-[17px] bg-neutral-800">
                                    {setting.value}
                                </span>
                                <button
                                    class="px-2 bg-neutral-800 rounded-e-sm"
                                    on:click={() => {
                                    updateModulo(modulo.settings.map(s => {
                                            if (s.name === setting.name) {
                                                return {
                                                    ...s,
                                                    value: (setting as ConstantSetting).value + 1,
                                                } as ConstantSetting;
                                            }

                                            return s;
                                        }));
                                }}
                                    >&plus;</button
                                >
                            {/if}
                        </li>
                    {/each}
                </ul>
            {/if}
        </header>
    </svelte:fragment>
    <svelte:fragment>
        {#if ativo !== undefined && !ativo}
            <div class="absolute inset-0 z-10 flex flex-col items-center justify-center select-none backdrop-blur-sm">
                <span class="font-bold">Módulo desativado</span>
                <small>Ative o módulo para ver os logs</small>
            </div>
        {/if}
        <LogBox topic={modulo.topic} bind:messages />
    </svelte:fragment>
</Card>
