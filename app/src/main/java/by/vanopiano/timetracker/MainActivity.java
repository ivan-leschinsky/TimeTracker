package by.vanopiano.timetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by De_Vano on 30 dec, 2014
 */
public class MainActivity extends Activity {
    private TextView elapsedTime;
    private Button outBtn, inBtn, stopBtn;
    private Settings settings;
    private Handler updateTextViewHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = new Settings(this);
        elapsedTime = (TextView) findViewById(R.id.elapsedTime);
        inBtn = (Button) findViewById(R.id.inBtn);
        outBtn = (Button) findViewById(R.id.outBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);

        updateTextViewHandler = new Handler();
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

    public void onResume() {
        super.onResume();
        LoadSettings();
        startUpdatingView();
    }

    public void onPause() {
        super.onPause();
        stopUpdatingView();
    }

    public void LoadSettings() {
        settings.load();
        boolean isResuming = settings.isResuming();
        boolean isStopped = settings.isStopped();
        if (isResuming) {
            inBtn.setEnabled(false);
            outBtn.setEnabled(true);
        } else {
            outBtn.setEnabled(false);
            inBtn.setEnabled(true);
        }
        if (isStopped) {
            stopBtn.setEnabled(false);
        } else {
            stopBtn.setEnabled(true);
        }

        updateView();
    }

    public void updateView() {
          elapsedTime.setText(settings.getCurrentDiff());
    }

    public void resumeStartTimer() {
        settings.resume();
        LoadSettings();
        startUpdatingView();
    }


    public void stopTimer() {
        pauseTimer();
        updateView();
        settings.stop();
        LoadSettings();
    }

    public void pauseTimer() {
        settings.pause();
        LoadSettings();
    }

    private void openSettingsActivity() {
//        startActivity(new Intent(this, SettingsActivity.class));
    }

    Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            updateView();
            if (settings.isResuming()) {
                updateTextViewHandler.postDelayed(updateTimerRunnable, 1000);
            }
        }
    };

    void startUpdatingView() {
        if (settings.isResuming() && !settings.isStopped()) {
            updateTimerRunnable.run();
        }
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
        if (item.getItemId() == R.id.action_settings) {
            openSettingsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
