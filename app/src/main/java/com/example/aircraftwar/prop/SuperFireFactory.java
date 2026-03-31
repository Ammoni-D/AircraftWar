package com.example.aircraftwar.prop;

public class SuperFireFactory implements PropFactory{
    @Override
    public BaseProp propCreator(int locationX, int locationY, int speedX, int speedY){
        return new SuperFireProp(locationX,locationY,speedX,speedY);
    }
}
