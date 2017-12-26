package cuhk.cse.cmsc5736project.utils;

/**
 * Created by TCC on 12/26/2017.
 */

import java.util.concurrent.Executor;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;

public class PeriodicExecutor implements Executor {
    private static final Handler poller = new Handler();
    private Runnable command;
    private long period;
    private boolean isStopped = true;

    public PeriodicExecutor(long period) {
        this.period = period;
    }

    public void stop() {
        this.isStopped = true;
    }

    @Override
    public void execute(Runnable command) {
        this.command = command;
        this.isStopped = false;
        poller.postDelayed(executor, period);
    }

    private final Runnable executor = new Runnable() {
        @Override
        public void run() {
            if(PeriodicExecutor.this.isStopped == false) {
                long uptimeMillis = SystemClock.uptimeMillis();
                command.run();
                poller.postAtTime(this, uptimeMillis + PeriodicExecutor.this.period);
            }
        }
    };
}
