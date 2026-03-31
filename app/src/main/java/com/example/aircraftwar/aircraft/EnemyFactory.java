package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.application.ShootStrategy;

public interface EnemyFactory {
    AbstractAircraft enemyCreator(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy strategy);
}
