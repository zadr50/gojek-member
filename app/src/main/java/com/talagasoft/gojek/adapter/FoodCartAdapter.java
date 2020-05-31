package com.talagasoft.gojek.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.talagasoft.gojek.R;
import com.talagasoft.gojek.controller.OrderCartController;
import com.talagasoft.gojek.model.OrderCartItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by andri on 04/28/2017.
 */

public class FoodCartAdapter  implements ListAdapter {
    private Context mContext;
    private static LayoutInflater inflater=null;
    ArrayList<OrderCartItem> mItems=new ArrayList<>();

    public FoodCartAdapter(Context vContext) {
        this.mContext=vContext;
        inflater = ( LayoutInflater ) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems=new OrderCartController(mContext).getAll();

    }

    public FoodCartAdapter(Context vContext, ArrayList<OrderCartItem> mItems) {
        this.mContext=vContext;
        inflater = ( LayoutInflater ) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItems=mItems;
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
            holder.qty=(TextView)vi.findViewById(R.id.txtClient);
            holder.harga=(TextView)vi.findViewById(R.id.txtHarga);
            holder.harga.setVisibility(View.GONE);
            holder.amount=(TextView)vi.findViewById(R.id.txtAmount);
            holder.note=(TextView)vi.findViewById(R.id.txtDesc);
            holder.icon =(ImageView)vi.findViewById(R.id.imgPhoto);
            holder.divTotal=(LinearLayout)vi.findViewById(R.id.dvTotal);
            holder.divTotal.setVisibility(View.VISIBLE);
            vi.setTag( holder );
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        OrderCartItem item = mItems.get(position);
        if (item  != null) {
            holder.nama.setText(item.get_item_name());
            DecimalFormat df = new DecimalFormat("###,###.##");
            holder.harga.setText("Rp. " + df.format(item.get_amount_item()));
            holder.qty.setText(item.get_qty()+" x "+"Rp. " + df.format(item.get_price()));
            holder.note.setText(item.get_note());
            holder.amount.setText(""+df.format(item.get_amount_item()));
            if(!item.get_icon().isEmpty()) {
                String iconFile=this.mContext.getResources().getString(R.string.url_source_images)+item.get_icon();
                Picasso.with(this.mContext).load(iconFile).into(holder.icon);
            }
        }
        return vi;
    }


    private class ViewHolder {
        TextView nama,harga,qty,amount,note;
        ImageView icon;
        LinearLayout divTotal;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
            return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
