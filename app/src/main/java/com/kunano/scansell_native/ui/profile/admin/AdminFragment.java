package com.kunano.scansell_native.ui.profile.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.android.billingclient.api.ProductDetails;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.databinding.FragmentAdminBinding;
import com.kunano.scansell_native.repository.firebase.Premium;
import com.kunano.scansell_native.ui.components.ViewModelListener;
import com.kunano.scansell_native.ui.components.billing_component.BillingComponent;
import com.kunano.scansell_native.ui.profile.ProfileFragmentDirections;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;

public class AdminFragment extends BottomSheetDialogFragment {
    View settingSetcion;
    View accountSection;
    View backUpSection;
    View parentView;

    FragmentAdminBinding binding;
    private AdminViewModel adminViewModel;
    private BillingComponent billingComponent;
    MainActivityViewModel mainActivityViewModel;

    public AdminFragment() {
    }

    public AdminFragment(View parentView) {
        this.parentView = parentView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        billingComponent = new BillingComponent(getActivity());
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

    }

    public void onDestroy() {
        super.onDestroy();
        if (billingComponent != null) billingComponent.endConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentAdminBinding.inflate(inflater, container, false);


        settingSetcion = binding.settingSection;
        accountSection = binding.accountSection;
        backUpSection = binding.backUpSection;

        settingSetcion.setOnClickListener(this::settingSectionAction);
        accountSection.setOnClickListener(this::accountSectionAction);
        backUpSection.setOnClickListener(this::backUpSectionAction);


        return binding.getRoot();
    }


    private void settingSectionAction(View view) {
        NavDirections navDirectionsSetting = ProfileFragmentDirections.actionProfileFragmentToSettingsFragment23();
        Navigation.findNavController(parentView).navigate(navDirectionsSetting);
        dismiss();

    }

    private void accountSectionAction(View view) {
        NavDirections navDirectionsAccount = ProfileFragmentDirections.actionProfileFragmentToAccountFragment();
        Navigation.findNavController(parentView).navigate(navDirectionsAccount);
        dismiss();
    }

    private AccountHelper accountHelper;
    private Premium premium;
    private void backUpSectionAction(View view) {

        accountHelper = new AccountHelper();
        premium = new Premium();

        if (mainActivityViewModel.isPremiumActive() == null){
            premium.getPremiumState(BillingComponent.PRODUCT_ID, accountHelper.getUserId(), new Premium.premiumListener<Boolean, Void>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        navigateToBackUpSection();
                    } else {
                        buyFunctionDialog();
                    }

                    mainActivityViewModel.setPremiumActive(result);
                }

                @Override
                public void onFailure(Void result) {

                }
            });
        }else {
            if (mainActivityViewModel.isPremiumActive()) {
                navigateToBackUpSection();
            } else {
                buyFunctionDialog();
            }
        }
    }

    private BuyBackUpFunctionFragment buyFunctionFragment;

    private void buyFunctionDialog() {
        buyFunctionFragment = new BuyBackUpFunctionFragment();
        buyFunctionFragment.setViewModelListener(new ViewModelListener() {
            @Override
            public void result(Object object) {
                billingComponent.queryProductsToBuy(BillingComponent.PRODUCT_ID, AdminFragment.this::launchPurchaseFlow);
            }
        });

        buyFunctionFragment.show(getChildFragmentManager(), "Ask to buy functionality");
    }

    private void launchPurchaseFlow(ProductDetails productDetails) {
        billingComponent.setHasTheFunctionBeenBoughtListener(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {
                if (object) buyFunctionFragment.dismiss();
            }
        });
        billingComponent.launchPurchaseFlow(productDetails);
    }

    private void navigateToBackUpSection(){
        if (getActivity() != null){
            getActivity().runOnUiThread(()->{
                NavDirections navDirectionsBackUp = ProfileFragmentDirections.actionProfileFragmentToBackUpFragment();
                Navigation.findNavController(parentView).navigate(navDirectionsBackUp);
                dismiss();
            });
        }
    }
}