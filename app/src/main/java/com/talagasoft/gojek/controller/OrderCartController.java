package com.talagasoft.gojek.controller;

import android.content.Context;

import com.talagasoft.gojek.database.Recordset;
import com.talagasoft.gojek.model.OrderCartItem;

import java.util.ArrayList;

/**
 * Created by andri on 04/28/2017.
 */

public class OrderCartController {
    //table order_header
    private int _order_no, _status, _item_count;
    private String _order_date,_driver_hp;
    private double _amount,_lat, _lon, _lat_driver,_lon_driver;
    //varable
    Context _context;

    public OrderCartController(Context vContext){
        this._context=vContext;
    }
    public boolean submitToServer(){
        boolean ret=false;


        return ret;
    }
    public boolean Save(){
        boolean ret=false;
        Recordset r=new Recordset(_context);
        String sql="select * from order_header where order_no="+this._order_no;
        r.openRecordset(sql,"item_master","item_no");
        if(r.eof()){
            r.addNew();
            r.put("order_no", String.valueOf(this._order_no));
        }
        r.put("order_date",this._order_date);
        r.put("status", String.valueOf(this._status));
        r.put("amount", String.valueOf(this._amount));
        r.put("lat", String.valueOf(this._lat));
        r.put("lon", String.valueOf(this._lon));
        r.put("item_count", String.valueOf(this._item_count));
        r.put("driver_hp",this._driver_hp);
        r.put("lat_driver", String.valueOf(this._lat_driver));
        r.put("lon_driver", String.valueOf(this._lon_driver));
        ret=r.save()>=0;
        return ret;
    }
    public boolean addItem(String vItemNo,String vItemName,int vQty,int vPrice,double vAmount,
                           String vNote){
        boolean ret=false;
        Recordset r=new Recordset(_context);
        String sql="select * from order_items where order_no="+this._order_no;
        r.openRecordset(sql,"order_items","order_no");
        //if(r.eof()){
            r.addNew();
            r.put("order_no", String.valueOf(this._order_no));
        //}
        r.put("item_code",vItemNo);
        r.put("item_name",vItemName);
        r.put("qty", String.valueOf(vQty));
        r.put("price", String.valueOf(vPrice));
        r.put("disc_prc", "0");
        r.put("amount", String.valueOf(vAmount));
        r.put("note",vNote);
        ret=r.save()>=0;

        return ret;
    }

    public int get_order_no() {
        return _order_no;
    }

    public void set_order_no(int _order_no) {
        this._order_no = _order_no;
    }

    public int get_status() {
        return _status;
    }

    public void set_status(int _status) {
        this._status = _status;
    }

    public int get_item_count() {
        return _item_count;
    }

    public void set_item_count(int _item_count) {
        this._item_count = _item_count;
    }

    public String get_order_date() {
        return _order_date;
    }

    public void set_order_date(String _order_date) {
        this._order_date = _order_date;
    }

    public String get_driver_hp() {
        return _driver_hp;
    }

    public void set_driver_hp(String _driver_hp) {
        this._driver_hp = _driver_hp;
    }

    public double get_amount() {
        return _amount;
    }

    public void set_amount(double _amount) {
        this._amount = _amount;
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

    public void set_lon(double _lon) {
        this._lon = _lon;
    }

    public double get_lat_driver() {
        return _lat_driver;
    }

    public void set_lat_driver(double _lat_driver) {
        this._lat_driver = _lat_driver;
    }

    public double get_lon_driver() {
        return _lon_driver;
    }

    public void set_lon_driver(double _lon_driver) {
        this._lon_driver = _lon_driver;
    }

    public ArrayList<OrderCartItem> getAll() {
        ArrayList<OrderCartItem> retVal=new ArrayList();
        Recordset r=new Recordset(_context);
        String sql="select o.*,i.description,i.icon_file from order_items o " +
                "left join item_master i on i.item_no=o.item_code";
        r.openRecordset(sql,"order_item","order_no");
        if (!r.eof()){
            for(int i=0;i<r.cursor().getCount();i++){
                OrderCartItem item=new OrderCartItem(_context);
                item.set_item_code(r.get("item_code"));
                item.set_item_name(r.get("item_name"));
                item.set_note(r.get("description"));
                item.set_icon(r.get("icon_file"));
                item.set_qty(Integer.parseInt(r.get("qty")));
                item.set_price(Double.parseDouble(r.get("price")));
                item.set_disc_prc(Float.parseFloat(r.get("disc_prc")));
                item.set_amount_item(Double.parseDouble(r.get("amount")));
                retVal.add(item);
                r.moveNext();
            }
        }
        return retVal;
    }
}
