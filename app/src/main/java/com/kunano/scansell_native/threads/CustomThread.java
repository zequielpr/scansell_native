package com.kunano.scansell_native.threads;

public class CustomThread extends Thread{
    Runnable task;

    final Object lock = new Object();
    public CustomThread(Runnable task){
        this.task = task;
    }

    @Override
    public void run() {
        synchronized (lock) {
            task.run();
        }


    }
}
