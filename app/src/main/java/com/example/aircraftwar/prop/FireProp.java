package com.example.aircraftwar.prop;

import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.application.DirectShoot;
import com.example.aircraftwar.application.Game;
import com.example.aircraftwar.application.ScatterShoot;

public class FireProp extends BaseProp {
    public FireProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(Game game) {
        HeroAircraft heroAircraft=game.getHeroAircraft();
        Runnable r=()->{
            try {
                heroAircraft.setShootStrategy(new ScatterShoot());
                heroAircraft.addPropNum();
                Thread.sleep(3000);
                if(heroAircraft.getPropNum()==1)
                    heroAircraft.setShootStrategy(new DirectShoot());
                heroAircraft.reducePropNum();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        };
        new Thread(r).start();
        // Todo:音效
    }
}