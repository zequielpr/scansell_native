package com.kunano.scansell_native.ui.sell.adapters;

import android.app.Activity;
import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductImg;
import com.kunano.scansell_native.repository.home.ProductRepository;
import com.kunano.scansell_native.components.ImageProcessor;
import com.kunano.scansell_native.components.Utils;
import com.kunano.scansell_native.ui.home.business.ProductCardAdapter;

import java.math.BigDecimal;
import java.util.List;

public class ProductToSellAdapter extends ListAdapter<Product, ProductToSellAdapter.CardHolder> {
    OnclickProductCardListener listener;
    ProductRepository productRepository;

    private Activity activityParent;

    private static DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getProductId().equalsIgnoreCase(newItem.getProductId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return  oldItem.getProductId().equalsIgnoreCase(newItem.getProductId()) &&
                    oldItem.getProductName().equalsIgnoreCase(newItem.getProductName()) &&
                    oldItem.getCratingDate().equals(newItem.getCratingDate()) &&
                    oldItem.getBuying_price() == newItem.getBuying_price() &&
                    oldItem.getSelling_price() == newItem.getSelling_price() &&
                    oldItem.getBusinessIdFK() == newItem.getBusinessIdFK() &&
                    oldItem.getStock().equals(newItem.getStock());
        }

    };

    @Override
    public void onCurrentListChanged(@NonNull List<Product> previousList, @NonNull List<Product> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        listener.onListChanged();
    }

    public ProductToSellAdapter() {
        super(DIFF_CALLBACK);
    }


    @Override
    public ProductToSellAdapter.CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        productRepository = new ProductRepository((Application) parent.getContext().getApplicationContext());
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_sell_design_card, parent, false);

        return new CardHolder(view);
    }




    @Override
    public void onBindViewHolder(ProductToSellAdapter.CardHolder holder, final int position) {
        Product product = getItem(position);
        holder.title.setText(product.getProductName());
        holder.sellingPrice.setText(activityParent.getString(R.string.price)+" ".
                concat(String.valueOf(Utils.formatDecimal(BigDecimal.valueOf(product.getSelling_price())))));
        //holder.imageViewProduct.setImageBitmap(ImageProcessor.bytesToBitmap(product.getImg()));
        productRepository.getProdductImage(product.getProductId(), product.getBusinessIdFK()
                , new ProductCardAdapter.LisnedProductImage() {
            @Override
            public void recieveProducImage(ProductImg productImg) {

                if (activityParent == null) return;
                activityParent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (productImg.getImg().length != 0){
                            holder.imageViewProduct.setImageBitmap(ImageProcessor.bytesToBitmap(productImg.getImg()));
                        }
                    }
                });
            }
        });
        holder.card.setTag(String.valueOf(product.getProductId()));
       // System.out.println("it is on bind");

        listener.getCardHolderOnBind(holder.itemView, product);
    }


    public class CardHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView sellingPrice;


        private ImageView imageViewProduct;

        ImageButton cancelButton;

        private  View card;

        public CardHolder(View itemView) {
            super(itemView);
            card = itemView;

            title = itemView.findViewById(R.id.textViewTitleProduct);
            sellingPrice = itemView.findViewById(R.id.textViewSellingPrice);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            cancelButton = itemView.findViewById(R.id.cancel_b_sell_product);

            if (listener != null && itemView != null){
                listener.reciveCardHol(itemView);
            }

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onCancel(getItem(position));
                    }
                }
            });

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



    public void setActivityParent(Activity activityParent) {
        this.activityParent = activityParent;
    }

    public interface OnclickProductCardListener{
        abstract void onShortTap(Product product, View cardHolder);
        abstract void onLongTap(Product  product, View cardHolder);
        abstract void getCardHolderOnBind(View cardHolder, Product  prod);
        abstract void reciveCardHol(View cardHolder);
        abstract void onCancel(Product product);

        abstract void onListChanged();

    }


    public interface LisnedProductImage{
        abstract void recieveProducImage(ProductImg productWithImage);
    }

}
