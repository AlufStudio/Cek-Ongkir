package com.codebreaker.rajaongkir;

import android.content.Context;
import android.icu.text.NumberFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterList extends ArrayAdapter<Ongkir> {
    Context mContext;
    ArrayList<Ongkir> list;

    int layout;

    public AdapterList(Context context, int textViewResourceId, ArrayList<Ongkir> list_params) {
        super(context, textViewResourceId, list_params);
        this.mContext = context;
        this.layout = textViewResourceId;
        this.list = list_params;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent)
// ;
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(layout, null);

            holder = new ViewHolder();
            holder.nama = (TextView) convertView.findViewById(R.id.adapter_nama);
            holder.desk = (TextView) convertView.findViewById(R.id.adapter_desk);
            holder.waktu = (TextView) convertView.findViewById(R.id.adapter_waktu);
            holder.harga = (TextView) convertView.findViewById(R.id.adapter_harga);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Ongkir m = list.get(position);
        if (null == m.getWaktu()) holder.waktu.setVisibility(View.GONE);

        holder.nama.setText(m.getNama());
        holder.waktu.setText(m.getWaktu() + " hari");
        holder.desk.setText(m.getDesc());
        holder.harga.setText("Rp "+m.getHarga());

        //NumberFormat formatter = NumberFormat.getCurrencyInstance();
        //holder.harga.setText(formatter.format(m.getHarga()));
        return convertView;
    }

    private class ViewHolder {
        TextView nama, desk, harga, waktu;
    }
}
