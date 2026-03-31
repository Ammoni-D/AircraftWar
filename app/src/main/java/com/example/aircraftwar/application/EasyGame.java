package com.example.aircraftwar.application;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.aircraftwar.MySurfaceView;
import com.example.aircraftwar.aircraft.*;
import java.util.Random;

public class EasyGame extends Game{
    public EasyGame(Context context){
        super(context);
        super.setBackgroundImage(ImageManager.BACKGROUND_IMAGE);
        gameMode = "EASY";
    }
    @Override
    public void initParameters() {
        enemyMaxNumber=3;//屏幕中出现的敌机最大数量
        mobEnemyHp=30;
        mobEnemySpeed=5;
        eliteEnemyHp=30;
        eliteEnemySpeed=5;
        superEliteEnemyHp=60;
        superEliteEnemySpeed=8;
        heroShootCycle=600;//英雄机射击周期
        enemyShootCycle=600;//敌机射击周期
        eliteProb=0.2;//精英机产生概率
        enemyCycle=600;//敌机产生周期
    }

    @Override
    public void addDifficulty() {}

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
            synchronized (enemyAircrafts) {
                enemyAircrafts.add(enemyFactory.enemyCreator(
                        (int) (Math.random() * (MySurfaceView.screenWidth - photo.getWidth())),
                        (int) (Math.random() * MySurfaceView.screenHeight * 0.05),
                        speedX,
                        speedY,
                        hp,
                        shootStrategy
                ));
            }
        }
    }
}
