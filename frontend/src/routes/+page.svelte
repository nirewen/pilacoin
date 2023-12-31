<script lang="ts">
    import { createQuery } from '@tanstack/svelte-query';

    import type { Modulo } from '$lib';
    import { IconExchange, IconInbox, IconLoader, IconUsers, IconWallet } from '$lib/icons';

    import LogCard from '$lib/components/cards/LogCard.svelte';
    import QueryCard from '$lib/components/cards/QueryCard.svelte';
    import TransferenciaCard from '$lib/components/cards/TransferenciaCard.svelte';
    import PilaList from '$lib/components/PilaList.svelte';
    import UsuarioList from '$lib/components/UsuarioList.svelte';

    async function getModulos() {
        return fetch('/api/modulo')
            .then((res) => res.json())
            .then((data: Modulo[]) => data);
    }

    const query = createQuery({
        queryKey: ['modulos'],
        queryFn: () => getModulos(),
    });
</script>

<div class="flex flex-wrap min-h-[46px] gap-2 overflow-auto resize-y h-80">
    {#if $query.isLoading}
        <div
            class="flex flex-col items-center justify-center flex-1 gap-2 border rounded-sm border-neutral-700 bg-neutral-950"
        >
            <IconLoader class="animate-spin" size={36} />
        </div>
    {:else if $query.isError}
        <div
            class="flex flex-col items-center justify-center flex-1 gap-2 border rounded-md border-neutral-700 bg-neutral-950"
        >
            <span>Não foi possível carregar os módulos</span>
            <small>Tente novamente mais tarde</small>
        </div>
    {:else if $query.isSuccess}
        {#each $query.data as modulo}
            <LogCard {modulo} />
        {/each}
    {/if}
</div>

<div class="flex flex-wrap flex-1 gap-2 overflow-hidden">
    <LogCard modulo={{ id: 0, name: 'Caixa de Entrada', topic: 'UserMessage', settings: [] }}>
        <IconInbox size={20} slot="icon" />
    </LogCard>
    <QueryCard title="Carteira" query="pilas" let:data>
        <IconWallet size={20} slot="icon" />
        <PilaList data={data.result} />
    </QueryCard>
    <QueryCard title="Usuários" query="usuarios" let:data>
        <IconUsers size={20} slot="icon" />
        <UsuarioList data={data.result} />
    </QueryCard>
    <TransferenciaCard>
        <IconExchange size={20} slot="icon" />
    </TransferenciaCard>
</div>
