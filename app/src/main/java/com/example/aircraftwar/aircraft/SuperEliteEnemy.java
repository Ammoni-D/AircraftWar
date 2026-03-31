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
public class SuperEliteEnemy extends AbstractAircraft {

    public SuperEliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy shootStrategy) {
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
        int n=random.nextInt(300);
        if(n<90) {
            propFactory=new AddBloodFactory();
        }
        else if(n<180) {
            propFactory=new BombFactory();
        }
        else if(n<270){
            propFactory=new FireFactory();
        }
        else{
            propFactory=new SuperFireFactory();
        }
        props.add(propFactory.propCreator(
                locationX,
                locationY,
                0,
                10
        ));
        return props;
    }

    @Override
    public void update() {
        hp-=30;
    }
}
