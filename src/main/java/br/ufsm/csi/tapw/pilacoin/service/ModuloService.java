package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.exception.ModuloNotFoundException;
import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.repository.ModuloRepository;
import br.ufsm.csi.tapw.pilacoin.types.IModulo;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ModuloService {
    private final ModuloRepository moduloRepository;
    private final DifficultyService difficultyService;
    private final Map<String, IModulo> modulos = new HashMap<>();

    public ModuloService(
        ModuloRepository moduloRepository,
        DifficultyService difficultyService,
        IModulo... modulos
    ) {
        this.moduloRepository = moduloRepository;
        this.difficultyService = difficultyService;

        for (IModulo modulo : modulos) {
            modulo.register(this.registerModulo(modulo));
        }
    }

    public Modulo getModuloEntity(String nome) {
        return moduloRepository.findOneByNome(nome);
    }

    public IModulo getModulo(String nome) {
        return this.modulos.get(nome);
    }

    public Modulo toggleModulo(String nome) {
        Modulo modulo = this.getModuloEntity(nome);

        modulo.setAtivo(!modulo.isAtivo());

        moduloRepository.save(modulo);

        IModulo moduloInterface = this.getModulo(nome);

        if (moduloInterface == null) {
            throw new ModuloNotFoundException();
        }

        moduloInterface.register(modulo);
        moduloInterface.update(this.difficultyService.getDifficulty());

        Logger.log("Modulo " + nome + " " + (modulo.isAtivo() ? "ativado" : "desativado"));
        moduloInterface.log("Modulo " + nome + " " + (modulo.isAtivo() ? "ativado" : "desativado"));

        return modulo;
    }

    public Modulo registerModulo(IModulo modulo) {
        Modulo foundModulo = moduloRepository.findOneByNome(modulo.getNome());

        this.modulos.put(modulo.getNome(), modulo);

        if (foundModulo != null) {
            return foundModulo;
        }

        Modulo moduloEntity = Modulo.builder()
            .nome(modulo.getNome())
            .ativo(true)
            .build();

        return moduloRepository.save(moduloEntity);
    }
}
