package com.talagasoft.gojek.model;

import android.content.Context;

import com.talagasoft.gojek.R;

/**
 * Created by andri on 06/08/2017.
 */

public class Paket {

    Context _context;
    String _msg="";
    String _url="";

    public Paket(Context c) {
        _context=c;
        _url=_context.getResources().getString(R.string.url_source);
    }
    public Paket(){
        _url=_context.getResources().getString(R.string.url_source);
    }


    public boolean Save(String mNoHp, String sNamaBarang, String sBerat, String sPanjang,
                        String sLebar, String sTinggi) {

        String mUrl=_url +"paket_save.php?handphone=" +
                mNoHp+"&berat="+sBerat+"&panjang="+sPanjang+"&lebar="+sLebar +
                "&tinggi="+sTinggi+"&nama="+sNamaBarang;

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

}
