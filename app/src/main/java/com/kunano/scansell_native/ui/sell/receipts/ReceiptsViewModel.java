package com.kunano.scansell_native.ui.sell.receipts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.db.Converters;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReceiptsViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> isSearchModeActive;
    public ReceiptsViewModel(@NonNull Application application){
        super(application);
        isSearchModeActive = new MutableLiveData<>(false);
    }

    public MutableLiveData<Boolean> getIsSearchModeActive() {
        return isSearchModeActive;
    }

    public void setIsSearchModeActive(boolean isSearchModeActive) {
        this.isSearchModeActive.postValue(isSearchModeActive);
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
}