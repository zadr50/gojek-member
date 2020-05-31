package com.talagasoft.gojek.model;

import android.content.Context;

import com.talagasoft.gojek.R;

/**
 * Created by compaq on 01/12/2017.
 */

public class SettingServer {
    Context _context;
    String _url;
    int _tarif=0;

    public  SettingServer(Context c){
        boolean ret=false;

        _context=c;
        _url=_context.getResources().getString(R.string.url_source);

        String mUrl=_url +"setting.php";
        HttpXml web=new HttpXml(mUrl);
        if(web != null){
            ret=true;
            _tarif= web.getKeyInt("tarif");
        }
    }
    public int tarif(){
        return _tarif;
    }

}
