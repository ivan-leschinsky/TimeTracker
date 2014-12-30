package by.vanopiano.timetracker;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.concurrent.TimeUnit;

/**
 * Created by De_Vano on 30 dec, 2014
 */
public class Settings {
    private SharedPreferences sp;
    long startedMilliss, workedMillis = 0;


    public Settings(Context context) {
        sp = context.getSharedPreferences("BaseSettings", Context.MODE_MULTI_PROCESS);
        load();
    }

    public void load() {
        startedMilliss = sp.getLong("timeStarted", 0);
        workedMillis = sp.getLong("workedMillis", 0);
    }

    public boolean isResuming() {
        return sp.getBoolean("isResuming", false);
    }

    public boolean isPaused() {
        return !isResuming();
    }

    public void resume() {
        sp.edit().putBoolean("isStopped", false).apply();

        setStartedTime(System.currentTimeMillis());
        sp.edit().putBoolean("isResuming", true).apply();
    }

    public void pause() {
        addTime(System.currentTimeMillis() - startedMilliss);
        sp.edit().putBoolean("isResuming", false).apply();
        setStartedTime(0);
    }

    public void addTime(long millisAdd) {
        workedMillis = millisAdd + sp.getLong("workedMillis", 0);
        sp.edit().putLong("workedMillis", workedMillis).apply();
    }

    public void stop() {
        pause();

        sp.edit().putLong("workedMillis", 0).apply();
        sp.edit().putBoolean("isStopped", true).apply();
    }

    public boolean isStopped() {
        return sp.getBoolean("isStopped", true);
    }

    public String getCurrentDiff() {
        long diff;
        if (isPaused())
            diff = workedMillis;
        else
            diff = (startedMilliss > 0) ? System.currentTimeMillis() - startedMilliss + workedMillis : workedMillis;

        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(diff),
                TimeUnit.MILLISECONDS.toMinutes(diff) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)),
                TimeUnit.MILLISECONDS.toSeconds(diff) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));
    }

    private void setStartedTime(long millis) {
        startedMilliss = millis;
        sp.edit().putLong("timeStarted", startedMilliss).apply();
    }
}
