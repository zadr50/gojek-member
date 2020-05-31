package com.talagasoft.gojek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.talagasoft.gojek.model.HttpXml;

/**
 * Created by compaq on 01/07/2017.
 */

public class JemputActivity  extends AppCompatActivity implements View.OnClickListener {

    EditText mNoHp;
    EditText mNama;
    EditText mAlamatAsal;
    EditText mAlamatTujuan;
    EditText mJam;
    EditText mBerat;
    SharedPreferences mSetting=null;
    RadioGroup mJenis;
    String mJenisText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jemput_activity);


        mSetting = getSharedPreferences(getResources().getString(R.string.setting), Context.MODE_WORLD_READABLE);

        mJenis = (RadioGroup) findViewById(R.id.jenis);
        mNoHp=(EditText) findViewById(R.id.handphone);
        mNama=(EditText) findViewById(R.id.nama);
        mAlamatAsal=(EditText) findViewById(R.id.alamat_asal);
        mAlamatTujuan=(EditText) findViewById(R.id.alamat_tujuan);
        mJam=(EditText) findViewById(R.id.jam);
        mBerat=(EditText) findViewById(R.id.berat);
        mJenisText="orang";

        mNama.setText(mSetting.getString("nama", "Guest"));
        mNama.setEnabled(false);
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        if(b!=null) {
            mNoHp.setText(b.getString("no_hp"));
        }
        if(mNoHp.getText().toString()==""){
            mNoHp.setText(mSetting.getString("no_hp","0000"));
        }
        mNoHp.setEnabled(false);


        mJenis.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.orang:
                        // do operations specific to this selection
                        mJenisText="orang";
                        break;
                    case R.id.barang:
                        // do operations specific to this selection
                        mJenisText="barang";
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch(id){
            case R.id.batal:
                // do operations specific to this selection
                finish();
                break;
            case R.id.simpan:
                // do operations specific to this selection
                if ( saveJemput() ) {
                    finish();
                }
                break;
        }
    }

    private boolean saveJemput() {

        String mUrl=getResources().getString(R.string.url_source)+"jemput_save.php?handphone=" +
                mNoHp.getText()+"&asal="+mAlamatAsal.getText()+"&tujuan="+mAlamatTujuan.getText() +
                "&nama="+mNama.getText()+"&berat="+mBerat.getText()+"&jam="+mJam.getText()+
                "&jenis="+mJenisText;

        boolean lTrue=false;
        String msg="Error simpan data !";
        HttpXml web=new HttpXml();
        StringBuilder doc=web.GetUrlData(mUrl);
        if(doc != null) {
            if(doc.toString().contains("success")) {
                msg="Sukses data sudah masuk.";
                Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
                lTrue=true;
            }
            msg=doc.toString();
        }
        if(!lTrue) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        return lTrue;
    }
}
