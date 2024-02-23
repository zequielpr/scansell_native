package com.kunano.scansell_native.ui.sell.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kunano.scansell_native.model.Home.business.Business;

import java.util.List;

public class BusinessSpinnerAdapter extends ArrayAdapter<Business> {

    private LayoutInflater inflater;
    private List<Business> businesses;
    private OnBusinessSelectedListener listener;

    public BusinessSpinnerAdapter(Context context, int resource, List<Business> businesses) {
        super(context, resource, businesses);
        inflater = LayoutInflater.from(context);
        this.businesses = businesses;
    }

    public void setOnBusinessSelectedListener(OnBusinessSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        // Get the current business object
        Business business = businesses.get(position);

        // Set the name of the business in the TextView
        TextView nameTextView = view.findViewById(android.R.id.text1);
        nameTextView.setText(business.getBusinessName());

        return view;
    }

    public interface OnBusinessSelectedListener {
        void onBusinessSelected(Business business);
    }


    public OnBusinessSelectedListener getListener() {
        return listener;
    }

    public void setListener(OnBusinessSelectedListener listener) {
        this.listener = listener;
    }
}
