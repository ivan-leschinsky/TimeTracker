package by.vanopiano.timetracker.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.serializer.TypeSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by De_Vano on 31 dec, 2014
 */

@Table(name = "Works")
public class Work extends Model {

    @Column(name = "timestamp", index = true)
    private Date timestamp;

    @Column(name = "workedMillis")
    public String workedTime;

    @Column(name = "Task")
    public Task task;

    public Work(){
        super();
    }

    public Work(Task task, long workedMillis){
        super();
        this.task = task;
        this.timestamp = new Date();
        this.workedTime = getWorkedTime(workedMillis);
    }

    //TODO: Remove this constructor
    public Work(Task task, Date date, long workedMillis){
        super();
        this.task = task;
        this.timestamp = date; // TODO: current Date
        this.workedTime = getWorkedTime(workedMillis);
    }

    public String getWorkedTime(long workedMillis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(workedMillis),
                TimeUnit.MILLISECONDS.toMinutes(workedMillis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(workedMillis)),
                TimeUnit.MILLISECONDS.toSeconds(workedMillis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(workedMillis)));
    }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yy");
        return sdf.format(timestamp);
    }

    public static List<Task> getAll(Task task) {
        return new Select()
                .from(Work.class)
                .where("Task = ?", task.getId())
                .orderBy("Id ASC")
                .execute();
    }
}


