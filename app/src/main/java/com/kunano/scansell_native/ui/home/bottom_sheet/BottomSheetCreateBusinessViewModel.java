package com.kunano.scansell_native.ui.home.bottom_sheet;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.ValidateData;
import com.kunano.scansell_native.repository.home.BusinessRepository;
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.ui.components.ViewModelListener;

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
        return ValidateData.validateName(name);
    }

    public boolean validateAddress(String address) {
        return ValidateData.validateAddress(address);
    }
}
