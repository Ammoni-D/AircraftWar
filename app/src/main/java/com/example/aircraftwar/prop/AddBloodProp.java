package com.example.aircraftwar.prop;

import com.example.aircraftwar.application.Game;

public class AddBloodProp extends BaseProp {
    public AddBloodProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(Game game) {
        game.getHeroAircraft().increaseHp(30);
        // Todo:音效
    }
}