package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.types.Observable;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DifficultyService implements Observable<Difficulty> {
    private final List<Observer<Difficulty>> observers = new ArrayList<>();
    private Difficulty currentDifficulty;

    public DifficultyService(MiningService miningService, ValidationService validationService, BlockService blockService) {
        this.subscribe(miningService);
        this.subscribe(validationService);
        this.subscribe(blockService);

        this.updateDifficulty(
            Difficulty.builder()
                .dificuldade(new BigInteger("f".repeat(59), 16))
                .validadeFinal(new Date())
                .inicio(new Date())
                .build()
        );
    }

    @RabbitListener(queues = "${queue.dificuldade}")
    public void getDifficulty(@Payload String difficulty) {
        Difficulty diff = JacksonUtil.convert(difficulty, Difficulty.class);

        if (currentDifficulty == null || !diff.getDificuldade().equals(currentDifficulty.getDificuldade())) {
            this.updateDifficulty(diff);
        }
    }

    private void updateDifficulty(Difficulty diff) {
        Logger.logBox("DIFICULDADE\n---\n" + diff.getDificuldade());

        currentDifficulty = diff;

        if (!this.observers.isEmpty()) {
            this.observers.forEach(observer ->
                observer.update(this.currentDifficulty)
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
