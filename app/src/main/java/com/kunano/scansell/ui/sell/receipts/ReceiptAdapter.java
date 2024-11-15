package com.kunano.scansell.ui.sell.receipts;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell.R;
import com.kunano.scansell.model.sell.Receipt;
import com.kunano.scansell.components.Utils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class ReceiptAdapter extends ListAdapter<Receipt, ReceiptAdapter.CardHolder> {
    OnclickReceiptCardListener listener;


    private static DiffUtil.ItemCallback<Receipt> DIFF_CALLBACK = new DiffUtil.ItemCallback<Receipt>() {
        @Override
        public boolean areItemsTheSame(@NonNull Receipt oldItem, @NonNull Receipt newItem) {
            return oldItem.getReceiptId().equalsIgnoreCase(newItem.getReceiptId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Receipt oldItem, @NonNull Receipt newItem) {
            return oldItem.getSellingDate().equals(newItem.getSellingDate()) &&
                    oldItem.getSpentAmount() == newItem.getSpentAmount();
        }
    };



    protected ReceiptAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.receipt_card, parent, false);

        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        Receipt receipt = getItem(position);

        holder.seriesNumber.setText(receipt.getReceiptId());
        holder.spentAmount.setText(String.valueOf(Utils.formatDecimal(BigDecimal.valueOf(receipt.getSpentAmount()))));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm");
            holder.sellDate.setText(String.valueOf(receipt.getSellingDate().format(formatter)));
        }

        if (listener != null){
            listener.getCardHolderOnBind(holder, receipt);

        }


    }

    public class CardHolder extends RecyclerView.ViewHolder{


        private TextView seriesNumber;
        private TextView spentAmount;
        private TextView sellDate;
        private ImageView checkIndicator;
        private TextView daysLeft;
        private CardView cardView;
        public CardHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.receiptCardView);
            seriesNumber = itemView.findViewById(R.id.textViewSeriesNumber);
            spentAmount = itemView.findViewById(R.id.spent_amount);
            sellDate = itemView.findViewById(R.id.sell_date);
            checkIndicator = itemView.findViewById(R.id.deleteReceiptImageButton);
            daysLeft = itemView.findViewById(R.id.daysLeftTextView);



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
        }

        public TextView getDaysLeft() {
            return daysLeft;
        }

        public ImageView getCheckIndicator() {
            return checkIndicator;
        }

        public void setCheckIndicator(ImageView checkIndicator) {
            this.checkIndicator = checkIndicator;
        }

        public CardView getCardView() {
            return cardView;
        }

        public void setCardView(CardView cardView) {
            this.cardView = cardView;
        }
    }




    public OnclickReceiptCardListener getListener() {
        return listener;
    }

    public void setListener(OnclickReceiptCardListener listener) {
        this.listener = listener;
    }
}




