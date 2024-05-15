package com.kunano.scansell_native.ui.profile.admin.premium;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kunano.scansell_native.components.billing_component.BillingComponent;
import com.kunano.scansell_native.repository.firebase.PremiumRepository;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;

public class PremiumViewModel extends ViewModel {
    private MutableLiveData<Boolean> subscriptionState;
    private PremiumRepository premiumRepository;
    private AccountHelper accountHelper;

    public PremiumViewModel(){
        subscriptionState = new MutableLiveData<>();
        premiumRepository = new PremiumRepository();
        accountHelper = new AccountHelper();
        premiumRepository.getPremiumStateRealTime(BillingComponent.PREMIUM_ID, accountHelper.getUserId(),
                subscriptionState::postValue);
    }

    public MutableLiveData<Boolean> getSubscriptionState() {
        return subscriptionState;
    }

    public void setSubscriptionState(Boolean subscriptionState) {
        this.subscriptionState.postValue(subscriptionState);
    }

    public void verifySubscription(boolean state){
        premiumRepository.setPremiumState(BillingComponent.PREMIUM_ID, accountHelper.getUserId(), state, (r)->{});
    }
}
