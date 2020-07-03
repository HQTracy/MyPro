package com.eryanet.common.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Logger {
    private static final String TAG = "eryalog";

    public static boolean isDebug = true;

    private static final String INFO = "INFO";

    private static final String DEBUG = "DEBUG";

    private static final String ERROR = "ERROR";

    private static Handler handler;

    public static void setDebugMode(boolean debugMode) {
        isDebug = debugMode;
    }

    public static void debug(String message) {
        if (isDebug)
            debug(TAG, message);
    }

    public static void debug(String tag, String message) {
        log(DEBUG, tag, message);
    }

    public static void info(String message) {
        if (isDebug)
            info(TAG, message);
    }

    public static void info(String tag, String message) {
        log(INFO, tag, message);
    }

    public static void error(String message) {
        if (isDebug)
            error(TAG, message);
    }

    public static void error(String tag, String message) {
        log(ERROR, tag, message);
    }

    public static void setHandler(Handler handler) {
        Logger.handler = handler;
    }

    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }

        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().contains("Logger")) {
                continue;
            }
            return "[" + Thread.currentThread().getId() + ": " + Thread.currentThread().getPriority() + " : " + st.getFileName() + ":" + st.getLineNumber() + "]";
        }
        return null;
    }

    private static void log(String level, String tag, String message) {
        String name = getFunctionName();
        String ls = (name == null ? message.toString() : (name + " >>> " + message));
        if (DEBUG.equals(level)) {
            Log.d(tag, ls);
        } else if (ERROR.equals(level)) {
            Log.e(tag, ls);
        } else if (INFO.equals(level)) {
            Log.i(tag, ls);
        }

        if (handler != null) {
            Message msg = Message.obtain();
            msg.obj = "[" + level + "]" + message + "\n";
            handler.sendMessage(msg);
        }
    }

}
