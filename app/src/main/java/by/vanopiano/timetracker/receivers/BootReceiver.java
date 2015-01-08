package by.vanopiano.timetracker.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import by.vanopiano.timetracker.services.LocationCheckService;
/*
 * Created by De_Vano on 06 Jan, 2015
 */

public class BootReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LocationCheckService.class));
    }
}
