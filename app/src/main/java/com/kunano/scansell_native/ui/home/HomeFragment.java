package com.kunano.scansell_native.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.controllers.home.BottomSheetCreateBusinessController;
import com.kunano.scansell_native.controllers.home.BusinessController;
import com.kunano.scansell_native.databinding.HomeFragmentBinding;
import com.kunano.scansell_native.databinding.HomeToolbarBinding;
import com.kunano.scansell_native.model.Home.Business;
import com.kunano.scansell_native.ui.ProgressBarDialog;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetViewModel;

public class HomeFragment extends Fragment {
    private HomeFragmentBinding binding;

    private ImageButton addBusinessButton;
    private ImageButton cancelDeleteModeButton;
    private  ImageButton selectAllButton;
    private ImageButton deleteButton;
    private HomeToolbarBinding toolBarHomeBinding;
    private Toolbar toolbarHoma;
    private TextView title;
    BusinessController businessesController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        LifecycleOwner homeLifecycleOwner = getViewLifecycleOwner();

        HomeViewModel homeViewModel =
                new HomeViewModel(inflater, homeLifecycleOwner);
        Business businessesModel = new Business();

        businessesController = new BusinessController(businessesModel, homeViewModel);

        businessesController.showData();


        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Set title on the app bar frame
        toolBarHomeBinding = binding.includeToolbar;
        toolbarHoma = toolBarHomeBinding.toolbar;
        title = toolbarHoma.findViewById(R.id.create_business_title);
        businessesController.setTitle(title);

        //Toolbar bottons
        addBusinessButton = toolBarHomeBinding.addBusinessButton;
        cancelDeleteModeButton = toolBarHomeBinding.cancelDeletingButton;
        selectAllButton = toolBarHomeBinding.selectAllButton;
        deleteButton = toolBarHomeBinding.deleteButton;

        AdminButtons adminButtons = new AdminButtons(cancelDeleteModeButton, selectAllButton, deleteButton, businessesController, this.getContext(), inflater);
        adminButtons.setClickEventsOnButtons();



        BottomSheetViewModel bottomSheetViewModel = new BottomSheetViewModel();
        BottomSheetCreateBusinessController adminBottomSheet = new BottomSheetCreateBusinessController(addBusinessButton, businessesController, bottomSheetViewModel, getActivity());
        adminBottomSheet.setClickEventShowBottomSheet();
        RecyclerView businessLIst = binding.businessList;

        homeViewModel.getListBusinessApader().observe(getViewLifecycleOwner(), businessLIst::setAdapter);
        businessLIst.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        homeViewModel.getAddButtonVisibility().observe(getViewLifecycleOwner(), addBusinessButton::setVisibility);
        homeViewModel.getCancelDeleteModeButtonVisibility().observe(getViewLifecycleOwner(), cancelDeleteModeButton::setVisibility);
        homeViewModel.getSelectAllButtonVisibility().observe(getViewLifecycleOwner(), selectAllButton::setVisibility);
        homeViewModel.getImageForSeletAllButton().observe(getViewLifecycleOwner(), selectAllButton::setImageDrawable);
        homeViewModel.getDeletButtonVisibility().observe(getViewLifecycleOwner(), deleteButton::setVisibility);
        homeViewModel.getTitleWidth().observe(getViewLifecycleOwner(), title::setLayoutParams);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

class AdminButtons {
    private ImageButton cancelDeleteModeButton;
    private  ImageButton selectAllButton;
    private ImageButton deleteButton;
    private BusinessController businessController;
    private Context context;
    private ProgressBarDialog progressBarDialog;
    LayoutInflater inflater;

    public AdminButtons(ImageButton cancelDeleteModeButton, ImageButton selectAllButton, ImageButton deleteButton, BusinessController businessController, Context context, LayoutInflater inflater) {
        this.cancelDeleteModeButton = cancelDeleteModeButton;
        this.selectAllButton = selectAllButton;
        this.deleteButton = deleteButton;
        this.businessController = businessController;
        this.context = context;
        this.inflater = inflater;
    }

    public void setClickEventsOnButtons(){
        setClickEventOnCancelButton();
        setClickEventOnDeleteButton();
        setClickEventOnSelectAll();
    }

    private void setClickEventOnCancelButton(){
        cancelDeleteModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                businessController.desactivateDeletMode();
            }
        });

    }

    private void setClickEventOnSelectAll(){
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (businessController.isAllSelected()){
                    businessController.uncheckAllCards();
                    return;
                }
                businessController.checkAllCards();
            }
        });
    }

    private void setClickEventOnDeleteButton(){
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });
    }


    private void showAlert() {

        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);

        // Reference views in the custom layout
        TextView customDialogTitle = dialogView.findViewById(R.id.customDialogTitle);
        TextView customDialogMessage = dialogView.findViewById(R.id.customDialogMesage);
        Button customDialogOkButton = dialogView.findViewById(R.id.customDialogOkButton);
        Button customDialogCancelButton = dialogView.findViewById(R.id.customDialogCancelButton);

        customDialogTitle.setText(context.getText(R.string.delete_businesses_title));
        customDialogMessage.setText(context.getText(R.string.delete_businesses_warn));


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        customDialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (businessController.getBusinessListToDelete().isEmpty())return;


                businessController.deleteBusinesses();

                //Hide the deleting dialog
                view.getRootView().setVisibility(View.GONE);

                HomeViewModel homeViewModel = businessController.getBusinessesView();

                progressBarDialog = new ProgressBarDialog(inflater, homeViewModel.getItemsToDelete(),
                        homeViewModel.getProgress(), homeViewModel.getHomeLifecycleOwner(), context,
                        "deleting businesses");

                progressBarDialog.getProgressBarDeletingBusiness().show();
                System.out.println("show progress");

                homeViewModel.getProgress().observe(homeViewModel.getHomeLifecycleOwner(), (progress) ->{
                    if(progress == 100){
                        progressBarDialog.getDialogView().getRootView().setVisibility(View.GONE);
                    }

                } );
                progressBarDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.getRootView().setVisibility(View.GONE);
                    }
                });

            }
        });

        customDialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getRootView().setVisibility(View.GONE);
            }
        });

        builder.setView(dialogView).show();
    }





}



