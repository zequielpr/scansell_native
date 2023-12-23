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
        View view = inflater.inflate(R.layout.home_card_view_business, parent, false);
        return new CustomAdapter.MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(CustomAdapter.MyViewHolder holder, final int position) {
        Map<String, Object> businessData = businessesList.get(position);
        holder.title.setText(businessData.get("name").toString());
        holder.direction.setText(businessData.get("direction").toString());
        holder.card.setTag(businessData.get("business_id").toString());
       holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("id " + holder.card.getTag());
                // Handle card click event
                // For example, you can launch a new activity or perform any other action
            }
        });
    }
    @Override
    public int getItemCount() {
        return businessesList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView direction;

        private  View card;

        public MyViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            title = itemView.findViewById(R.id.titleTextView);
            direction = itemView.findViewById(R.id.textViewDirection);
        }
    }
}