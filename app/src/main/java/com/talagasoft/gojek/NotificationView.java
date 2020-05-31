package com.talagasoft.gojek;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by andri on 03/29/2017.
 */

public class NotificationView extends Activity {
    SharedPreferences mSetting = null;
    TextView txtMessage;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);
        mSetting = getSharedPreferences(getResources().getString(R.string.setting), Context.MODE_WORLD_READABLE);
        String hpDiver,namaDriver;
        hpDiver=mSetting.getString("driver_handphone","");
        namaDriver=mSetting.getString("driver_name","");
        txtMessage=(TextView)findViewById(R.id.txtMessage);
        txtMessage.setText("Ada driver yang accept Nama: "+namaDriver+", Handphone: "+hpDiver);
    }
}
