package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.MySurfaceView;
import com.example.aircraftwar.application.ShootStrategy;
import com.example.aircraftwar.prop.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 超级精英敌机
 *
 * @author ammoni
 */
public class BossEnemy extends AbstractAircraft {

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy shootStrategy) {
        super(locationX, locationY, speedX, speedY, hp, shootStrategy);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= MySurfaceView.screenHeight) {
            vanish();
        }
    }

    @Override
    public List<BaseProp> drop() {
        List<BaseProp> props=new LinkedList<>();
        Random random=new Random();
        if(random.nextBoolean()){
            propFactory=new AddBloodFactory();
            props.add(propFactory.propCreator(
                    locationX-30,
                    locationY,
                    0,
                    10
            ));
        }
        if(random.nextBoolean()){
            if(random.nextBoolean()){
                propFactory=new SuperFireFactory();
            }
            else{
                propFactory=new FireFactory();
            }
            props.add(propFactory.propCreator(
                    locationX,
                    locationY,
                    0,
                    10
            ));
        }
        if(random.nextBoolean()){
            propFactory=new BombFactory();
            props.add(propFactory.propCreator(
                    locationX+30,
                    locationY,
                    0,
                    10
            ));
        }
        return props;
    }

    @Override
    public void update() {}
}
