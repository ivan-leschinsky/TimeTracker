package by.vanopiano.timetracker.services;

import by.vanopiano.timetracker.util.Helpers;

/**
 * Created by De_Vano on 08 Jan, 2015
 */
public class NowActionTaskNotificationService extends BaseTaskNotificationService {
    @Override
    public void pauseTask() {
        task.pause();
        super.pauseTask();
    }

    @Override
    public void startTask() {
        task.resume();
        super.startTask();
    }
}
