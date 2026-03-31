package com.example.aircraftwar.application;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class ScatterShoot implements ShootStrategy{
    /**
     * 子弹一次发射数量
     */
    private int shootNum = 3;

    /**
     * 子弹伤害
     */
    private int power = 30;

    //子弹速度
    private int speed = 5;

    //射击角度
    private double[] degree_set={-0.5,0,0.5};

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        /*
         * 子弹射击方向 (向上发射：1，向下发射：-1)
         */
        int direction;
        if(aircraft instanceof HeroAircraft)
            direction =-1;
        else
            direction =1;

        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction*2;
        int speedX = 0;
        int speedY = aircraft.getSpeedY();
        BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            if(aircraft instanceof HeroAircraft)
                bullet = new HeroBullet(x + (i*2 - shootNum + 1)*10, y,
                        (int)(speedX+speed*Math.sin(degree_set[i])),
                        (int)(speedY+direction*speed*Math.cos(degree_set[i])), power);
            else
                bullet = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y,
                        (int)(speedX+speed*Math.sin(degree_set[i])),
                        (int)(speedY+direction*speed*Math.cos(degree_set[i])), power);
            res.add(bullet);
        }
        return res;
    }
}
