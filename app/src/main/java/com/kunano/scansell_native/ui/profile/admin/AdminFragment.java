package com.kunano.scansell_native.ui.profile.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.databinding.FragmentAdminBinding;
import com.kunano.scansell_native.ui.profile.ProfileFragmentDirections;
import com.qonversion.android.sdk.Qonversion;
import com.qonversion.android.sdk.QonversionError;
import com.qonversion.android.sdk.QonversionPermissionsCallback;
import com.qonversion.android.sdk.dto.QPermission;
import com.qonversion.android.sdk.dto.products.QProduct;

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

        if (adminViewModel.isHasPremiumPermission()){
            NavDirections navDirectionsBackUp = ProfileFragmentDirections.actionProfileFragmentToBackUpFragment();
            Navigation.findNavController(parentView).navigate(navDirectionsBackUp);
            dismiss();
        }else {
            buyFunctionality();
        }

        /*BuyBackUpFunctionFragment buyBackUpFunctionFragment = new BuyBackUpFunctionFragment();
        buyBackUpFunctionFragment.show(getParentFragmentManager(), "buy function");*/


    }

    private void buyFunctionality() {

        List<QProduct> qProducts = adminViewModel.getOfferings().get(0).getProducts();

        Qonversion.purchase(
                getActivity(), qProducts.isEmpty() ? null : qProducts.get(0),
                new QonversionPermissionsCallback() {
                    @Override
                    public void onError(QonversionError error) {
                        Toast.makeText(
                                getContext(),
                                "Purchase failed: " + error.getDescription() + ", " + error.getAdditionalMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onSuccess(Map<String, QPermission> permissions) {
                        Toast.makeText(
                                getContext(),
                                "Purchase successful",
                                Toast.LENGTH_LONG
                        ).show();
                        adminViewModel.updatePermissions();
                    }
                });
    }
}