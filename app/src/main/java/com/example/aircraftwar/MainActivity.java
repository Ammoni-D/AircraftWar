package com.example.aircraftwar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean musicSetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
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
}