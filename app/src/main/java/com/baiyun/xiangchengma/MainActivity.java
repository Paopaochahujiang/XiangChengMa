package com.baiyun.xiangchengma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mBtnNfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnNfc = findViewById(R.id.btn_NFC);
        OnClick onClick = new OnClick();
        mBtnNfc.setOnClickListener(onClick);
    }

    class OnClick implements View.OnClickListener {
        Intent intent = null;

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_NFC:
                    intent = new Intent(MainActivity.this, NfcDemoActivity.class);
                    break;
            }
            startActivity(intent);
        }
    }

}