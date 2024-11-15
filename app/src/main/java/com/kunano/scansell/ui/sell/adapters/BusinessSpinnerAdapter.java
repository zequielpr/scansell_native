package com.kunano.scansell.ui.sell.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kunano.scansell.model.Home.business.Business;

import java.util.List;

public class BusinessSpinnerAdapter extends ArrayAdapter<Business> {

    private LayoutInflater inflater;
    private List<Business> businesses;
    private Integer textColor;

    private int mResource;

    public BusinessSpinnerAdapter(Context context, int resource, List<Business> businesses, Integer textColor) {
        super(context, resource, businesses);
        inflater = LayoutInflater.from(context);
        this.businesses = businesses;
        this.mResource = resource;
        this.textColor = textColor;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent, true);
    }

    private View createItemView(int position, View convertView, ViewGroup parent, boolean isDropDownView) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(mResource, parent, false);
        }

        // Get the current business object
        Business business = businesses.get(position);

        // Set the name of the business in the TextView
        TextView nameTextView = view.findViewById(android.R.id.text1);

        if (isDropDownView){
            nameTextView.setTextColor(Color.BLACK);
        }else{
            nameTextView.setTextColor(textColor);
        }


        //nameTextView.setLinkTextColor(parent.getContext().getResources().getColor(R.color.white));
        nameTextView.setText(business.getBusinessName());

        return view;
    }

    public interface OnBusinessSelectedListener {
        void onBusinessSelected(Business business);
    }

}
