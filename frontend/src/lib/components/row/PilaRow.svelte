<script lang="ts">
    import { format } from 'date-fns';

    import { queries, type PilaCoinJson } from '$lib';
    import { IconChevron, IconX } from '$lib/icons';
    import { cn } from '$lib/utils';
    import UserAvatar from '../UserAvatar.svelte';

    export let pilacoin: PilaCoinJson;
    export let large = false;

    const pilas = queries.get('pilas');

    function selectPilaCoin() {
        if (!pilacoin) return;

        let foundPila = $pilas?.result.find((u) => u.selected);

        if (foundPila) {
            foundPila.selected = false;
        }

        let newPila = $pilas?.result.find((u) => u.nonce === pilacoin?.nonce);

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
    <div class="flex flex-col min-w-0 gap-1">
        <span class="flex items-center gap-1">
            <UserAvatar nome={pilacoin.nomeCriador} class="w-4 h-4" />
            {pilacoin.nomeCriador}
        </span>
        <span class="overflow-hidden font-mono text-sm leading-4 text-ellipsis">{pilacoin.nonce}</span>
    </div>
    <div class="flex items-center h-full gap-1">
        <div class="flex flex-col items-end justify-between h-full gap-1">
            {@render formatStatus(pilacoin)}
            <time
                class="px-1 overflow-hidden text-sm leading-4 rounded-sm text-ellipsis whitespace-nowrap bg-neutral-700"
            >
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
