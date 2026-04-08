package com.example.aircraftwar.scoredisplay;

public class Score {
    private String username;
    private int score;
    private String recordTime;

    public Score(String username, int score, String recordTime) {
        this.username = username;
        this.score = score;
        this.recordTime = recordTime;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public String getRecordTime() {
        return recordTime;
    }
}