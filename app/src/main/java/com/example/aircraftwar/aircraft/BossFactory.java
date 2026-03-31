package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.application.ShootStrategy;

public class BossFactory implements EnemyFactory{
    @Override
    public AbstractAircraft enemyCreator(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy strategy){
        return new BossEnemy(locationX,locationY,speedX,speedY,hp,strategy);
    }
}
