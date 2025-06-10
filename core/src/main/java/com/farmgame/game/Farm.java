package com.farmgame.game;

public class Farm {
    private final int width;
    private final int height;
    private final Plot[][] plots;
    private final int[][] plotPrices;

    private final AnimalPen[][] animalPens;
    private final int[][] penPrices;
    private final int penWidth;
    private final int penHeight;

    private final int BASE_PLOT_PRICE = 10;
    private final float MULTIPLIER_PLOT = 1.2f;
    private final float BASE_PEN_PRICE = 100;
    private final float MULTIPLIER_PEN = 1.5f;


    public Farm(int width, int height, int penWidth, int penHeight) {
        this.width = width;
        this.height = height;

        this.penWidth = penWidth;
        this.penHeight = penHeight;
        plotPrices = new int[width][height];
        penPrices = new int[penWidth][penHeight];

        plots = new Plot[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                plots[x][y] = new Plot();
                plotPrices[x][y] = (int) ( BASE_PLOT_PRICE * MULTIPLIER_PLOT * (x + y));
            }
        }
        plots[0][0].unlock();

        animalPens = new AnimalPen[penWidth][penHeight];
        for (int x = 0; x < penWidth; x++) {
            for (int y = 0; y < penHeight; y++) {
                animalPens[x][y] = new AnimalPen(x, y);
                penPrices[x][y] = (int) (BASE_PEN_PRICE * MULTIPLIER_PEN * (x + y));
            }
        }
        animalPens[0][0].unlock();
    }

    public Plot getPlot(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return plots[x][y];
    }

    public int getPlotPrice(int x, int y) {
        return plotPrices[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public AnimalPen getAnimalPen(int x, int y) {
        if (x < 0 || x >= penWidth || y < 0 || y >= penHeight) {
            return null;
        }
        return animalPens[x][y];
    }

    public int getPenPrice(int x, int y) {
        return penPrices[x][y];
    }

    public int getPenWidth() {
        return penWidth;
    }

    public int getPenHeight() {
        return penHeight;
    }

    public boolean inPenRange(int x, int y) {
        return (x >= 0 && x < penWidth && y >= 0 && y < penHeight);
    }

}
