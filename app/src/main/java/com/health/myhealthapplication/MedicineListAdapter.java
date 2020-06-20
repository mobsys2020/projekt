package com.health.myhealthapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class MedicineListAdapter extends ArrayAdapter<Meds> {

    private Context mContext;
    int mResource;
    private List<Meds> medlist;


    public MedicineListAdapter(Context context, int resource, List<Meds> objects) {
        super(context, resource, objects);
        mContext = context;
        medlist = objects;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position,View convertView, ViewGroup parent){
        Log.i("TAG", "Adapter macht was ");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        Meds med = medlist.get(position);
        TextView tvName = (TextView) convertView.findViewById(R.id.textView1);
        TextView tvQuantity = (TextView) convertView.findViewById(R.id.textView2);
        TextView tvTime = (TextView) convertView.findViewById(R.id.textView3);
        TextView tvDays = (TextView) convertView.findViewById(R.id.textView4);

        tvName.setText(med.name);
        //name.setTextSize(textsize);
        tvQuantity.setText(med.quantity);
        //quantity.setTextSize(textsize);
        tvTime.setText(med.time);
        //time.setTextSize(textsize);
        tvDays.setText(med.days);
        //days.setTextSize(textsize);

        return convertView;

    }
}
