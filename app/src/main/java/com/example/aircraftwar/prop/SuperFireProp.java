package com.example.aircraftwar.prop;

import android.media.SoundPool;

import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.application.CircleShoot;
import com.example.aircraftwar.application.DirectShoot;
import com.example.aircraftwar.application.Game;

public class SuperFireProp extends BaseProp {
    public SuperFireProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(Game game) {
        HeroAircraft heroAircraft=game.getHeroAircraft();
        Runnable r=()->{
            try {
                heroAircraft.setShootStrategy(new CircleShoot());
                heroAircraft.addPropNum();
                Thread.sleep(3000);
                if(heroAircraft.getPropNum()==1)
                    heroAircraft.setShootStrategy(new DirectShoot());
                heroAircraft.reducePropNum();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        };
        new Thread(r).start();
        // 音效
        if(game.musicSetting) {
            SoundPool soundPool = game.getSoundPool();
            soundPool.play(game.supplyMusicId, 1, 1, 1, 0, 1);
        }
    }
}
