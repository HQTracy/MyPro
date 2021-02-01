package com.eryanet.tlivedata;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.eryanet.common.utils.Logger;
import com.eryanet.tlivedata.model.TestViewModel;
import com.eryanet.tlivedata.model.TestViewModel_pramas;
import com.eryanet.tlivedata.model_zidingyi.NetworkLiveData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.showcontent)
    TextView showcontent;
    @BindView(R.id.btn_setvalue)
    Button btnSetvalue;
    @BindView(R.id.btn_postvalue)
    Button btnPostvalue;
    @BindView(R.id.btn_params)
    Button btnParams;

    TestViewModel mTestViewModel;
    TestViewModel_pramas viewModel_pramas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //普通
        mTestViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
        MutableLiveData<String> nameEvent = mTestViewModel.getNameEvent();
        nameEvent.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Logger.debug("onChanged s: " + s + " thread is " + Thread.currentThread().getName());

                showcontent.setText(s);
            }
        });

        //of带参数
        viewModel_pramas = ViewModelProviders.of(this, new TestViewModel_pramas.Factory("testKey")).get(TestViewModel_pramas.class);

        viewModel_pramas.getNameEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Logger.debug("viewModel_pramas.getKey() is  " + viewModel_pramas.getKey());
                Logger.debug("viewModel_pramas String is  " + s);
            }
        });


        //自定义livedata 监听网络状态
        NetworkLiveData.getInstance(this).observe(this, new Observer<NetworkInfo>() {
            @Override
            public void onChanged(NetworkInfo networkInfo) {
                Logger.debug("NetworkLiveData onChanged : " + networkInfo);
            }
        });


    }


    @OnClick({R.id.btn_setvalue, R.id.btn_postvalue, R.id.btn_params})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setvalue:
                //主线程调用
                mTestViewModel.getNameEvent().setValue("I am setValue");
                break;
            case R.id.btn_postvalue:
                //主/子线程均可
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mTestViewModel.getNameEvent().postValue("I am postvalue");
                    }
                }).start();
                break;
            case R.id.btn_params:
                viewModel_pramas.getNameEvent().postValue("params postvalue");
                break;
        }
    }
}
