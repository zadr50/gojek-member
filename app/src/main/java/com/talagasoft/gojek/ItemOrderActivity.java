package com.talagasoft.gojek;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.talagasoft.gojek.controller.OrderCartController;
import com.talagasoft.gojek.libs.GPSTracker;
import com.talagasoft.gojek.model.HttpXml;

import java.text.DecimalFormat;

public class ItemOrderActivity extends AppCompatActivity {
    static SharedPreferences mSetting;
    String mItemNo,mItemName,mItemIcon,mNoHp;
    int mItemPrice,mItemQty,mItemTotal,mJarak;
    float mItemLat,mItemLon,mFromLat,mFromLon,mToLat,mToLon,mOngkos;
    TextView txtNama, txtTotal, txtHarga,txtQty,txtOngkos,txtJarak;
    TextView txtFromLoc,txtToLoc;
    String _url;
    private int mDepositAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_order);
        _url=getBaseContext().getResources().getString(R.string.url_source);
        mSetting = getSharedPreferences("setting_gojek", Context.MODE_PRIVATE);
        mNoHp=mSetting.getString("no_hp","");
        mItemNo=mSetting.getString("item_no","");
        mItemName=mSetting.getString("item_name","");
        mItemPrice=mSetting.getInt("item_price",0);
        mItemLat=mSetting.getFloat("item_lat",0);
        mItemLon=mSetting.getFloat("item_lon",0);
        mItemIcon=mSetting.getString("item_icon","");
        mToLat=mItemLat;
        mToLon=mItemLon;
        GPSTracker gps = new GPSTracker(getBaseContext());
        mFromLat = (float) gps.getLatitude();
        mFromLon = (float) gps.getLongitude();
        String locNameFrom=gps.getAddressName(new LatLng(mFromLat,mFromLon));
        String locNameTo=gps.getAddressName(new LatLng(mToLat,mToLon));
        mJarak = (int) gps.getDistanceKm(new LatLng(mFromLat,mFromLon),new LatLng(mToLat,mToLon));
        if(mJarak>5)mJarak=1;
        int mTarif = mSetting.getInt("tarif",1);
        mOngkos = (float) (mJarak*mTarif);


        mItemQty=1;
        mItemTotal=mItemPrice;

        txtNama = (TextView) findViewById(R.id.txtNama);
        txtNama.setText(mItemName);
        txtHarga = (TextView) findViewById(R.id.txtHarga);
        txtHarga.setText("Harga Rp. "+mItemPrice);

        txtQty = (TextView) findViewById(R.id.txtQty);
        txtJarak = (TextView) findViewById(R.id.txtJarak);
        txtJarak.setText("" + mJarak + " Km");
        txtOngkos = (TextView) findViewById(R.id.txtOngkos);
        txtOngkos.setText("Rp. "+mOngkos);

        mItemTotal = (int) (mItemPrice+mOngkos);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        txtTotal.setText("Rp. "+mItemTotal);

        reCalc();

        ImageView imgIcon=(ImageView)findViewById(R.id.imgIcon);

        if(!mItemIcon.isEmpty()){
            String iconFile=this.getResources().getString(R.string.url_source_images)+mItemIcon;
            Picasso.with(this).load(iconFile).into(imgIcon);
        }
        txtQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bHasFocus) {
                if(!bHasFocus){
                    reCalc();
                }
            }
        });
        Button cmdCancel=(Button)findViewById(R.id.cmdCancel);
        cmdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button cmdSubmit=(Button)findViewById(R.id.cmdSubmit);
        cmdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderCartController order=new OrderCartController(getBaseContext());
                if(!order.addItem(mItemNo,mItemName,mItemQty,mItemPrice,mItemTotal,"")){
                    Toast.makeText(getBaseContext(),"Gagal tambah item kedalam keranjang belanja.",
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getBaseContext(),"Sukses tambah item, silahkan dilihat keranjang belanja",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        String sDepo=mSetting.getString("deposit","0");
        if(sDepo.isEmpty())sDepo="0";
        mDepositAmount = Integer.parseInt(sDepo);

        DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"
        TextView txtDeposit=(TextView)findViewById(R.id.deposit);
        txtDeposit.setText(df.format(mDepositAmount));


    }
    private boolean submitToServer(){
        boolean ret=false;
        //untuk makanan tidak dicek deposit, akan ditalangin dulu sama drivernya
        mItemQty = Integer.parseInt(txtQty.getText().toString());
        String mUrl=_url +"order_item_new.php?handphone=" + mNoHp + "&item_no="+mItemNo
                +"&item_name="+mItemName+"&qty="+mItemQty+"&price="+mItemPrice+"&total="+mItemTotal
                +"&from_lat="+mFromLat+"&from_lon="+mFromLon+"&to_lat="+mToLat+"&to_lon="+mToLon
                +"&jarak="+mJarak+"&ongkos="+mOngkos;
        if(new HttpXml(mUrl).Success()){
            Toast.makeText(getBaseContext(),"Pesanan anda sudah masuk, tunggu sampai ada driver yang " +
                    "mengantar pesanan anda, terimakasih.",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(),"Gagal menyimpan pesanan anda coba lagi bebeapa saat.",Toast.LENGTH_LONG).show();
        }

        return ret;
    }
    private void reCalc(){
        String sQty=txtQty.getText().toString();
        if(sQty.isEmpty())txtQty.setText("0");
        mItemQty= Integer.parseInt(txtQty.getText().toString());
        mItemTotal = (int) ((mItemPrice*mItemQty)+mOngkos);

        DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"
        txtTotal.setText("Rp. "+df.format(mItemTotal));
    }

}
