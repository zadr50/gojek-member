package com.talagasoft.gojek.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.talagasoft.gojek.R;
import com.talagasoft.gojek.model.OrderRecord;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by compaq on 01/18/2017.
 */
public class TransactionAdapter implements ListAdapter {
    Context _context;
    String _hp;
    ArrayList<OrderRecord> _orders;
    TextView tanggal,amount,km,tujuan,jenis;

    private static LayoutInflater inflater=null;

    public TransactionAdapter(Context context, String mNomorHp) {
        this._context=context;
        this._hp=mNomorHp;
        _orders=new OrderRecord().getList(context,mNomorHp);
        inflater = ( LayoutInflater )_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override

    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {

        if(_orders.size()>0){
            return _orders.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return _orders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return _orders.get(i).get_id();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View vi = convertView;
        ViewHolder holder;
        if(vi==null){
            holder = new ViewHolder();
            vi = inflater.inflate(R.layout.order_row, null);
            holder.tanggal=(TextView)vi.findViewById(R.id.tanggal);
            holder.amount=(TextView)vi.findViewById(R.id.amount);
            holder.km=(TextView)vi.findViewById(R.id.km);
            holder.tujuan=(TextView)vi.findViewById(R.id.tujuan);
            holder.jenis=(TextView)vi.findViewById(R.id.jenis);
            vi.setTag( holder );
        } else {
            holder = (ViewHolder) vi.getTag();
        }


        OrderRecord order=new OrderRecord();
        order=_orders.get(position);
        if (order != null) {
            holder.jenis.setText(order.get_jenis());
            holder.tanggal.setText(order.get_tgl());
            DecimalFormat df = new DecimalFormat("###,###.##");
            holder.amount.setText("Rp. " + df.format(order.get_amount()));
            holder.km.setText("" + df.format(order.get_km()) + " Km");
            holder.tujuan.setText("" + order.get_to_lat() + "/" + order.get_to_lng());
        }
        return vi;
    }
    private class ViewHolder {
        TextView jenis,tanggal,amount,km,tujuan;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        if(_orders.size()>0){
            return _orders.size();
        } else {
            return 1;
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
