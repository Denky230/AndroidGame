package com.stucom.grupo4.settings.model;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String name;
    private String image;
    private String from;
    private int totalScore;
    private int lastLevel;
    private int lastScore;
    private String[] scores;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public int getTotalScore() { return totalScore; }
}
