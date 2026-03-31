package com.example.aircraftwar.prop;

public class FireFactory implements PropFactory{
    @Override
    public BaseProp propCreator(int locationX, int locationY, int speedX, int speedY){
        return new FireProp(locationX,locationY,speedX,speedY);
    }
}