package br.ufsm.csi.tapw.pilacoin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Modulo não encontrado")
public class EmptyResultException extends RuntimeException {
}
