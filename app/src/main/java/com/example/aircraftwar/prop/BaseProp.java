package com.example.aircraftwar.prop;

import com.example.aircraftwar.MySurfaceView;
import com.example.aircraftwar.application.Game;
import com.example.aircraftwar.basic.AbstractFlyingObject;

/**
 * 道具类
 *
 * @author ammoni
 */
public abstract class BaseProp extends AbstractFlyingObject {


    public BaseProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void forward() {
        super.forward();

        // 判定 x 轴出界
        if (locationX <= 0 || locationX >= MySurfaceView.screenWidth) {
            vanish();
        }

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= MySurfaceView.screenHeight) {
            // 向下飞行出界
            vanish();
        }else if (locationY <= 0){
            // 向上飞行出界
            vanish();
        }
    }

    abstract public void activate(Game game);
}
