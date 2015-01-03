package by.vanopiano.timetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;

import by.vanopiano.timetracker.adapters.TasksRecyclerAdapter;
import by.vanopiano.timetracker.models.Task;
import by.vanopiano.timetracker.util.RecyclerItemClickListener;

/**
 * Created by De_Vano on 30 dec, 2014
 */
public class MainActivity extends BaseActivity {
    private Handler updateTextViewHandler;
    private TasksRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.ic_launcher);

        updateTextViewHandler = new Handler();
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TasksRecyclerAdapter(this);

        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        DetailActivity.launch(MainActivity.this,
                                view.findViewById(R.id.task_small_title),
                                adapter.getTaskId(position), position);
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
    }

    private void showAddTaskDialog() {
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.task_form, null);

        new MaterialDialog.Builder(MainActivity.this)
            .title(R.string.create_new_task)
            .customView(view, true)
            .positiveText(R.string.save)
            .negativeText(R.string.cancel)
            .callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    String name = ((EditText) view.findViewById(R.id.tv_name)).
                            getText().toString();
                    String desc = ((EditText) view.findViewById(R.id.tv_description)).
                            getText().toString();
                    if (!name.isEmpty()) {
                        Task t = new Task(name, desc);
                        Toast.makeText(MainActivity.this,
                                R.string.successfully_created, Toast.LENGTH_SHORT).show();
                        t.save();
                        adapter.addTask(t);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this,
                                R.string.should_fill_name_field, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNegative(MaterialDialog dialog) {
                    dialog.dismiss();
                }
            })
            .autoDismiss(false)
            .show();
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    public void onResume() {
        super.onResume();
        checkManyTasks();
        startUpdatingViews();
    }

    public void onPause() {
        super.onPause();
        stopUpdatingView();
    }

    private void checkManyTasks() {
        boolean atLeastOneTaskRunning = false;
        if (sp.getBoolean("only_one_work_at_a_time", false)) {
            for (Task t : Task.getAll()) {
                if (t.isRunning()) {
                    if (atLeastOneTaskRunning) {
                        Toast.makeText(this, R.string.warning_many_tasks_running,
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    atLeastOneTaskRunning = true;
                }
            }
        }
    }

    private void openSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
            updateTextViewHandler.postDelayed(updateTimerRunnable, 1000);
        }
    };

    void startUpdatingViews() {
        updateTextViewHandler.postDelayed(updateTimerRunnable, 1000);
    }

    void stopUpdatingView() {
        updateTextViewHandler.removeCallbacks(updateTimerRunnable);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        int position = data.getIntExtra(DetailActivity.EXTRA_TASK_POSITION_TO_DELETE, 0);
        adapter.removeItem(position);
    }
}
