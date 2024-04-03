package com.kunano.scansell_native.ui.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.HomeFragmentBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.ProgressBarDialog;
import com.kunano.scansell_native.ui.components.SpinningWheel;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.components.ViewModelListener;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragmentCreateBusiness;

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

    private View createBusinessView;
    private ImageButton createNewBusinessImgButton;
    private MainActivityViewModel mainActivityViewModel;

    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = HomeFragmentBinding.inflate(inflater, container, false);

        createBusinessView = binding.createNewBusinessView.createNewBusinessView;
        createNewBusinessImgButton = binding.createNewBusinessView.createNewBusinessImgButton;


        recyclerViewBusinessList = binding.businessList;
        recyclerViewBusinessList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewBusinessList.setHasFixedSize(true);

        businessCardAdepter = new BusinessCardAdepter();
        recyclerViewBusinessList.setAdapter(businessCardAdepter);

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
    }

    private MenuItem addIcon;
    private MenuItem deleteIcon;
    private MenuItem selectAllIcon;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        addIcon = toolbar.getMenu().findItem(R.id.add);
        deleteIcon = toolbar.getMenu().findItem(R.id.delete);
        selectAllIcon = toolbar.getMenu().findItem(R.id.select_all);

        homeViewModel.getAllBusinesses().observe(getViewLifecycleOwner(),this::handleCreateBusinessVisibility);
        createNewBusinessImgButton.setOnClickListener(this::createNewBusiness);

        homeViewModel.getCreateNewBusinessVisibilityMD().observe(getViewLifecycleOwner(),
                createBusinessView::setVisibility);

        mainActivityViewModel.setHandleBackPress(this::handleBackPress);

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


    public void handleBackPress(){
        if (homeViewModel.isDeleteModeActive()){
            desactivateDeleteMode(getView());
        }
        else {
            Utils.askToLeaveApp(this);
        }
    }

    private void handleCreateBusinessVisibility(List<Business> l){
        if (l.size() > 0) {
            homeViewModel.setCreateNewBusinessVisibilityMD(View.GONE);
        } else {
            homeViewModel.setCreateNewBusinessVisibilityMD(View.VISIBLE);
        }
    }

    private void createNewBusiness(View view){
        BottomSheetFragmentCreateBusiness createBusiness = new BottomSheetFragmentCreateBusiness();
        createBusiness.setRequestResult(this::processCreateBusinessRequest);
        createBusiness.show(getParentFragmentManager(), BottomSheetFragmentCreateBusiness.TAG);
    }

    private void processCreateBusinessRequest(boolean result){

    }


    public void showBottomSheet() {
        BottomSheetFragmentCreateBusiness bottomSheetFragmentCreateBusiness =
                new BottomSheetFragmentCreateBusiness();

        bottomSheetFragmentCreateBusiness.setRequestResult(homeViewModel::notifyInsertNewBusinessResult);

        bottomSheetFragmentCreateBusiness.show(suportFmanager, bottomSheetFragmentCreateBusiness.getTag());
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
        deleteIcon.setVisible(homeViewModel.getItemsToDelete().size()>0);

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

               TextView quantity =  cardHolder.findViewById(R.id.textViewNumProducts);

               homeViewModel.getQuantityOfProductsInBusiness(business.getBusinessId()).observe(
                       getViewLifecycleOwner(), (q)-> quantity.setText(getString(R.string.current_products).
                               concat(": ").concat(String.valueOf(q)))
               );
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
        updateToolbar();

    }


    public void unSelectAll() {
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        homeViewModel.setCheckedOrUncheckedCirclLivedata(null);
        homeViewModel.unSelectAll();
        updateToolbar();
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
        spinningWheelDialog = new SpinningWheel();
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

        String title =  getString(R.string.delete);
        MutableLiveData<Integer> progress = homeViewModel.getDeleteProgressLiveData();
        MutableLiveData<String> deletedBusiness = homeViewModel.getDeletedItemsLiveData();

         progressBarDialog = new ProgressBarDialog(
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
                AskForActionDialog( title);
        askWhetherDeleteDialog.setButtonListener(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {
                if(object){
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



