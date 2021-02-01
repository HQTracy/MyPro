package com.eryanet.mabsintf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_gomirror).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MirrorActivity.class));
            }
        });
    }

    @Override
    public void connnect() {
        Log.i("haha", "connnect");
    }


    @Override
    public int getPage() {
        return 1;
    }
}
