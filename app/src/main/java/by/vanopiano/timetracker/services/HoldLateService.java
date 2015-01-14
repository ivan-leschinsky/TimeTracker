package by.vanopiano.timetracker.services;

import android.content.Intent;
import by.vanopiano.timetracker.R;
import by.vanopiano.timetracker.models.Task;
import by.vanopiano.timetracker.util.Helpers;

/**
 * Created by De_Vano on 10 Jan, 2015
 */
public class HoldLateService extends BaseTaskNotificationService {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        context = getApplicationContext();
        task = Task.load(Task.class, intent.getLongExtra(NOTIF_TASK_ID, 0));

        if (task != null) {
            task.updateHoldedTime();
        } else {
            Helpers.toast(context, task.name + " " + context.getResources().getString(R.string.doesnt_exist));
        }

        Helpers.closeNotification(getApplicationContext(), task.getId().intValue());

        stopSelf(startId);
        return START_NOT_STICKY;
    }
}
