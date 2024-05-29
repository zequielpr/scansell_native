package com.kunano.scansell.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell.model.Home.business.Business;
import com.kunano.scansell.R;

public class BusinessCardAdepter extends ListAdapter<Business, BusinessCardAdepter.CardHolder> {
    private OnclickBusinessCardListener listener;

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

        listener.getCardHolderOnBind(holder, businessData);
    }


    public class CardHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView address;

        private ImageView unCheckedCircle;

        private ImageButton imageButtonRestore;
        private TextView numProducts;
        private CardView card;

        public CardHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardView);
            title = itemView.findViewById(R.id.titleTextView);
            address = itemView.findViewById(R.id.textViewDirection);
            unCheckedCircle = itemView.findViewById(R.id.checked_unchecked_image_view);
            imageButtonRestore = itemView.findViewById(R.id.imageButtonRestoreFromTrash);
            numProducts = itemView.findViewById(R.id.textViewNumProducts);


            listener.reciveCardHol(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onShortTap(getItem(position), CardHolder.this);
                    }
                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onLongTap(getItem(position), CardHolder.this);
                    }
                    return true;
                }
            });

            imageButtonRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onRestore(getItem(position));
                    }
                }
            });
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public TextView getAddress() {
            return address;
        }

        public void setAddress(TextView address) {
            this.address = address;
        }

        public ImageView getUnCheckedCircle() {
            return unCheckedCircle;
        }

        public void setUnCheckedCircle(ImageView unCheckedCircle) {
            this.unCheckedCircle = unCheckedCircle;
        }

        public ImageButton getImageButtonRestore() {
            return imageButtonRestore;
        }

        public void setImageButtonRestore(ImageButton imageButtonRestore) {
            this.imageButtonRestore = imageButtonRestore;
        }

        public CardView getCard() {
            return card;
        }

        public void setCard(CardView card) {
            this.card = card;
        }

        public TextView getNumProducts() {
            return numProducts;
        }

        public void setNumProducts(TextView numProducts) {
            this.numProducts = numProducts;
        }
    }

    public interface OnclickBusinessCardListener{
        abstract void onShortTap(Business business, CardHolder cardHolder);
        abstract void onLongTap(Business business, CardHolder cardHolder);
        abstract void getCardHolderOnBind(CardHolder cardHolder, Business business);
        abstract void reciveCardHol(CardHolder cardHolder);
        abstract void onRestore(Business business);

    }


    public void setListener(OnclickBusinessCardListener listener) {
        this.listener = listener;
    }

}