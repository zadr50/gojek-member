package com.talagasoft.gojek;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.talagasoft.gojek.adapter.ItemMasterAdapter;
import com.talagasoft.gojek.model.ItemMaster;

public class DrugActivity extends AppCompatActivity {

    SharedPreferences mSetting;
    String _supp_code,_supp_name,_type_item,mNoHp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug);
        _supp_code = getIntent().getStringExtra("supp_code");
        _supp_name = getIntent().getStringExtra("supp_name");
        _type_item=getIntent().getStringExtra("type");
        mSetting = getSharedPreferences("setting_gojek", Context.MODE_PRIVATE);
        mNoHp=mSetting.getString("no_hp","");
        ListView listView = (ListView) findViewById(R.id.lstData);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ItemMaster item= (ItemMaster) adapterView.getAdapter().getItem(i);
                Toast.makeText(getBaseContext(),"selected item index " + item.getItemName(),Toast.LENGTH_LONG).show();
            }
        });
        Button cmdKeranjang=(Button)findViewById(R.id.cmdKeranjang);
        cmdKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.talagasoft.oc_member.FoodsCartActivity");
                intent.putExtra("supp_code",_supp_code);
                intent.putExtra("no_hp",mNoHp);
                startActivity(intent);

            }
        });


        String sJenis="health";
        listView.setAdapter(new ItemMasterAdapter(getBaseContext(),sJenis,_supp_code));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ItemMaster item = (ItemMaster) adapterView.getAdapter().getItem(i);
                //Toast.makeText(getBaseContext(),"selected item index " + item.getItemName(),Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = mSetting.edit();
                editor.putString("item_no",item.getItemNo());
                editor.putString("item_name",item.getItemName());
                editor.putInt("item_price",item.getHarga());
                editor.putFloat("item_lat",item.getLat());
                editor.putFloat("item_lon",item.getLon());
                editor.putString("item_icon",item.getIconFile());

                editor.commit();
                startActivity(new Intent("com.talagasoft.oc_member.ItemOrderActivity"));
            }
        });


    }
}
