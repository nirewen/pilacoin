import type { Writable } from 'svelte/store';

import { browser } from '$app/environment';
import { QueryClient } from '@tanstack/svelte-query';

export const queries = new Map<string, Writable<QueryResponse<any>>>();

export const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            enabled: browser,
        },
    },
});

export type StringSetting = {
    kind: 'STRING';
    name: string;
    value: string;
};

export type BooleanSetting = {
    kind: 'BOOLEAN';
    name: string;
    value: boolean;
};

export type ConstantSetting = {
    kind: 'CONSTANT';
    name: string;
    value: number;
};

export type RangeSetting = {
    kind: 'RANGE';
    name: string;
    value: {
        value: number;
        min: number;
        max: number;
    };
};

export type ModuloSettings = StringSetting | BooleanSetting | ConstantSetting | RangeSetting;

export type Modulo = {
    id: number;
    name: string;
    topic: string;
    settings: ModuloSettings[];
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

export type QueryResponseJson = {
    idQuery: number;
    usuario: string;
    pilasResult: PilaCoinJson[];
    blocosResult: BlocoJson[];
    usuariosResult: UsuarioJson[];
};

export type QueryResponse<T> = {
    idQuery: number;
    usuario: string;
    result: T[];
};

export type LogMessage = {
    timestamp: number;
    title: string;
    message: string;
    extra: object;
    level: 'INFO' | 'ERROR';
    expanded?: boolean;
};
