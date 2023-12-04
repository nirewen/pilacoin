package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.service.modulos.BlockDiscoveryService;
import br.ufsm.csi.tapw.pilacoin.service.modulos.BlockValidationService;
import br.ufsm.csi.tapw.pilacoin.service.modulos.PilaCoinMiningService;
import br.ufsm.csi.tapw.pilacoin.service.modulos.PilaCoinValidationService;
import br.ufsm.csi.tapw.pilacoin.types.observer.DifficultyObservable;
import br.ufsm.csi.tapw.pilacoin.types.observer.DifficultyObserver;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import lombok.Getter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Getter
@Service
public class DifficultyService implements DifficultyObservable {
    private Difficulty difficulty;

    public DifficultyService(
        PilaCoinMiningService pilaCoinMiningService,
        PilaCoinValidationService validationService,
        BlockDiscoveryService blockDiscoveryService,
        BlockValidationService blockValidationService
    ) {
        this.subscribe(pilaCoinMiningService);
        this.subscribe(validationService);
        this.subscribe(blockDiscoveryService);
        this.subscribe(blockValidationService);
    }

    @RabbitListener(queues = "${queue.dificuldade}")
    public void onReceiveDifficulty(@Payload String difficultyStr) {
        Difficulty difficulty = JacksonUtil.convert(difficultyStr, Difficulty.class);

        if (difficulty == null) {
            return;
        }

        if (this.difficulty == null || !difficulty.getDificuldade().equals(this.difficulty.getDificuldade())
        ) {
            this.updateDifficulty(difficulty);
        }
    }

    private void updateDifficulty(Difficulty diff) {
        Logger.log("Dificuldade alterada para " + diff.getDificuldade());

        difficulty = diff;

        if (!this.observers.isEmpty()) {
            this.observers.forEach(observer ->
                observer.updateDifficulty(this.difficulty)
            );
        }
    }

    @Override
    public void subscribe(DifficultyObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void unsubscribe(DifficultyObserver observer) {
        this.observers.remove(observer);
    }
}
