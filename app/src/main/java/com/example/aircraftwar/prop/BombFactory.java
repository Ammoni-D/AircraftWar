package com.example.aircraftwar.prop;

public class BombFactory implements PropFactory{
    @Override
    public BaseProp propCreator(int locationX, int locationY, int speedX, int speedY){
        return new BombProp(locationX,locationY,speedX,speedY);
    }
}
