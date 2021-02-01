package com.eryanet.mthread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.eryanet.common.utils.Logger;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.LoggingMXBean;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_handleT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testHandleT();
            }
        });

        findViewById(R.id.btn_ExecutorsT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testExecutors();
            }
        });

        findViewById(R.id.btn_normalT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNormalThread();
            }
        });

        findViewById(R.id.btn_handThread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNormalThread();
            }
        });

        findViewById(R.id.btn_syncThread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });

        Logger.debug("" + Thread.currentThread().getName());
        send("aaaa");
        send(new byte[4]);
        send("new byte[4]");
    }


    public void send(Object obj) {
        String type = obj.getClass().getSimpleName();
        Log.d("hahaha", "type: " + type);
        if ("String".equals(type)) {
            Log.d("hahaha", "String");
        } else if ("byte[]".equals(type)) {
            Log.d("hahaha", "Byte");
        }else if (obj instanceof ByteBuffer) {
            Log.d("hahaha", "Byte");
        }

    }

    private volatile int count = 100;
    private boolean isRunning = false;

    private void test() {
        isRunning = true;
        SyncThread syncThread1 = new SyncThread("线程一");
        SyncThread syncThread2 = new SyncThread("线程二");
        SyncThread syncThread3 = new SyncThread("线程三");

        syncThread1.start();
        syncThread2.start();
        syncThread3.start();

    }

    private class SyncThread extends Thread {

        SyncThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (isRunning) {
                count();
            }
        }
    }

    Lock lock = new ReentrantLock();

    private void count() {
        lock.lock();
//        synchronized (this) {
        if (count > 0) {
            Logger.debug(Thread.currentThread().getName() + "--->" + count--);
        } else {
            isRunning = false;
        }
//        }
        lock.unlock();
    }

    private void testNormalThread() {
        MRunalbe mRunalbe1 = new MRunalbe();
        MRunalbe mRunalbe2 = new MRunalbe();
        Thread thread1 = new Thread(mRunalbe1, "thread1");
        Thread thread2 = new Thread(mRunalbe2, "thread2");
        thread1.start();
        thread2.start();
    }

    class MRunalbe implements Runnable {

        @Override
        public void run() {

            Logger.debug("thread id : " + Thread.currentThread().getId() + " name: " + Thread.currentThread().getName());

        }
    }

    private HandlerThread handlerThread;
    private Handler sendMsgHandler;
    ExecutorService executorService;

    ExecutorService pool1;

    private void testExecutors() {

//        if (executorService != null && !executorService.isShutdown()) {
//            executorService.shutdownNow();
//        }

        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }

//        if(executorService == null){
//            executorService = Executors.newFixedThreadPool(2);
//        }

//        if (!executorService.isShutdown()) {
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 6; i++) {
//                    Log.d("mythread", "count > " + i +
//                            " thread Id > " + Thread.currentThread().getId());
//                    SystemClock.sleep(1000 * 3);
//                }
//            }
//        });
//        }

        pool1 = Executors.newCachedThreadPool(new NamedThreadFactory("EY"));
//        pool1 = Executors.newFixedThreadPool(3);
//        pool1 = Executors.newSingleThreadExecutor(new NamedThreadFactory());
        pool1.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 6; i++) {
                    Log.d("mythread", "count > " + i +
                            " thread Id > " + Thread.currentThread().getName());
                    SystemClock.sleep(1000 * 3);
                }
            }
        });
//        pool1 = Executors.newFixedThreadPool(3, new NamedThreadFactory("EY"));
        pool1.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 6; i++) {
                    Log.d("mythread", "count > " + i +
                            " thread Id > " + Thread.currentThread().getName());
                    SystemClock.sleep(1000 * 3);
                }
            }
        });
    }


    private void testHandleT() {
        if (handlerThread == null) {
            handlerThread = new HandlerThread("[apps]");
            handlerThread.start();
        }
        if (sendMsgHandler == null) {
            sendMsgHandler = new Handler(handlerThread.getLooper(), new SendMsgHandler());
        }

        for (int i = 0; i < 3; i++) {
            sendMsgHandler.obtainMessage(SENDMSG2PHONE, i).sendToTarget();
        }
    }

    private static final int SENDMSG2PHONE = 0x9;

    /**
     * 该线程用于发送消息
     */
    class SendMsgHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SENDMSG2PHONE:
                    int count = (int) msg.obj;
                    Log.d("mythread", "count > " + count +
                            " thread Id > " + Thread.currentThread().getId());
                    SystemClock.sleep(3 * 1000);
                    break;
            }
            return false;
        }
    }

}
