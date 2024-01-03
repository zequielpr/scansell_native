package com.kunano.scansell_native.ui.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.controllers.home.BusinessController;

import java.util.List;
import java.util.Map;

public class BusinessCardAdepter extends RecyclerView.Adapter<BusinessCardAdepter.CardHolder>{
    private List<Map<String, Object>> businessesList;
    private BusinessController businessController;
    LifecycleOwner homeLifecycleOwner;

    Context context;
    public BusinessCardAdepter(List<Map<String, Object>> businessesList, BusinessController businessController, LifecycleOwner homeLifecycleOwner, Context context){
        this.context = context;
        this.businessesList = businessesList;
        this.businessController = businessController;
        this.homeLifecycleOwner = homeLifecycleOwner;
    }
    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_card_view_business, parent, false);
        return new CardHolder(view);
    }
    @Override
    public void onBindViewHolder(CardHolder holder, final int position) {
        Map<String, Object> businessData = businessesList.get(position);
        holder.title.setText(businessData.get("name").toString());
        holder.address.setText(businessData.get("address").toString());
        holder.card.setTag(businessData.get("business_id").toString());

        //Visibility of the circle
        businessController.getUncheckedCircleVisibility().observe(homeLifecycleOwner, holder.unCheckedCircle::setVisibility );


        //Checked the touch card only
        businessController.getImageForTouchedCard().observe(homeLifecycleOwner, (data) -> holder.setCheckedImage(data, holder) );

        //Set image para todas las card
        businessController.getCircleForAllCards().observe(homeLifecycleOwner, holder.unCheckedCircle::setImageDrawable);
       holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                businessController.shortPressBusinessCard(view);
                // Handle card click event
                // For example, you can launch a new activity or perform any other action
            }
        });

       holder.card.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {
               businessController.longPressBusinessCard(view);
               return true;
           }
       });
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

        public void setCheckedImage(Map<String, Object> imageBusinessId, CardHolder holder){
            if(imageBusinessId.get("businessId") == holder.card.getTag().toString()){
                holder.unCheckedCircle.setImageDrawable((Drawable)imageBusinessId.get("checkedCircle"));
            }
        }
    }
}