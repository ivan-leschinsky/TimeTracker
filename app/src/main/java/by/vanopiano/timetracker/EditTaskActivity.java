package by.vanopiano.timetracker;

import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import by.vanopiano.timetracker.models.Task;
import by.vanopiano.timetracker.services.LocationCheckService;
import by.vanopiano.timetracker.util.Helpers;

/**
 * Created by De_Vano on 6 Jan, 2014
 */
public class EditTaskActivity extends BaseActivity {

    public static final String EXTRA_ID = "EditTaskActivity:id";
    public static final String EXTRA_TASK_SAVED = "EditTaskActivity:task_saved";
    private Task task;
    private EditText name_ed, desc_ed, locationTreshholdMeters;
    private CheckBox locationResumeEnabledCheckBox;
    private GoogleMap googleMap;
    private LatLng userLocation = new LatLng(0, 0);
    private CameraPosition cameraPosition;
    private boolean firstOpen = true;
    private Marker resumeTaskMarker;
    private Intent locationServiceIntent;

    LinearLayout locationTreshholdLayout;
    MapFragment mapFragment;

    private GoogleMap.OnMyLocationChangeListener locationListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            userLocation = new LatLng(location.getLatitude(),location.getLongitude());
            if (firstOpen) {
                firstOpen = false;
                cameraPosition = new CameraPosition.Builder().target(userLocation).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    };

    private GoogleMap.OnMapLongClickListener mapLongClickListener = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(final LatLng location) {
            new MaterialDialog.Builder(EditTaskActivity.this)
                    .content(R.string.save_this_as_resume_task_potition)
                    .positiveText(R.string.save)
                    .negativeText(R.string.no)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                        task.setResumeLocation(location);
                        stopService(locationServiceIntent);
                        startService(locationServiceIntent);
                        putMarker();
                        }
                    })
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationServiceIntent = new Intent(this, LocationCheckService.class);

        task = Task.load(Task.class, getIntent().getLongExtra(EXTRA_ID, 0));

        name_ed = (EditText) findViewById(R.id.tv_name);
        desc_ed = (EditText) findViewById(R.id.tv_description);
        locationResumeEnabledCheckBox = (CheckBox) findViewById(R.id.location_resume_enabled);
        locationTreshholdMeters = (EditText) findViewById(R.id.tv_location_treshhold_meters);

        locationTreshholdLayout = (LinearLayout) findViewById(R.id.location_treshhold_layout);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        initilizeMap();

        locationResumeEnabledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeViewByCheckbox(isChecked);
                stopService(locationServiceIntent);
                startService(locationServiceIntent);
            }
        });

        if (task != null) {
            name_ed.setText(task.name);
            desc_ed.setText(task.description);
            locationResumeEnabledCheckBox.setChecked(task.locationResumeEnabled);
            locationTreshholdMeters.setText(String.valueOf(task.locationTreshholdMeters));
        }
        changeViewByCheckbox(locationResumeEnabledCheckBox.isChecked());

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!enabled) {
            new MaterialDialog.Builder(this)
                    .content(R.string.go_to_the_location_settings)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    private void changeViewByCheckbox(boolean isChecked) {
        mapFragment.getView().setClickable(isChecked);

        googleMap.getUiSettings().setMyLocationButtonEnabled(isChecked);
        googleMap.setMyLocationEnabled(isChecked);
        googleMap.getUiSettings().setZoomControlsEnabled(isChecked);
        googleMap.setOnMapLongClickListener(isChecked ? mapLongClickListener : null);

        mapFragment.getView().setAlpha(isChecked ? 1f : 0.3f);
        googleMap.getUiSettings().setAllGesturesEnabled(isChecked);

        for ( int i = 0; i < locationTreshholdLayout.getChildCount();  i++ ){
            View view = locationTreshholdLayout.getChildAt(i);
            view.setEnabled(isChecked);
        }
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_edit_task;
    }

    @Override public void onResume() {
        super.onResume();
        initilizeMap();
        putMarker();
    }

    private void initilizeMap() {
        try {
            if (googleMap == null) {
                googleMap = mapFragment.getMap();

                if (googleMap == null) {
                    Helpers.snackAlert(EditTaskActivity.this, "Sorry! unable to create maps");
                } else {
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.setMyLocationEnabled(true);
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.setOnMyLocationChangeListener(locationListener);
                    googleMap.setOnMapLongClickListener(mapLongClickListener);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putMarker() {
        if (task.latitude > 0 && task.longitude > 0) {
            firstOpen = false;

            if (resumeTaskMarker != null)
                resumeTaskMarker.remove();
            resumeTaskMarker = googleMap.addMarker(new MarkerOptions()
                .position(task.getResumeLocation())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(EditTaskActivity.this.getResources()
                        .getString(R.string.current_resume_location)));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(task.getResumeLocation()).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void saveTask() {
        String name = name_ed.getText().toString();
        if (!name.isEmpty()) {
            task.name = name;
            task.description = desc_ed.getText().toString();
            task.locationResumeEnabled = locationResumeEnabledCheckBox.isChecked();
            task.locationTreshholdMeters = Integer.parseInt(locationTreshholdMeters.getText().toString());
            task.save();

            stopService(locationServiceIntent);
            startService(locationServiceIntent);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_TASK_SAVED, true);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Helpers.snackAlert(EditTaskActivity.this, R.string.should_fill_name_field);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettingsActivity();
                return true;
            case R.id.action_save_task:
                saveTask();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}