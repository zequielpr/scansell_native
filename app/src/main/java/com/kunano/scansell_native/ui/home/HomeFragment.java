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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.ListenResponse;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.HomeFragmentBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.ProgressBarDialog;
import com.kunano.scansell_native.ui.SpinningWheel;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragment;
import com.kunano.scansell_native.ui.notifications.AskWhetherDeleteDialog;

import java.util.List;

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
        homeViewModel.getSelectedBusinessesNumbLiveData().observe(getViewLifecycleOwner(),toolbar::setTitle);





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
                    if (homeViewModel.getIsAllSelected()) {
                        unSelectAll();
                    } else {
                        selectAll();
                    }
                    // Save profile changes.
                    return true;
                case R.id.add:
                    showBottomSheet();
                default:
                    return false;
            }
        });

        if (homeViewModel.isDeleteModeActive()) {
            toolbar.setNavigationIcon(R.drawable.cancel_24);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    desactivateDeleteMode();
                }
            });
        }
        updateToolbar();

    }


    public void showBottomSheet() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(suportFmanager, bottomSheetFragment.getTag());

    }

    public void updateToolbar() {
        boolean isDeleteModeActivate = homeViewModel.isDeleteModeActive();
        addIcon.setVisible(!isDeleteModeActivate);

        deleteIcon.setVisible(isDeleteModeActivate);
        selectAllIcon.setVisible(isDeleteModeActivate);

        if (homeViewModel.getIsAllSelected()){
            selectAllIcon.setIcon(R.drawable.checked_circle);
        }else {
            selectAllIcon.setIcon(R.drawable.unchked_circle);
        }

        if(!isDeleteModeActivate){
            toolbar.setNavigationIcon(null);
        }else{
            toolbar.setNavigationIcon(R.drawable.cancel_24);
        }

    }


    public void updateBusinessView(List<Business> businessesList) {
        businessCardAdepter.submitList(businessesList);
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


        });
    }


    public void selectAll() {
        selectAllIcon.setIcon(R.drawable.checked_circle);
        homeViewModel.setCheckedOrUncheckedCirclLivedata(checkedCircle);
        homeViewModel.selectAll();


    }


    public void unSelectAll() {
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        homeViewModel.setCheckedOrUncheckedCirclLivedata(null);
        homeViewModel.unSelectAll();
    }


    public void checkCard(View cardHolder, Business business) {
        updateToolbar();

        if (homeViewModel.getBusinessesToDelete().contains(business)) {
            cardHolder.findViewById(R.id.checked_unchecked_image_view).setBackground(checkedCircle);
            System.out.println("Seleccionada" +  cardHolder.getTag());
            return;
        }
        cardHolder.findViewById(R.id.checked_unchecked_image_view).setBackground(null);


    }


    public void activateDeleteMode() {
        homeViewModel.setDeleteModeActive(true);
        toolbar.setNavigationIcon(R.drawable.cancel_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desactivateDeleteMode();
            }
        });
        updateToolbar();
    }

    public void desactivateDeleteMode() {
        homeViewModel.setCheckedOrUncheckedCirclLivedata(null);
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        homeViewModel.desactivateDeleteMod();
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
    public void navigateToProducts(String businessId) {
        HomeFragmentDirections.ActionNavigationHomeToProductsFragment22 action = HomeFragmentDirections.actionNavigationHomeToProductsFragment22();
        action.setBusinessKey(businessId);
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
        MutableLiveData<String> deletedBusiness = homeViewModel.getDeletedBusnLiveData();

         progressBarDialog = new ProgressBarDialog(action, getLayoutInflater(),
                title, getViewLifecycleOwner(), progress, deletedBusiness);

        progressBarDialog.show(getParentFragmentManager(), "spinning_wheel");

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
        ListenResponse action = (response)->{
            if(response){
                homeViewModel.deletetBusiness();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateToolbar();
                    }
                });

            }else {
                desactivateDeleteMode();
            }
        };

        String title = getString(R.string.delete_businesses_warn);
        AskWhetherDeleteDialog askWhetherDeleteDialog = new
                AskWhetherDeleteDialog(getLayoutInflater(),action, title);

        askWhetherDeleteDialog.show(suportFmanager, "ask to delete business");

    }
}



