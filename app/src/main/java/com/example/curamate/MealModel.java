package com.example.curamate;

public class MealModel {
    public String name; // Breakfast, Lunch, etc.
    public int hour;
    public int minute;

    public MealModel(String name, int hour, int minute) {
        this.name = name;
        this.hour = hour;
        this.minute = minute;
    }
}
