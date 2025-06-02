package com.farmgame.game;

public class Farm {
    private final int width;
    private final int height;
    private final Plot[][] plots;

    public Farm(int width, int height) {
        this.width = width;
        this.height = height;
        plots = new Plot[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                plots[x][y] = new Plot();
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
}
