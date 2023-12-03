import type { Writable } from 'svelte/store';

import { browser } from '$app/environment';
import { QueryClient } from '@tanstack/svelte-query';

export const queries = new Map<string, Writable<QueryResponse>>();

export const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            enabled: browser,
        },
    },
});

export type Modulo = {
    modulo: {
        id: number;
        nome: string;
        descricao: string;
        ativo: boolean;
    };
    nome: string;
};

export type TransacaoJson = {
    id: number;
    chaveUsuarioOrigem: string;
    chaveUsuarioDestino: string;
    assinatura: string;
    origem: string;
    noncePila: string;
    status: string;
    dataTransacao: string;
};

export type PilaCoinJson = {
    id: number;
    dataCriacao: string;
    chaveCriador: string;
    nomeCriador: string;
    status: string;
    nonce: string;
    transacoes: TransacaoJson[];
    selected: boolean;
};

export type BlocoJson = {
    numeroBloco: number;
    nonceBlocoAnterior: string;
    nonce: string;
    chaveUsuarioMinerador: string;
    nomeUsuarioMinerador: string;
    transacoes: TransacaoJson[];
    minerado: boolean;
};

export type UsuarioJson = {
    id: number;
    nome: string;
    chavePublica: string;
    selected: boolean;
};

export type QueryResponse = {
    idQuery: number;
    usuario: string;
    pilasResult: PilaCoinJson[];
    blocosResult: BlocoJson[];
    usuariosResult: UsuarioJson[];
};
