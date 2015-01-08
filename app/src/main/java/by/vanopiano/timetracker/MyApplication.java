package by.vanopiano.timetracker;

import com.activeandroid.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by De_Vano on 30 Dec, 2014
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
