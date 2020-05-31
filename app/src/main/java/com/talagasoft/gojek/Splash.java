package com.talagasoft.gojek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by compaq on 02/29/2016.
 */
public class Splash extends Activity {
    SharedPreferences sharedPreferences = null;
    Boolean mLoggedIn=false;
    String mNama="";
    String mNoHp="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.setting), Context.MODE_WORLD_READABLE);
        mLoggedIn = sharedPreferences.getBoolean("logged_in", false);
        mNama = sharedPreferences.getString("nama", "Guest");
        mNoHp = sharedPreferences.getString("no_hp", "00000000");


        Thread logoTimer = new Thread() {
            public void run(){
                try {
                    int logoTimer = 0;
                    while (logoTimer < 3000) {
                        sleep(100);
                        logoTimer = logoTimer + 100;
                    };
//                    if(mLoggedIn) {
                        startActivity(new Intent("MainActivity"));
//                    } else {
//                        startActivity(new Intent("LoginActivity"));
//                    }

                }
                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                finally{
                    finish();
                }
            }
        };

        logoTimer.start();

    }

}
