package com.talagasoft.gojek.model;

import android.content.Context;

import com.talagasoft.gojek.R;

import java.util.ArrayList;

/**
 * Created by compaq on 01/18/2017.
 */

public class OrderRecord {
    private String _tgl,_hp,_driver,_jenis;
    private long _id;
    private float _from_lat;
    private float _from_lng;
    private float _to_lat;
    private float _to_lng;
    private int _status;
    private Double _km;
    private double _amount;

    public String get_tgl() {
        return _tgl;
    }

    public void set_tgl(String _tgl) {
        this._tgl = _tgl;
    }

    public String get_hp() {
        return _hp;
    }

    public void set_hp(String _hp) {
        this._hp = _hp;
    }

    public String get_driver() {
        return _driver;
    }

    public void set_driver(String _driver) {
        this._driver = _driver;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public float get_from_lat() {
        return _from_lat;
    }

    public void set_from_lat(float _from_lat) {
        this._from_lat = _from_lat;
    }

    public float get_from_lng() {
        return _from_lng;
    }

    public void set_from_lng(float _from_lng) {
        this._from_lng = _from_lng;
    }

    public float get_to_lat() {
        return _to_lat;
    }

    public void set_to_lat(float _to_lat) {
        this._to_lat = _to_lat;
    }

    public float get_to_lng() {
        return _to_lng;
    }

    public void set_to_lng(float _to_lng) {
        this._to_lng = _to_lng;
    }

    public int get_status() {
        return _status;
    }

    public void set_status(int _status) {
        this._status = _status;
    }

    public Double get_km() {
        return _km;
    }

    public void set_km(Double _km) {
        this._km = _km;
    }

    public double get_amount() {
        return _amount;
    }

    public void set_amount(double _amount) {
        this._amount = _amount;
    }

    public ArrayList<OrderRecord> getList(Context _context, String mNomorHp) {
        String url=_context.getResources().getString(R.string.url_source);
        String mUrl = url + "order_trans_list.php?hp=" + mNomorHp + "&type_tran=1";

        ArrayList<OrderRecord> arOrder=new ArrayList<>();

        HttpXml web = new HttpXml(mUrl);
        web.getGroup("order");
        for(int i=0;i<web.getCount();i++){
            OrderRecord rc = new OrderRecord();
            rc.set_amount(web.getKeyIndexFloat(i,"amount"));
            rc.set_from_lat(web.getKeyIndexFloat(i,"from_lat"));
            rc.set_from_lng(web.getKeyIndexFloat(i,"from_lng"));
            rc.set_hp(web.getKeyIndex(i,"handphone"));
            rc.set_to_lat(web.getKeyIndexFloat(i,"to_lat"));
            rc.set_to_lng(web.getKeyIndexFloat(i,"to_lng"));
            rc.set_tgl(web.getKeyIndex(i,"tgl"));
            rc.set_km((double) web.getKeyIndexFloat(i,"km"));
            rc.set_jenis(web.getKeyIndex(i,"jenis"));
            arOrder.add(rc);
        }
        return arOrder;
    }

    public String get_jenis() {
        return _jenis;
    }

    public void set_jenis(String _jenis) {
        this._jenis = _jenis;
    }
}
