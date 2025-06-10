package com.farmgame.game;

public class GameClock {

    public enum TimeOfDay {
        MORNING, NOON, EVENING, NIGHT;

        public TimeOfDay nextTimeOfDay() {
            return values()[(this.ordinal() + 1) %values().length];
        }
    }

    public enum WeekDay {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

        public WeekDay nextWeekDay() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    private float timeAccumulator = 0f;
    private final float secondsPerPhase = 60f;

    private int currentDay = 1;
    private TimeOfDay timeOfDay = TimeOfDay.MORNING;
    private WeekDay currentWeekDay = WeekDay.MONDAY;

    public void update(float delta) {
        timeAccumulator += delta;
        if (timeAccumulator >= secondsPerPhase) {
            timeAccumulator -= secondsPerPhase;
            timeOfDay = timeOfDay.nextTimeOfDay();
            if (timeOfDay == TimeOfDay.MORNING) {
                currentDay++;
                currentWeekDay = currentWeekDay.nextWeekDay();
            }
        }
    }

    public int getDay() {
        return currentDay;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public WeekDay getWeekDay() {
        return currentWeekDay;
    }

    public float getCurrentTime() {
        return timeAccumulator;
    }

    public void setDay(int day) {
        currentDay = day;
    }
    public void setWeekDay(WeekDay weekDay) {
        currentWeekDay = weekDay;
    }
    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public void setCurrentTime(float currentTime) {
        timeAccumulator = currentTime;
    }

}
