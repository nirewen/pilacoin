<script lang="ts">
    import type { LogMessage } from '$lib';
    import { IconMaximize, IconMinimize, IconTrash } from '$lib/icons';
    import { cn } from '$lib/utils';

    import { Switch } from '$lib/components/ui/switch';
    import LogBox from '../LogBox.svelte';
    import Card from './Card.svelte';

    export let nome: string;
    export let topic = nome;
    export let ativo: boolean | undefined = undefined;

    let expanded = false;
    let messages: LogMessage[] = [];

    function toggleModulo() {
        fetch(`/api/modulo/${topic}/toggle`, {
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

<Card
    class={cn({
        'basis-full order-1': expanded,
    })}
>
    <svelte:fragment slot="header">
        <header class="flex items-center">
            <div class="flex-1">
                <h2 class="text-xl font-bold">{nome}</h2>
            </div>
            <div class="flex items-center gap-2">
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

                {#if ativo !== undefined}
                    <Switch class="data-[checked]:dark:bg-green-500" bind:checked={ativo} on:click={toggleModulo} />
                {/if}
            </div>
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
        <LogBox {topic} bind:messages />
    </svelte:fragment>
</Card>
