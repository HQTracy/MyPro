package com.eryanet.trxjava;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.eryanet.common.utils.Logger;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        func1();

    }

    public void func1() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Logger.debug("subscribe : " + Thread.currentThread().getName());
                emitter.onNext(22);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Logger.debug("doOnComplete : " + Thread.currentThread().getName());
                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Logger.debug("doOnNext : " + Thread.currentThread().getName());
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.debug("onSubscribe ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Logger.debug("onNext : " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.debug("onError ");
                    }

                    @Override
                    public void onComplete() {
                        Logger.debug("onComplete ");
                    }
                });
    }
}
