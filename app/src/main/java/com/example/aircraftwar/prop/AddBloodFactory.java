package com.example.aircraftwar.prop;

public class AddBloodFactory implements PropFactory{
    @Override
    public BaseProp propCreator(int locationX, int locationY, int speedX, int speedY){
        return new AddBloodProp(locationX,locationY,speedX,speedY);
    }
}
