package by.vanopiano.timetracker.events;

/**
 * Created by De_Vano on 08 Jan, 2015
 */
public class OnTaskStartedEvent {
    public final long taskId;

    public OnTaskStartedEvent(long taskId) {
        this.taskId = taskId;
    }
}
