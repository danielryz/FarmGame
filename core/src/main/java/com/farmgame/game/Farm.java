package com.farmgame.game;

public class Farm {
    private final int width;
    private final int height;
    private final Plot[][] plots;
    private int[][] plotPrices;
    private final int BASE_PRICE = 10;
    private final float MULTIPLIER = 1.2f;

    public Farm(int width, int height) {
        this.width = width;
        this.height = height;
        plots = new Plot[width][height];
        plotPrices = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                plots[x][y] = new Plot();
                plotPrices[x][y] = (int) ( BASE_PRICE * MULTIPLIER * (x + y));
            }
        }
        plots[0][0].unlock();
    }

    public Plot getPlot(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        return plots[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPlotPrice(int x, int y) {
        return plotPrices[x][y];
    }
}
