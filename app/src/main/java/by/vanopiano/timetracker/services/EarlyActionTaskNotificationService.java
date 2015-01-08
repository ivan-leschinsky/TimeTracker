package by.vanopiano.timetracker.services;

import android.content.Intent;
import android.os.IBinder;

import by.vanopiano.timetracker.util.Helpers;

/**
 * Created by De_Vano on 08 Jan, 2015
 */
public class EarlyActionTaskNotificationService extends BaseTaskNotificationService {
    @Override
    public void pauseTask() {
        task.pause(startedPausedMillis);
        super.pauseTask();
    }

    @Override
    public void startTask() {
        task.resume(startedPausedMillis);
        super.startTask();
    }
}
