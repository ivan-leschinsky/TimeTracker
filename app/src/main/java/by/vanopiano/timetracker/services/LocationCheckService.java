package by.vanopiano.timetracker.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import by.vanopiano.timetracker.models.Task;
import by.vanopiano.timetracker.util.Helpers;
import by.vanopiano.timetracker.util.MultiprocessPreferences;
import by.vanopiano.timetracker.util.MultiprocessPreferences.MultiprocessSharedPreferences;

/**
 * Created by De_Vano on 06 Jan, 2015
 */
public class LocationCheckService extends Service
{
    private LocationManager mLocationManager = null;
    private static final int NETWORK_LOCATION_INTERVAL = 1000;
    private static final int GPS_LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    private LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;
        public LocationListener(String provider)
        {
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            mLastLocation.set(location);
            checkTasksForDistance(location);
        }
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void initializeNetworkUpdates() {
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, NETWORK_LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException | IllegalArgumentException ex) {
            // Do nothing
        }
    }

    private void initializeGpsUpdates() {
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, GPS_LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException | IllegalArgumentException ex) {
            // Do nothing
        }
    }

    @Override
    public void onCreate()
    {
        MultiprocessSharedPreferences sp = MultiprocessPreferences.getDefaultSharedPreferences(getApplicationContext());

        initializeLocationManager();
        initializeNetworkUpdates();

        if (sp.getBoolean("use_gps_new", false))
            initializeGpsUpdates();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    // Do nothing
                }
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void checkTasksForDistance(Location currentLocation) {
        for (Task task : Task.getAll()) {
            task.inDistance(currentLocation);

            if (task.latitude > 0 && task.longitude > 0 && task.locationResumeEnabled) {
                if (task.inDistance(currentLocation)) {
                    if (task.isPaused()) {
                        Helpers.createNotification(getApplicationContext(), task, BaseTaskNotificationService.NOTIF_TASK_TYPE_RESUME);
                    } else {
                        Helpers.closeNotification(getApplicationContext(), task.getId().intValue());
                    }
                } else {
                    if (task.isRunning()) {
                        Helpers.createNotification(getApplicationContext(), task, BaseTaskNotificationService.NOTIF_TASK_TYPE_PAUSE);
                    } else {
                        Helpers.closeNotification(getApplicationContext(), task.getId().intValue());
                    }
                }
            }
        }
    }
}