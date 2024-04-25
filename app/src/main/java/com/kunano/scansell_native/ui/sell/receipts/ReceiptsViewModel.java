package com.kunano.scansell_native.ui.sell.receipts;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.db.Converters;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.ui.sell.receipts.dele_component.ProcessItemsComponent;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReceiptsViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> isSearchModeActive;
    private MutableLiveData<Drawable> allSelectedIconMutableLiveData;
    private MutableLiveData<Integer> selectedItemQuantityMutableLiveData;
    private MutableLiveData<Integer> receiptCardBackgroundColor;

    public ReceiptsViewModel(@NonNull Application application){
        super(application);
        isSearchModeActive = new MutableLiveData<>(false);

        allSelectedIconMutableLiveData = new MutableLiveData<>();
        selectedItemQuantityMutableLiveData = new MutableLiveData<>();
        receiptCardBackgroundColor = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getIsSearchModeActive() {
        return isSearchModeActive;
    }

    public void setIsSearchModeActive(boolean isSearchModeActive) {
        this.isSearchModeActive.postValue(isSearchModeActive);
    }



    public boolean checkIfReceiptIsChecked(Receipt receipt, ProcessItemsComponent<Receipt> processItemsComponent){

       return  processItemsComponent.getItemsToProcess().stream().anyMatch((r)->{
           return r.getReceiptId().trim().equals(receipt.getReceiptId().trim());
       });

    }



    public String calculateDaysTobeDeleted(LocalDateTime sellDate) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Long daysLeftTimestamp = Converters.dateToTimestamp(LocalDate.now()) - Converters.dateToTimestamp(sellDate);

            int days = (int) (30 - (daysLeftTimestamp / (1000 * 60 * 60 * 24)));

            String daysLeft = Integer.toString(days).concat(" ").
                    concat(days > 1 ?
                            getApplication().getString(R.string.days) :
                            getApplication().getString(R.string.day));
            return daysLeft;
        }
        return "";

    }


    public MutableLiveData<Drawable> getAllSelectedIconMutableLiveData() {
        return allSelectedIconMutableLiveData;
    }

    public void setAllSelectedIconMutableLiveData(Drawable allSelectedIconMutableLiveData) {
        this.allSelectedIconMutableLiveData.postValue(allSelectedIconMutableLiveData);
    }

    public MutableLiveData<Integer> getSelectedItemQuantityMutableLiveData() {
        return selectedItemQuantityMutableLiveData;
    }

    public void setSelectedItemQuantityMutableLiveData(Integer selectedItemQuantityMutableLiveData) {
        this.selectedItemQuantityMutableLiveData.postValue(selectedItemQuantityMutableLiveData);
    }

    public MutableLiveData<Integer> getReceiptCardBackgroundColor() {
        return receiptCardBackgroundColor;
    }

    public void setReceiptCardBackgroundColor(Integer receiptCardBackgroundColor) {
        this.receiptCardBackgroundColor.postValue(receiptCardBackgroundColor);
    }
}