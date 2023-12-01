package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.repository.ModuloRepository;
import br.ufsm.csi.tapw.pilacoin.types.IModulo;
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

    public Modulo getModulo(String nome) {
        return moduloRepository.findOneByNome(nome);
    }

    public Modulo toggleModulo(String nome) {
        Modulo modulo = this.getModulo(nome);

        modulo.setAtivo(!modulo.isAtivo());

        moduloRepository.save(modulo);

        IModulo moduloInterface = this.modulos.get(nome);

        if (moduloInterface != null) {
            moduloInterface.register(modulo);
            moduloInterface.update(this.difficultyService.getDifficulty());
        }

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
