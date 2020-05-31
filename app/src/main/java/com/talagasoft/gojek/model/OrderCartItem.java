package com.talagasoft.gojek.model;

import android.content.Context;

/**
 * Created by andri on 04/28/2017.
 */

public class OrderCartItem {
    //table order_items
    private String _item_code, _item_name, _note,_icon;
    private float _disc_prc;
    private int _qty, _id;
    private double _price, _amount_item;
    Context _context;

    public OrderCartItem(Context context) {
        this._context=context;
    }


    public String get_item_code() {
        return _item_code;
    }

    public void set_item_code(String _item_code) {
        this._item_code = _item_code;
    }

    public String get_item_name() {
        return _item_name;
    }

    public void set_item_name(String _item_name) {
        this._item_name = _item_name;
    }

    public String get_note() {
        return _note;
    }

    public void set_note(String _note) {
        this._note = _note;
    }

    public float get_disc_prc() {
        return _disc_prc;
    }

    public void set_disc_prc(float _disc_prc) {
        this._disc_prc = _disc_prc;
    }

    public int get_qty() {
        return _qty;
    }

    public void set_qty(int _qty) {
        this._qty = _qty;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public double get_price() {
        return _price;
    }

    public void set_price(double _price) {
        this._price = _price;
    }

    public double get_amount_item() {
        return _amount_item;
    }

    public void set_amount_item(double _amount_item) {
        this._amount_item = _amount_item;
    }

    public String get_icon() {
        return _icon;
    }

    public void set_icon(String _icon) {
        this._icon = _icon;
    }
}
