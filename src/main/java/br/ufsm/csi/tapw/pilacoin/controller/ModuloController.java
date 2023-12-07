package br.ufsm.csi.tapw.pilacoin.controller;

import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.model.internal.AbstractSetting;
import br.ufsm.csi.tapw.pilacoin.service.ModuloService;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/modulo")
public class ModuloController {
    private final ModuloService moduloService;

    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @GetMapping
    public List<Modulo> getModulos() {
        return moduloService.getAllModulos();
    }

    @GetMapping("/{nome}")
    public Modulo getModulo(@PathVariable String nome) {
        return moduloService.getModuloEntity(nome);
    }

    @SneakyThrows
    @GetMapping(value = "/logs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getLogs() {
        return this.moduloService.onConnect();
    }

    @PatchMapping("/{nome}")
    public Modulo updateSettings(@PathVariable String nome, @RequestBody List<AbstractSetting<?>> settings) {
        return moduloService.updateSettings(nome, settings);
    }
}
