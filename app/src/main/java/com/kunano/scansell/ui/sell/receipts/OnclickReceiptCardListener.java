package com.kunano.scansell.ui.sell.receipts;

import com.kunano.scansell.model.sell.Receipt;

public interface OnclickReceiptCardListener {
    abstract void onShortTap(Receipt receipt,  ReceiptAdapter.CardHolder cardHolder);

    abstract void onLongTap(Receipt receipt, ReceiptAdapter.CardHolder cardHolder);

    abstract void getCardHolderOnBind(ReceiptAdapter.CardHolder cardHolder, Receipt receipt);

    abstract void reciveCardHol(ReceiptAdapter.CardHolder cardHolder);

    abstract void onDelete(Receipt receipt);

}
