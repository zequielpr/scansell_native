package com.kunano.scansell_native.ui.home;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.HomeFragmentBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.components.SpinningWheel;
import com.kunano.scansell_native.ui.components.ViewModelListener;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragmentCreateBusiness;
import com.kunano.scansell_native.ui.sell.receipts.dele_component.ProcessItemsComponent;

import java.util.LinkedHashSet;
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
    private Drawable uncheckedCircle;

    private View createBusinessView;
    private ImageButton createNewBusinessImgButton;
    private MainActivityViewModel mainActivityViewModel;
    private Toolbar toolbar;

    private ProcessItemsComponent<Business> businessesProcessor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        businessesProcessor = new ProcessItemsComponent<>(this);
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
        checkedCircle = ContextCompat.getDrawable(getContext(), R.drawable.checked_circle);
        uncheckedCircle = ContextCompat.getDrawable(getContext(), R.drawable.unchked_circle);
        homeViewModel.getSelectedItems().observe(getViewLifecycleOwner(), toolbar::setTitle);

        setBusinessCardOncliListener();
        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                        handleBackPress();
                    }
                });

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

        homeViewModel.getAllBusinesses().observe(getViewLifecycleOwner(), this::handleCreateBusinessVisibility);
        createNewBusinessImgButton.setOnClickListener(this::createNewBusiness);

        homeViewModel.getCreateNewBusinessVisibilityMD().observe(getViewLifecycleOwner(), createBusinessView::setVisibility);

        toolbar.addMenuProvider(menuProvider);
    }

    MenuProvider menuProvider = new MenuProvider() {
        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.actions_toolbar_home, menu);

        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.add:
                    showBottomSheet();
                    return true;
                case R.id.bin:
                    navigateToBin();
                    return true;
                case R.id.delete_button:
                    businessesProcessor.binItems(new ViewModelListener<Void>() {
                        @Override
                        public void result(Void object) {
                            getActivity().runOnUiThread(()->desActivateDeleteMode(getView()));
                        }
                    });
                    return true;
                case R.id.select_all_button:
                    if (businessesProcessor.isAllSelected()){
                        unSelectAll();
                    }else {
                        selectAll();
                    }
                    return true;
            }
            return false;
        }
    };


    public void handleBackPress() {
        if (businessesProcessor.isProcessItemActive()) {
            desActivateDeleteMode(getView());
        } else {
            Navigation.findNavController(getView()).navigate(R.id.sell_navigation_graph);
        }
    }

    private void handleCreateBusinessVisibility(List<Business> l) {
        if (l.size() > 0) {
            homeViewModel.setCreateNewBusinessVisibilityMD(View.GONE);
        } else {
            homeViewModel.setCreateNewBusinessVisibilityMD(View.VISIBLE);
        }
    }

    private void createNewBusiness(View view) {
        BottomSheetFragmentCreateBusiness createBusiness = new BottomSheetFragmentCreateBusiness();
        createBusiness.setRequestResult(this::processCreateBusinessRequest);
        createBusiness.show(getParentFragmentManager(), BottomSheetFragmentCreateBusiness.TAG);
    }

    private void processCreateBusinessRequest(boolean result) {

    }


    public void showBottomSheet() {
        BottomSheetFragmentCreateBusiness bottomSheetFragmentCreateBusiness = new BottomSheetFragmentCreateBusiness();

        bottomSheetFragmentCreateBusiness.setRequestResult(homeViewModel::notifyInsertNewBusinessResult);

        bottomSheetFragmentCreateBusiness.show(suportFmanager, bottomSheetFragmentCreateBusiness.getTag());
    }

    public void navigateToBin() {
        @NonNull NavDirections action = HomeFragmentDirections.actionNavigationHomeToUserBinFragment();

        Navigation.findNavController(getView()).navigate(action);
    }


    public void setBusinessCardOncliListener() {
        businessCardAdepter.setListener(new BusinessCardAdepter.OnclickBusinessCardListener() {
            @Override
            public void onShortTap(Business business, BusinessCardAdepter.CardHolder cardHolder) {
                if (businessesProcessor.isProcessItemActive()){
                    homeViewModel.addOrRemoveItemToProcess(businessesProcessor, business);
                    checkCard(cardHolder, business);
                    return;
                }
                navigateToProducts(business.getBusinessId());

            }

            @Override
            public void onLongTap(Business business, BusinessCardAdepter.CardHolder cardHolder) {
                if (!businessesProcessor.isProcessItemActive()) {
                    activateDeleteMode();
                }
                homeViewModel.addOrRemoveItemToProcess(businessesProcessor, business);
                checkCard(cardHolder, business);

            }

            @Override
            public void getCardHolderOnBind(BusinessCardAdepter.CardHolder cardHolder, Business business) {
                if (businessesProcessor.isProcessItemActive()) checkCard(cardHolder, business);

                TextView quantity = cardHolder.getNumProducts();

                homeViewModel.getQuantityOfProductsInBusiness(business.getBusinessId()).
                        observe(getViewLifecycleOwner(), (q) ->{
                            String label = getString(R.string.current_products);
                            label = q < 2? label.substring(0, label.length()-1):label;
                            quantity.setText(String.valueOf(q) + " " + label);
                        });
            }

            @Override
            public void reciveCardHol(BusinessCardAdepter.CardHolder cardHolder) {
                //Show empty circle when the delete mode is activated
                homeViewModel.getCardBackgroundColor().observe(getViewLifecycleOwner(),
                        cardHolder.getCard()::setCardBackgroundColor);
                homeViewModel.getCheckedOrUncheckedCircleLivedata().observe(getViewLifecycleOwner(), cardHolder.getUnCheckedCircle()::setBackground);
            }

            @Override
            public void onRestore(Business business) {

            }


        });
    }


    public void selectAll() {
        selectAllMenuItem.setIcon(checkedCircle);
        homeViewModel.setCheckedOrUncheckedCircleLivedata(checkedCircle);
        homeViewModel.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_transparent));
        LinkedHashSet<Business> receipts = new LinkedHashSet<>(businessCardAdepter.getCurrentList());
        businessesProcessor.setItemsToProcess(receipts);
        businessesProcessor.setAllSelected(true);
        deleteMenuItem.setVisible(true);
        homeViewModel.setSelectedItems(String.valueOf(businessesProcessor.getItemsToProcess().size()));


    }


    public void unSelectAll() {
        selectAllMenuItem.setIcon(uncheckedCircle);
        homeViewModel.setCheckedOrUncheckedCircleLivedata(null);
        homeViewModel.setCardBackgroundColor(Color.WHITE);
        businessesProcessor.clearItemsToProcess();
        deleteMenuItem.setVisible(false);
        homeViewModel.setSelectedItems(String.valueOf(businessesProcessor.getItemsToProcess().size()));

    }



    public void checkCard(BusinessCardAdepter.CardHolder cardHolder, Business business) {

        boolean isChecked = businessesProcessor.isItemToBeProcessed(business);
        System.out.println("Business checked: " + isChecked);

        if(isChecked){
            cardHolder.getCard().setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_transparent));
            cardHolder.getUnCheckedCircle().setBackground(checkedCircle);

        }else {
            cardHolder.getCard().setCardBackgroundColor(Color.WHITE);
            cardHolder.getUnCheckedCircle().setBackground(null);
        }

        if (businessCardAdepter.getCurrentList().size() ==businessesProcessor.getItemsToProcess().size()){
            businessesProcessor.setAllSelected(true);
            selectAllMenuItem.setIcon(checkedCircle);
        }else {
            businessesProcessor.setAllSelected(false);
            selectAllMenuItem.setIcon(uncheckedCircle);
        }

        if (businessesProcessor.getItemsToProcess().isEmpty()){
            deleteMenuItem.setVisible(false);
        }else {
            deleteMenuItem.setVisible(true);
        }

    }


    private MenuItem selectAllMenuItem;
    private MenuItem deleteMenuItem;
    public void activateDeleteMode() {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.toolbar_delete_mode_menu);
        toolbar.setNavigationIcon(R.drawable.close_24);
        toolbar.setNavigationOnClickListener(this::desActivateDeleteMode);
        selectAllMenuItem = toolbar.getMenu().findItem(R.id.select_all_button);
        deleteMenuItem = toolbar.getMenu().findItem(R.id.delete_button);
        businessesProcessor.setProcessItemActive(true);
        mainActivityViewModel.hideBottomNavBar();
        homeViewModel.getSelectedItems().observe(getViewLifecycleOwner(),
                (i) ->{toolbar.setTitle(String.valueOf(i));});
    }

    public void desActivateDeleteMode(View view) {
        businessesProcessor.setProcessItemActive(false);
        homeViewModel.setCheckedOrUncheckedCircleLivedata(null);
        homeViewModel.setCardBackgroundColor(Color.WHITE);
        mainActivityViewModel.showBottomNavBar();
        toolbar.setNavigationIcon(null);
        toolbar.setTitle(getString(R.string.businesses_title));
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.actions_toolbar_home);

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

}



