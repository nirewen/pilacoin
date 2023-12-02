<script lang="ts">
    import { pilacoinTransferencia, type PilaCoinJson } from '$lib';
    import IconChevron from '$lib/components/icons/IconChevron.svelte';
    import IconX from '$lib/components/icons/IconX.svelte';
    import { cn } from '$lib/utils';
    import { format } from 'date-fns';

    export let pilacoin: PilaCoinJson | null = null;
    export let large = false;

    $: selected = $pilacoinTransferencia?.id === pilacoin?.id;

    function setPilacoinTransferencia() {
        if (!pilacoin) return;

        if (selected) {
            $pilacoinTransferencia = null;

            return;
        }

        $pilacoinTransferencia = pilacoin;
    }
</script>

{#snippet formatStatus(value: string)}
    <div
        class={cn('whitespace-nowrap px-1 rounded-sm text-sm bg-slate-600', {
            'bg-yellow-700': value === 'BLOCO_EM_VALIDACAO',
            'bg-green-700': value === 'VALIDO',
        })}
    >
        {#if value === 'BLOCO_EM_VALIDACAO'}
            BLOCO EM VALIDAÇÃO
        {:else if value === 'VALIDO'}
            VÁLIDO
        {:else}
            {value}
        {/if}
    </div>
{/snippet}

{#if pilacoin}
    <button
        type="button"
        class="focus:outline-none flex justify-between w-full p-2 gap-6 odd:bg-[#1f1f1f]"
        on:click={setPilacoinTransferencia}
    >
        <div class="flex flex-col min-w-0">
            <span class="flex items-center gap-1">
                <img
                    src="https://api.dicebear.com/7.x/identicon/svg?seed={pilacoin.nomeCriador}"
                    alt={pilacoin.nomeCriador}
                    class="w-4 h-4"
                />
                {pilacoin.nomeCriador}
            </span>
            <span class="overflow-hidden font-mono text-sm text-ellipsis">{pilacoin.nonce}</span>
        </div>
        <div class="flex items-center h-full">
            <div class="flex flex-col items-end h-full">
                {@render formatStatus(pilacoin.status)}
                <time class="overflow-hidden text-ellipsis whitespace-nowrap">
                    {format(new Date(pilacoin.dataCriacao), 'dd/MM/yyyy HH:mm')}
                </time>
            </div>
            <div class="grid place-items-center">
                {#if selected}
                    <IconX />
                {:else}
                    <IconChevron />
                {/if}
            </div>
        </div>
    </button>
{:else}
    <button type="button" class="focus:outline-none h-[60px] bg-[#2f2f2f]"></button>
{/if}
