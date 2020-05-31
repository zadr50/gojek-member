package com.talagasoft.gojek;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.talagasoft.gojek.adapter.SupplierAdapter;
import com.talagasoft.gojek.model.Supplier;

public class SupplierActivity extends AppCompatActivity {
    ListView lstData;
    String mType="bike";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        lstData = (ListView) findViewById(R.id.lstData);
        mType=getIntent().getStringExtra("type");

        SupplierAdapter adapter=new SupplierAdapter(getBaseContext());

        lstData.setAdapter(adapter);
        lstData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Supplier supplier= (Supplier) adapterView.getAdapter().getItem(i);
                Intent intent;

                if(mType.contains("drug")) {
                    intent = new Intent("DrugActivity");
                } else {
                    intent=new Intent("FoodsActivity");
                }

                intent.putExtra("supp_code",supplier.get_supp_code());
                intent.putExtra("supp_name",supplier.get_supp_name());
                intent.putExtra("type",mType);
                startActivity(intent);
            }
        });

    }
}
