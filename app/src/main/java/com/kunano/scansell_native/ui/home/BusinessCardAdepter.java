package com.kunano.scansell_native.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.business.Business;

public class BusinessCardAdepter extends ListAdapter<Business, BusinessCardAdepter.CardHolder> {
    OnclickBusinessCardListener onclickBusinessCardListener;

    private static DiffUtil.ItemCallback<Business> DIFF_CALLBACK = new DiffUtil.ItemCallback<Business>() {
        @Override
        public boolean areItemsTheSame(@NonNull Business oldItem, @NonNull Business newItem) {
            return oldItem.getBusinessId() == newItem.getBusinessId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Business oldItem, @NonNull Business newItem) {
            return oldItem.getBusinessName().endsWith(newItem.getBusinessName()) &&
                    oldItem.getBusinessAddress().equals(newItem.getBusinessAddress()) &&
                    oldItem.getCratingDate().equals(oldItem.getCratingDate());
        }
    };

    public BusinessCardAdepter(){
        super(DIFF_CALLBACK);

    }


    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.home_card_view_business, parent, false);
        return new CardHolder(view);
    }
    @Override
    public void onBindViewHolder(CardHolder holder, final int position) {
        Business businessData = getItem(position);
        holder.title.setText(businessData.getBusinessName());
        holder.address.setText(businessData.getBusinessAddress());
        holder.card.setTag(String.valueOf(businessData.getBusinessId()));
    }


    public class CardHolder extends RecyclerView.ViewHolder {
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if(onclickBusinessCardListener != null && position != RecyclerView.NO_POSITION){
                        onclickBusinessCardListener.onShortTap(getItem(position));
                    }
                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if(onclickBusinessCardListener != null && position != RecyclerView.NO_POSITION){
                        onclickBusinessCardListener.onLongTap(getItem(position));
                    }
                    return true;
                }
            });
        }

    }

    public interface OnclickBusinessCardListener{
        abstract void onShortTap(Business business);
        abstract void onLongTap(Business business);
    }



    public void setOnclickBusinessCardListener(OnclickBusinessCardListener onclickBusinessCardListener) {
        this.onclickBusinessCardListener = onclickBusinessCardListener;
    }

}