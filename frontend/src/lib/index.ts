export const logEventSource = new EventSource(`/api/modulo/logs`);

export type Modulo = {
    modulo: {
        id: number;
        nome: string;
        descricao: string;
        ativo: boolean;
    };
    nome: string;
};
