package by.vanopiano.timetracker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
    TextView elapsedTime;
    Button outBtn, inBtn, stopBtn;
    Settings settings;

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
//            stopBtn.setEnabled(false);
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
//        String ccc = String.format("%02d:%02d:%02d",
//                TimeUnit.MILLISECONDS.toHours(millis),
//                TimeUnit.MILLISECONDS.toMinutes(millis) -
//                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
//                TimeUnit.MILLISECONDS.toSeconds(millis) -
//                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

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
//        stopUpdatingView();
    }


    Handler updateTextViewHandler;


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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
