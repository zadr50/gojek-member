package com.talagasoft.gojek.model;

import android.content.Context;

import com.talagasoft.gojek.R;

/**
 * Created by compaq on 01/11/2017.
 */

public class Deposit {

    Context _context;
    String _msg="";
    String _url="";

    public Deposit(Context c) {
        _context=c;
        _url=_context.getResources().getString(R.string.url_source);
    }
    public Deposit(){
        _url=_context.getResources().getString(R.string.url_source);
    }


    public boolean Save(String mNoHp,String mBank,String mNama,int mJumlah) {

        String mUrl=_url +"deposit_save.php?handphone=" +
                mNoHp+"&bank="+mBank+"&nama="+mNama+"&jumlah="+mJumlah;

        boolean lTrue=false;
        _msg="";
        HttpXml web=new HttpXml();
        StringBuilder doc=web.GetUrlData(mUrl);
        if(doc != null) {
            if(doc.toString().contains("success")) {
                lTrue=true;
            }
            _msg=doc.toString();
        }
        return lTrue;
    }
    public String getError(){
        return _msg;
    }

    public int Saldo(String mNoHp){
        String mUrl=_url +"deposit_saldo.php?handphone="+mNoHp;
        HttpXml web=new HttpXml(mUrl);
        int saldo=0;
        String s=web.getKey("saldo");
        if(s!="")saldo=Integer.parseInt(s);
        return saldo;
    }
}
