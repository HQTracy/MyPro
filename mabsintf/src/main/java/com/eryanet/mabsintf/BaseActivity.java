package com.eryanet.mabsintf;

import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SystemClock.sleep(3000);
                    if (getPage() == 1) {
                        connnect();
                    } else if (getPage() == 2) {
                        setMirror();
                    }
                }
            }
        }).start();
    }


    public int getPage() {
        return 0;
    }

    public void connnect() {

    }

    public void setMirror() {

    }


}
