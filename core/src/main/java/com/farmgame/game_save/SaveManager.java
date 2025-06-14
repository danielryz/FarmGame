package com.farmgame.game_save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.farmgame.game.*;
import com.farmgame.player.InventoryItem;
import com.farmgame.player.Player;

import java.util.ArrayList;

public class SaveManager {
    private static final String SAVE_FILE = "farmgame_save.json";
    private Json json;

    public SaveManager() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
    }

    // Konwersja Color do hex string
    private String colorToHex(Color color) {
        if (color == null) return "#FFFFFF";
        return String.format("#%02X%02X%02X",
            (int)(color.r * 255),
            (int)(color.g * 255),
            (int)(color.b * 255));
    }

    // Konwersja hex string do Color
    private Color hexToColor(String hex) {
        if (hex == null || hex.length() != 7 || !hex.startsWith("#")) {
            return Color.WHITE;
        }
        try {
            int r = Integer.parseInt(hex.substring(1, 3), 16);
            int g = Integer.parseInt(hex.substring(3, 5), 16);
            int b = Integer.parseInt(hex.substring(5, 7), 16);
            return new Color(r/255f, g/255f, b/255f, 1f);
        } catch (NumberFormatException e) {
            return Color.WHITE;
        }
    }

    // Konwertuj obiekt gry do stanu zapisu
    public GameState convertToSaveState(Player player, Farm farm, GameClock gameClock,
                                        Weather weather, PlantType selectedPlant,
                                        String currentAction) {
        // Konwertuj gracza
        SavedPlayer savedPlayer = new SavedPlayer();
        savedPlayer.name = player.getName();
        savedPlayer.money = player.getMoney();
        savedPlayer.level = player.getLevel();
        savedPlayer.exp = player.getExp();

        // Konwertuj inwentarz
        savedPlayer.inventory = new ArrayList<>();
        for (InventoryItem item : player.getPlayerInventory().getItems()) {
            if (item.getQuantity() > 0) {
                savedPlayer.inventory.add(new SavedInventoryItem(
                    item.getName(),
                    item.getQuantity(),
                    item.getSellPrice()
                ));
            }
        }

        // Konwertuj farmę
        SavedFarm savedFarm = new SavedFarm();
        savedFarm.width = farm.getWidth();
        savedFarm.height = farm.getHeight();
        savedFarm.penWidth = farm.getPenWidth();
        savedFarm.penHeight = farm.getPenHeight();

        // Konwertuj działki
        savedFarm.plots = new SavedPlot[farm.getWidth()][farm.getHeight()];
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                if (plot != null) {
                    SavedPlant savedPlant = null;
                    if (plot.getPlant() != null) {
                        Plant plant = plot.getPlant();
                        savedPlant = new SavedPlant(
                            plant.getType().getName(),
                            plant.getTimeRemaining(),
                            plant.isWatered(),
                            colorToHex(plant.getType().getColor())
                        );
                    }

                    savedFarm.plots[x][y] = new SavedPlot(
                        plot.isBlocked(),
                        plot.getState().name(),
                        savedPlant
                    );
                }
            }
        }

        // Konwertuj zagrody dla zwierząt
        savedFarm.animalPens = new Weather.SavedAnimalPen[farm.getPenWidth()][farm.getPenHeight()];
        for (int px = 0; px < farm.getPenWidth(); px++) {
            for (int py = 0; py < farm.getPenHeight(); py++) {
                AnimalPen pen = farm.getAnimalPen(px, py);
                if (pen != null) {
                    SavedAnimal savedAnimal = null;
                    if (pen.getCurrentAnimal() != null) {
                        Animal animal = pen.getCurrentAnimal();
                        savedAnimal = new SavedAnimal(
                            animal.getType().getName(),
                            animal.getProductState().name(),
                            animal.getTimeToNextProduct(),
                            colorToHex(animal.getType().getColor())
                        );
                    }

                    savedFarm.animalPens[px][py] = new Weather.SavedAnimalPen(
                        pen.isBlocked(),
                        pen.getState().name(),
                        savedAnimal
                    );
                }
            }
        }

        // Konwertuj zegar gry
        SavedGameClock savedClock = new SavedGameClock(
            gameClock.getDay(),
            gameClock.getWeekDay().name(),
            gameClock.getTimeOfDay().name(),
            gameClock.getCurrentTime()
        );

        // Konwertuj pogodę
        SavedWeather savedWeather = new SavedWeather(
            weather.getCurrentWeatherName(), // będziesz musiał dodać getter
            weather.getTimeUntilChange() // będziesz musiał dodać getter
        );

        return new GameState(
            savedPlayer,
            savedFarm,
            savedClock,
            savedWeather,
            selectedPlant != null ? selectedPlant.getName() : null,
            currentAction
        );
    }

    // Zapisywanie stanu gry
    public void saveGame(Player player, Farm farm, GameClock gameClock,
                         Weather weather, PlantType selectedPlant, String currentAction) {
        try {
            GameState gameState = convertToSaveState(player, farm, gameClock,
                weather, selectedPlant, currentAction);
            String jsonString = json.toJson(gameState);
            FileHandle file = Gdx.files.local(SAVE_FILE);
            file.writeString(jsonString, false);
            Gdx.app.log("SaveManager", "Gra zapisana pomyślnie");
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Błąd podczas zapisywania: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Odczytywanie stanu gry
    public GameState loadGame() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            if (file.exists()) {
                String jsonString = file.readString();
                GameState gameState = json.fromJson(GameState.class, jsonString);
                Gdx.app.log("SaveManager", "Gra wczytana pomyślnie");
                return gameState;
            } else {
                Gdx.app.log("SaveManager", "Plik zapisu nie istnieje, tworzę nowy stan gry");
                return null; // Zwróć null jeśli nie ma zapisu
            }
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Błąd podczas wczytywania: " + e.getMessage());
            e.printStackTrace();
            return null; // Zwróć null w przypadku błędu
        }
    }

    // Metoda do aplikowania wczytanego stanu do obiektów gry
    public void applyGameState(GameState gameState, Player player, Farm farm,
                               GameClock gameClock, Weather weather) {
        if (gameState == null) return;

        try {
            // Przywróć stan gracza
            if (gameState.player != null) {
                player.setName(gameState.player.name);
                player.setMoney(gameState.player.money);
                player.setLevel(gameState.player.level);
                player.setExp(gameState.player.exp);

                // Wyczyść i przywróć inwentarz
                player.getPlayerInventory().clearInventory();
                for (SavedInventoryItem savedItem : gameState.player.inventory) {
                    InventoryItem item = new InventoryItem(
                        savedItem.name,
                        savedItem.quantity,
                        savedItem.sellPrice
                    );
                    player.getPlayerInventory().addItem(item);
                }
            }

            // Przywróć stan farmy
            if (gameState.farm != null) {
                // Przywróć działki
                for (int x = 0; x < gameState.farm.width && x < farm.getWidth(); x++) {
                    for (int y = 0; y < gameState.farm.height && y < farm.getHeight(); y++) {
                        SavedPlot savedPlot = gameState.farm.plots[x][y];
                        if (savedPlot != null) {
                            Plot plot = farm.getPlot(x, y);
                            if (plot != null) {
                                // Przywróć stan działki
                                if (savedPlot.isBlocked) {
                                    plot.block();
                                } else {
                                    plot.unlock();
                                }

                                // Przywróć roślinę, jeśli istnieje
                                if (savedPlot.plant != null) {
                                    // Znajdź typ rośliny na podstawie nazwy
                                    PlantType plantType = findPlantTypeByName(savedPlot.plant.typeName);
                                    if (plantType != null) {
                                        Plant plant = new Plant(plantType);
                                        plant.setTimeRemaining(savedPlot.plant.timeRemaining); // dodaj setter
                                        plant.setWatered(savedPlot.plant.isWatered); // dodaj setter
                                        plot.plant(plant);

                                        // Ustaw stan działki
                                        plot.setState(Plot.State.valueOf(savedPlot.state)); // dodaj setter
                                    }
                                }
                            }
                        }
                    }
                }

                // Przywróć zagrody
                for (int px = 0; px < gameState.farm.penWidth && px < farm.getPenWidth(); px++) {
                    for (int py = 0; py < gameState.farm.penHeight && py < farm.getPenHeight(); py++) {
                        Weather.SavedAnimalPen savedPen = gameState.farm.animalPens[px][py];
                        if (savedPen != null) {
                            AnimalPen pen = farm.getAnimalPen(px, py);
                            if (pen != null) {
                                // Przywróć stan zagrody
                                if (savedPen.isBlocked) {
                                    pen.block(); // będziesz musiał dodać metodę
                                } else {
                                    pen.unlock(); // już masz
                                }

                                // Przywróć zwierzę, jeśli istnieje
                                if (savedPen.currentAnimal != null) {
                                    AnimalType animalType = findAnimalTypeByName(savedPen.currentAnimal.typeName);
                                    if (animalType != null) {
                                        Animal animal = new Animal(animalType);
                                        animal.setProductState(Animal.ProductState.valueOf(savedPen.currentAnimal.productState)); // dodaj setter
                                        animal.setTimeToNextProduct(savedPen.currentAnimal.timeToNextProduct); // dodaj setter
                                        pen.setCurrentAnimal(animal); // dodaj setter
                                        pen.setState(AnimalPen.State.valueOf(savedPen.state)); // dodaj setter
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Przywróć zegar gry
            if (gameState.gameClock != null) {
                gameClock.setDay(gameState.gameClock.day); // dodaj setter
                gameClock.setWeekDay(GameClock.WeekDay.valueOf(gameState.gameClock.weekDay)); // dodaj setter
                gameClock.setTimeOfDay(GameClock.TimeOfDay.valueOf(gameState.gameClock.timeOfDay)); // dodaj setter
                gameClock.setCurrentTime(gameState.gameClock.currentTime); // dodaj setter
            }

            // Przywróć pogodę
            if (gameState.weather != null) {
                weather.setCurrentWeather(gameState.weather.currentWeather); // dodaj setter
                weather.setTimeUntilChange(gameState.weather.timeUntilChange); // dodaj setter
            }

            Gdx.app.log("SaveManager", "Stan gry przywrócony pomyślnie");

        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Błąd podczas przywracania stanu gry: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private PlantType findPlantTypeByName(String name) {
        return PlantType.getByName(name); // przykład
    }

    private AnimalType findAnimalTypeByName(String name) {
        return AnimalType.getByName(name); // przykład
    }

    public boolean saveExists() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        return file.exists();
    }

    public void deleteSave() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            if (file.exists()) {
                file.delete();
                Gdx.app.log("SaveManager", "Zapis usunięty");
            }
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Błąd podczas usuwania zapisu: " + e.getMessage());
        }
    }
}
