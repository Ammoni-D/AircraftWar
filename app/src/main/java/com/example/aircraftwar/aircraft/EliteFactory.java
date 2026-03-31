package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.application.ShootStrategy;

public class EliteFactory implements EnemyFactory{
    @Override
    public AbstractAircraft enemyCreator(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy shootStrategy){
        return new EliteEnemy(locationX,locationY,speedX,speedY,hp,shootStrategy);
    }
}
