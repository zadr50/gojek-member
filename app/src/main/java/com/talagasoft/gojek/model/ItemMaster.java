package com.talagasoft.gojek.model;

import android.content.Context;

/**
 * Created by andri on 03/21/2017.
 */

public class ItemMaster {
    private String ItemName,Client,Alamat,Phone,Keterangan,Pemilik,Kota,JoinDate,ExpireDate;
    private String ItemNo,IconFile,Fax, Email, Category;
    private float Lat,Lon,Id;
    private int Rating,Harga,Saldo,Icon;
    private Context mContext;

    public ItemMaster(Context mContext) {
        this.mContext=mContext;

    }

    public ItemMaster(String sNama, String sClient, int nHarga, String sKet, int nIcon) {
        ItemName = sNama;
        Client = sClient;
        Harga = nHarga;
        Keterangan = sKet;
        Icon=nIcon;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getClient() {
        return Client;
    }

    public void setClient(String client) {
        Client = client;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }

    public String getPemilik() {
        return Pemilik;
    }

    public void setPemilik(String pemilik) {
        Pemilik = pemilik;
    }

    public String getKota() {
        return Kota;
    }

    public void setKota(String kota) {
        Kota = kota;
    }

    public String getJoinDate() {
        return JoinDate;
    }

    public void setJoinDate(String joinDate) {
        JoinDate = joinDate;
    }

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
    }

    public float getLat() {
        return Lat;
    }

    public void setLat(float lat) {
        Lat = lat;
    }

    public float getLon() {
        return Lon;
    }

    public void setLon(float lon) {
        Lon = lon;
    }

    public float getId() {
        return Id;
    }

    public void setId(float id) {
        Id = id;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public int getHarga() {
        return Harga;
    }

    public void setHarga(int harga) {
        Harga = harga;
    }

    public int getSaldo() {
        return Saldo;
    }

    public void setSaldo(int saldo) {
        Saldo = saldo;
    }

    public int getIcon() {
        return Icon;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }


    public String getIconFile() {
        return IconFile;
    }

    public void setIconFile(String iconFile) {
        IconFile = iconFile;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getFax() {
        return Fax;
    }

    public void setFax(String fax) {
        Fax = fax;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
