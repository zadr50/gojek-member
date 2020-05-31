package com.talagasoft.gojek.controller;

import android.content.Context;
import android.util.Log;

import com.talagasoft.gojek.R;
import com.talagasoft.gojek.database.Recordset;
import com.talagasoft.gojek.model.HttpXml;
import com.talagasoft.gojek.model.ItemMaster;

import java.util.ArrayList;

/**
 * Created by andri on 03/25/2017.
 */

public class ItemMasterController {
    Context _context;
    String _url;
    private String TAG="ItemMasterController";

    public ItemMasterController(Context baseContext) {
        _context=baseContext;
        _url=_context.getResources().getString(R.string.url_source);
    }

    public boolean downloadUpdate() {
        boolean lOK=false;
        String url = _url + "item_master.php?cmd=list&type=newest";
        HttpXml web = new HttpXml(url);
        web.getGroup("item");
        for (int i = 0; i < web.getCount(); i++) {
            ItemMaster item=new ItemMaster(_context);
            item.setItemNo(web.getKeyIndex(i,"item_no"));
            item.setAlamat(web.getKeyIndex(i,"address"));
            item.setItemName(web.getKeyIndex(i,"item_name"));
            item.setKeterangan(web.getKeyIndex(i,"description"));
            item.setClient(web.getKeyIndex(i,"client_no"));
            item.setExpireDate(web.getKeyIndex(i,"expire"));
            item.setHarga((int) web.getKeyIndexFloat(i,"price"));
            item.setIconFile(web.getKeyIndex(i,"icon_file"));
            item.setJoinDate(web.getKeyIndex(i,"join_date"));
            item.setKota(web.getKeyIndex(i,"city"));
            item.setLat(web.getKeyIndexFloat(i,"lat"));
            item.setLon(web.getKeyIndexFloat(i,"lon"));
            item.setPemilik(web.getKeyIndex(i,"owner"));
            //item.setIcon((web.getKeyIndexInt(i,"icon")));
            item.setCategory(web.getKeyIndex(i,"category"));

            if( !saveToLocal(item) ){
                Log.d(TAG,"downloadUpdate() unable to save.");
                lOK=false;
            }
        }

        return lOK;
    }

    private boolean saveToLocal(ItemMaster item) {
        boolean retVal=true;

        Recordset rstItem=new Recordset(_context);
        String sql="select * from item_master where item_no='"+item.getItemNo()+"'";
        rstItem.openRecordset(sql,"item_master","item_no");
        if(rstItem.eof()){
            rstItem.addNew();
            rstItem.put("item_no",item.getItemNo());
        }
        rstItem.put("item_name",item.getItemName());
        rstItem.put("address",item.getAlamat());
        rstItem.put("client_no",item.getClient());
        rstItem.put("expire",item.getExpireDate());
        rstItem.put("icon_file",item.getIconFile());
        rstItem.put("icon", String.valueOf(item.getIcon()));
        rstItem.put("join_date",item.getJoinDate());
        rstItem.put("description",item.getKeterangan());
        rstItem.put("city",item.getKota());
        rstItem.put("owner",item.getPemilik());
        rstItem.put("phone",item.getPhone());
        rstItem.put("price", String.valueOf(item.getHarga()));
        rstItem.put("id", String.valueOf(item.getId()));
        rstItem.put("lat", String.valueOf(item.getLat()));
        rstItem.put("lon", String.valueOf(item.getLon()));
        rstItem.put("rating", String.valueOf(item.getRating()));
        rstItem.put("saldo", String.valueOf(item.getSaldo()));
        rstItem.put("category",item.getCategory());

        retVal=rstItem.save()>=0;
        return retVal;
    }
    private void setData(ItemMaster item,Recordset rstItem){
        item.setItemNo(rstItem.get("item_no"));
        item.setItemName(rstItem.get("item_name"));
        item.setKeterangan(rstItem.get("description"));
        item.setAlamat(rstItem.get("address"));
        item.setExpireDate(rstItem.get("expire"));
        item.setClient(rstItem.get("client_no"));
        item.setIcon(Integer.parseInt(rstItem.get("icon")));
        item.setIconFile(rstItem.get("icon_file"));
        item.setJoinDate(rstItem.get("join_date"));
        item.setKota(rstItem.get("city"));
        item.setPemilik(rstItem.get("owner"));
        item.setPhone(rstItem.get("phone"));
        item.setFax(rstItem.get("fax"));
        item.setEmail(rstItem.get("email"));
        item.setCategory(rstItem.get("category"));
        item.setSaldo(rstItem.getInt("saldo"));
        item.setRating(rstItem.getInt("rating"));
        item.setId(Float.parseFloat(rstItem.get("id")));
        item.setLat(Float.parseFloat(rstItem.get("lat")));
        item.setLon(Float.parseFloat(rstItem.get("lon")));
        item.setHarga(rstItem.getInt("price"));
    }
    public ArrayList<ItemMaster> getAll(){
        ArrayList<ItemMaster> retVal=new ArrayList();
        Recordset rstItem=new Recordset(_context);
        rstItem.openRecordset("select * from item_master","item_master","item_no");
        if (!rstItem.eof()){
            for(int i=0;i<rstItem.cursor().getCount();i++){
                ItemMaster item=new ItemMaster(_context);
                setData(item,rstItem);
                retVal.add(item);
                rstItem.moveNext();
            }
        }
        return retVal;
    }


    public ArrayList<ItemMaster> getByCategory(String vCategory) {
        ArrayList<ItemMaster> retVal=new ArrayList();
        Recordset rstItem=new Recordset(_context);
        String sql="select * from item_master  where category='"+vCategory+"'";
        rstItem.openRecordset(sql,"item_master","item_no");
        if (!rstItem.eof()){
            for(int i=0;i<rstItem.cursor().getCount();i++){
                ItemMaster item=new ItemMaster(_context);
                setData(item,rstItem);
                retVal.add(item);
                rstItem.moveNext();
            }
        }
        return retVal;

    }

    public ArrayList<ItemMaster> getByCatSupp(String mJenis, String mSupplier) {
        ArrayList<ItemMaster> retVal=new ArrayList();
        Recordset rstItem=new Recordset(_context);
        String sql="select * from item_master  where category='"+mJenis+"'";
        if (mSupplier.isEmpty()==false)sql = sql + "  and owner='"+mSupplier+"'";
        rstItem.openRecordset(sql,"item_master","item_no");
        if (!rstItem.eof()){
            for(int i=0;i<rstItem.cursor().getCount();i++){
                ItemMaster item=new ItemMaster(_context);
                setData(item,rstItem);
                retVal.add(item);
                rstItem.moveNext();
            }
        }
        return retVal;

    }
}
