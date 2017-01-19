package xchg.online.studenttrack.utils;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rsankarx on 16/01/17.
 */

public class TimerUtil {
    private static Map<String, TimerRun> RUNS = new ConcurrentHashMap<>();

    private static class TimerRun {
        private int frequency;
        private int startAfter;
        private Runnable task;

        private Timer thistimer;
        private TimerTask thistask;

        TimerRun(int startat, int freq, Runnable run) {
            frequency = freq;
            startAfter = startat;
            task = run;
            thistimer = new Timer();

            thistask = new TimerTask() {
                @Override
                public void run() {
                    task.run();
                }
            };


            if (startat < 0) {
                //one time
                thistimer.schedule(thistask, frequency);
            } else {
                thistimer.schedule(thistask, startAfter, frequency);
            }
        }

        TimerRun(int freq, Runnable run) {
            this(-1, freq, run);
        }

        void stopTimer() {
            if (thistimer != null) {
                thistimer.cancel();
                thistimer = null;
            }
        }
    }

    public static TimerRun startTimer(String name, int freq, Runnable run) {
        TimerRun timer = new TimerRun(0, freq, run);
        RUNS.put(name, timer);
        return timer;
    }

    public static TimerRun startTimer(String name, int startat, int freq, Runnable run) {
        TimerRun timer = new TimerRun(startat, freq, run);
        RUNS.put(name, timer);
        return timer;
    }

    public static TimerRun startTimer(int runafter, Runnable run) {
        TimerRun timer = new TimerRun(runafter, run);
        return timer;
    }

    public static void stopTimer(String name) {
        if (RUNS.containsKey(name)) {
            TimerRun timer = RUNS.get(name);
            timer.stopTimer();
        }
    }

    public static void stopAll() {
        for (TimerRun timer : RUNS.values()) {
            timer.stopTimer();
        }
    }
}
