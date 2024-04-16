package com.kunano.scansell_native.ui.home.business;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
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

    public void onCreate(Bundle savedState){
        super.onCreate(savedState);
        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

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
        businessViewModel.getSelectedItemsNumbLiveData().observe(getViewLifecycleOwner(), toolbar::setTitle);


        fButton.setOnClickListener(this::navigateToCreateProduct);

        productCardAdapter.setLifecycleOwner(getViewLifecycleOwner());
        productCardAdapter.setActivityParent(getActivity());


        setCardListener();
        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                System.out.println("back");
               handlerBackPress();
            }
        });


        return binding.getRoot();
    }


    private void handlerBackPress() {
        if (businessViewModel.isDeleteModeActive()) {
            desactivateDeleteMode(getView());
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
                businessViewModel.shortTap(product);
                if (businessViewModel.isDeleteModeActive()) {
                    checkCard(cardHolder, product);
                    return;
                }
                ;
                showProductDetails(product.getProductId());

            }

            @Override
            public void onLongTap(Product product, ProductCardAdapter.CardHolder cardHolder) {
                businessViewModel.longTap(product);
                checkCard(cardHolder, product);
                if (!businessViewModel.isDeleteModeActive()) {
                    activateDeleteMode();
                }


            }

            @Override
            public void getCardHolderOnBind(ProductCardAdapter.CardHolder cardHolder, Product prod) {
                if (businessViewModel.isDeleteModeActive()) checkCard(cardHolder, prod);
            }

            @Override
            public void reciveCardHol(ProductCardAdapter.CardHolder cardHolder) {

                businessViewModel.getCheckedOrUncheckedCirclLivedata().observe(getViewLifecycleOwner(),
                        cardHolder.getUnCheckedCircle()::setBackground);

                //If it is all selected, then, the backgrounds of the product cards turn black transparent
                businessViewModel.getCheckedOrUncheckedCirclLivedata().observe(getViewLifecycleOwner(),
                        (icon)->{
                    if (icon != null){
                        cardHolder.getCardView().setBackgroundColor(getResources().
                                getColor(R.color.black_transparent, getActivity().getTheme()));
                        return;
                    }
                            cardHolder.getCardView().setBackgroundColor(getResources().
                                    getColor(R.color.white, getActivity().getTheme()));
                        });
            }

            @Override
            public void onRestore(Product product) {

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
        updateToolbar();


    }

    public void navigateBusinessBin() {
        NavDirections directions = BusinessFragmentDirections.actionBusinessFragment2ToBusinessBinFragment2(businessKey);

        Navigation.findNavController(getView()).navigate(directions);
    }


    public void updateToolbar() {
        boolean isDeleteModeActivate = businessViewModel.isDeleteModeActive();
        toolbar.getMenu().clear();


        if (isDeleteModeActivate) {
            inflateDeleteMenu();
            inflateBackIcon();
        } else {
            inflateNormarMenu();
            inflateBackIcon();
            return;
        }


        if (businessViewModel.isAllSelected()) {
            selectAllIcon.setIcon(R.drawable.checked_circle);
        } else {
            selectAllIcon.setIcon(R.drawable.unchked_circle);
        }

    }

    private void inflateBackIcon() {

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener((v) -> handlerBackPress());
    }


    public void inflateNormarMenu() {

        toolbar.inflateMenu(R.menu.actions_toolbar_business_screen);
        toolbar.setOnMenuItemClickListener((intemMenu) -> {

            switch (intemMenu.getItemId()) {
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
                    setFiltreMenuListener();
                    return true;
                default:
                    return true;
            }

        });


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


    private void setFiltreMenuListener() {
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

        deleteIcon.setVisible(businessViewModel.getItemsToDelete().size() > 0);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete_button:
                    askToSendProductsBin();
                    return true;
                case R.id.select_all_button:
                    if (businessViewModel.isAllSelected()) {
                        unSelectAll();
                    } else {
                        selectAll();
                    }
                    return true;
                case R.id.add:

                default:
                    return false;
            }
        });

    }


    public void selectAll() {
        selectAllIcon.setIcon(checkedCircle);
        businessViewModel.setCheckedOrUncheckedCirclLivedata(checkedCircle);
        businessViewModel.selectAll(businessViewModel.parseProductListToGeneric());
        updateToolbar();
    }


    public void unSelectAll() {
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        businessViewModel.setCheckedOrUncheckedCirclLivedata(null);
        businessViewModel.unSelectAll();
        updateToolbar();
    }


    public void checkCard(ProductCardAdapter.CardHolder cardHolder, Product product) {
        updateToolbar();

        if (businessViewModel.getItemsToDelete().contains((Object) product)) {
            cardHolder.getUnCheckedCircle().setBackground(checkedCircle);

            cardHolder.getCardView().setBackgroundColor(getResources().
                    getColor(R.color.black_transparent, getActivity().getTheme()));
            System.out.println("selected");
            return;
        }
        System.out.println("unselected");
        cardHolder.getUnCheckedCircle().setBackground(null);
        cardHolder.getCardView().setBackgroundColor(getResources().getColor(R.color.white, getActivity().getTheme()));

    }


    public void activateDeleteMode() {
        businessViewModel.setDeleteModeActive(true);
        sendingBusinessToBin = false;
        mainActivityViewModel.hideBottomNavBar();
        fButton.setVisibility(View.GONE);
        updateToolbar();
    }

    public void desactivateDeleteMode(View view) {
        businessViewModel.setCheckedOrUncheckedCirclLivedata(null);
        businessViewModel.desactivateDeleteMod(businessViewModel.getBusinessName());
        toolbar.setNavigationIcon(null);
        mainActivityViewModel.showBottomNavBar();
        fButton.setVisibility(View.VISIBLE);
        updateToolbar();
    }


    public void askToSendProductsBin() {
        System.out.println("Ask whether delete businiesses");
        String title = getString(R.string.send_items_to_bin_warning);
        AskForActionDialog askWhetherDeleteDialog = new
                AskForActionDialog(title);
        askWhetherDeleteDialog.setButtonListener(this::pasToBinOrCancel);
        askWhetherDeleteDialog.show(suportFmanager, "ask to delete product");

    }


    public void pasToBinOrCancel(boolean response) {
        if (response) {
            showProgressBar();
            businessViewModel.passItemsToBin(this::hideProgressBar, businessViewModel.getBusinessName());
            getActivity().runOnUiThread(() -> updateToolbar());

        } else {
            desactivateDeleteMode(getView());
        }
    }


    public void showProgressBar() {


        String title = getString(R.string.send_items_to_bin_warning);
        MutableLiveData<Integer> progress = businessViewModel.getDeleteProgressLiveData();
        MutableLiveData<String> deletedBusiness = businessViewModel.getDeletedItemsLiveData();

        progressBarDialog = new ProgressBarDialog(
                title, progress, deletedBusiness);
        progressBarDialog.setAction(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean cancelDeleteProcess) {
                if (cancelDeleteProcess) {
                    businessViewModel.cancelDeleteProcess();
                }
            }
        });

        progressBarDialog.show(getParentFragmentManager(), "progress bar");

    }


    public void hideProgressBar(boolean result) {
        if (result) {
            getActivity().runOnUiThread(()->desactivateDeleteMode(getView()) );
            Utils.showToast(getActivity(), getString(R.string.product_sent_to_bin_successfuly),
                    Toast.LENGTH_SHORT);
        }else {
            Utils.showToast(getActivity(), getString(R.string.thera_has_been_an_error),
                    Toast.LENGTH_SHORT);
        }

        if (progressBarDialog != null) {
            if (sendingBusinessToBin) getActivity().runOnUiThread(() -> navigateBAck());
            progressBarDialog.dismiss();
        }

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
            showToast(getString(R.string.business_update_successfully), Toast.LENGTH_SHORT);
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
            showToast(getString(R.string.business_binned_successfuly), Toast.LENGTH_SHORT);
            navigateBAck();
        } else {
            showToast(getString(R.string.error_to_bin_business), Toast.LENGTH_SHORT);
        }
    }

    private void showToast(String message, Integer lenght) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getContext(), message, lenght).show());

    }


    private void showProductDetails(String productId) {
        NavDirections navDirections = BusinessFragmentDirections.actionBusinessFragment2ToCreateProductFragment2(businessKey, productId);
        Navigation.findNavController(getView()).navigate(navDirections);
    }


}