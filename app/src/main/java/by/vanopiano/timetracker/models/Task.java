package by.vanopiano.timetracker.models;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.TimeUnit;

import by.vanopiano.timetracker.util.Helpers;

/**
 * Created by De_Vano on 30 dec, 2014
 */


@Table(name = "Tasks")
public class Task extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "Description")
    public String description;

    @Column(name = "Running")
    public boolean running;

    @Column(name = "workedMillis")
    public long workedMillis;

    @Column(name = "startedMillis")
    public long startedMillis;

    @Column(name = "notificationStartedMillis")
    public long notificationStartedMillis = 0;

    @Column(name = "ResumeLatitude")
    public double latitude = 0;

    @Column(name = "ResumeLongitude")
    public double longitude = 0;

    @Column(name = "LocationResumeEnabled")
    public boolean locationResumeEnabled = false;

    @Column(name = "LocationTreshhold")
    public int locationTreshholdMeters = 100;

    @Column(name = "TimeApperedInLocation")
    public long timeApperedInLocation = 0;

    @Column(name = "holdedTime")
    public long holdedTime = 0;

    public double distance = -1;

    public Task(){
        super();
    }

    public Task(String name, String description){
        super();
        this.name = name;
        this.description = description;
    }

    public boolean isStableLocation(int stabilitySeconds) {
        return timeApperedInLocation > 0 && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeApperedInLocation) >= stabilitySeconds;
    }

    public boolean isPaused() {
        return !running;
    }

    public boolean isRunning() {
        return running;
    }

    public void updateHoldedTime() {
        holdedTime = System.currentTimeMillis();
        save();
    }
    public boolean isNotHolded(int settingMinutes) {
        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - holdedTime) >= settingMinutes;
    }
    public String getCurrentDiff() {
        long diff;
        if (isPaused())
            diff = workedMillis;
        else
            diff = (startedMillis > 0) ? System.currentTimeMillis() - startedMillis + workedMillis : workedMillis;

        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(diff),
                TimeUnit.MILLISECONDS.toMinutes(diff) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)),
                TimeUnit.MILLISECONDS.toSeconds(diff) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));
    }

    public void setNotificationStartedMillis(long notificationStartedMillis) {
        this.notificationStartedMillis = notificationStartedMillis;
        save();
    }

    public void updateTimeApperedInLocation() {
        timeApperedInLocation = System.currentTimeMillis();
        save();
    }
    public void clearTimeApperedInLocation() {
        timeApperedInLocation = 0;
        save();
    }

    public int getWorkedHours() {
        return (int)TimeUnit.MILLISECONDS.toHours(workedMillis);
    }

    public int getWorkedMinutes() {
        return (int)(TimeUnit.MILLISECONDS.toMinutes(workedMillis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(workedMillis)));
    }

    public void setWorkedTime(int hours, int minutes) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(workedMillis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(workedMillis));
        this.workedMillis = TimeUnit.HOURS.toMillis(hours)
                + TimeUnit.MINUTES.toMillis(minutes)
                + TimeUnit.SECONDS.toMillis(seconds);
        save();
    }

    private void setStartedTime(long millis) {
        startedMillis = millis;
    }

    public void setResumeLocation(LatLng location) {
        this.latitude = location.latitude;
        this.longitude = location.longitude;
        save();
    }

    public LatLng getResumeLocation() {
        return new LatLng(latitude, longitude);
    }

    public boolean inDistance(Location currentLocation) {
        if (currentLocation == null)
            return false;
        distance = Helpers.distance(currentLocation.getLatitude(), currentLocation.getLongitude(), latitude, longitude);
        return distance < locationTreshholdMeters;
    }

    public void resume() {
        resume(System.currentTimeMillis());
    }

    public void resume(long startedMillis) {
        running = true;
        notificationStartedMillis = 0;
        setStartedTime(startedMillis);
        save();
    }

    public void pause(long pausedMillis) {
        if (running) {
            running = false;
            addTime(pausedMillis - startedMillis);
            setStartedTime(0);
            save();
        }
    }

    public void pause() {
        pause(System.currentTimeMillis());
    }

    public void addTime(long millisAdd) {
        workedMillis += millisAdd;
    }

    public Work stop() {
        if (isRunning())
            pause();
        Work nw = new Work(this, workedMillis);
        nw.save();

        workedMillis = 0;
        save();
        return nw;
    }

    public static List<Task> getAll() {
        return new Select()
                .from(Task.class)
                .orderBy("Id ASC")
                .execute();
    }

    public List<Work> works() {
        return getMany(Work.class, "Task");
    }
}

