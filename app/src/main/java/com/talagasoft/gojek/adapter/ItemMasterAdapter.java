package com.talagasoft.gojek.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.talagasoft.gojek.R;
import com.talagasoft.gojek.controller.ItemMasterController;
import com.talagasoft.gojek.model.ItemMaster;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by andri on 03/21/2017.
 */

public class ItemMasterAdapter implements ListAdapter {
    private Context mContext;
    private String mJenis,mSupplier;
    private static LayoutInflater inflater=null;
    ArrayList<ItemMaster> mItems=new ArrayList<>();

    public ItemMasterAdapter(Context vContext,String vJenis, String vSupp) {
        this.mContext=vContext;
        this.mJenis=vJenis;
        this.mSupplier=vSupp;
        inflater = ( LayoutInflater ) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems=new ItemMasterController(mContext).getByCatSupp(mJenis,mSupplier);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        ViewHolder holder;
        if(vi==null){
            holder = new ViewHolder();
            vi = inflater.inflate(R.layout.food_row, null);
            holder.nama=(TextView)vi.findViewById(R.id.txtItem);
            holder.client=(TextView)vi.findViewById(R.id.txtClient);
            holder.harga=(TextView)vi.findViewById(R.id.txtHarga);
            holder.description=(TextView)vi.findViewById(R.id.txtDesc);
            holder.icon =(ImageView)vi.findViewById(R.id.imgPhoto);
            vi.setTag( holder );
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        ItemMaster item = mItems.get(position);
        if (item  != null) {
            holder.nama.setText(item.getItemName());
            DecimalFormat df = new DecimalFormat("###,###.##");
            holder.harga.setText("Rp. " + df.format(item.getHarga()));
            holder.client.setText(item.getPemilik());
            holder.client.setVisibility(View.GONE);
            holder.description.setText(item.getKeterangan());
            if(!item.getIconFile().isEmpty()) {
                String iconFile=this.mContext.getResources().getString(R.string.url_source_images)+item.getIconFile();
                Picasso.with(this.mContext).load(iconFile).into(holder.icon);
            }
        }
        return vi;
    }
    private class ViewHolder {
        TextView nama,harga,client,description;
        ImageView icon;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        if(mItems.size()>0){
            return mItems.size();
        } else {
            return 1;
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
