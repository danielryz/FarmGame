package com.farmgame.game_save;

public class SavedGameClock {
    public int day;
    public String weekDay; // MONDAY, TUESDAY, etc.
    public String timeOfDay; // MORNING, NOON, EVENING, NIGHT
    public float currentTime;

    public SavedGameClock() {}

    public SavedGameClock(int day, String weekDay, String timeOfDay, float currentTime) {
        this.day = day;
        this.weekDay = weekDay;
        this.timeOfDay = timeOfDay;
        this.currentTime = currentTime;
    }
}
