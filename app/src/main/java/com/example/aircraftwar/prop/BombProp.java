package com.example.aircraftwar.prop;


import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.application.Game;
import com.example.aircraftwar.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

public class BombProp extends BaseProp {
    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    private List<AbstractAircraft> aircraftList=new LinkedList<>();

    public void activate(Game game) {
        System.out.println("BombSupply active!");
        notifyAllAircraft();
        // Todo:音效

        List<BaseBullet> enemyBullets=game.getEnemyBullets();
        for(BaseBullet bullet:enemyBullets){
            bullet.vanish();
        }
        for(AbstractAircraft aircraft:aircraftList){
            if(aircraft.notValid())
                game.addScore(10);
        }
    }

    public void addAircraft(AbstractAircraft aircraft){
        aircraftList.add(aircraft);
    }

    public void removeAircraft(AbstractAircraft aircraft){
        aircraftList.remove(aircraft);
    }

    public void notifyAllAircraft(){
        for(AbstractAircraft aircraft:aircraftList){
            aircraft.update();
        }
    }
}