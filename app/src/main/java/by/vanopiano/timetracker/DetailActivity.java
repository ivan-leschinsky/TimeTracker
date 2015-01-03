package by.vanopiano.timetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import by.vanopiano.timetracker.adapters.WorksRecyclerAdapter;
import by.vanopiano.timetracker.models.Task;
import by.vanopiano.timetracker.models.Work;
import by.vanopiano.timetracker.util.RecyclerItemClickListener;

/**
 * Created by De_Vano on 31 dec, 2014
 */
public class DetailActivity extends BaseActivity {

    public static final String EXTRA_ID = "DetailActivity:id";
    public static final String EXTRA_TASK_POSITION_TO_DELETE = "DetailActivity:task_position";
    private Task task;
    private TextView tvTitle, tvElapsedTime, tvDescription;
    private Button outBtn, inBtn, stopBtn;
    private Handler updateTextViewHandler;
    private int taskPosition;

    private WorksRecyclerAdapter adapter;
    private RecyclerView recyclerView;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        updateTextViewHandler = new Handler();
        tvTitle = (TextView) findViewById(R.id.view_task_title);
        tvDescription = (TextView) findViewById(R.id.view_task_description);
        tvElapsedTime = (TextView) findViewById(R.id.view_task_elapsed_time);

        inBtn = (Button) findViewById(R.id.inBtn);
        outBtn = (Button) findViewById(R.id.outBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);

        ViewCompat.setTransitionName(tvTitle, EXTRA_ID);
        taskPosition = getIntent().getIntExtra(EXTRA_TASK_POSITION_TO_DELETE, 0);
        task = Task.load(Task.class, getIntent().getLongExtra(EXTRA_ID, 0));


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WorksRecyclerAdapter(this, task);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
            new RecyclerItemClickListener(DetailActivity.this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        removeWorkDialog(position);
                    }
                })
        );
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_detail;
    }

    Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            updateView();
            if (task.isRunning()) {
                updateTextViewHandler.postDelayed(updateTimerRunnable, 1000);
            }
        }
    };

    void startUpdatingView() {
        if (task.isRunning()) {
            updateTimerRunnable.run();
        }
    }

    void stopUpdatingView() {
        updateTextViewHandler.removeCallbacks(updateTimerRunnable);
    }

    public void updateDataOnViews() {
        if (task != null) {
            tvTitle.setText(task.name);
            tvDescription.setText(task.description);
            tvElapsedTime.setText(task.getCurrentDiff());
        }
    }

    @Override public void onResume() {
        super.onResume();
        LoadButtons();
        updateDataOnViews();
        startUpdatingView();
    }

    @Override public void onPause() {
        super.onPause();
        stopUpdatingView();
    }

    public void LoadButtons() {
        if (task.isRunning()) {
            inBtn.setVisibility(View.GONE);
            outBtn.setVisibility(View.VISIBLE);
        } else {
            outBtn.setVisibility(View.GONE);
            inBtn.setVisibility(View.VISIBLE);
        }
        if (task.isRunning() || task.workedMillis > 0)
            stopBtn.setVisibility(View.VISIBLE);
        else
            stopBtn.setVisibility(View.GONE);

        updateView();
    }

    public void updateView() {
        tvElapsedTime.setText(task.getCurrentDiff());
    }

    public void resumeStartTimer() {
        if (sp.getBoolean("only_one_work_at_a_time", false)) {
            for (Task t : Task.getAll()) {
                t.pause();
            }
        }
        task.resume();
        LoadButtons();
        startUpdatingView();
    }


    public void stopTimer() {
        Work w = task.stop();

        adapter.notifyTaskAdded(w);

        updateView();
        LoadButtons();
    }

    public void pauseTimer() {
        task.pause();
        LoadButtons();
    }

    public void buttonsClick(View btn) {
        switch (btn.getId()) {
            case R.id.inBtn:
                resumeStartTimer();
                break;
            case R.id.outBtn:
                pauseTimer();
                break;
            case R.id.stopBtn:
                stopTimer();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettingsActivity();
                return true;
            case R.id.action_edit_task:
                openEditDialog();
                return true;
            case R.id.action_delete_task:
                removeTaskDialog();
                return true;
            case android.R.id.home:
                // Explanation: To simulate back pressed. or nimated returning to the MainActivity.
                // Alternative: finish() method calling here.
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void removeTaskDialog() {
        new MaterialDialog.Builder(DetailActivity.this)
                .content(R.string.really_delete)
                .positiveText(R.string.action_delete)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        for (Work w : task.works()) {
                            w.delete();
                        }
                        task.delete();
                        if (task != null) {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_TASK_POSITION_TO_DELETE, taskPosition);
                            setResult(RESULT_OK, intent);
                        }
                        finish();
                    }
                })
                .show();
    }

    public void removeWorkDialog(final int position) {
        new MaterialDialog.Builder(DetailActivity.this)
                .content(R.string.really_delete)
                .positiveText(R.string.action_delete)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        adapter.removeItem(position);
                    }
                })
                .show();
    }

    public void openEditDialog() {
        final View view = LayoutInflater.from(DetailActivity.this).inflate(R.layout.task_form, null);
        final EditText name_ed = (EditText) view.findViewById(R.id.tv_name);
        final EditText desc_ed = (EditText) view.findViewById(R.id.tv_description);
        name_ed.setText(task.name);
        desc_ed.setText(task.description);
        new MaterialDialog.Builder(DetailActivity.this)
                .title(R.string.edit_task)
                .customView(view, true)
                .positiveText(R.string.save)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String name = name_ed.getText().toString();
                        String desc = desc_ed.getText().toString();
                        if (!name.isEmpty()) {
                            task.name = name;
                            task.description = desc;
                            task.save();
                            updateDataOnViews();
                            Toast.makeText(DetailActivity.this, R.string.successfully_changed, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(DetailActivity.this, R.string.should_fill_name_field, Toast.LENGTH_SHORT).show();
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

    public void openSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public static void launch(BaseActivity activity, View transitionView, long taskId, int taskPosition) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_ID);
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(EXTRA_ID, taskId);
        intent.putExtra(EXTRA_TASK_POSITION_TO_DELETE, taskPosition);
        ActivityCompat.startActivityForResult(activity, intent, 1, options.toBundle());
    }
}