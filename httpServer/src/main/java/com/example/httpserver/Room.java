package com.example.httpserver;

public class Room {
    private String user1 = null;
    private String user2 = null;
    private int score1;
    private int score2;
    private String mode;
    private boolean alive1 = false;
    private boolean alive2 = false;
    public boolean isFull() {
        return user1 != null && user2 != null;
    }

    public int addUser(String username) {
        if(user1 == null) { user1 = username; alive1 = true; return 1;}
        else { user2 = username; alive2 = true; return 2;}
    }

    public int getOppScore(String username) {
        if(username.equals(user1)) return score2;
        else return score1;
    }

    public String getOppName(String username) {
        if(username.equals(user1)) return user2;
        else return user1;
    }

    public void setScore(String username, int score) {
        if(username.equals(user1)) score1 = score;
        else score2 = score;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void resetRoom() {
        user1 = null;
        user2 = null;
        mode = null;
        alive1 = false;
        alive2 = false;
    }

    public String getMode() {
        return mode;
    }

    public boolean isAlive1() {
        return alive1;
    }

    public boolean isAlive2() {
        return alive2;
    }

    public boolean oppAlive(String username) {
        if(username.equals(user1)) return alive2;
        else return alive1;
    }

    public void gameOver(String username) {
        if(username.equals(user1)) alive1 = false;
        else alive2 = false;
    }
}
