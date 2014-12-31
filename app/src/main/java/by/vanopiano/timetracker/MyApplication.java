package by.vanopiano.timetracker;

import com.activeandroid.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by OnLiker developers (De_Vano) on 30 дек, 2014
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
