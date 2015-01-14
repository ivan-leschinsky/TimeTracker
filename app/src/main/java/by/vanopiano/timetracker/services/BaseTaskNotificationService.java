package by.vanopiano.timetracker.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import by.vanopiano.timetracker.R;
import by.vanopiano.timetracker.events.OnTaskPausedEvent;
import by.vanopiano.timetracker.events.OnTaskStartedEvent;
import by.vanopiano.timetracker.models.Task;
import by.vanopiano.timetracker.util.Helpers;
import de.greenrobot.event.EventBus;

/**
 * Created by De_Vano on 07 Jan, 2015
 */
public class BaseTaskNotificationService extends Service {
    public static final String NOTIF_TASK_ID = "notif_task_id";
    public static final String NOTIF_STARTED_MILLIS = "notif_started_millis";
    public static final String NOTIF_TASK_TYPE = "notif_task_type";
    public static final int NOTIF_TASK_TYPE_NONE = -1;
    public static final int NOTIF_TASK_TYPE_RESUME = 0;
    public static final int NOTIF_TASK_TYPE_PAUSE = 1;

    protected Context context;
    protected long startedPausedMillis = 0;
    protected Task task;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        context = getApplicationContext();
        super.onStartCommand(intent, flags, startId);

        startedPausedMillis = intent.getLongExtra(NOTIF_STARTED_MILLIS, 0);
        task = Task.load(Task.class, intent.getLongExtra(NOTIF_TASK_ID, 0));
        int taskType = intent.getIntExtra(NOTIF_TASK_TYPE, NOTIF_TASK_TYPE_NONE);

        if (task != null) {
            switch (taskType) {
                case NOTIF_TASK_TYPE_RESUME:
                    if (task.isPaused())
                        startTask();
                    break;
                case NOTIF_TASK_TYPE_PAUSE:
                    if (task.isRunning())
                        pauseTask();
                    break;
            }
        } else {
            Helpers.toast(context, task.name + " " +  context.getResources().getString(R.string.doesnt_exist));
        }

        Helpers.closeNotification(getApplicationContext(), task.getId().intValue());

        stopSelf(startId);
        return START_NOT_STICKY;
    }

    public void pauseTask() {
        EventBus.getDefault().post(new OnTaskPausedEvent(task.getId()));
    }

    public void startTask() {
        EventBus.getDefault().post(new OnTaskStartedEvent(task.getId()));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}