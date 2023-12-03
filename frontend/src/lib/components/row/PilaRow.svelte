<script lang="ts">
    import { format } from 'date-fns';

    import { queries, type PilaCoinJson } from '$lib';
    import { IconChevron, IconX } from '$lib/icons';
    import { cn } from '$lib/utils';

    export let pilacoin: PilaCoinJson;
    export let large = false;

    const pilas = queries.get('pilas');

    function selectPilaCoin() {
        if (!pilacoin) return;

        let foundPila = $pilas?.pilasResult.find((u) => u.selected);

        if (foundPila) {
            foundPila.selected = false;
        }

        let newPila = $pilas?.pilasResult.find((u) => u.nonce === pilacoin?.nonce);

        if (newPila && newPila.nonce !== foundPila?.nonce) {
            newPila.selected = true;
        }

        $pilas = $pilas;
    }

    function getStatusString(pilacoin: PilaCoinJson) {
        if (pilacoin.transacoes?.length > 1) return 'TRANSFERIDO';
        if (pilacoin.status === 'BLOCO_EM_VALIDACAO') return 'BLOCO EM VALIDAÇÃO';
        if (pilacoin.status === 'VALIDO') return 'VÁLIDO';
        return pilacoin.status;
    }
</script>

{#snippet formatStatus(pilacoin: PilaCoinJson)}
    {@const status = getStatusString(pilacoin)}
    <div
        class={cn('whitespace-nowrap px-1 rounded-sm text-sm bg-slate-600', {
            'bg-yellow-700': status === 'BLOCO EM VALIDAÇÃO',
            'bg-green-700': status === 'VÁLIDO',
            'bg-indigo-500': status === 'TRANSFERIDO',
        })}
    >
        {status}
    </div>
{/snippet}

<button type="button" class="flex justify-between w-full gap-6 p-2 focus:outline-none" on:click={selectPilaCoin}>
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
            {@render formatStatus(pilacoin)}
            <time class="overflow-hidden text-ellipsis whitespace-nowrap">
                {format(new Date(pilacoin.dataCriacao), 'dd/MM/yyyy HH:mm')}
            </time>
        </div>
        <div class="grid place-items-center">
            {#if pilacoin.selected}
                <IconX />
            {:else}
                <IconChevron />
            {/if}
        </div>
    </div>
</button>
