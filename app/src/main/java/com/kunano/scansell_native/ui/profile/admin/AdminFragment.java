package com.kunano.scansell_native.ui.profile.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.databinding.FragmentAdminBinding;
import com.kunano.scansell_native.ui.profile.ProfileFragmentDirections;
import com.qonversion.android.sdk.Qonversion;
import com.qonversion.android.sdk.dto.QonversionError;
import com.qonversion.android.sdk.dto.entitlements.QEntitlement;
import com.qonversion.android.sdk.dto.products.QProduct;
import com.qonversion.android.sdk.listeners.QonversionEntitlementsCallback;

import java.util.List;
import java.util.Map;

public class AdminFragment extends BottomSheetDialogFragment {
    View settingSetcion;
    View accountSection;
    View backUpSection;
    View parentView;

    FragmentAdminBinding binding;
    private AdminViewModel adminViewModel;

    public AdminFragment(View parentView){
        this.parentView = parentView;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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


    private void settingSectionAction(View view){
        NavDirections navDirectionsSetting = ProfileFragmentDirections.actionProfileFragmentToSettingsFragment23();
        Navigation.findNavController(parentView).navigate(navDirectionsSetting);
        dismiss();

    }

    private void accountSectionAction(View view){
        NavDirections navDirectionsAccount = ProfileFragmentDirections.actionProfileFragmentToAccountFragment();
        Navigation.findNavController(parentView).navigate(navDirectionsAccount);
        dismiss();
    }

    private void backUpSectionAction(View view){

        NavDirections navDirectionsBackUp = ProfileFragmentDirections.actionProfileFragmentToBackUpFragment();
        Navigation.findNavController(parentView).navigate(navDirectionsBackUp);
        dismiss();
     /*   if (adminViewModel.isHasPremiumPermission()){

        }else {
            buyFunctionality();
        }*/

        /*BuyBackUpFunctionFragment buyBackUpFunctionFragment = new BuyBackUpFunctionFragment();
        buyBackUpFunctionFragment.show(getParentFragmentManager(), "buy function");*/


    }

    private void buyFunctionality() {

        List<QProduct> qProducts = adminViewModel.getOfferings().get(0).getProducts();


        Qonversion.getSharedInstance().purchase(

                getActivity(), qProducts.isEmpty() ? null : qProducts.get(0).toPurchaseModel(),
                new QonversionEntitlementsCallback() {
                    @Override
                    public void onSuccess(@NonNull Map<String, QEntitlement> map) {
                        Toast.makeText(
                                getContext(),
                                "Purchase successful",
                                Toast.LENGTH_LONG
                        ).show();
                        adminViewModel.updatePermissions();
                    }

                    @Override
                    public void onError(@NonNull QonversionError error) {
                        Toast.makeText(
                                getContext(),
                                "Purchase failed: " + error.getDescription() + ", " + error.getAdditionalMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}