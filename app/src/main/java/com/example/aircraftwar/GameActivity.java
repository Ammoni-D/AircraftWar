package com.example.aircraftwar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aircraftwar.application.EasyGame;
import com.example.aircraftwar.application.Game;
import com.example.aircraftwar.application.HardGame;
import com.example.aircraftwar.application.SimpleGame;

public class GameActivity extends AppCompatActivity {
    public Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                setContentView(R.layout.ranking_list);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        String mode = getIntent().getStringExtra("mode");
        assert mode != null;

        Game game;
        switch (mode) {
            case "simple":
                game = new SimpleGame(this);
                break;
            case "hard":
                game = new HardGame(this);
                break;
            default:
                game = new EasyGame(this);
                break;
        }

        setContentView(game);
        game.action();

        ViewCompat.setOnApplyWindowInsetsListener(game, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public Handler getmHandler() {
        return mHandler;
    }
}
