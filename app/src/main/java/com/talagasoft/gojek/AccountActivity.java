package com.talagasoft.gojek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.talagasoft.gojek.model.HttpXml;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mNoHp;
    private EditText mNama;
    private EditText mAlamat;
    private  EditText mPassword;
    private SharedPreferences mSetting=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);

        mSetting = getSharedPreferences(getResources().getString(R.string.setting), Context.MODE_WORLD_READABLE);

        mNoHp=(EditText) findViewById(R.id.handphone);
        mNama=(EditText) findViewById(R.id.nama);
        mAlamat=(EditText) findViewById(R.id.alamat);
        mPassword=(EditText) findViewById(R.id.password);
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
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.submit){
            if( saveAccount()){

            }
            finish();
        } else if (id==R.id.cancel){
            finish();
        }
    }

    public boolean saveAccount() {

        String mUrl=getResources().getString(R.string.url_source)+"account_save.php?handphone=" +
                mNoHp+"&alamat="+mAlamat.getText()+"&nama="+mNama.getText()+"&password="+mPassword.getText();

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
