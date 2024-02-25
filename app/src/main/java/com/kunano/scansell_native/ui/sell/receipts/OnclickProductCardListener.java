package com.kunano.scansell_native.ui.sell.receipts;

import android.view.View;

import com.kunano.scansell_native.model.sell.Receipt;

public interface OnclickProductCardListener {
    abstract void onShortTap(Receipt receipt, View cardHolder);

    abstract void onLongTap(Receipt receipt, View cardHolder);

    abstract void getCardHolderOnBind(View cardHolder, Receipt receipt);

    abstract void reciveCardHol(View cardHolder);

    abstract void onDelete(Receipt receipt);

}
