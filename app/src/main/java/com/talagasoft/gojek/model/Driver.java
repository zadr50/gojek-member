package com.talagasoft.gojek.model;

import android.content.Context;

import com.talagasoft.gojek.R;

/**
 * Created by compaq on 03/13/2016.
 */
public class Driver extends  User {

    private Context _context;
    private String _url;

    public Driver(Context context){
        super(context);
        _context=context;
        _url=_context.getResources().getString(R.string.url_source);

    }
    private User getDriverAccepted(String vHpPenumpang) {
        User penumpang=new User(_context);
        User driver=null;
        if ( penumpang.loadByPhoneJob(vHpPenumpang,"penumpang") ) {
            if ( penumpang.get_status() == 1){                      // 1 - penumpang lagi cari driver
                driver = new User(_context);
                if ( driver.loadByPhoneJob(penumpang.get_handphone(),"driver") ) {
                    if ( penumpang.get_driver() == driver.get_handphone()){
                        // bila nomor hp driver sama nomor hape driver penumpang berarti selected
                        return driver;
                    }
                }
            }
        }
        return null;
    }

}
