package com.kunano.scansell_native.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.databinding.HomeFragmentBinding;
import com.kunano.scansell_native.databinding.HomeToolbarBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.SpinningWheel;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragment;

import java.util.List;

public class HomeFragment extends Fragment implements ListenHomeViewModel {
    private HomeFragmentBinding binding;

    private Button addBusinessButton;
    private Button cancelDeleteModeButton;
    private Button selectAllButton;
    private Button deleteButton;
    private HomeToolbarBinding toolBarHomeBinding;
    private Toolbar toolbarHoma;
    private TextView title;
    private TextView selectedBusinessesNumb;
    HomeViewModel homeViewModel;

    RecyclerView recyclerViewBusinessList;

    ListenHomeViewModel listenHomeViewModel;
    SpinningWheel spinningWheelDialog;
    FragmentManager suportFmanager;
    BusinessCardAdepter businessCardAdepter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = HomeFragmentBinding.inflate(inflater, container, false);


        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.getAllBusinesses().observe(getViewLifecycleOwner(), this::updateBusinessView);
        listenHomeViewModel = this;
        homeViewModel.setListenHomeViewModel(listenHomeViewModel);

        suportFmanager = getActivity().getSupportFragmentManager();


        recyclerViewBusinessList = binding.businessList;
        recyclerViewBusinessList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewBusinessList.setHasFixedSize(true);

        businessCardAdepter = new BusinessCardAdepter();
        recyclerViewBusinessList.setAdapter(businessCardAdepter);


        setBusinessCardOncliListener();
        setListenerShowBottomSheetCrtNewBusines();
        //setBusinessCardOncliListener();


        return binding.getRoot();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void updateBusinessView(List<Business> businessesList) {
        businessCardAdepter.submitList(businessesList);
    }

    public void setListenerShowBottomSheetCrtNewBusines() {
        binding.includeToolbar.addBusinessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(suportFmanager, bottomSheetFragment.getTag());
            }
        });

    }

    public void setBusinessCardOncliListener() {
        businessCardAdepter.setOnclickBusinessCardListener(new BusinessCardAdepter.OnclickBusinessCardListener() {
            @Override
            public void onShortTap(Business business) {
                homeViewModel.shortTap(business);
            }

            @Override
            public void onLongTap(Business business) {

            }
        });
    }

    @Override
    public void activateWaitingMode() {
        spinningWheelDialog = new SpinningWheel(getLayoutInflater());
        spinningWheelDialog.show(getParentFragmentManager(), "MyDialogFragmentTag");
    }

    @Override
    public void desactivateWaitingMode() {
        if (spinningWheelDialog != null) spinningWheelDialog.dismiss();

    }

    @Override
    public void navigateToProducts(String businessId) {
        HomeFragmentDirections.ActionNavigationHomeToProductsFragment22 action = HomeFragmentDirections.actionNavigationHomeToProductsFragment22();
        action.setBusinessKey(businessId);
        Navigation.findNavController(getView()).navigate(action);
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void updateProgressBar() {

    }
}



