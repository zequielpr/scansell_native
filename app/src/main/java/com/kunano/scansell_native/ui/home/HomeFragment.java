package com.kunano.scansell_native.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.controllers.home.BusinessController;
import com.kunano.scansell_native.databinding.HomeFragmentBinding;
import com.kunano.scansell_native.databinding.HomeToolbarBinding;
import com.kunano.scansell_native.model.Home.Business;
import com.kunano.scansell_native.controllers.home.BottomSheetCreateBusinessController;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetViewModel;

public class HomeFragment extends Fragment {
    private HomeFragmentBinding binding;

    private ImageButton addBusinessButton;
    private HomeToolbarBinding toolBarHomeBinding;
    private Toolbar toolbarHoma;
    private TextView title;
    BusinessController businessesController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new HomeViewModel(inflater);
        Business businessesModel = new Business();

        businessesController = new BusinessController(businessesModel, homeViewModel);

        businessesController.showData();


        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Set title on the app bar frame
        toolBarHomeBinding = binding.includeToolbar;
        toolbarHoma = toolBarHomeBinding.toolbar;
        title = toolbarHoma.findViewById(R.id.create_business_title);
        title.setText(getString(R.string.businesses_title));


        //Get add business button
        addBusinessButton = toolBarHomeBinding.addBusinessButton;


        BottomSheetViewModel bottomSheetViewModel = new BottomSheetViewModel();
        BottomSheetCreateBusinessController adminBottomSheet = new BottomSheetCreateBusinessController(addBusinessButton, businessesController, bottomSheetViewModel, getActivity());
        adminBottomSheet.setClickEventShowBottomSheet();




        RecyclerView businessLIst = binding.businessList;

        homeViewModel.getListBusinessApader().observe(getViewLifecycleOwner(), businessLIst::setAdapter);
        businessLIst.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}



