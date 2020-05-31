package com.talagasoft.gojek.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.talagasoft.gojek.libs.Global.NAMA_DB;
import static com.talagasoft.gojek.libs.Global.VERSI_DB;

/**
 * Created by andri on 03/26/2017.
 */

public class Helper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    public Helper(Context konteks, String nama, SQLiteDatabase.CursorFactory f, int versi) {
        super(konteks, nama, f, versi);
    }
    public Helper(Context konteks){
        super(konteks, NAMA_DB, null, VERSI_DB);
        try {
            this.db = getWritableDatabase();
        }
        catch (SQLiteException e) {
            this.db = getReadableDatabase();
        }
    }
    public void Execute(String sql){
        Log.d("Sql:",sql);
        this.db.execSQL(sql);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        create_table_item_master();
    }
    private void create_table_item_master() {
        String sql="";
        try {
            sql="create table item_master ( " +
                    "item_no text, item_name text null, description text," +
                    "client_no text, expire text, price double, icon_file text, join_date text, " +
                    "address text, city text, lat double, lon double, owner text, " +
                    "phone text, fax text, email text, icon integer, id integer," +
                    "rating integer,category text,saldo double)";
            this.db.execSQL(sql);
            sql="create table supplier ( " +
                    "supp_code text, supp_name text null, address text," +
                    "telp text, hp text, lat double, lon double, icon text, " +
                    "rating integer,owner text)";
            db.execSQL(sql);
            sql="create table order_header ( " +
                    "order_no integer,order_date text,status integer, amount double, lat double, lon double," +
                    "item_count integer,driver_hp text, lat_driver double, lon_driver double)";
            db.execSQL(sql);
            sql="create table order_items ( " +
                    "order_no integer, item_code text, item_name text,disc_prc float," +
                    "qty integer, price double, amount double,id integer,note text)";
            db.execSQL(sql);


        }
        catch (SQLiteException e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versiLama, int versiBaru) {
        String sql="";

        try {
            if(versiLama<=VERSI_DB){

            }
        }catch (SQLiteException e){
        }
    }
}