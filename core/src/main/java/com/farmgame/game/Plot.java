package com.farmgame.game;

public class Plot {
    public enum State {
        EMPTY, PLANTED, GROWTH, READY_TO_HARVEST
    }

    private State state = State.EMPTY;
    private Plant plant;

    public State getState() {
        return state;
    }

    public Plant getPlant() {
        return plant;
    }

    public void plant(Plant plant) {
        if (state == State.EMPTY) {
            this.plant = plant;
            this.state = State.PLANTED;
        }
    }

    public void water() {
        if (plant != null && state != State.READY_TO_HARVEST) {
            plant.water();
            if(state == State.PLANTED || state == State.GROWTH) {
                state = State.GROWTH;
            }
        }
    }

    public void update(float delta) {
        if (plant != null && state != State.READY_TO_HARVEST) {
            plant.update(delta);

            switch (plant.getStage()) {
                case PLANTED -> state = State.PLANTED;
                case GROWING -> state = State.GROWTH;
                case READY -> state = State.READY_TO_HARVEST;
            }
        }
    }

    public void harvest() {
        if (state == State.READY_TO_HARVEST) {
            if (plant != null) {
                plant.resetWatered();
            }
            plant = null;
            state = State.EMPTY;
        }
    }
}
