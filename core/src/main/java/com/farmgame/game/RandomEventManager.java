package com.farmgame.game;

import com.farmgame.player.Player;
import java.util.Random;

public class RandomEventManager {
    private final Random random = new Random();
    private int lastProcessedDay = -1;

    public void update(GameClock clock, Farm farm, Player player) {
        if (clock.getDay() != lastProcessedDay) {
            lastProcessedDay = clock.getDay();
            triggerEvent(farm, player);
        }
    }

    private void triggerEvent(Farm farm, Player player) {
        float roll = random.nextFloat();
        if (roll < 0.05f) { // ok 5%
            destroySomeCrops(farm);
            MessageManager.warning("Burza zniszczyła część plonów!");
        } else if (roll < 0.07f) { // ok 2% >= 0.05 < 0.07
            destroyAllCrops(farm);
            MessageManager.error("Tornado zniszczyło wszystkie plony!");
        } else if (roll < 0.13f) { // ok.5%
            thiefSteals(player);
            MessageManager.warning("Złodziej ukradł część pieniędzy!");
        }
    }

    private void destroySomeCrops(Farm farm) {
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                if (plot != null && plot.getPlant() != null && random.nextBoolean()) {
                    plot.destroyPlant();
                }
            }
        }
    }

    private void destroyAllCrops(Farm farm) {
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                if (plot != null) {
                    plot.destroyPlant();
                }
            }
        }
    }

    private void thiefSteals(Player player) {
        float stealFactor = 0.2f + random.nextFloat() * 0.3f;
        int money = player.getMoney();
        int lost = Math.round(money * stealFactor);
        if (lost > 0) {
            player.addMoney(-lost);
        }
    }
}
