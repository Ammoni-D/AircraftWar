package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.MySurfaceView;
import com.example.aircraftwar.application.ShootStrategy;
import com.example.aircraftwar.prop.BaseProp;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author ammoni
 */
public class MobEnemy extends AbstractAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy shootStrategy) {
        super(locationX, locationY, speedX, speedY, hp, shootStrategy);
    }

    @Override
    public List<BaseProp> drop() {
        return new LinkedList<>();
    }

    @Override
    public void update() {
        vanish();
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= MySurfaceView.screenHeight) {
            vanish();
        }
    }

}
