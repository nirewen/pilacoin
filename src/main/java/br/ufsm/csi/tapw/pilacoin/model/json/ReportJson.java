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

    public boolean compareTo(ReportJson otherReport) {
        return this.nomeUsuario.equals(otherReport.nomeUsuario)
            && this.minerouPila.equals(otherReport.minerouPila)
            && this.validouPila.equals(otherReport.validouPila)
            && this.minerouBloco.equals(otherReport.minerouBloco)
            && this.validouBloco.equals(otherReport.validouBloco)
            && this.transferiuPila.equals(otherReport.transferiuPila);
    }

    public void printReport() {
        Logger.logBox(STR. """
            REPORT                  \{ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(geradoEm)) }
            ---
            Nome de usuário: \{ nomeUsuario }
            Minerou pila: \{ minerouPila ? "Sim" : "Não" }
            Validou pila: \{ validouPila ? "Sim" : "Não" }
            Minerou bloco: \{ minerouBloco ? "Sim" : "Não" }
            Validou bloco: \{ validouBloco ? "Sim" : "Não" }
            Transferiu pila: \{ transferiuPila ? "Sim" : "Não" }
            """ );
    }
}
