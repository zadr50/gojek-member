package com.talagasoft.gojek;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.talagasoft.gojek.model.HttpXml;

import org.w3c.dom.Document;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WaitingDriver extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    TimerTask _task;
    final Handler _handler = new Handler();
    Timer _timer;
    int _order_id=0;
    boolean _driver_accepted=false;
    String _url="",_handphone="",_nama_driver="",_lokasi="",_user_id="";
    float _lat=0,_lng=0;
    SharedPreferences mSetting=null;
    int mMode = 0, MODE_CARI_AlAMAT = 0, MODE_WAIT_DRIVER = 1, MODE_ANTAR_BERSAMA = 2;


    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private Context _context=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waiting_driver);
        _context=getBaseContext();
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        _url=getResources().getString(R.string.url_source);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        mSetting = getSharedPreferences("setting_gojek", Context.MODE_PRIVATE);
        _order_id=mSetting.getInt("order_id",0);

        findViewById(R.id.batal_button).setOnTouchListener(mDelayHideTouchListener);
        Button cancel_button= (Button) findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = mSetting.edit();
                editor.putInt("mode", 0);
                editor.putFloat("tujuan_lat", 0);
                editor.putFloat("tujuan_lng", 0);
                editor.putString("tujuan_name", "");
                editor.putInt("order_id",0);
                editor.commit();
                finish();


            }
        });
        startTimer();
    }
    void startTimer(){
        if (_timer == null) {
            _timer = new Timer();
        }
        _task = new TimerTask() {
            public void run() {
                _handler.post(new Runnable() {
                    public void run() {
                    Log.d("statTimer", "Start timer ... ");
                    new OrderStatus().execute();
                    if(_driver_accepted){
                        SharedPreferences.Editor editor = mSetting.edit();
                        editor.putInt("mode", MODE_ANTAR_BERSAMA);
                        editor.putString("driver_handphone", _handphone);
                        editor.putString("driver_name", _nama_driver);
                        editor.putString("driver_user_id", _user_id);
                        editor.commit();
                        finish();
                        Toast.makeText(_context,"Ada driver yang siap nama "+_nama_driver,Toast.LENGTH_LONG);
                        if (_task != null) {
                            Log.d("TIMER", "timer stop");
                            if(_timer != null ) _timer.cancel();
                            _timer=null;
                        }

                    }
                    }
                });
            }};
        _timer.schedule(_task, 6000, 30000);
    }
    private class OrderStatus extends AsyncTask<Void, Integer, String>
    {

        @Override
        protected String doInBackground(Void... voids) {

            String url = _url + "order_status.php?id=" + _order_id;
            HttpXml web=new HttpXml();
            Document doc=web.GetUrl(url);
            if(doc != null) {
                _handphone=web.getKey("driver");
                if(_handphone!=""){
                    _driver_accepted=true;
                    _nama_driver=web.getKey("user_name");
                    _user_id=web.getKey("user_id");
                    _lat=web.getKeyFloat("lat");
                    _lng=web.getKeyFloat("lng");
                    _lokasi=web.getKey("lokasi");


                }
            }

            return null;
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
