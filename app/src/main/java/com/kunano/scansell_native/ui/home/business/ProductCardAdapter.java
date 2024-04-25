package com.kunano.scansell_native.ui.home.business;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductImg;
import com.kunano.scansell_native.repository.home.ProductRepository;
import com.kunano.scansell_native.ui.components.ImageProcessor;

public class ProductCardAdapter extends ListAdapter<Product, ProductCardAdapter.CardHolder> {
    OnclickProductCardListener listener;
    ContentResolver contentResolver;
    ProductRepository productRepository;
    LifecycleOwner lifecycleOwner;
    private Activity activityParent;

    private static DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getProductId().equals(newItem.getProductId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getProductName().equals(newItem.getProductName()) &&
                    oldItem.getCratingDate().equals(newItem.getCratingDate()) &&
                    oldItem.getBuying_price() == newItem.getBuying_price() &&
                    oldItem.getSelling_price() == newItem.getSelling_price() &&
                    oldItem.getBusinessIdFK() == newItem.getBusinessIdFK() &&
                    oldItem.getStock().equals(newItem.getStock());
        }
    };

    public ProductCardAdapter() {
        super(DIFF_CALLBACK);
    }


    @Override
    public ProductCardAdapter.CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        productRepository = new ProductRepository((Application) parent.getContext().getApplicationContext());
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
        //holder.imageViewProduct.setImageBitmap(ImageProcessor.bytesToBitmap(product.getImg()));
        productRepository.getProdductImage(product.getProductId(), product.getBusinessIdFK()
                , new LisnedProductImage() {
            @Override
            public void recieveProducImage(ProductImg productImg) {

                if (activityParent == null) return;
                activityParent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (productImg != null){
                            holder.imageViewProduct.setImageBitmap(ImageProcessor.bytesToBitmap(productImg.getImg()));
                            if (productImg.getImg().length < 1){
                                holder.imageViewProduct.
                                        setImageDrawable(ContextCompat.
                                                getDrawable(activityParent.getApplicationContext(),R.drawable.broken_image ));
                            }
                        }
                    }
                });
            }
        });
        holder.view.setTag(String.valueOf(product.getProductId()));
        System.out.println("it is on bind");

        listener.getCardHolderOnBind(holder, product);
    }


    public class CardHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView stock;
        private TextView sellingPrice;
        private TextView buyingPrice;

        private ImageView imageViewProduct;
        private ImageView unCheckedCircle;
        ImageButton restoreButton;

        private  View view;

        public CardHolder(View itemView) {
            super(itemView);
            view = itemView;

            title = itemView.findViewById(R.id.textViewTitleProduct);
            stock = itemView.findViewById(R.id.textViewStock);
            sellingPrice = itemView.findViewById(R.id.textViewSellingPrice);
            buyingPrice = itemView.findViewById(R.id.textViewBuyingPrice);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            unCheckedCircle = itemView.findViewById(R.id.uncheckedCircle);
            restoreButton = itemView.findViewById(R.id.restoreButton);
            //unCheckedCircle = itemView.findViewById(R.id.checked_unchecked_image_view);

            if (listener != null && itemView != null){
                listener.reciveCardHol(CardHolder.this);
            }

            restoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onRestore(getItem(position));
                    }
                }
            });

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
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public TextView getStock() {
            return stock;
        }

        public void setStock(TextView stock) {
            this.stock = stock;
        }

        public TextView getSellingPrice() {
            return sellingPrice;
        }

        public void setSellingPrice(TextView sellingPrice) {
            this.sellingPrice = sellingPrice;
        }

        public TextView getBuyingPrice() {
            return buyingPrice;
        }

        public void setBuyingPrice(TextView buyingPrice) {
            this.buyingPrice = buyingPrice;
        }

        public ImageView getImageViewProduct() {
            return imageViewProduct;
        }

        public void setImageViewProduct(ImageView imageViewProduct) {
            this.imageViewProduct = imageViewProduct;
        }

        public ImageView getUnCheckedCircle() {
            return unCheckedCircle;
        }

        public void setUnCheckedCircle(ImageView unCheckedCircle) {
            this.unCheckedCircle = unCheckedCircle;
        }

        public ImageButton getRestoreButton() {
            return restoreButton;
        }

        public void setRestoreButton(ImageButton restoreButton) {
            this.restoreButton = restoreButton;
        }

        public CardView getCardView() {
            return view.findViewById(R.id.productCard);
        }

        public void setView(View view) {
            this.view = view;
        }
    }




    public void setListener(OnclickProductCardListener listener) {
        this.listener = listener;
    }


    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setActivityParent(Activity activityParent) {
        this.activityParent = activityParent;
    }

    public interface OnclickProductCardListener{
        abstract void onShortTap(Product product, ProductCardAdapter.CardHolder cardHolder);
        abstract void onLongTap(Product  product,ProductCardAdapter.CardHolder cardHolder);
        abstract void getCardHolderOnBind(ProductCardAdapter.CardHolder cardHolder, Product  prod);
        abstract void reciveCardHol(ProductCardAdapter.CardHolder cardHolder);
        abstract void onRestore(Product product);

    }

    @FunctionalInterface
    public interface LisnedProductImage{
        abstract void recieveProducImage(ProductImg productWithImage);
    }

}