package com.eryanet.mabsintf;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.logging.Logger;

public class MirrorActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void setMirror() {
        Log.i("haha", "setMirror");
    }

    @Override
    public int getPage() {
        return 2;
    }
}
