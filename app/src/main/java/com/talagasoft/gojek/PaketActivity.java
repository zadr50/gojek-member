package com.talagasoft.gojek;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.talagasoft.gojek.model.Paket;


public class PaketActivity extends AppCompatActivity {
    private String mNoHp="";
    private SharedPreferences mSetting;
    TextView txtNamaBarang,txtBerat,txtPanjang,txtLebar,txtTinggi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paket);

        txtNamaBarang=(TextView)findViewById(R.id.txtNamaBarang);
        txtBerat=(TextView)findViewById(R.id.txtBerat);
        txtPanjang=(TextView)findViewById(R.id.txtPanjang);
        txtLebar=(TextView)findViewById(R.id.txtLebar);
        txtTinggi=(TextView)findViewById(R.id.txtTinggi);

        mSetting = getSharedPreferences(getResources().getString(R.string.setting), Context.MODE_WORLD_READABLE);
        mNoHp=mSetting.getString("no_hp","0000");

        Button cmdCancel = (Button) findViewById(R.id.cmdCancel);
        cmdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button cmdSubmit = (Button) findViewById(R.id.cmdSubmit);
        cmdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sNamaBarang=txtNamaBarang.getText().toString();
                String sBerat=txtBerat.getText().toString();
                String sPanjang=txtPanjang.getText().toString();
                String sLebar=txtLebar.getText().toString();
                String sTinggi = txtTinggi.getText().toString();

                Paket paket=new Paket(getBaseContext());
                if( paket.Save(mNoHp,sNamaBarang,sBerat,sPanjang,sLebar,sTinggi) ) {
                    String msg="Sukses data paket anda sudah masuk, tunggu beberapa saat untuk verifikasi.";
                    Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getBaseContext(),paket.getError(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
