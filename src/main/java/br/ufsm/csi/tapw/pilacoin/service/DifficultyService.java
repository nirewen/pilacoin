package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.service.modulos.BlockDiscoveryService;
import br.ufsm.csi.tapw.pilacoin.service.modulos.BlockValidationService;
import br.ufsm.csi.tapw.pilacoin.service.modulos.PilaCoinMiningService;
import br.ufsm.csi.tapw.pilacoin.service.modulos.PilaCoinValidationService;
import br.ufsm.csi.tapw.pilacoin.types.Observable;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import lombok.Getter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DifficultyService implements Observable<Difficulty> {
    private final List<Observer<Difficulty>> observers = new ArrayList<>();

    @Getter
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
//
//        this.updateDifficulty(
//            Difficulty.builder()
//                .dificuldade(new BigInteger("f".repeat(59), 16))
//                .validadeFinal(new Date())
//                .inicio(new Date())
//                .build()
//        );
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
        Logger.logBox("DIFICULDADE\n---\n" + diff.getDificuldade());

        difficulty = diff;

        if (!this.observers.isEmpty()) {
            this.observers.forEach(observer ->
                observer.update(this.difficulty)
            );
        }
    }

    @Override
    public void subscribe(Observer<Difficulty> observer) {
        this.observers.add(observer);
    }

    @Override
    public void unsubscribe(Observer<Difficulty> observer) {
        this.observers.remove(observer);
    }
}
