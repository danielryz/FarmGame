package com.farmgame.game;

public class Plot {
    public enum State {
        EMPTY, PLANTED, GROWTH, READY_TO_HARVEST
    }

    private State state = State.EMPTY;

    public void nextState() {
        switch (state) {
            case EMPTY -> state = State.PLANTED;
            case PLANTED -> state = State.GROWTH;
            case GROWTH -> state = State.READY_TO_HARVEST;
            case READY_TO_HARVEST -> state = State.EMPTY;
        }
    }

    public State getState() {
        return state;
    }
}
