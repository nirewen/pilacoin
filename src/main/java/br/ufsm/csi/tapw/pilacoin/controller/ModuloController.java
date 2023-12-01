package br.ufsm.csi.tapw.pilacoin.controller;

import br.ufsm.csi.tapw.pilacoin.exception.ModuloNotFoundException;
import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.service.ModuloService;
import br.ufsm.csi.tapw.pilacoin.types.IModulo;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/modulo")
public class ModuloController {
    private final ModuloService moduloService;

    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @GetMapping("/{nome}")
    public Modulo getModulo(@PathVariable String nome) {
        return moduloService.getModuloEntity(nome);
    }

    @SneakyThrows
    @GetMapping(value = "/{nome}/logs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getModuleLogs(@PathVariable String nome) {
        IModulo modulo = moduloService.getModulo(nome);

        if (modulo == null) {
            throw new ModuloNotFoundException();
        }

        return modulo.logEmitter;
    }

    @PostMapping("/{nome}/toggle")
    public Modulo toggleModulo(@PathVariable String nome) {
        return moduloService.toggleModulo(nome);
    }
}
