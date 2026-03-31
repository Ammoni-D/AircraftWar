package com.example.aircraftwar.prop;

public interface PropFactory {
    public abstract BaseProp propCreator(int locationX, int locationY, int speedX, int speedY);
}
