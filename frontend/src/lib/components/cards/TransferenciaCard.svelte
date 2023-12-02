<script lang="ts">
    import type { PilaCoinJson, UsuarioJson } from '$lib';
    import { createMutation } from '@tanstack/svelte-query';
    import PilaRow from './row/PilaRow.svelte';
    import UsuarioRow from './row/UsuarioRow.svelte';

    export let usuario: UsuarioJson | null = null;
    export let pilacoin: PilaCoinJson | null = null;

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

            return response.json();
        },
    });

    $: canTransfer = usuario && pilacoin && pilacoin.status === 'VALIDO';
</script>

<div class="flex flex-col gap-1 w-0 flex-1 basis-[24%] p-2 h-full order-2 border rounded-md border-neutral-800">
    <div class="flex items-center justify-between">
        <h2 class="text-xl font-bold text-capitalize">Transferir pila</h2>
    </div>
    <div class="flex flex-col h-full gap-2 p-2 overflow-x-hidden overflow-y-auto rounded-sm bg-neutral-800">
        <span class="text-sm font-bold uppercase">Selecione o PilaCoin</span>
        <PilaRow {pilacoin} large />
        <span class="text-sm font-bold uppercase">Selecione o Usuário</span>
        <UsuarioRow {usuario} large />

        <div class="flex flex-col mt-auto">
            {#if pilacoin && pilacoin.status !== 'VALIDO'}
                <div class="text-red-500">Esse PilaCoin não pode ser transferido.</div>
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
</div>
