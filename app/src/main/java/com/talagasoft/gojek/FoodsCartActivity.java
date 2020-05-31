package com.talagasoft.gojek;

import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.talagasoft.gojek.adapter.FoodCartAdapter;
import com.talagasoft.gojek.controller.OrderCartController;
import com.talagasoft.gojek.database.Recordset;
import com.talagasoft.gojek.model.HttpXml;
import com.talagasoft.gojek.model.OrderCartItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FoodsCartActivity extends AppCompatActivity {
    TextView txtQty,txtItem,txtTotal,txtOngkos;
    Button cmdClear,cmdBelanja,cmdSubmit;
    ArrayList<OrderCartItem> mItems;
    String _supp_code,_no_hp,_type_item;
    TextView txtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods_cart);

        txtQty=(TextView)findViewById(R.id.txtQty);
        txtItem=(TextView)findViewById(R.id.txtItem);
        txtTotal=(TextView)findViewById(R.id.txtTotal);
        txtOngkos=(TextView)findViewById(R.id.txtOngkos);
        txtNote=(TextView)findViewById(R.id.txtNote);
        cmdClear=(Button)findViewById(R.id.cmdDelete);
        cmdBelanja=(Button)findViewById(R.id.cmdBelanja);
        cmdSubmit=(Button)findViewById(R.id.cmdSubmit);

        DecimalFormat df = new DecimalFormat("###,###.##");
        mItems=new OrderCartController(getBaseContext()).getAll();
        double vItem_amt=0, vOngkos=0, vAmount=0;
        int vQty=0;

        ListView lstData=(ListView)findViewById(R.id.lstData);
        ListAdapter listData=new FoodCartAdapter(getBaseContext(),mItems);
        lstData.setAdapter(listData);

        _supp_code = getIntent().getStringExtra("supp_code");
        _no_hp = getIntent().getStringExtra("no_hp");
        _type_item=getIntent().getStringExtra("type");

        for(int i=0;i<mItems.size();i++){
            vAmount+=mItems.get(i).get_amount_item();
            vQty+=mItems.get(i).get_qty();
        }
        txtItem.setText("Rp. " + df.format(vAmount));
        txtItem.setVisibility(View.GONE);
        txtTotal.setText("Rp. " + df.format(vAmount));
        txtOngkos.setText("Rp. " + df.format(vOngkos));
        txtOngkos.setVisibility(View.GONE);
        txtQty.setText(""+vQty);

        cmdClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                konfirmasi(view);
            }
        });
        cmdBelanja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cmdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitOrder();
            }
        });


    }
    private void submitOrder(){
        String url=getString(R.string.url_source)+"order_item_multi.php";
        String sNote= txtNote.getText().toString();
        JSONObject jsonObject=new JSONObject();

        HttpXml httpXml=new HttpXml();

        try {
            jsonObject.accumulate("no_hp",_no_hp);
            jsonObject.accumulate("supp_code",_supp_code);
            jsonObject.accumulate("note_header",sNote);
            for(int i=0;i<mItems.size();i++){
                    jsonObject.accumulate("item", mItems.get(i).get_item_code());
                    jsonObject.accumulate("qty", mItems.get(i).get_qty());
                    jsonObject.accumulate("note", mItems.get(i).get_note());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result=httpXml.postData(url,jsonObject);
        if(result.contains("success")){
            Toast.makeText(getBaseContext(),"Order anda sudah masuk, tunggu driver disekitar anda.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(),"Gagal masukan order anda , coba beberapa saat lagi." + result,
                    Toast.LENGTH_LONG).show();
        }

    }
    private void konfirmasi(View view){
        new android.app.AlertDialog.Builder(view.getContext())
                .setTitle("Konfimasi")
                .setMessage("Yakin kantong belanja mau dikosongkan? ")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(hapusKantongBelanja()){
                                    Toast.makeText(getBaseContext(),"Kantong belanja sudah dikosongkan.",Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(getBaseContext(),"Ada kesalahan hapus data belanjaan.",Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public boolean hapusKantongBelanja(){
        boolean ret=false;
        String sql="delete from order_items";
        Recordset r=new Recordset(getBaseContext());
        r.db().execSQL(sql);
        return true;
    }
}
