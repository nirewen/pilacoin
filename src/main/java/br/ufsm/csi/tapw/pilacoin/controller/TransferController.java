package br.ufsm.csi.tapw.pilacoin.controller;

import br.ufsm.csi.tapw.pilacoin.dto.TransferirPilaDTO;
import br.ufsm.csi.tapw.pilacoin.model.Transferencia;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import br.ufsm.csi.tapw.pilacoin.util.jackson.JacksonUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/transferir")
public class TransferController {
    private final SharedUtil sharedUtil;
    private final QueueService queueService;

    public TransferController(SharedUtil sharedUtil, QueueService queueService) {
        this.sharedUtil = sharedUtil;
        this.queueService = queueService;
    }

    @PostMapping
    public Transferencia transferir(@RequestBody TransferirPilaDTO dto) {
        Transferencia transferencia = Transferencia.builder()
            .chaveUsuarioOrigem(this.sharedUtil.getPublicKey().getEncoded())
            .nomeUsuarioOrigem(this.sharedUtil.getProperties().getUsername())
            .chaveUsuarioDestino(CryptoUtil.decodeBase64(dto.getChaveUsuarioDestino()))
            .nomeUsuarioDestino(dto.getNomeUsuarioDestino())
            .noncePila(dto.getNoncePila())
            .dataTransacao(new Date())
            .build();

        transferencia.setAssinatura(CryptoUtil.sign(JacksonUtil.toString(transferencia), this.sharedUtil.getPrivateKey()));

        this.queueService.publishTransferencia(transferencia);

        return transferencia;
    }
}
