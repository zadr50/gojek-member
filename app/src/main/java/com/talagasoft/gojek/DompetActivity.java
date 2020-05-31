package com.talagasoft.gojek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.talagasoft.gojek.adapter.TransactionAdapter;
import com.talagasoft.gojek.model.Deposit;

import java.text.DecimalFormat;

public class DompetActivity extends AppCompatActivity {
    TextView txtSaldo;
    ListView lstTrans;
    String mNama,mNomorHp;
    SharedPreferences mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dompet_activity);
        txtSaldo=(TextView)findViewById(R.id.txtSaldo);
        lstTrans=(ListView)findViewById(R.id.lstTrans);

        mSetting = getSharedPreferences("setting_gojek", Context.MODE_PRIVATE);
        mNama=mSetting.getString("nama", "Guest");
        mNomorHp=mSetting.getString("no_hp", "0000000000");
        int mDeposit=new Deposit(this).Saldo(mNomorHp);
        DecimalFormat df = new DecimalFormat("###,###.##");
        txtSaldo.setText(df.format(mDeposit));
        TransactionAdapter transactionAdapter=new TransactionAdapter(this,mNomorHp);
        lstTrans.setAdapter(transactionAdapter);
    }
}
