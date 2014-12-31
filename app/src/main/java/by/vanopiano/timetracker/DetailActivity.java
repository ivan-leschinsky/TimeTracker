package by.vanopiano.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import by.vanopiano.timetracker.models.Task;

/**
 * Created by OnLiker developers (De_Vano) on 31 дек, 2014
 */
public class DetailActivity extends BaseActivity {

    public static final String EXTRA_ID = "DetailActivity:id";
    private Task task;
    private TextView tvTitle, tvElapsedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvTitle = (TextView) findViewById(R.id.view_task_title);
        tvElapsedTime = (TextView) findViewById(R.id.view_task_elapsed_time);
        ViewCompat.setTransitionName(tvTitle, EXTRA_ID);
        task = Task.load(Task.class, getIntent().getLongExtra(EXTRA_ID, 0));
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_detail;
    }

    public void updateDataOnViews() {
        if (task != null) {
            tvTitle.setText(task.name);
            tvElapsedTime.setText(task.getCurrentDiff());
        }
    }

    @Override public void onResume() {
        super.onResume();
        updateDataOnViews();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                        task.delete();
                        startActivity(new Intent(DetailActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

    public static void launch(BaseActivity activity, View transitionView, long taskId) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_ID);
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(EXTRA_ID, taskId);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}