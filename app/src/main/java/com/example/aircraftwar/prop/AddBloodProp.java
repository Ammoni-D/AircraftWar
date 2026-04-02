package com.example.aircraftwar.prop;

import android.media.SoundPool;

import com.example.aircraftwar.application.Game;

public class AddBloodProp extends BaseProp {
    public AddBloodProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(Game game) {
        game.getHeroAircraft().increaseHp(30);
        // 音效
        if(game.musicSetting) {
            SoundPool soundPool = game.getSoundPool();
            soundPool.play(game.supplyMusicId, 1, 1, 1, 0, 1);
        }
    }
}