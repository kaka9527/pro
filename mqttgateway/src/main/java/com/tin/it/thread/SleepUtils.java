package com.tin.it.thread;

import java.util.concurrent.TimeUnit;

public class SleepUtils {
    public static void second(long s){
        try {
            TimeUnit.SECONDS.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
