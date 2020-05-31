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
import com.talagasoft.gojek.model.Supplier;
import java.util.ArrayList;

/**
 * Created by andri on 04/25/2017.
 */

public class SupplierAdapter implements ListAdapter {
    private Context mContext;
    private static LayoutInflater inflater=null;
    ArrayList<Supplier> mItems=new ArrayList<>();

    public SupplierAdapter(Context vContext) {
        this.mContext=vContext;
        inflater = ( LayoutInflater ) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems=new Supplier(mContext).getAll();
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
            holder = new SupplierAdapter.ViewHolder();
            vi = inflater.inflate(R.layout.food_row, null);
            holder.nama=(TextView)vi.findViewById(R.id.txtItem);
            holder.client=(TextView)vi.findViewById(R.id.txtClient);
            holder.harga=(TextView)vi.findViewById(R.id.txtHarga);
            holder.harga.setVisibility(View.GONE);
            holder.description=(TextView)vi.findViewById(R.id.txtDesc);
            holder.icon =(ImageView)vi.findViewById(R.id.imgPhoto);
            vi.setTag( holder );
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        Supplier item = mItems.get(position);
        if (item  != null) {
            holder.nama.setText(item.get_supp_name());
            holder.description.setText(item.get_address());
            if(!item.get_icon().isEmpty()) {
                String iconFile=this.mContext.getResources().getString(R.string.url_source_images)+item.get_icon();
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
