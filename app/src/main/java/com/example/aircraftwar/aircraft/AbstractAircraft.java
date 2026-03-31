package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.application.ShootStrategy;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.prop.BaseProp;
import com.example.aircraftwar.prop.PropFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * 所有种类飞机的抽象父类：
 * 敌机（BOSS, ELITE, MOB），英雄飞机
 *
 * @author ammoni
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {
    /**
     * 生命值
     */
    protected int maxHp;
    protected int hp;
    private ShootStrategy shootStrategy;

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy shootStrategy) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
        this.shootStrategy=shootStrategy;
    }

    public void decreaseHp(int decrease){
        hp -= decrease;
        if(hp <= 0){
            hp=0;
            vanish();
        }
    }

    public void increaseHp(int increase){
        hp += increase;
        if(hp >= maxHp){
            hp=maxHp;
        }
    }

    public int getHp() {
        return hp;
    }

    public void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }

    /**
     * 飞机射击方法，可射击对象必须实现
     * @return
     *  可射击对象需实现，返回子弹
     *  非可射击对象空实现，返回null
     */
    public List<BaseBullet> shoot(){
        if(shootStrategy==null)
            return new LinkedList<>();
        else
            return shootStrategy.shoot(this);
    }

    PropFactory propFactory=null;
    abstract public List<BaseProp> drop();
    abstract public void update();
}


