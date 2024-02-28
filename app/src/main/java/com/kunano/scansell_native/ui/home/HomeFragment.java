package com.kunano.scansell_native.ui.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.HomeFragmentBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.components.ProgressBarDialog;
import com.kunano.scansell_native.ui.components.SpinningWheel;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragment;
import com.kunano.scansell_native.ui.components.AskForActionDialog;

public class HomeFragment extends Fragment implements ListenHomeViewModel {
    private HomeFragmentBinding binding;
    HomeViewModel homeViewModel;

    RecyclerView recyclerViewBusinessList;

    ListenHomeViewModel listenHomeViewModel;
    SpinningWheel spinningWheelDialog;
    FragmentManager suportFmanager;
    BusinessCardAdepter businessCardAdepter;
    private Drawable checkedCircle;
    private  Drawable uncheckedCircle;

    Toolbar toolbar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = HomeFragmentBinding.inflate(inflater, container, false);

        recyclerViewBusinessList = binding.businessList;
        recyclerViewBusinessList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewBusinessList.setHasFixedSize(true);

        businessCardAdepter = new BusinessCardAdepter();
        recyclerViewBusinessList.setAdapter(businessCardAdepter);



        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.getAllBusinesses().observe(getViewLifecycleOwner(), businessCardAdepter::submitList);
        listenHomeViewModel = this;
        homeViewModel.setListenHomeViewModel(listenHomeViewModel);

        suportFmanager = getActivity().getSupportFragmentManager();

        toolbar = binding.homeFragmentToolbar;
        toolbar.inflateMenu(R.menu.actions_toolbar_home);
        checkedCircle = ContextCompat.getDrawable(getContext(), R.drawable.checked_circle);
        uncheckedCircle = ContextCompat.getDrawable(getContext(), R.drawable.unchked_circle);
        homeViewModel.getSelectedItemsNumbLiveData().observe(getViewLifecycleOwner(),toolbar::setTitle);





        setBusinessCardOncliListener();


        return binding.getRoot();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        System.out.println("Activity obliterated");
    }

    private MenuItem addIcon;
    private MenuItem deleteIcon;
    private MenuItem selectAllIcon;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        addIcon = toolbar.getMenu().findItem(R.id.add);
        deleteIcon = toolbar.getMenu().findItem(R.id.delete);
        selectAllIcon = toolbar.getMenu().findItem(R.id.select_all);

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete:
                    askDeleteBusiness();
                    return true;
                case R.id.select_all:
                    if (homeViewModel.isAllSelected()) {
                        unSelectAll();
                    } else {
                        selectAll();
                    }
                    // Save profile changes.
                    return true;
                case R.id.add:
                    showBottomSheet();
                    return true;
                case R.id.bin:
                    navigateToBin();
                    return true;
                default:
                    return false;
            }
        });

        if (homeViewModel.isDeleteModeActive()) {
            toolbar.setNavigationIcon(R.drawable.close_24);
            toolbar.setNavigationOnClickListener(this::desactivateDeleteMode);
        }
        updateToolbar();

    }


    public void showBottomSheet() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(getString(R.string.create_new_business),
                getString(R.string.save), "", "");

        bottomSheetFragment.setButtomSheetFragmentListener(homeViewModel::insertNewBusiness);

        bottomSheetFragment.show(suportFmanager, bottomSheetFragment.getTag());
    }

    public void navigateToBin(){
        @NonNull NavDirections action =
                HomeFragmentDirections.actionNavigationHomeToUserBinFragment();

        Navigation.findNavController(getView()).navigate(action);
    }




    public void updateToolbar() {
        boolean isDeleteModeActivate = homeViewModel.isDeleteModeActive();
        addIcon.setVisible(!isDeleteModeActivate);

        deleteIcon.setVisible(isDeleteModeActivate);
        selectAllIcon.setVisible(isDeleteModeActivate);

        if (homeViewModel.isAllSelected()){
            selectAllIcon.setIcon(R.drawable.checked_circle);
        }else {
            selectAllIcon.setIcon(R.drawable.unchked_circle);
        }

        if(!isDeleteModeActivate){
            toolbar.setNavigationIcon(null);
        }else{
            toolbar.setNavigationIcon(R.drawable.close_24);
        }

    }


    public void setBusinessCardOncliListener() {
        businessCardAdepter.setListener(new BusinessCardAdepter.OnclickBusinessCardListener() {
            @Override
            public void onShortTap(Business business, View cardHolder) {
                homeViewModel.shortTap(business);
                if (homeViewModel.isDeleteModeActive()) checkCard(cardHolder, business);

            }

            @Override
            public void onLongTap(Business business, View cardHolder) {
                homeViewModel.longTap(business);
                checkCard(cardHolder, business);
                if(!homeViewModel.isDeleteModeActive()){
                    activateDeleteMode();
                }

            }

            @Override
            public void getCardHolderOnBind(View cardHolder, Business business) {
                if (homeViewModel.isDeleteModeActive()) checkCard(cardHolder, business);
            }

            @Override
            public void reciveCardHol(View cardHolder) {
                //Show empty circle when the delete mode is activated
                homeViewModel.getCheckedOrUncheckedCirclLivedata().observe(getViewLifecycleOwner(),
                        cardHolder.findViewById(R.id.checked_unchecked_image_view)::setBackground);
            }

            @Override
            public void onRestore(Business business) {

            }


        });
    }


    public void selectAll() {
        selectAllIcon.setIcon(R.drawable.checked_circle);
        homeViewModel.setCheckedOrUncheckedCirclLivedata(checkedCircle);
        homeViewModel.selectAll(homeViewModel.parseBusinessListToGeneric());


    }


    public void unSelectAll() {
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        homeViewModel.setCheckedOrUncheckedCirclLivedata(null);
        homeViewModel.unSelectAll();
    }


    public void checkCard(View cardHolder, Business business) {
        updateToolbar();

        if (homeViewModel.getItemsToDelete().contains(business)) {
            cardHolder.findViewById(R.id.checked_unchecked_image_view).setBackground(checkedCircle);
            return;
        }
        cardHolder.findViewById(R.id.checked_unchecked_image_view).setBackground(null);


    }



    public void activateDeleteMode() {
        homeViewModel.setDeleteModeActive(true);
        toolbar.setNavigationIcon(R.drawable.close_24);
        toolbar.setNavigationOnClickListener(this::desactivateDeleteMode);
        updateToolbar();
    }

    public void desactivateDeleteMode(View view) {
        homeViewModel.setCheckedOrUncheckedCirclLivedata(null);
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        homeViewModel.desactivateDeleteMod(getString(R.string.businesses_title));
        toolbar.setNavigationIcon(null);
        updateToolbar();
    }


    @Override
    public void activateWaitingMode() {
        spinningWheelDialog = new SpinningWheel(getLayoutInflater());
        spinningWheelDialog.show(getParentFragmentManager(), "spinning_wheel");
    }

    @Override
    public void desactivateWaitingMode() {
        if (spinningWheelDialog != null) spinningWheelDialog.dismiss();

    }

    @Override
    public void navigateToProducts(Long businessId) {
        NavDirections action = HomeFragmentDirections.actionNavigationHomeToBusinessFragment2(businessId);
        Navigation.findNavController(getView()).navigate(action);
    }

    @Override
    public void showProgressBar() {
        ListenResponse action = (cancelDeleteProcess)->{
            if(cancelDeleteProcess){
                homeViewModel.cancelDeleteProcess();
            }
        };


        String title =  getString(R.string.delete);
        MutableLiveData<Integer> progress = homeViewModel.getDeleteProgressLiveData();
        MutableLiveData<String> deletedBusiness = homeViewModel.getDeletedItemsLiveData();

         progressBarDialog = new ProgressBarDialog(action, getLayoutInflater(),
                title, getViewLifecycleOwner(), progress, deletedBusiness);

        progressBarDialog.show(getParentFragmentManager(), "progress bar");

    }
    private  ProgressBarDialog progressBarDialog;

    @Override
    public void hideProgressBar() {
        if(progressBarDialog != null){
            progressBarDialog.dismiss();
        }





    }

    @Override
    public void askDeleteBusiness() {
        String title = getString(R.string.send_items_to_bin_warning);
        AskForActionDialog askWhetherDeleteDialog = new
                AskForActionDialog(getLayoutInflater(), title);
        askWhetherDeleteDialog.setButtonListener(new ListenResponse() {
            @Override
            public void isSuccessfull(boolean resultado) {
                if(resultado){
                    homeViewModel.passBusinessToBin();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateToolbar();
                        }
                    });

                }else {
                    desactivateDeleteMode(null);
                }
            }
        });

        askWhetherDeleteDialog.show(suportFmanager, "ask to send business to bin");

    }

}



