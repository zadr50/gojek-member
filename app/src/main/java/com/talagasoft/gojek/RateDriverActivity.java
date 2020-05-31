package com.talagasoft.gojek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.talagasoft.gojek.model.Driver;

/**
 * Created by compaq on 01/22/2017.
 */

public class RateDriverActivity extends AppCompatActivity implements View.OnClickListener   {
    TextView txtNama,txtKomentar;
    RatingBar rateBar;
    String fromHp,toHp;
    SharedPreferences mSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_driver);
        txtNama = (TextView) findViewById(R.id.txtNama);
        txtKomentar = (TextView) findViewById(R.id.txtKomentar);
        rateBar = (RatingBar) findViewById(R.id.ratingBar);

        mSetting = getSharedPreferences("setting_gojek", Context.MODE_PRIVATE);

        fromHp = mSetting.getString("no_hp", "0000");
        toHp = mSetting.getString("driver_handphone", "0000");
        txtNama.setText(mSetting.getString("driver_name","Guest"));

        Button cmdSubmit = (Button) findViewById(R.id.btnSubmit);
        cmdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Driver driver = new Driver(getBaseContext());

                if (driver.updateRating(fromHp, toHp, txtKomentar.getText().toString(), rateBar.getNumStars())) {

                    Toast.makeText(getBaseContext(), "Terimakasih..", Toast.LENGTH_LONG).show();

                    //reset tujuan
                    SharedPreferences.Editor editor = mSetting.edit();
                    editor.putFloat("tujuan_lat", 0);
                    editor.putFloat("tujuan_lng", 0);
                    editor.putString("tujuan_name", "");
                    editor.putString("driver_handphone","");
                    editor.putString("driver_name","");
                    editor.putInt("mode",0);    //cari alamat lagi
                    editor.commit();


                    finish();
                }

            }
        });
        Button cmdCancel = (Button) findViewById(R.id.btnCancel);
        cmdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View view) {

    }
}
