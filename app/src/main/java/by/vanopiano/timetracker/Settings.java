package by.vanopiano.timetracker;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by OnLiker developers (De_Vano) on 30 дек, 2014
 */
public class Settings {
    private SharedPreferences sp;
    private SimpleDateFormat sdf;
    long startedMilliss, currentMillis, workedMillis = 0;


    public Settings(Context context) {
        String timeZoneId = "Minsk";
        sp = context.getSharedPreferences("BaseSettings", Context.MODE_MULTI_PROCESS);
//        timeZoneId = Calendar.getInstance().getTimeZone().getID();   // "Europe/Minsk" - doesn't work with Simple Data Format
        sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));

        load();
    }

    public void load() {
        currentMillis = System.currentTimeMillis();
//
//        if (isResuming()) {
//            startedMilliss = sp.getLong("timeStarted",currentMillis);
//        }
        startedMilliss = sp.getLong("timeStarted",currentMillis);
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

        startedMilliss = System.currentTimeMillis();
        sp.edit().putLong("timeStarted", startedMilliss).apply();
        sp.edit().putBoolean("isResuming", true).apply();
    }

    public void pause() {
        addTime(System.currentTimeMillis() - startedMilliss);
        sp.edit().putBoolean("isResuming", false).apply();
    }

    public void addTime(long millisAdd) {
        workedMillis = millisAdd + sp.getLong("workedMillis", 0);
        sp.edit().putLong("workedMillis", workedMillis).apply();
    }

    public void stop() {
        pause();
        workedMillis = 0;
        sp.edit().putLong("timeStarted", 0).apply(); // TODO: Check it in the future.
        sp.edit().putLong("workedMillis", workedMillis).apply();
        sp.edit().putBoolean("isStopped", true).apply();
    }

    public boolean isStopped() {
        return sp.getBoolean("isStopped", true);
    }

    public String getCurrentDiff() {
        if (isPaused())
            return sdf.format(new Date(workedMillis));
        long diff = (startedMilliss > 0) ? System.currentTimeMillis() - startedMilliss : 0;
        String workedTime = sdf.format(new Date(workedMillis));
        String diffTime = sdf.format(new Date(diff));
        String allTime = sdf.format(new Date(workedMillis + diff));
        return allTime;
    }
}
