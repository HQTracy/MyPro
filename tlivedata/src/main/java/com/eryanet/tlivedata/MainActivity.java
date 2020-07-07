package com.eryanet.tlivedata;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.eryanet.common.utils.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    TestViewModel mTestViewModel;
    @BindView(R.id.showcontent)
    TextView showcontent;
    @BindView(R.id.btn_setvalue)
    Button btnSetvalue;
    @BindView(R.id.btn_postvalue)
    Button btnPostvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mTestViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
        MutableLiveData<String> nameEvent = mTestViewModel.getNameEvent();
        nameEvent.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Logger.debug("onChanged s: " + s);
                showcontent.setText(s);
            }
        });

    }


    @OnClick({R.id.btn_setvalue, R.id.btn_postvalue})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setvalue:
                Logger.debug("setValue");
                //线程
                mTestViewModel.getNameEvent().setValue("I am setValue");
                break;
            case R.id.btn_postvalue:
                //主线程
                mTestViewModel.getNameEvent().postValue("I am setValue");
                break;
        }
    }
}
