package br.ufsm.csi.tapw.pilacoin.model.json;

import br.ufsm.csi.tapw.pilacoin.util.Logger;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class ReportJson {
    public Long geradoEm;
    public String nomeUsuario;
    public Boolean minerouPila;
    public Boolean validouPila;
    public Boolean minerouBloco;
    public Boolean validouBloco;
    public Boolean transferiuPila;

    public void printReport() {
        Logger.logBox("""
            Relatório                    %s
            Nome de usuário: %s
            Minerou pila: %s
            Validou pila: %s
            Minerou bloco: %s
            Validou bloco: %s
            Transferiu pila: %s
            """.formatted(
            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(this.geradoEm)),
            this.nomeUsuario,
            this.minerouPila ? "Sim" : "Não",
            this.validouPila ? "Sim" : "Não",
            this.minerouBloco ? "Sim" : "Não",
            this.validouBloco ? "Sim" : "Não",
            this.transferiuPila ? "Sim" : "Não"
        ));
    }
}
