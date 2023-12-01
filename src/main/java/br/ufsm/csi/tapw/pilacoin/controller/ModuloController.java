package br.ufsm.csi.tapw.pilacoin.controller;

import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.service.ModuloService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modulo")
public class ModuloController {
    private final ModuloService moduloService;

    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @GetMapping("/{nome}")
    public Modulo getModulo(@PathVariable String nome) {
        return moduloService.getModulo(nome);
    }

    @PostMapping("/{nome}/toggle")
    public Modulo toggleModulo(@PathVariable String nome) {
        return moduloService.toggleModulo(nome);
    }
}
