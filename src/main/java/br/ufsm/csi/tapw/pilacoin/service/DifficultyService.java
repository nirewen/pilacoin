package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.internal.AppModule;
import br.ufsm.csi.tapw.pilacoin.types.Observable;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.jackson.JacksonUtil;
import lombok.Getter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class DifficultyService implements Observable<Difficulty> {
    private final List<Observer<Difficulty>> observers = new ArrayList<>();
    private Difficulty difficulty;

    public DifficultyService(AppModule... modules) {
        for (AppModule module : modules) {
            this.subscribe(module);
        }
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

        this.difficulty = diff;

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
