package by.vanopiano.timetracker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.util.List;

import by.vanopiano.timetracker.services.LocationCheckService;
import by.vanopiano.timetracker.util.MultiprocessPreferences;

public class SettingsActivity extends PreferenceActivity {
    Intent locationServiceIntent;

    @SuppressWarnings("deprecation")
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        locationServiceIntent = new Intent(this, LocationCheckService.class);
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPreferencesFromResource(R.xml.pref_general);

        CheckBoxPreference useGpsPref = (CheckBoxPreference) getPreferenceManager().findPreference("use_gps");
        CheckBoxPreference swipingNotificationHoldingPref = (CheckBoxPreference) getPreferenceManager().findPreference("swiping_notification_holding");
        EditTextPreference holdingMinutes = (EditTextPreference) getPreferenceManager().findPreference("later_minutes");
        EditTextPreference stabilitySeconds = (EditTextPreference) getPreferenceManager().findPreference("stability_seconds");

    //TODO: Remove
//        holdingMinutes.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
//        stabilitySeconds.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

        useGpsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object useGps) {
                MultiprocessPreferences.getDefaultSharedPreferences(SettingsActivity.this)
                        .edit().putBoolean("use_gps_multi_process", (boolean)useGps).apply();
                restartLocationService();
                return true;
            }
        });

        swipingNotificationHoldingPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object swipingNotificationHolding) {
                MultiprocessPreferences.getDefaultSharedPreferences(SettingsActivity.this)
                        .edit().putBoolean("swiping_notification_holding_multi_process", (boolean)swipingNotificationHolding).apply();
                restartLocationService();
                return true;
            }
        });

        stabilitySeconds.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object holdingMinutes) {
                MultiprocessPreferences.getDefaultSharedPreferences(SettingsActivity.this)
                        .edit().putInt("stability_seconds_multi_process", Integer.parseInt((String) holdingMinutes)).apply();
                restartLocationService();
                return true;
            }
        });

        holdingMinutes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object holdingMinutes) {
                MultiprocessPreferences.getDefaultSharedPreferences(SettingsActivity.this)
                        .edit().putInt("later_minutes_multi_process", Integer.parseInt((String) holdingMinutes)).apply();
                return true;
            }
        });
    }

    private void restartLocationService() {
        stopService(locationServiceIntent);
        startService(locationServiceIntent);
    }
}
