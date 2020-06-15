package com.health.myhealthapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * Created by User on 3/14/2017.
 */

public class MedicineListAdapter extends ArrayAdapter<Medicine> {

    private static final String TAG = "PersonListAdapter";

    private Context mContext;
    int mResource;

    /**
     * Holds variables in a View
     */


    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public MedicineListAdapter(Context context, int resource, ArrayList<Medicine> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource  = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String name = getItem(position).getName();
        String datetime = getItem(position).getAmount();
        String amount = getItem(position).getDatetime();

        //Create the person object with the information
        Medicine medicine = new Medicine(name,datetime,amount);


            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            TextView tvName = (TextView) convertView.findViewById(R.id.textView1);
            TextView tvBirthday = (TextView) convertView.findViewById(R.id.textView2);
            TextView tvSex = (TextView) convertView.findViewById(R.id.textView3);

            tvName.setText(name);
            tvBirthday.setText(datetime);
            tvSex.setText(amount);
        return convertView;
    }
}
