<script lang="ts">
    import type { PilaCoinJson } from '$lib';
    import { cn } from '$lib/utils';

    export let data: PilaCoinJson[];
</script>

{#snippet formatStatus(value: string)}
    <div
        class={cn('px-1 rounded-sm text-sm', {
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

{#each data as pilacoin}
    <div class="flex justify-between w-full p-2 odd:bg-[#1f1f1f]">
        <div class="flex flex-col">
            <span class="flex items-center gap-1">
                <img
                    src="https://api.dicebear.com/7.x/identicon/svg?seed={pilacoin.nomeCriador}"
                    alt={pilacoin.nomeCriador}
                    class="w-4 h-4"
                />
                {pilacoin.nomeCriador}
            </span>
            <span class="font-mono text-sm">{pilacoin.nonce.slice(0, 24)}...</span>
        </div>
        <div class="flex flex-col">
            <span>{@render formatStatus(pilacoin.status)}</span>
        </div>
    </div>
{/each}
