package com.kunano.scansell_native.ui.home.business;

import android.content.ContentResolver;
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
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.ui.ImageProcessor;

public class ProductCardAdapter extends ListAdapter<Product, ProductCardAdapter.CardHolder> {
    OnclickProductCardListener listener;
    ContentResolver contentResolver;

    private static DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getProductId()== newItem.getProductId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getProductName().equals(newItem.getProductName()) &&
                    oldItem.getCratingDate().equals(newItem.getCratingDate()) &&
                    oldItem.getImg() == oldItem.getImg();
        }
    };

    protected ProductCardAdapter() {
        super(DIFF_CALLBACK);
    }


    @Override
    public ProductCardAdapter.CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       contentResolver = parent.getContext().getContentResolver();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_card, parent, false);
        return new CardHolder(view);
    }




    @Override
    public void onBindViewHolder(CardHolder holder, final int position) {
        Product product = getItem(position);
        holder.title.setText(product.getProductName());
        holder.stock.setText(Integer.toString(product.getStock()));
        holder.sellingPrice.setText(Double.toString(product.getSelling_price()));
        holder.buyingPrice.setText(Double.toString(product.getBuying_price()));
        holder.imageViewProduct.setImageBitmap(ImageProcessor.bytesToBitmap(product.getImg()));

        holder.card.setTag(String.valueOf(product.getProductId()));

        listener.getCardHolderOnBind(holder.itemView, product);
    }


    public class CardHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView stock;
        private TextView sellingPrice;
        private TextView buyingPrice;

        private ImageView imageViewProduct;
        private ImageView unCheckedCircle;

        private  View card;

        public CardHolder(View itemView) {
            super(itemView);
            card = itemView;

            title = itemView.findViewById(R.id.textViewTitleProduct);
            stock = itemView.findViewById(R.id.textViewStock);
            sellingPrice = itemView.findViewById(R.id.textViewSellingPrice);
            buyingPrice = itemView.findViewById(R.id.textViewBuyingPrice);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);

            //unCheckedCircle = itemView.findViewById(R.id.checked_unchecked_image_view);

            listener.reciveCardHol(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onShortTap(getItem(position), itemView);
                    }
                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onLongTap(getItem(position), itemView);
                    }
                    return true;
                }
            });
        }

    }




    public void setListener(OnclickProductCardListener listener) {
        this.listener = listener;
    }



    public interface OnclickProductCardListener{
        abstract void onShortTap(Product business, View cardHolder);
        abstract void onLongTap(Product  business, View cardHolder);
        abstract void getCardHolderOnBind(View cardHolder, Product  business);
        abstract void reciveCardHol(View cardHolder);

    }

}