package com.example.aircraftwar.scoredisplay;

import java.util.List;

public interface ScoreDao {
    void addScore(Score score);
    List<Score> getAllScores();
    void deleteScore(Score score);
}