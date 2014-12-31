package by.vanopiano.timetracker.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public Task(){
        super();
    }

    public Task(String name, String description){
        super();
        this.name = name;
        this.description = description;
    }

    public boolean isPaused() {
        return !running;
    }

    public boolean isRunning() {
        return running;
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

    private void setStartedTime(long millis) {
        startedMillis = millis;
    }


    public void resume() {
        running = true;
        setStartedTime(System.currentTimeMillis());
        save();
    }

    public void pause() {
        running = false;
        addTime(System.currentTimeMillis() - startedMillis);
        setStartedTime(0);
        save();
    }

    public void addTime(long millisAdd) {
        workedMillis += millisAdd;
    }

    public Work stop() {
        pause();

        Work nw = new Work(this, workedMillis);
        nw.save();
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

