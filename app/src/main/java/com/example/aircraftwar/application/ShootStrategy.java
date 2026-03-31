package com.example.aircraftwar.application;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.bullet.BaseBullet;
import java.util.List;

public interface ShootStrategy {
    List<BaseBullet> shoot(AbstractAircraft aircraft);
}
