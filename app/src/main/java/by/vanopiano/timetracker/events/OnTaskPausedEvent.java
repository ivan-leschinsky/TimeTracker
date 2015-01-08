package by.vanopiano.timetracker.events;

/**
 * Created by De_Vano on 08 Jan, 2015
 */
public class OnTaskPausedEvent {
    public final long taskId;

    public OnTaskPausedEvent(long taskId) {
        this.taskId = taskId;
    }
}
