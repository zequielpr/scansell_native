package com.kunano.scansell_native.ui.home.business;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentBusinessBinding;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.ProgressBarDialog;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.components.ViewModelListener;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragmentCreateBusiness;
import com.kunano.scansell_native.ui.components.ProcessItemsComponent;

import java.util.LinkedHashSet;


public class BusinessFragment extends Fragment {


    private BusinessViewModel businessViewModel;
    private FragmentBusinessBinding binding;

    private Toolbar toolbar;

    private FloatingActionButton fButton;

    private RecyclerView recyclerViewProduct;
    private ProductCardAdapter productCardAdapter;
    private Drawable checkedCircle;
    private Drawable uncheckedCircle;
    private Long businessKey;

    private MenuItem deleteIcon;
    private MenuItem selectAllIcon;
    private FragmentManager suportFmanager;
    private ProgressBarDialog progressBarDialog;

    MainActivityViewModel mainActivityViewModel;
    private boolean sendingBusinessToBin;
    private SearchView searchView;
    private View emptyBusinessLayout;
    private ProcessItemsComponent<Product> productProcessItemsComponent;

    public BusinessFragment() {
    }

    public void onCreate(Bundle savedState){
        super.onCreate(savedState);
        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        productProcessItemsComponent = new ProcessItemsComponent<>(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            businessKey = getArguments().getLong("business_key");
            System.out.println("Business id: " + businessKey);
        }




        binding = FragmentBusinessBinding.inflate(inflater, container, false);


        toolbar = binding.toolbarProducts;
        toolbar.inflateMenu(R.menu.actions_toolbar_business_screen);
        fButton = binding.floatingActionButtonCreateProduct;
        recyclerViewProduct = binding.recycleViewProducts;
        checkedCircle = ContextCompat.getDrawable(getContext(), R.drawable.checked_circle);
        uncheckedCircle = ContextCompat.getDrawable(getContext(), R.drawable.unchked_circle);
        emptyBusinessLayout = binding.emptyBusinessLayout.emptyBusinessLayout;


        suportFmanager = getActivity().getSupportFragmentManager();

        productCardAdapter = new ProductCardAdapter();
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewProduct.setHasFixedSize(true);
        recyclerViewProduct.setAdapter(productCardAdapter);


        //businessViewModel.setCurrentBusinessId(businessKey);
        //businessViewModel.getBusinessListWithProductsList().observe(getViewLifecycleOwner(), this::updateProductsList);

        businessViewModel.queryAllProducts(businessKey).observe(getViewLifecycleOwner(), productCardAdapter::submitList);

        businessViewModel.getCurrentBusinessLiveData().observe(getViewLifecycleOwner(), businessViewModel::updateCurrentBusiness);
        businessViewModel.getToolBarTitle().observe(getViewLifecycleOwner(), toolbar::setTitle);


        fButton.setOnClickListener(this::navigateToCreateProduct);

        productCardAdapter.setLifecycleOwner(getViewLifecycleOwner());
        productCardAdapter.setActivityParent(getActivity());


        setCardListener();
        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               handlerBackPress();
            }
        });


        return binding.getRoot();
    }


    private void handlerBackPress() {
        if (productProcessItemsComponent.isProcessItemActive()) {
            desActivateDeleteMode(getView());
            return;
        } else if (businessViewModel.isSearchModeActive()) {
            System.out.println("Desactivate search mode: ");
            searchView.onActionViewCollapsed();
            businessViewModel.setSearchModeActive(false);
            return;
        }
        System.out.println("Desactivate search mode?: " + businessViewModel.isSearchModeActive());

        navigateBAck();
    }

    public void navigateBAck() {
        getActivity().runOnUiThread(() -> {
            NavDirections action = BusinessFragmentDirections.actionBusinessFragment2ToNavigationHome();
            Navigation.findNavController(getView()).navigate(action);
        });
    }


    private void setCardListener() {
        productCardAdapter.setListener(new ProductCardAdapter.OnclickProductCardListener() {
            @Override
            public void onShortTap(Product product, ProductCardAdapter.CardHolder cardHolder) {
                if (productProcessItemsComponent.isProcessItemActive()) {
                    businessViewModel.shortTap(product, productProcessItemsComponent);
                    checkCard(cardHolder, product);
                    return;
                }

                showProductDetails(product.getProductId());

            }

            @Override
            public void onLongTap(Product product, ProductCardAdapter.CardHolder cardHolder) {
                if (!productProcessItemsComponent.isProcessItemActive()) {
                    activateDeleteMode();
                }
                businessViewModel.longTap(product, productProcessItemsComponent);
                checkCard(cardHolder, product);


            }

            @Override
            public void getCardHolderOnBind(ProductCardAdapter.CardHolder cardHolder, Product prod) {
                if (productProcessItemsComponent.isProcessItemActive()) checkCard(cardHolder, prod);
            }

            @Override
            public void reciveCardHol(ProductCardAdapter.CardHolder cardHolder) {

                businessViewModel.getCheckedOrUncheckedCircleLivedata().observe(getViewLifecycleOwner(),
                        cardHolder.getUnCheckedCircle()::setBackground);

                //If it is all selected, then, the backgrounds of the product cards turn black transparent
                businessViewModel.getCheckedOrUncheckedCircleLivedata().observe(getViewLifecycleOwner(),
                        (icon)->{
                    if (icon != null){
                        cardHolder.getCardView().setCardBackgroundColor(ContextCompat.getColor(getContext(),
                                R.color.black_transparent));
                        return;
                    }
                            cardHolder.getCardView().setCardBackgroundColor(Color.WHITE);
                        });
            }

            @Override
            public void onRestore(Product product) {

            }

            @Override
            public void onListChanged() {
                recyclerViewProduct.scrollToPosition(0);
            }
        });
    }


    private void navigateToCreateProduct(View view) {
        NavDirections action = BusinessFragmentDirections.actionBusinessFragment2ToScannProductCreateFragment2(businessKey);
        Navigation.findNavController(getView()).navigate(action);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        businessViewModel.getEmptyBusinessVisibility().observe(getViewLifecycleOwner(),
                emptyBusinessLayout::setVisibility);
        toolbar.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                boolean isDeleteModeActivate = productProcessItemsComponent.isProcessItemActive();
                toolbar.getMenu().clear();


                if (isDeleteModeActivate) {
                    inflateDeleteMenu();
                    inflateBackIcon();
                    if (productProcessItemsComponent.isAllSelected()) {
                        selectAllIcon.setIcon(R.drawable.checked_circle);
                    } else {
                        selectAllIcon.setIcon(R.drawable.unchked_circle);
                    }
                } else {
                    inflateNormalMenu();
                    inflateBackIcon();
                }

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bin_action:
                        navigateBusinessBin();
                        return true;
                    case R.id.update_action:
                        upateBusiness();
                        return true;
                    case R.id.delete_action:
                        sendCurrentBusinessTobin();
                        return true;
                    case R.id.filter_action:
                        setFilterMenuListener();
                        return true;
                    case R.id.delete_button:
                        askToSendProductsBin();
                        return true;
                    case R.id.select_all_button:
                        if (productProcessItemsComponent.isAllSelected()) {
                            unSelectAll();
                        } else {
                            selectAll();
                        }
                        return true;
                    case R.id.add:
                    default:
                        return true;
                }
            }
        });


    }

    public void navigateBusinessBin() {
        NavDirections directions = BusinessFragmentDirections.actionBusinessFragment2ToBusinessBinFragment2(businessKey);

        Navigation.findNavController(getView()).navigate(directions);
    }

    private void inflateBackIcon() {

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener((v) -> handlerBackPress());
    }


    public void inflateNormalMenu() {

        toolbar.inflateMenu(R.menu.actions_toolbar_business_screen);
        searchView = (SearchView) toolbar.getMenu().findItem(R.id.search_action).getActionView();
        searchView.setOnSearchClickListener((v) -> businessViewModel.setSearchModeActive(true));
        searchView.setOnCloseListener(() -> {
            searchView.onActionViewCollapsed();
            businessViewModel.setSearchModeActive(false);
            return true;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //businessViewModel.setSearchModeActive(true);
                businessViewModel.searchProduct(newText.trim());
                return false;
            }
        });


    }


    private void setFilterMenuListener() {
        MenuItem filter = toolbar.getMenu().findItem(R.id.filter_action);
        SubMenu filterOptionsMenu = filter.getSubMenu();

        for (int i = 0; i < filterOptionsMenu.size(); i++) {
            filterOptionsMenu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.name_a_z:
                            businessViewModel.sortProductByNameAsc();
                            return true;
                        case R.id.name_z_a:
                            businessViewModel.sortProductByNameDesc();
                            return true;
                        case R.id.stock_more_less:
                            businessViewModel.sortProductByStcokDesc();
                            return true;
                        case R.id.stock_less_more:
                            businessViewModel.sortProductByStockAsc();
                            return true;
                        default:
                            return true;

                    }


                }
            });
        }


    }


    public void inflateDeleteMenu() {
        toolbar.inflateMenu(R.menu.toolbar_delete_mode_menu);
        deleteIcon = toolbar.getMenu().findItem(R.id.delete_button);
        selectAllIcon = toolbar.getMenu().findItem(R.id.select_all_button);

        deleteIcon.setVisible(productProcessItemsComponent.getItemsToProcess().size() > 0);
    }


    public void selectAll() {
        selectAllIcon.setIcon(checkedCircle);
        businessViewModel.setCheckedOrUncheckedCircleLivedata(checkedCircle);
        productProcessItemsComponent.setItemsToProcess(new LinkedHashSet<>(productCardAdapter.getCurrentList()));
        productProcessItemsComponent.setAllSelected(true);
        businessViewModel.setToolBarTitle(String.valueOf(productProcessItemsComponent.getItemsToProcess().size()));
        toolbar.invalidateMenu();
    }


    public void unSelectAll() {
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        businessViewModel.setCheckedOrUncheckedCircleLivedata(null);
        productProcessItemsComponent.clearItemsToProcess();
        businessViewModel.setToolBarTitle(String.valueOf(productProcessItemsComponent.getItemsToProcess().size()));
        toolbar.invalidateMenu();;
    }


    public void checkCard(ProductCardAdapter.CardHolder cardHolder, Product product) {

        if (productProcessItemsComponent.isItemToBeProcessed(product)) {
            cardHolder.getUnCheckedCircle().setBackground(checkedCircle);

            cardHolder.getCardView().setCardBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.black_transparent));
        }else {
            cardHolder.getUnCheckedCircle().setBackground(null);
            cardHolder.getCardView().setCardBackgroundColor(Color.WHITE);
        }
        if (productProcessItemsComponent.getItemsToProcess().size() ==
                productCardAdapter.getCurrentList().size()){
            productProcessItemsComponent.setAllSelected(true);
        }else {
            productProcessItemsComponent.setAllSelected(false);
        }
        toolbar.invalidateMenu();

    }


    public void activateDeleteMode() {
        productProcessItemsComponent.setProcessItemActive(true);
        sendingBusinessToBin = false;
        mainActivityViewModel.hideBottomNavBar();
        fButton.setVisibility(View.GONE);
        toolbar.invalidateMenu();
    }

    public void desActivateDeleteMode(View view) {
        businessViewModel.setCheckedOrUncheckedCircleLivedata(null);
        unSelectAll();
        businessViewModel.setToolBarTitle(businessViewModel.getBusinessName());
        toolbar.setNavigationIcon(null);
        mainActivityViewModel.showBottomNavBar();
        productProcessItemsComponent.setProcessItemActive(false);
        fButton.setVisibility(View.VISIBLE);
        toolbar.invalidateMenu();
    }


    public void askToSendProductsBin() {
        productProcessItemsComponent.binItems(new ViewModelListener<Void>() {
            @Override
            public void result(Void object) {
                getActivity().runOnUiThread(() -> {
                    desActivateDeleteMode(getView());
                });
            }
        });
    }



    BottomSheetFragmentCreateBusiness bottomSheetFragmentCreateBusiness;

    //Update name and address
    private void upateBusiness() {
        String businessName = businessViewModel.getBusinessName();
        String businessAddress = businessViewModel.getBusinessAddress();

        bottomSheetFragmentCreateBusiness = new BottomSheetFragmentCreateBusiness(
                businessName, businessAddress, true, businessViewModel.getCurrentBusinessId());

        bottomSheetFragmentCreateBusiness.setRequestResult(this::handleResult);

        bottomSheetFragmentCreateBusiness.show(getParentFragmentManager(), "Update business");
    }

    private void handleResult(Boolean result) {

        if (result) {
            Utils.showToast(getActivity(), getString(R.string.business_update_successfully), Toast.LENGTH_SHORT );
        }
    }


    private AskForActionDialog askWhetherDeleteDialogBinBusiness;

    private void sendCurrentBusinessTobin() {
        askWhetherDeleteDialogBinBusiness = new AskForActionDialog(getString(R.string.bin_business));
        askWhetherDeleteDialogBinBusiness.setButtonListener(this::handleCancelOrBinBusiness);
        askWhetherDeleteDialogBinBusiness.show(getParentFragmentManager(), getString(R.string.bin_business));
    }

    public void handleCancelOrBinBusiness(boolean result) {
        if (result) {
            businessViewModel.binSingleBusiness(this::processResult);
        } else {
            askWhetherDeleteDialogBinBusiness.dismiss();
        }
    }

    private void processResult(Object result) {
        boolean r = (Boolean) result;

        if (r) {
            Utils.showToast(getActivity(), getString(R.string.business_binned_successfully), Toast.LENGTH_SHORT );
            navigateBAck();
        } else {
            Utils.showToast(getActivity(), getString(R.string.error_to_bin_business), Toast.LENGTH_SHORT );
        }
    }



    private void showProductDetails(String productId) {
        NavDirections navDirections = BusinessFragmentDirections.actionBusinessFragment2ToCreateProductFragment2(businessKey, productId);
        Navigation.findNavController(getView()).navigate(navDirections);
    }


}