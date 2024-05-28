package com.kunano.scansell.ui.home.bottom_sheet;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.kunano.scansell.components.ListenResponse;
import com.kunano.scansell.components.ViewModelListener;
import com.kunano.scansell.model.Home.business.Business;
import com.kunano.scansell.model.ValidateData;
import com.kunano.scansell.repository.home.BusinessRepository;

public class BottomSheetCreateBusinessViewModel extends AndroidViewModel {

    private BusinessRepository businessRepository;


    public BottomSheetCreateBusinessViewModel(@NonNull Application application) {
        super(application);
        businessRepository = new BusinessRepository(application);
    }

    public void createBusiness(Business business, ViewModelListener<Boolean> listener){
        businessRepository.insertBusiness(business, listener::result);
    }

    public void updateBusiness(Business business, ListenResponse listenResponse){
        businessRepository.updateBusiness(business, listenResponse);
    }


    //Validate data
    public boolean validateName(String name) {
        return ValidateData.validateName(name.trim());
    }

    public boolean validateAddress(String address) {
        return ValidateData.validateAddress(address.trim());
    }
}
