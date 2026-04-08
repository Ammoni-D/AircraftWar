package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.MySurfaceView;
import com.example.aircraftwar.application.DirectShoot;
import com.example.aircraftwar.application.ImageManager;
import com.example.aircraftwar.application.ShootStrategy;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.HeroBullet;
import com.example.aircraftwar.prop.BaseProp;

import java.util.LinkedList;
import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author ammoni
 */
public class HeroAircraft extends AbstractAircraft {

    /**攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 1;

    /**
     * 子弹伤害
     */
    private int power = 30;

    /**
     * 子弹射击方向 (向上发射：1，向下发射：-1)
     */
    private int direction = -1;

    private int propNum=0;//记录获得道具数量

    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    public HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy shootStrategy) {
        super(locationX, locationY, speedX, speedY, hp, shootStrategy);
    }

    @Override
    public List<BaseProp> drop() {
        return new LinkedList<>();
    }

    @Override
    public void update() {}

    private volatile static HeroAircraft heroAircraft;
    public static HeroAircraft getHeroAircraft(){
        if(heroAircraft==null){
            synchronized (HeroAircraft.class){
                if(heroAircraft==null){
                    heroAircraft=new HeroAircraft(MySurfaceView.screenWidth / 2,
                            MySurfaceView.screenHeight - ImageManager.HERO_IMAGE.getHeight(),
                            0, 0, 1000, new DirectShoot());
                }
            }
        }
        return heroAircraft;
    }

    // 重置英雄机
    public static void resetHeroAircraft() {
        heroAircraft = null;
    }
    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    public int getPropNum() {
        return propNum;
    }

    public void addPropNum() {
        propNum++;
    }

    public void reducePropNum() {
        propNum--;
    }
}
