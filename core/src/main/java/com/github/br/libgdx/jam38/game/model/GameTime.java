package com.github.br.libgdx.jam38.game.model;

public class GameTime {

    private int timerSec;
    private float accumulator;

    public GameTime(int timerSec) {
        this.timerSec = timerSec;
    }

    public void tick(float deltaTime) {
        accumulator += deltaTime;
        if (accumulator >= 1f) {
            accumulator -= 1f;
            if (!isTimeOver()) {
                timerSec--;
            }
        }
    }

    public boolean isTimeOver() {
        return timerSec == 0;
    }

    public void reduceTime(int seconds) {
        timerSec -= seconds;
    }

    public int getTimerSec() {
        return timerSec;
    }

}
