package com.chenjimou.skinloaddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chenjimou.skin_load_sdk.SkinManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener((v) -> {
            SkinManager.getInstance().loadSkin("/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/skin-debug.apk");
        });

        Button button1 = findViewById(R.id.button2);
        button1.setOnClickListener((v -> {
            SkinManager.getInstance().loadSkin(null);
        }));
    }
}