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
import android.widget.Toast;

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
import com.example.aircraftwar.web.generalCallBack;
import com.example.aircraftwar.web.getRankingCallBack;
import com.example.aircraftwar.web.httpClient;
import com.example.aircraftwar.web.requestMatchingCallBack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {
    public Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                if(MainActivity.online) uploadScore();
                else showInputNameDialog();
            }
        }
    };

    private static final ScoreDao scoreDao = MainActivity.scoreDao;
    private Game game;
    private Runnable matchRunnable;
    private int count;
    private boolean isBackgroundSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // 每局游戏开始前：重置英雄机
        HeroAircraft.resetHeroAircraft();
        count = 0;

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

        // 定义轮询任务
        matchRunnable = new Runnable() {
            @Override
            public void run() {
                // 发起单次匹配查询
                httpClient.requestMatching(MainActivity.sessionID, mode, new requestMatchingCallBack() {
                    @Override
                    public void onSuccess(String oppName) {
                        runOnUiThread(() -> {
                            // 匹配成功，停止轮询
                            mHandler.removeCallbacks(matchRunnable);
                            game.setOppUsername(oppName);
                            // 启动游戏
                            launchGame();
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        // 只在第一次进入时设置背景
                        if (!isBackgroundSet) {
                            runOnUiThread(() -> {
                                View rootView = getWindow().getDecorView();
                                rootView.setBackgroundResource(R.drawable.match);
                                isBackgroundSet = true;
                            });
                        }

                        if(count % 5 == 0) {
                            runOnUiThread(() -> Toast.makeText(GameActivity.this, "等待匹配······", Toast.LENGTH_SHORT).show());
                        }
                        mHandler.postDelayed(matchRunnable, 500);
                    }
                });
                count++;
            }
        };

        // 开始第一次轮询
        if(MainActivity.online) mHandler.post(matchRunnable);
        else launchGame();

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
        if(MainActivity.online) {
            httpClient.getUserRanking(MainActivity.sessionID, new getRankingCallBack() {
                @Override
                public void onSuccess(List<Score> scores) {
                    runOnUiThread(() -> listView.setAdapter(new ScoreAdapter(GameActivity.this, scores)));
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> Toast.makeText(GameActivity.this, error, Toast.LENGTH_SHORT).show());
                }
            });
        }
        else {
            ScoreAdapter adapter = new ScoreAdapter(this, scoreDao.getAllScores(), scoreDao);
            listView.setAdapter(adapter);
        }

        findViewById(R.id.btn_back).setOnClickListener(view -> finish());
        findViewById(R.id.btn_clear).setOnClickListener(view -> {
            if(MainActivity.online) {
                httpClient.delAllUserRanking(MainActivity.sessionID, new generalCallBack() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> Toast.makeText(GameActivity.this, "已清除全部记录", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> Toast.makeText(GameActivity.this, error, Toast.LENGTH_SHORT).show());
                    }
                });
                listView.setAdapter(new ScoreAdapter(this, new ArrayList<>()));
            }
            else {
                ((ScoreDaoImpl) scoreDao).clearAllScores();
                listView.setAdapter(new ScoreAdapter(this, scoreDao.getAllScores(), scoreDao));
            }
        });
    }

    private void uploadScore() {
        httpClient.addUserRanking(MainActivity.sessionID, game.getScore(), new generalCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(GameActivity.this, "成功上传分数", Toast.LENGTH_SHORT).show();
                    showRankingList();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(GameActivity.this, error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    public Handler getmHandler() {
        return mHandler;
    }

    private void launchGame() {
        setContentView(game);
        game.action();
    }
}