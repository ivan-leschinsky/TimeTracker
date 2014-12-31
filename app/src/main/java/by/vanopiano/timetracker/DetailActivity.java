package by.vanopiano.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
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

import by.vanopiano.timetracker.models.Task;
import by.vanopiano.timetracker.models.Work;

/**
 * Created by De_Vano on 31 dec, 2014
 */
public class DetailActivity extends BaseActivity {

    public static final String EXTRA_ID = "DetailActivity:id";
    public static final String EXTRA_TASK_POSITION_TO_DELETE = "DetailActivity:task_position";
    private Task task;
    private TextView tvTitle, tvElapsedTime;
    private Button outBtn, inBtn, stopBtn;
    private Handler updateTextViewHandler;
    private int taskPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateTextViewHandler = new Handler();
        tvTitle = (TextView) findViewById(R.id.view_task_title);
        tvElapsedTime = (TextView) findViewById(R.id.view_task_elapsed_time);

        inBtn = (Button) findViewById(R.id.inBtn);
        outBtn = (Button) findViewById(R.id.outBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);

        ViewCompat.setTransitionName(tvTitle, EXTRA_ID);
        taskPosition = getIntent().getIntExtra(EXTRA_TASK_POSITION_TO_DELETE, 0);
        task = Task.load(Task.class, getIntent().getLongExtra(EXTRA_ID, 0));
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
            inBtn.setEnabled(false);
            outBtn.setEnabled(true);
        } else {
            outBtn.setEnabled(false);
            inBtn.setEnabled(true);
        }
        if (task.isRunning() || task.workedMillis > 0)
            stopBtn.setEnabled(true);
        else
            stopBtn.setEnabled(false);

        updateView();
    }

    public void updateView() {
        tvElapsedTime.setText(task.getCurrentDiff());
    }

    public void resumeStartTimer() {
        task.resume();
        LoadButtons();
        startUpdatingView();
    }


    public void stopTimer() {
        task.stop();
        //TODO: Update Works of this Task.
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
                .content(R.string.remove_task)
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
                            new Task(name, desc).save();
                            updateDataOnViews();
                            Toast.makeText(DetailActivity.this, R.string.successfully_changed, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(DetailActivity.this, R.string.should_fill_name_field, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .autoDismiss(false)
                .show();
    }

    public void openSettingsActivity() {
        //TODO: Implement this method
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