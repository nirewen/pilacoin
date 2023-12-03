<script lang="ts">
    import { IconMaximize, IconMinimize, IconTrash } from '$lib/components/icons';

    import type { LogMessage } from '$lib';
    import { Switch } from '$lib/components/ui/switch';
    import { cn } from '$lib/utils';
    import Card from './Card.svelte';
    import LogBox from './LogBox.svelte';

    export let nome: string;
    export let ativo: boolean | undefined = undefined;

    let expanded = false;
    let messages: LogMessage[] = [];

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

<Card
    class={cn({
        'basis-full order-1': expanded,
    })}
>
    <svelte:fragment slot="title">
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
        <LogBox {nome} bind:messages />
    </svelte:fragment>
</Card>
