package com.example.aircraftwar;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aircraftwar.application.EasyGame;
import com.example.aircraftwar.application.Game;
import com.example.aircraftwar.application.HardGame;
import com.example.aircraftwar.application.SimpleGame;

public class GameActivity extends AppCompatActivity {
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
}
