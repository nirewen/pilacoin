package br.ufsm.csi.tapw.pilacoin.controller;

import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.service.ModuloService;
import br.ufsm.csi.tapw.pilacoin.types.IModulo;
import br.ufsm.csi.tapw.pilacoin.types.ModuloLogMessage;
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

    @GetMapping("/{nome}")
    public Modulo getModulo(@PathVariable String nome) {
        return moduloService.getModuloEntity(nome);
    }

    @GetMapping
    public List<IModulo> getModulos() {
        return moduloService.getAllModulos();
    }

    @SneakyThrows
    @GetMapping(value = "/logs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getLogs() {
        final SseEmitter sseEmitter = new SseEmitter(-1L);

        sseEmitter.onCompletion(() -> {
            synchronized (moduloService.sseEmitters) {
                moduloService.sseEmitters.remove(sseEmitter);
            }
        });
        sseEmitter.onTimeout(sseEmitter::complete);

        moduloService.sseEmitters.add(sseEmitter);

        moduloService.log(
            ModuloLogMessage.builder()
                .topic("UserMessage")
                .title("Conectado")
                .message("Conectado ao servidor de logs")
                .build()
        );

        return sseEmitter;
    }

    @PostMapping("/{nome}/toggle")
    public Modulo toggleModulo(@PathVariable String nome) {
        return moduloService.toggleModulo(nome);
    }
}
