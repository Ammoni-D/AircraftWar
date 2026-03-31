package com.example.aircraftwar.application;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.aircraftwar.MySurfaceView;
import com.example.aircraftwar.aircraft.*;
import java.util.Random;

public class HardGame extends Game{
    public HardGame(Context context){
        super(context);
    }
    @Override
    public void initParameters() {
        enemyMaxNumber=7;//屏幕中出现的敌机最大数量
        mobEnemyHp=30;
        mobEnemySpeed=8;
        eliteEnemyHp=60;
        eliteEnemySpeed=8;
        superEliteEnemyHp=90;
        superEliteEnemySpeed=10;
        bossEnemyHp=500;
        heroShootCycle=540;//英雄机射击周期
        enemyShootCycle=540;//敌机射击周期
        eliteProb=0.4;//精英机产生概率
        enemyCycle=600;//敌机产生周期
        bossThreshold=200;//boss机产生阈值
        addDifficultyCycle=15000;//难度增加周期
    }

    @Override
    public void addDifficulty() {
        eliteProb+=0.05;
        enemyShootCycle-=20;
        enemyCycle-=20;
        System.out.println("难度升级！当前精英机产生概率："+eliteProb+"，敌机周期："+enemyCycle+"，敌机射击周期："+enemyShootCycle);
    }

    @Override
    public void generateEnemy(){
        count++;
        EnemyFactory enemyFactory;
        int speedX=0,speedY;
        Bitmap photo;
        ShootStrategy shootStrategy=null;
        int hp;
        if (enemyAircrafts.size() < enemyMaxNumber) {
            //超级精英敌机
            if (count % 10 == 0) {
                enemyFactory = new SuperEliteFactory();
                speedX = 5;
                speedY = superEliteEnemySpeed;
                hp = superEliteEnemyHp;
                photo = ImageManager.SUPER_ELITE_ENEMY_IMAGE;
                shootStrategy = new ScatterShoot();
            } else {
                Random random = new Random();
                int n = random.nextInt(100);
                //普通敌机
                if (n > eliteProb*100) {
                    enemyFactory = new MobFactory();
                    photo = ImageManager.MOB_ENEMY_IMAGE;
                    speedY=mobEnemySpeed;
                    hp=mobEnemyHp;
                }
                //精英敌机
                else {
                    enemyFactory = new EliteFactory();
                    photo = ImageManager.ELITE_ENEMY_IMAGE;
                    shootStrategy = new DirectShoot();
                    speedY=eliteEnemySpeed;
                    hp=eliteEnemyHp;
                }
            }
            enemyAircrafts.add(enemyFactory.enemyCreator(
                    (int) (Math.random() * (MySurfaceView.screenWidth - photo.getWidth())),
                    (int) (Math.random() * MySurfaceView.screenHeight * 0.05),
                    speedX,
                    speedY,
                    hp,
                    shootStrategy
            ));
            // 产生BOSS
            if (score > 0 && (score % bossThreshold == 0 || score/bossThreshold-scoreReg/bossThreshold==1)) {
                boolean flag = true;
                for (AbstractAircraft aircraft : enemyAircrafts)
                    if (aircraft instanceof BossEnemy) {
                        flag = false;
                        break;
                    }
                if (flag) {
                    enemyFactory = new BossFactory();
                    photo = ImageManager.BOSS_ENEMY;
                    enemyAircrafts.add(enemyFactory.enemyCreator(
                            (int) (Math.random() * (MySurfaceView.screenWidth - photo.getWidth())),
                            (int) ((double) photo.getHeight() / 2 + (Math.random() * MySurfaceView.screenHeight * 0.05)),
                            5,
                            0,
                            bossEnemyHp,
                            new CircleShoot()
                    ));
                    // Todo:背景音乐暂停

                    // Todo:boss音乐

                    bossEnemyHp+=100;//每次增加boss机血量
                }
            }
        }
    }
}
