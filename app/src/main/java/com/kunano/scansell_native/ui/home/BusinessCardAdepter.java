package com.kunano.scansell_native.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.business.Business;

import java.util.List;

public class BusinessCardAdepter extends RecyclerView.Adapter<BusinessCardAdepter.CardHolder>{
    private List<Business>businessesList;
    private HomeFragment homeFragment;
    private Context context;
    private HomeViewModel homeViewModel;

    public BusinessCardAdepter(List<Business> businessesList, HomeFragment homeFragment, HomeViewModel homeViewModel){
        this.context = homeFragment.getContext();
        this.businessesList = businessesList;
        this.homeFragment = homeFragment;
        this.homeViewModel = homeViewModel;
    }
    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_card_view_business, parent, false);
        return new CardHolder(view);
    }
    @Override
    public void onBindViewHolder(CardHolder holder, final int position) {
        Business businessData = businessesList.get(position);
        holder.title.setText(businessData.getBusinessName());
        holder.address.setText(businessData.getBusinessAddress());
        holder.card.setTag(String.valueOf(businessData.getBusinessId()));

    }
    @Override
    public int getItemCount() {
        return businessesList.size();
    }
    public static class CardHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView address;

        private ImageView unCheckedCircle;

        private  View card;

        public CardHolder(View itemView) {
            super(itemView);
            card = itemView;
            title = itemView.findViewById(R.id.titleTextView);
            address = itemView.findViewById(R.id.textViewDirection);
            unCheckedCircle = itemView.findViewById(R.id.checked_unchecked_image_view);
        }

    }
}