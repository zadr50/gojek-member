package com.talagasoft.gojek.model;

import android.content.Context;
import android.util.Log;

import com.talagasoft.gojek.R;
import com.talagasoft.gojek.database.Recordset;

import java.util.ArrayList;

/**
 * Created by andri on 04/25/2017.
 */

public class Supplier {
    private String _supp_code, _supp_name, _address, _owner, _telp, _hp, _icon;
    private double _lat, _lon;
    private int _rating;
    Context _context;
    String _url;
    private String TAG="Supplier";

    public Supplier(){

    }
    public Supplier(Context baseContext) {
        _context=baseContext;
        _url=_context.getResources().getString(R.string.url_source);
    }

    public boolean downloadUpdate() {
        boolean lOK=false;
        String url = _url + "supplier.php?cmd=list&type=newest";
        HttpXml web = new HttpXml(url);
        web.getGroup("item");
        for (int i = 0; i < web.getCount(); i++) {
            Supplier item=new Supplier(_context);
            item.set_supp_code(web.getKeyIndex(i,"supp_code"));
            item.set_supp_name(web.getKeyIndex(i,"supp_name"));
            item.set_address(web.getKeyIndex(i,"address"));
            item.set_telp(web.getKeyIndex(i,"phone"));
            item.set_hp(web.getKeyIndex(i,"hp"));
            item.set_lat(web.getKeyIndexFloat(i,"lat"));
            item.set_lon(web.getKeyIndexFloat(i,"lon"));
            item.set_icon(web.getKeyIndex(i,"icon"));
            item.set_owner(web.getKeyIndex(i,"owner"));
            if( !saveToLocal(item) ){
                Log.d(TAG,"downloadUpdate() unable to save.");
                lOK=false;
            }
        }

        return lOK;
    }

    private boolean saveToLocal(Supplier item) {
        boolean retVal=true;

        Recordset rstItem=new Recordset(_context);
        String sql="select * from supplier where supp_code='"+item.get_supp_code()+"'";
        rstItem.openRecordset(sql,"supplier","supp_code");
        if(rstItem.eof()){
            rstItem.addNew();
            rstItem.put("supp_code",item.get_supp_code());
        }
        rstItem.put("supp_name",item.get_supp_name());
        rstItem.put("address",item.get_address());
        rstItem.put("lat", String.valueOf(item.get_lat()));
        rstItem.put("lon", String.valueOf(item.get_lon()));
        rstItem.put("telp",item.get_telp());
        rstItem.put("hp",item.get_hp());
        rstItem.put("icon",item.get_icon());
        rstItem.put("owner", item.get_owner());
        rstItem.put("rating", String.valueOf(item.get_rating()));

        retVal=rstItem.save()>=0;
        return retVal;
    }
    private void setData(Supplier item,Recordset rstItem){
        item.set_supp_code(rstItem.get("supp_code"));
        item.set_supp_name(rstItem.get("supp_name"));
        item.set_address(rstItem.get("address"));
        item.set_lat(Double.parseDouble(rstItem.get("lat")));
        item.set_lon(Double.parseDouble(rstItem.get("lon")));
        item.set_telp(rstItem.get("telp"));
        item.set_hp(rstItem.get("hp"));
        item.set_icon(rstItem.get("icon"));
        item.set_rating(Integer.parseInt(rstItem.get("rating")));
        item.set_owner(rstItem.get("owner"));
    }
    public ArrayList<Supplier> getAll(){
        ArrayList<Supplier> retVal=new ArrayList();
        Recordset rstItem=new Recordset(_context);
        rstItem.openRecordset("select * from supplier","supplier","supp_code");
        if (!rstItem.eof()){
            for(int i=0;i<rstItem.cursor().getCount();i++){
                Supplier item=new Supplier(_context);
                setData(item,rstItem);
                retVal.add(item);
                rstItem.moveNext();
            }
        }
        return retVal;
    }

    public String get_supp_code() {
        return _supp_code;
    }

    public void set_supp_code(String _supp_code) {
        this._supp_code = _supp_code;
    }

    public String get_supp_name() {
        return _supp_name;
    }

    public void set_supp_name(String _supp_name) {
        this._supp_name = _supp_name;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_owner() {
        return _owner;
    }

    public void set_owner(String _owner) {
        this._owner = _owner;
    }

    public String get_telp() {
        return _telp;
    }

    public void set_telp(String _telp) {
        this._telp = _telp;
    }

    public String get_hp() {
        return _hp;
    }

    public void set_hp(String _hp) {
        this._hp = _hp;
    }

    public double get_lat() {
        return _lat;
    }

    public void set_lat(double _lat) {
        this._lat = _lat;
    }

    public double get_lon() {
        return _lon;
    }

    public void set_lon(double _lng) {
        this._lon = _lng;
    }

    public int get_rating() {
        return _rating;
    }

    public void set_rating(int _rating) {
        this._rating = _rating;
    }

    public String get_icon() {
        return _icon;
    }

    public void set_icon(String _icon) {
        this._icon = _icon;
    }
}
