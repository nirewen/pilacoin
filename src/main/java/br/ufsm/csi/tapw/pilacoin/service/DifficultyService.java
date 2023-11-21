package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.types.Observable;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DifficultyService implements Observable<Difficulty> {
    private final QueueService queueService;
    private final PilaCoinService pilaCoinService;

    private final List<Observer<Difficulty>> observers = new ArrayList<>();
    private Difficulty currentDifficulty;
    private boolean started = false;

    public DifficultyService(QueueService queueService, PilaCoinService pilaCoinService, ValidationService validationService, BlockService blockService) {
        this.queueService = queueService;
        this.pilaCoinService = pilaCoinService;

        this.subscribe(pilaCoinService);
        this.subscribe(validationService);
        this.subscribe(blockService);
    }

    @RabbitListener(queues = "${queue.dificuldade}")
    public void getDifficulty(@Payload String difficulty) {
        Difficulty diff = JacksonUtil.convert(difficulty, Difficulty.class);

        if (currentDifficulty == null || !diff.getDificuldade().equals(currentDifficulty.getDificuldade())) {
            this.updateDifficulty(diff);
        }

        if (!started) {
            this.startMining();
        }
    }

    private void updateDifficulty(Difficulty diff) {
        System.out.println("Dificuldade atual: " + diff.getDificuldade());

        currentDifficulty = diff;

        if (!this.observers.isEmpty()) {
            this.observers.forEach(observer ->
                    observer.update(this.currentDifficulty)
            );
        }
    }

    private void startMining() {
        this.started = true;

        MiningService miningService = new MiningService(this.queueService, this.pilaCoinService);

        new Thread(miningService).start();
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
