package com.kunano.scansell_native.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;

import java.util.List;
import java.util.Map;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>{
    private List<Map<String, Object>> businessesList;
    Context context;
    public CustomAdapter(List<Map<String, Object>> businessesList, Context context){
        this.context = context;
        this.businessesList = businessesList;
    }
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_item, parent, false);
        return new CustomAdapter.MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(CustomAdapter.MyViewHolder holder, final int position) {
        Map<String, Object> businessData = businessesList.get(position);
        holder.title.setText(businessData.get("name").toString());
        holder.direction.setText(businessData.get("direction").toString());
    }
    @Override
    public int getItemCount() {
        return businessesList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView direction;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTextView);
            direction = itemView.findViewById(R.id.textViewDirection);
        }
    }
}