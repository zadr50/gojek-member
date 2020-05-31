package com.talagasoft.gojek;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;


/**
 * Created by compaq on 03/02/2016.
 */
public class LegalNoticesActivity extends Activity

    {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legal);

        TextView legal=(TextView)findViewById(R.id.legal);

        //legal.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this));
    }
    }
