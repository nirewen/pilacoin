<script lang="ts">
    import { createMutation } from '@tanstack/svelte-query';

    import { queries, queryClient } from '$lib';

    import PilaRow from '../row/PilaRow.svelte';
    import UsuarioRow from '../row/UsuarioRow.svelte';
    import Card from './Card.svelte';

    $: usuarios = queries.get('usuarios');
    $: pilas = queries.get('pilas');

    $: usuario = $usuarios?.result.find((u) => u.selected);
    $: pilacoin = $pilas?.result.find((u) => u.selected);

    const mutation = createMutation({
        mutationKey: ['transferir', usuario?.id, pilacoin?.nonce],
        mutationFn: async () => {
            const response = await fetch(`/api/transferir`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    chaveUsuarioDestino: usuario?.chavePublica,
                    nomeUsuarioDestino: usuario?.nome,
                    noncePila: pilacoin?.nonce,
                }),
            });

            queryClient.refetchQueries({
                queryKey: ['query', 'pilas'],
                exact: true,
            });

            return response.json();
        },
    });

    $: isValidPila = pilacoin && pilacoin.status === 'VALIDO' && pilacoin.transacoes?.length === 1;
    $: canTransfer = usuario && isValidPila;
</script>

<Card>
    <svelte:fragment slot="header">
        <header class="flex items-center gap-2">
            <div class="p-1 aspect-square empty:hidden">
                <slot name="icon" />
            </div>
            <div class="flex-1">
                <h2 class="text-xl font-bold text-capitalize">Transferir PilaCoin</h2>
            </div>
        </header>
    </svelte:fragment>
    <div class="flex flex-col h-full gap-2 p-2 overflow-x-hidden overflow-y-auto rounded-sm bg-neutral-800">
        <span class="text-sm font-bold uppercase">Selecione o PilaCoin</span>
        <div class="flex flex-col bg-[#2f2f2f] h-[60px]">
            {#if pilacoin}
                <PilaRow {pilacoin} large />
            {/if}
        </div>
        <span class="text-sm font-bold uppercase">Selecione o Usuário</span>
        <div class="flex flex-col bg-[#2f2f2f] h-[68px]">
            {#if usuario}
                <UsuarioRow {usuario} large />
            {/if}
        </div>

        <div class="flex flex-col mt-auto">
            {#if pilacoin && !isValidPila}
                <div class="flex flex-col text-red-500">
                    <span>Esse PilaCoin não pode ser transferido.</span>
                    <span>Ele já foi transferido ou ainda não foi validado</span>
                </div>
            {/if}

            <button
                type="button"
                class="px-6 py-2 bg-green-600 rounded-md disabled:opacity-70 disabled:cursor-not-allowed"
                on:click={() => $mutation.mutate()}
                disabled={!canTransfer}
            >
                Transferir
            </button>
        </div>
    </div>
</Card>
