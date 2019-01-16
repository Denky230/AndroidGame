package com.stucom.grupo4.settings.model;

public class User {

    private int id;
    private String name;
    private String image;
    private String from;
    private int totalScore;
    private int lastLevel;
    private int lastScore;
    private String[] scores;

    public User(int id, String name, String image, String from, int totalScore, int lastLevel, int lastScore, String[] scores) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.from = from;
        this.totalScore = totalScore;
        this.lastLevel = lastLevel;
        this.lastScore = lastScore;
        this.scores = scores;
    }

    public String getName() { return name; }
}
