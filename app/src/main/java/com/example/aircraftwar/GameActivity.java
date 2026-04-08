package com.example.aircraftwar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.application.EasyGame;
import com.example.aircraftwar.application.Game;
import com.example.aircraftwar.application.HardGame;
import com.example.aircraftwar.application.SimpleGame;
import com.example.aircraftwar.scoredisplay.Score;
import com.example.aircraftwar.scoredisplay.ScoreAdapter;
import com.example.aircraftwar.scoredisplay.ScoreDao;
import com.example.aircraftwar.scoredisplay.ScoreDaoImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {
    public Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                showInputNameDialog();
            }
        }
    };

    private ScoreDao scoreDao;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        scoreDao = new ScoreDaoImpl(this);

        // 每局游戏开始前：重置英雄机
        HeroAircraft.resetHeroAircraft();

        String mode = getIntent().getStringExtra("mode");
        boolean musicSetting = getIntent().getBooleanExtra("musicSetting", false);
        assert mode != null;

        switch (mode) {
            case "simple":
                game = new SimpleGame(this, musicSetting);
                break;
            case "hard":
                game = new HardGame(this, musicSetting);
                break;
            default:
                game = new EasyGame(this, musicSetting);
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

    private void showInputNameDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_name, null);
        EditText etName = view.findViewById(R.id.et_name);

        new AlertDialog.Builder(this)
                .setTitle("游戏结束")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) name = "匿名玩家";

                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    scoreDao.addScore(new Score(name, game.getScore(), time));
                    showRankingList();
                })
                .setCancelable(false)
                .show();
    }

    private void showRankingList() {
        setContentView(R.layout.ranking_list);
        ListView listView = findViewById(R.id.list_view);
        ScoreAdapter adapter = new ScoreAdapter(this, scoreDao.getAllScores(), scoreDao);
        listView.setAdapter(adapter);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            ((ScoreDaoImpl) scoreDao).clearAllScores();
            listView.setAdapter(new ScoreAdapter(this, scoreDao.getAllScores(), scoreDao));
        });
    }

    public Handler getmHandler() {
        return mHandler;
    }
}