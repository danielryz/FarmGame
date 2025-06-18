package com.farmgame.game_save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.farmgame.game.*;
import com.farmgame.player.InventoryItem;
import com.farmgame.player.Player;
import com.farmgame.quest.QuestManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveManager {
    private static final String SAVE_FILE_TEMPLATE = "farmgame_save_%d.json";
    private static final int MAX_SAVE_SLOTS = 5;
    private Json json;
    private DifficultyManager difficultyManager;

    public SaveManager() {
        this(new DifficultyManager());
    }

    public SaveManager(DifficultyManager difficultyManager) {
        this.difficultyManager = difficultyManager;
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
                                        String currentAction, QuestManager questManager) {
        // Konwertuj gracza
        SavedPlayer savedPlayer = new SavedPlayer();
        savedPlayer.name = player.getName();
        savedPlayer.money = player.getMoney();
        savedPlayer.level = player.getLevel();
        savedPlayer.exp = player.getExp();
        savedPlayer.inventoryCapacity = player.getInventoryCapacity();
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
        savedFarm.hasWateringSystem = farm.hasWateringSystem();

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
                                colorToHex(plant.getType().getColor()),
                                plant.isAutoWatered(),
                                plant.getFertilizerTimeRemaining()
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
                    List<SavedAnimal> savedAnimals = new ArrayList<>();
                    for (Animal animal : pen.getAnimals()) {
                        savedAnimals.add(new SavedAnimal(
                                animal.getType().getName(),
                                animal.getProductState().name(),
                                animal.getTimeToNextProduct(),
                                colorToHex(animal.getType().getColor()),
                                animal.getProductCount()
                        ));
                    }

                    List<SavedInventoryItem> savedFeed = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : pen.getFeedStock().entrySet()) {
                        if (entry.getValue() > 0) {
                            savedFeed.add(new SavedInventoryItem(entry.getKey(), entry.getValue(), 0));
                        }
                    }

                    savedFarm.animalPens[px][py] = new Weather.SavedAnimalPen(
                            pen.isBlocked(),
                            pen.getState().name(),
                            pen.getCapacity(),
                            savedAnimals.toArray(new SavedAnimal[0]),
                            savedFeed.toArray(new SavedInventoryItem[0])
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
            weather.getCurrentWeatherName(),
            weather.getTimeUntilChange()
        );

        ArrayList<SavedQuest> savedQuests = new java.util.ArrayList<>();
        if (questManager != null) {
            for (com.farmgame.quest.Quest q : questManager.getQuests()) {
                savedQuests.add(new SavedQuest(
                        q.getName(),
                        q.getItemName(),
                        q.getRequiredQuantity(),
                        q.getRewardMoney(),
                        q.getRewardExp(),
                        q.isClaimed()
                ));
            }
        }

        return new GameState(
            savedPlayer,
            savedFarm,
            savedClock,
            savedWeather,
            selectedPlant != null ? selectedPlant.getName() : null,
            currentAction,
                savedQuests
        );
    }

    public float getDifficultyMultiplier() {
        return difficultyManager.getDifficultyMultiplier();
    }

    public void setDifficultyMultiplier(float multiplier) {
        difficultyManager.setDifficultyMultiplier(multiplier);
    }

    public DifficultyManager getDifficultyManager() {
        return difficultyManager;
    }

    private String getSaveFileName(int slot) {
        return String.format(SAVE_FILE_TEMPLATE, slot);
    }

    // Zapisywanie stanu gry
    public void saveGame(Player player, Farm farm, GameClock gameClock,
                         Weather weather, PlantType selectedPlant, String currentAction, QuestManager questManager) {
        saveGame(player, farm, gameClock, weather, selectedPlant, currentAction, questManager, 1);
    }

    public void saveGame(Player player, Farm farm, GameClock gameClock,
                         Weather weather, PlantType selectedPlant, String currentAction, QuestManager questManager, int slot) {
        try {
            if (slot < 1 || slot > MAX_SAVE_SLOTS) {
                slot = 1;
            }

            GameState gameState = convertToSaveState(player, farm, gameClock,
                    weather, selectedPlant, currentAction, questManager);

            gameState.difficultyMultiplier = difficultyManager.getDifficultyMultiplier();

            String jsonString = json.toJson(gameState);
            FileHandle file = Gdx.files.local(getSaveFileName(slot));
            file.writeString(jsonString, false);
            Gdx.app.log("SaveManager", "Gra zapisana pomyślnie w slocie " + slot);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Błąd podczas zapisywania: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Odczytywanie stanu gry
    public GameState loadGame() {
        return loadGame(1);
    }

    public GameState loadGame(int slot) {
        try {
            if (slot < 1 || slot > MAX_SAVE_SLOTS) {
                slot = 1;
            }

            FileHandle file = Gdx.files.local(getSaveFileName(slot));
            if (file.exists()) {
                String jsonString = file.readString();
                GameState gameState = json.fromJson(GameState.class, jsonString);

                if (gameState.difficultyMultiplier > 0) {
                    difficultyManager.setDifficultyMultiplier(gameState.difficultyMultiplier);
                }

                Gdx.app.log("SaveManager", "Gra wczytana pomyślnie ze slotu " + slot);
                return gameState;
            } else {
                Gdx.app.log("SaveManager", "Plik zapisu nie istnieje w slocie " + slot + ", tworzę nowy stan gry");
                return null;
            }
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Błąd podczas wczytywania: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Metoda do aplikowania wczytanego stanu do obiektów gry
    public void applyGameState(GameState gameState, Player player, Farm farm,
                               GameClock gameClock, Weather weather, com.farmgame.quest.QuestManager questManager) {
        if (gameState == null) return;

        try {
            // Przywróć stan gracza
            if (gameState.player != null) {
                player.setName(gameState.player.name);
                player.setMoney(gameState.player.money);
                player.setLevel(gameState.player.level);
                player.setExp(gameState.player.exp);

                int cap = gameState.player.inventoryCapacity > 0 ? gameState.player.inventoryCapacity : Player.BASE_INVENTORY_CAPACITY;
                player.getPlayerInventory().setCapacity(cap);

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
                                        plant.setTimeRemaining(savedPlot.plant.timeRemaining);
                                        plant.setWatered(savedPlot.plant.isWatered);
                                        plant.setAutoWatered(savedPlot.plant.isAutoWatered);
                                        if (savedPlot.plant.fertilizerTime > 0f) {
                                            plant.applyFertilizer(savedPlot.plant.fertilizerTime);
                                        }
                                        plot.plant(plant);

                                        // Ustaw stan działki
                                        plot.setState(Plot.State.valueOf(savedPlot.state));
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
                                    pen.block();
                                } else {
                                    pen.unlock();
                                }

                                pen.setCapacity(savedPen.capacity);

                                if (savedPen.animals != null) {
                                    List<Animal> animals = new ArrayList<>();
                                    for (SavedAnimal savedAnimal : savedPen.animals) {
                                        AnimalType animalType = findAnimalTypeByName(savedAnimal.typeName);
                                        if (animalType != null) {
                                            Animal animal = new Animal(animalType);
                                            animal.setProductState(Animal.ProductState.valueOf(savedAnimal.productState));
                                            animal.setTimeToNextProduct(savedAnimal.timeToNextProduct);
                                            animal.setProductCount(savedAnimal.productCount);
                                            animals.add(animal);
                                        }
                                    }
                                    pen.setAnimals(animals);
                                    pen.setState(AnimalPen.State.valueOf(savedPen.state));

                                    if (savedPen.feedStock != null) {
                                        Map<String, Integer> stock = new HashMap<>();
                                        for (SavedInventoryItem item : savedPen.feedStock) {
                                            stock.put(item.name, item.quantity);
                                        }
                                        pen.setFeedStock(stock);
                                    }
                                }
                            }

                            farm.setWateringSystem(gameState.farm.hasWateringSystem);
                        }
                        }
                    }
                }

            // Przywróć zegar gry
            if (gameState.gameClock != null) {
                gameClock.setDay(gameState.gameClock.day);
                gameClock.setWeekDay(GameClock.WeekDay.valueOf(gameState.gameClock.weekDay));
                gameClock.setTimeOfDay(GameClock.TimeOfDay.valueOf(gameState.gameClock.timeOfDay));
                gameClock.setCurrentTime(gameState.gameClock.currentTime);
            }

            // Przywróć pogodę
            if (gameState.weather != null) {
                weather.setCurrentWeather(gameState.weather.currentWeather);
                weather.setTimeUntilChange(gameState.weather.timeUntilChange);
            }

            if (questManager != null) {
                questManager.clear();
                if (gameState.quests != null) {
                    for (SavedQuest sq : gameState.quests) {
                        com.farmgame.quest.Quest q = new com.farmgame.quest.Quest(
                                sq.name,
                                sq.itemName,
                                sq.requiredQuantity,
                                sq.rewardMoney,
                                sq.rewardExp
                        );
                        q.setClaimed(sq.claimed);
                        questManager.addQuest(q);
                    }
                }
            }

            Gdx.app.log("SaveManager", "Stan gry przywrócony pomyślnie");

        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Błąd podczas przywracania stanu gry: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private PlantType findPlantTypeByName(String name) {
        return PlantType.getByName(name);
    }

    private AnimalType findAnimalTypeByName(String name) {
        return AnimalType.getByName(name);
    }

    public boolean saveExists() {
        return saveExists(1);
    }

    public boolean saveExists(int slot) {
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            slot = 1;
        }
        FileHandle file = Gdx.files.local(getSaveFileName(slot));
        return file.exists();
    }

    public void deleteSave() {
        deleteSave(1);
    }

    public void deleteSave(int slot) {
        try {
            if (slot < 1 || slot > MAX_SAVE_SLOTS) {
                slot = 1;
            }
            FileHandle file = Gdx.files.local(getSaveFileName(slot));
            if (file.exists()) {
                file.delete();
                Gdx.app.log("SaveManager", "Zapis usunięty ze slotu " + slot);
            }
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Błąd podczas usuwania zapisu: " + e.getMessage());
        }
    }
}
