package by.vanopiano.timetracker;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import by.vanopiano.timetracker.models.Task;

/**
 * Created by De_Vano on 30 dec, 2014
 */
public class MainActivity extends BaseActivity {
//    private TextView elapsedTime;
//    private Button outBtn, inBtn, stopBtn;
    private Settings settings;
    private Handler updateTextViewHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.ic_launcher);
        settings = new Settings(this);
        updateTextViewHandler = new Handler();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, getResources()
                .getStringArray(R.array.countries));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        String url = "http://lorempixel.com/800/600/sports/" + String.valueOf(position);
                        DetailActivity.launch(MainActivity.this, view.findViewById(R.id.image), url);
                    }
                })
        );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });

//        elapsedTime = (TextView) findViewById(R.id.elapsedTime);
//        inBtn = (Button) findViewById(R.id.inBtn);
//        outBtn = (Button) findViewById(R.id.outBtn);
//        stopBtn = (Button) findViewById(R.id.stopBtn);


//        ListView gridView = (ListView) findViewById(android.R.id.list);
//        gridView.setAdapter(new GridViewAdapter());
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String url = (String) view.getTag();
//                DetailActivity.launch(MainActivity.this, view.findViewById(R.id.image), url);
//            }
//        });
    }

    private void showAddTaskDialog() {
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.task_form, null);

        new MaterialDialog.Builder(MainActivity.this)
                .title(R.string.create_new_task)
                .customView(view, true)
                .positiveText(R.string.create)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String name = ((EditText) view.findViewById(R.id.tv_name)).getText().toString();
                        String desc = ((EditText) view.findViewById(R.id.tv_description)).getText().toString();
                        if (!name.isEmpty() && !desc.isEmpty()) {
                            new Task(name, desc).save();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this,R.string.should_fill_all_fields,Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    @Override protected int getLayoutResource() {
        return R.layout.activity_main;
    }


    public void buttonsClick(View btn) {
//        switch (btn.getId()) {
//            case R.id.inBtn:
//                resumeStartTimer();
//                break;
//            case R.id.outBtn:
//                pauseTimer();
//                break;
//            case R.id.stopBtn:
//                stopTimer();
//                break;
//        }
    }

    public void onResume() {
        super.onResume();
        LoadSettings();
//        startUpdatingView();
    }

    public void onPause() {
        super.onPause();
//        stopUpdatingView();
    }

    public void LoadSettings() {
//        settings.load();
//        boolean isResuming = settings.isResuming();
//        boolean isStopped = settings.isStopped();
//        if (isResuming) {
//            inBtn.setEnabled(false);
//            outBtn.setEnabled(true);
//        } else {
//            outBtn.setEnabled(false);
//            inBtn.setEnabled(true);
//        }
//        if (isStopped) {
//            stopBtn.setEnabled(false);
//        } else {
//            stopBtn.setEnabled(true);
//        }
//
//        updateView();
    }
//
//    public void updateView() {
//          elapsedTime.setText(settings.getCurrentDiff());
//    }
//
//    public void resumeStartTimer() {
//        settings.resume();
//        LoadSettings();
//        startUpdatingView();
//    }
//
//
//    public void stopTimer() {
//        pauseTimer();
//        updateView();
//        settings.stop();
//        LoadSettings();
//    }
//
//    public void pauseTimer() {
//        settings.pause();
//        LoadSettings();
//    }

    private void openSettingsActivity() {
//        startActivity(new Intent(this, SettingsActivity.class));
    }

//    Runnable updateTimerRunnable = new Runnable() {
//        @Override
//        public void run() {
//            updateView();
//            if (settings.isResuming()) {
//                updateTextViewHandler.postDelayed(updateTimerRunnable, 1000);
//            }
//        }
//    };
//
//    void startUpdatingView() {
//        if (settings.isResuming() && !settings.isStopped()) {
//            updateTimerRunnable.run();
//        }
//    }
//
//    void stopUpdatingView() {
//        updateTextViewHandler.removeCallbacks(updateTimerRunnable);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettingsActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
