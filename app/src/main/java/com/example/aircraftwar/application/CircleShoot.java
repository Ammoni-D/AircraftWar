package com.example.aircraftwar.application;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class CircleShoot implements ShootStrategy{
    //子弹速度
    private int speed = 10;
    /**
     * 子弹一次发射数量
     */
    private int shootNum = 20;

    /**
     * 子弹伤害
     */
    private int power = 30;

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();
        int speedX = 0;
        int speedY = aircraft.getSpeedY();
        BaseBullet bullet;

        double degree_start=-Math.PI;
        double degree_add=2*Math.PI/20;
        for(int i=0; i<shootNum; i++){
            double degree=degree_start+i*degree_add;
            // 子弹发射位置相对飞机位置向前偏移
            if(aircraft instanceof HeroAircraft)
                bullet = new HeroBullet(x, y,
                        (int)(speedX+speed*Math.sin(degree)),
                        (int)(speedY+speed*Math.cos(degree)), power);
            else
                bullet = new EnemyBullet(x, y,
                        (int)(speedX+speed*Math.sin(degree)),
                        (int)(speedY+speed*Math.cos(degree)), power);
            res.add(bullet);
        }
        return res;
    }
}
