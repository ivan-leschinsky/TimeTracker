package by.vanopiano.timetracker.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;

import java.text.SimpleDateFormat;
import java.util.Date;

import by.vanopiano.timetracker.DetailActivity;
import by.vanopiano.timetracker.R;
import by.vanopiano.timetracker.models.Task;
import by.vanopiano.timetracker.services.BaseTaskNotificationService;
import by.vanopiano.timetracker.services.EarlyActionTaskNotificationService;
import by.vanopiano.timetracker.services.NowActionTaskNotificationService;

/**
 * Created by De_Vano on 07 Jan, 2015
 */
public class Helpers {

    public static void createNotification(Context mContext, Task task, int taskType) {
        Context context = mContext;

        String title, startPauseText, startPauseEarlierText,
        text = context.getString(R.string.notif_description);
        int startPauseIcon, startPauseEarlierIcon;

        switch (taskType) {
            case BaseTaskNotificationService.NOTIF_TASK_TYPE_RESUME:
                title = context.getString(R.string.notif_would_you_like_to_start) + task.name + "\'?";
                startPauseText = context.getString(R.string.notif_start_now);
                startPauseEarlierText = context.getString(R.string.notif_start_from);
                startPauseIcon = R.drawable.ic_notif_start_now;
                startPauseEarlierIcon = R.drawable.ic_notif_start_pause_from;
                break;

            case BaseTaskNotificationService.NOTIF_TASK_TYPE_PAUSE:
                title = context.getString(R.string.notif_would_you_like_to_pause) + task.name + "\'?";
                startPauseText = context.getString(R.string.notif_pause_now);
                startPauseEarlierText = context.getString(R.string.notif_pause_from);
                startPauseIcon = R.drawable.ic_notif_pause_now;
                startPauseEarlierIcon = R.drawable.ic_notif_start_pause_from;
                break;
            default:
                return;
        }
        long notificationStartedMillis = task.notificationStartedMillis;

        if (notificationStartedMillis == 0) {
            task.setNotificationStartedMillis(System.currentTimeMillis());
            notificationStartedMillis = task.notificationStartedMillis;
        }
        Intent activityIntent = new Intent(context, DetailActivity.class).putExtra(DetailActivity.EXTRA_ID, task.getId());

        Intent startNowIntent = new Intent(context, NowActionTaskNotificationService.class)
                .putExtra(BaseTaskNotificationService.NOTIF_TASK_ID, task.getId())
                .putExtra(BaseTaskNotificationService.NOTIF_TASK_TYPE, taskType);

        Intent startEarlierIntent = new Intent(context, EarlyActionTaskNotificationService.class)
                .putExtra(BaseTaskNotificationService.NOTIF_TASK_ID, task.getId())
                .putExtra(BaseTaskNotificationService.NOTIF_TASK_TYPE, taskType)
                .putExtra(BaseTaskNotificationService.NOTIF_STARTED_MILLIS, notificationStartedMillis);


        PendingIntent piActivity = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piStartPauseFromNow = PendingIntent.getService(context, 0, startNowIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piStartPauseFromEarlier = PendingIntent.getService(context, 0, startEarlierIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String formattedStartedTime = new SimpleDateFormat(" HH:mm").format(notificationStartedMillis);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_location_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(piActivity)
                .addAction(startPauseIcon, startPauseText, piStartPauseFromNow)
                .addAction(startPauseEarlierIcon, startPauseEarlierText + formattedStartedTime, piStartPauseFromEarlier);

        int notificationId = task.getId().intValue();
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, nBuilder.build());
    }

    public static void closeNotification(Context context, int notificationId) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notificationId);
    }

    public static void toast(Context context, CharSequence message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 50);
        toast.show();
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        // haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist);
    }

    public static void snackInfo(Activity activity, String text) {
        snack(activity, text, SnackBar.Style.INFO);
    }

    public static void snackInfo(Activity activity, int textId) {
        snack(activity, textId, SnackBar.Style.INFO);
    }

    public static void snackAlert(Activity activity, String text) {
        snack(activity, text, SnackBar.Style.ALERT);
    }

    public static void snackAlert(Activity activity, int textId) {
        snack(activity, textId, SnackBar.Style.ALERT);
    }

    public static void snack(Activity activity, String text, SnackBar.Style style) {
        new SnackBar.Builder(activity)
                .withMessage(text)
                .withTextColorId(R.color.colorPrimaryDark)
                .withStyle(style)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    public static void snack(Activity activity, int textId, SnackBar.Style style) {
        new SnackBar.Builder(activity)
                .withMessageId(textId)
                .withTextColorId(R.color.colorPrimaryDark)
                .withStyle(style)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }


    public static boolean isTablet(Context context) {
        TelephonyManager manager =
                (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            //Tablet
            return true;
        } else {
            //Mobile
            return false;
        }
    }

    public static boolean isTabletDevice(Context context) {
        try {
            // Compute screen size
            DisplayMetrics dm =
                    context.getResources().getDisplayMetrics();
            float screenWidth  = dm.widthPixels / dm.xdpi;
            float screenHeight = dm.heightPixels / dm.ydpi;
            double size = Math.sqrt(Math.pow(screenWidth, 2) +
                    Math.pow(screenHeight, 2));
            // Tablet devices should have a screen size greater than 6 inches
            return size >= 6;
        } catch(Throwable t) {
            return false;
        }

    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
