package com.example.aircraftwar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aircraftwar.scoredisplay.Score;
import com.example.aircraftwar.scoredisplay.ScoreAdapter;
import com.example.aircraftwar.scoredisplay.ScoreDao;
import com.example.aircraftwar.scoredisplay.ScoreDaoImpl;
import com.example.aircraftwar.web.generalCallBack;
import com.example.aircraftwar.web.getRankingCallBack;
import com.example.aircraftwar.web.httpClient;
import com.example.aircraftwar.web.loginCallBack;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean musicSetting = false;
    public static boolean online = false;
    public static String sessionID = null;
    private String myName;

    public static ScoreDao scoreDao;

    private View layoutStart;
    private View layoutRanking;
    private View layoutMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        scoreDao = new ScoreDaoImpl(this);

        // 初始化起始页面
        layoutStart = getLayoutInflater().inflate(R.layout.start, null);
        // 初始化排名页面
        layoutRanking = getLayoutInflater().inflate(R.layout.ranking_list, null);
        // 初始化难度选择页面
        layoutMode = getLayoutInflater().inflate(R.layout.mode_selection, null);

        setContentView(layoutStart);

        Button button_startgame = (Button) findViewById(R.id.button_startgame);
        Button button_login = (Button) findViewById(R.id.button_login);
        Button button_signup = (Button) findViewById(R.id.button_signup);
        Button button_showranking = (Button) findViewById(R.id.button_showranking);
        Button button_setServer = (Button) findViewById(R.id.setServer);
        TextView webState = (TextView) findViewById(R.id.webState);
        TextView start_username = (TextView) findViewById(R.id.username);

        button_startgame.setOnClickListener(v -> {
            setContentView(layoutMode);
            Button button = (Button) findViewById(R.id.button);
            Button button2 = (Button) findViewById(R.id.button2);
            Button button3 = (Button) findViewById(R.id.button3);
            TextView webState2 = (TextView) findViewById(R.id.webState2);
            TextView start_username2 = (TextView) findViewById(R.id.username2);
            if(online) {
                webState2.setText("状态：已登录");
                start_username2.setText("用户名: "+ myName);
            }
            SwitchCompat musicSwitch = (SwitchCompat) findViewById(R.id.switch1);
            button.setOnClickListener(this);
            button2.setOnClickListener(this);
            button3.setOnClickListener(this);

            musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    Toast.makeText(MainActivity.this, "音乐开关已开启", Toast.LENGTH_SHORT).show();
                    musicSetting = true;
                } else {
                    Toast.makeText(MainActivity.this, "音乐开关已关闭", Toast.LENGTH_SHORT).show();
                    musicSetting = false;
                }
            });
        });

        button_login.setOnClickListener(v -> {
            showInputUserInfoDialog("登录", new UserInfoListener() {
                @Override
                public void onUserInfoConfirmed(String username, String password) {
                    httpClient.Login(username, password, new loginCallBack() {
                        @Override
                        public void onSuccess(String sessionID) {
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                MainActivity.online = true;
                                MainActivity.sessionID = sessionID;
                                myName = username;
                                webState.setText("状态：已登录");
                                start_username.setText("用户名: "+username);}
                            );
                        }

                        @Override
                        public void onFailure(String error) {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            });
        });

        button_signup.setOnClickListener(v -> {
            showInputUserInfoDialog("注册", new UserInfoListener() {
                @Override
                public void onUserInfoConfirmed(String username, String password) {
                    httpClient.signUp(username, password, new generalCallBack() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_SHORT).show());
                        }
                        @Override
                        public void onFailure(String error) {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            });
        });

        button_showranking.setOnClickListener(v -> {
            setContentView(layoutRanking);
            ListView listView = findViewById(R.id.list_view);
            if(online) {
                httpClient.getUserRanking(sessionID, new getRankingCallBack() {
                    @Override
                    public void onSuccess(List<Score> scores) {
                        runOnUiThread(() -> listView.setAdapter(new ScoreAdapter(MainActivity.this, scores)));
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show());
                    }
                });
            }
            else {
                ScoreAdapter adapter = new ScoreAdapter(this, scoreDao.getAllScores(), scoreDao);
                listView.setAdapter(adapter);
            }

            findViewById(R.id.btn_back).setOnClickListener(view -> setContentView(layoutStart));
            findViewById(R.id.btn_clear).setOnClickListener(view -> {
                if(online) {
                    httpClient.delAllUserRanking(sessionID, new generalCallBack() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "已清除全部记录", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onFailure(String error) {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show());
                        }
                    });
                    listView.setAdapter(new ScoreAdapter(this, new ArrayList<>()));
                }
                else {
                    ((ScoreDaoImpl) scoreDao).clearAllScores();
                    listView.setAdapter(new ScoreAdapter(this, scoreDao.getAllScores(), scoreDao));
                }
            });
        });

        button_setServer.setOnClickListener(v -> {
            showInputIPAddressDialog("设置服务器地址");
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent(this, GameActivity.class);
        if (id == R.id.button) {
            intent.putExtra("mode", "easy");
        } else if (id == R.id.button2) {
            intent.putExtra("mode", "simple");
        } else if (id == R.id.button3) {
            intent.putExtra("mode", "hard");
        }
        intent.putExtra("musicSetting", musicSetting);
        startActivity(intent);
    }

    public interface UserInfoListener {
        void onUserInfoConfirmed(String username, String password);
    }

    private void showInputUserInfoDialog(String title, UserInfoListener listener) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_userinfo, null);
        EditText etUsername = view.findViewById(R.id.et_username);
        EditText etPassword = view.findViewById(R.id.et_password);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String username = etUsername.getText().toString().trim();
                    if (username.isEmpty()) username = "匿名玩家";
                    String password = etPassword.getText().toString().trim();
                    listener.onUserInfoConfirmed(username, password);
                    setContentView(layoutStart);
                })
                .setCancelable(false)
                .show();
    }

    private void showInputIPAddressDialog(String title) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_ipaddress, null);
        EditText ipAddress = view.findViewById(R.id.ip_address);
        EditText port = view.findViewById(R.id.port);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    httpClient.setServerIp(ipAddress.getText().toString().trim());
                    httpClient.setServerPort(port.getText().toString().trim());
                    setContentView(layoutStart);
                })
                .setCancelable(false)
                .show();
    }
}