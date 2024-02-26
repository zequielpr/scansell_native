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
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentBusinessBinding;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.ui.components.AskWhetherDeleteDialog;
import com.kunano.scansell_native.ui.components.ProgressBarDialog;
import com.kunano.scansell_native.ui.home.HomeViewModel;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragment;
import com.kunano.scansell_native.ui.home.business.create_product.CreateProductViewModel;


public class BusinessFragment extends Fragment {


    private BusinessViewModel businessViewModel;
    private FragmentBusinessBinding binding;

    private Toolbar toolbar;

    private FloatingActionButton fButton;

    private RecyclerView recyclerViewProduct;
    private ProductCardAdapter productCardAdapter;
    private Drawable checkedCircle;
    private  Drawable uncheckedCircle;
    private  Long businessKey;

    private MenuItem deleteIcon;
    private MenuItem selectAllIcon;
    private FragmentManager suportFmanager;
    private  ProgressBarDialog progressBarDialog;

    MainActivityViewModel mainActivityViewModel;
    HomeViewModel homeViewModel;
    private boolean sendingBusinessToBin;
    private CreateProductViewModel createProductViewModel;
    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {




        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        createProductViewModel = new ViewModelProvider(requireActivity()).get(CreateProductViewModel.class);

        businessKey = homeViewModel.getCurrentBusinessId();
                binding = FragmentBusinessBinding.inflate(inflater, container, false);

        toolbar = binding.toolbarProducts;
        toolbar.inflateMenu(R.menu.actions_toolbar_business_screen);
        fButton = binding.floatingActionButtonCreateProduct;
        recyclerViewProduct = binding.recycleViewProducts;
        checkedCircle = ContextCompat.getDrawable(getContext(), R.drawable.checked_circle);
        uncheckedCircle = ContextCompat.getDrawable(getContext(), R.drawable.unchked_circle);



        suportFmanager = getActivity().getSupportFragmentManager();

        productCardAdapter= new ProductCardAdapter();
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

        mainActivityViewModel.setHandleBackPress(this::handlerBackPress);


        return binding.getRoot();
    }


    private void handlerBackPress(){
        if (businessViewModel.isDeleteModeActive()){
            desactivateDeleteMode(getView());
            updateToolbar();
            return;
        }else if(businessViewModel.isSearchModeActive()){
            System.out.println("Desactivate search mode: ");
            searchView.onActionViewCollapsed();
            businessViewModel.setSearchModeActive(false);
            return;
        }
        System.out.println("Desactivate search mode?: " + businessViewModel.isSearchModeActive());

        navigateBAck();
    }

    public void navigateBAck(){
        getActivity().runOnUiThread(()->{
            NavDirections action = BusinessFragmentDirections.actionBusinessFragment2ToNavigationHome();
            Navigation.findNavController(getView()).navigate(action);
            mainActivityViewModel.setHandleBackPress(null);
        });
    }







    private void  setCardListener(){
        productCardAdapter.setListener(new ProductCardAdapter.OnclickProductCardListener() {
            @Override
            public void onShortTap(Product product, View cardHolder) {
                businessViewModel.shortTap(product);
                if (businessViewModel.isDeleteModeActive()){
                    checkCard(cardHolder, product);
                    return;
                };
                showProductDetails(product.getProductId());

            }

            @Override
            public void onLongTap(Product product, View cardHolder) {
                businessViewModel.longTap(product);
                checkCard(cardHolder, product);
                if(!businessViewModel.isDeleteModeActive()){
                    activateDeleteMode();
                }



            }

            @Override
            public void getCardHolderOnBind(View cardHolder, Product prod) {
                if (businessViewModel.isDeleteModeActive()) checkCard(cardHolder, prod);
            }

            @Override
            public void reciveCardHol(View cardHolder) {

                businessViewModel.getCheckedOrUncheckedCirclLivedata().observe(getViewLifecycleOwner(),
                        cardHolder.findViewById(R.id.uncheckedCircle)::setBackground);
            }

            @Override
            public void onRestore(Product product) {

            }
        });
    }


    private void navigateToCreateProduct(View view ) {
        NavDirections action = BusinessFragmentDirections.actionBusinessFragment2ToScannProductCreateFragment2();
        Navigation.findNavController(getView()).navigate(action);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        updateToolbar();


    }

    public void navigateBusinessBin(){
        NavDirections directions = BusinessFragmentDirections.actionBusinessFragment2ToBusinessBinFragment2();

        Navigation.findNavController(getView()).navigate(directions);
    }





    public void updateToolbar() {
        boolean isDeleteModeActivate = businessViewModel.isDeleteModeActive();
        toolbar.getMenu().clear();


        if(isDeleteModeActivate){
        inflateDeleteMenu();
            inflateBackIcon();
        }else{
            inflateNormarMenu();
            inflateBackIcon();
            return;
        }


        if (businessViewModel.isAllSelected()){
            selectAllIcon.setIcon(R.drawable.checked_circle);
        }else {
            selectAllIcon.setIcon(R.drawable.unchked_circle);
        }

    }

    private void inflateBackIcon(){

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener((v)->handlerBackPress());
    }


    public void inflateNormarMenu(){

        toolbar.inflateMenu(R.menu.actions_toolbar_business_screen);
        toolbar.setOnMenuItemClickListener((intemMenu)->{

            switch (intemMenu.getItemId()){
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
        searchView.setOnSearchClickListener((v)->businessViewModel.setSearchModeActive(true));
        searchView.setOnCloseListener(()->{
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
                businessViewModel.setSearchModeActive(true);
                System.out.println("Query: " + newText);
                businessViewModel.searchProduct(newText.trim());
                return false;
            }
        });


    }


    private void setFiltreMenuListener(){
       MenuItem filter = toolbar.getMenu().findItem(R.id.filter_action);
        SubMenu filterOptionsMenu = filter.getSubMenu();

        for(int i = 0; i < filterOptionsMenu.size(); i++){
            filterOptionsMenu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
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



    public void inflateDeleteMenu(){
        toolbar.inflateMenu(R.menu.toolbar_delete_mode_menu);
        deleteIcon = toolbar.getMenu().findItem(R.id.delete_button);
        selectAllIcon = toolbar.getMenu().findItem(R.id.select_all_button);

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


    }


    public void unSelectAll() {
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        businessViewModel.setCheckedOrUncheckedCirclLivedata(null);
        businessViewModel.unSelectAll();
    }


    public void checkCard(View cardHolder, Product product) {
        updateToolbar();
        System.out.println("Items to delete: " + businessViewModel.getItemsToDelete().size());

        if (businessViewModel.getItemsToDelete().contains((Object) product)) {
            cardHolder.findViewById(R.id.uncheckedCircle).setBackground(checkedCircle);
            System.out.println("Seleccionada" +  cardHolder.getTag());
            return;
        }
        cardHolder.findViewById(R.id.uncheckedCircle).setBackground(null);


    }


    public void activateDeleteMode() {
        businessViewModel.setDeleteModeActive(true);
        sendingBusinessToBin = false;
        updateToolbar();
    }

    public void desactivateDeleteMode(View view) {
        businessViewModel.setCheckedOrUncheckedCirclLivedata(null);
        businessViewModel.desactivateDeleteMod(businessViewModel.getBusinessName());
        toolbar.setNavigationIcon(null);
        updateToolbar();
    }





    public void askToSendProductsBin() {
        System.out.println("Ask whether delete businiesses");
        String title = getString(R.string.send_items_to_bin_warning);
        AskWhetherDeleteDialog askWhetherDeleteDialog = new
                AskWhetherDeleteDialog(getLayoutInflater(),this::pasToBinOrCancel, title);
        askWhetherDeleteDialog.show(suportFmanager, "ask to delete product");

    }


    public void pasToBinOrCancel(boolean response){
        if(response){
            showProgressBar();
            businessViewModel.passItemsToBin(this::hideProgressBar, businessViewModel.getBusinessName());
            getActivity().runOnUiThread(()-> updateToolbar());

        }else {
            desactivateDeleteMode(null);
        }
    }





    public void showProgressBar() {
        ListenResponse action = (cancelDeleteProcess)->{
            if(cancelDeleteProcess){
                businessViewModel.cancelDeleteProcess();
            }

        };


        String title =  getString(R.string.send_items_to_bin_warning);
        MutableLiveData<Integer> progress = businessViewModel.getDeleteProgressLiveData();
        MutableLiveData<String> deletedBusiness = businessViewModel.getDeletedItemsLiveData();

        progressBarDialog = new ProgressBarDialog(action, getLayoutInflater(),
                title, getViewLifecycleOwner(), progress, deletedBusiness);

        progressBarDialog.show(getParentFragmentManager(), "progress bar");

    }



    public void hideProgressBar(boolean result) {
        if(!result){
            //Show result
            return;
        }

        if(progressBarDialog != null){
            if(sendingBusinessToBin)getActivity().runOnUiThread(()->navigateBAck());
            progressBarDialog.dismiss();
        }

    }



    //Update name and address
    private void upateBusiness(){
        String businessName = businessViewModel.getBusinessName();
        String businessAddress = businessViewModel.getBusinessAddress();

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(getString(R.string.update),
                getString(R.string.update), businessName, businessAddress);

        bottomSheetFragment.setButtomSheetFragmentListener(new BottomSheetFragment.ButtomSheetFragmentListener() {
            @Override
            public void receiveData(String name, String address) {
                homeViewModel.updateBusiness(name, address, "");
                //getActivity().runOnUiThread(BusinessFragment.this::navigateBAck);
            }
        });

        bottomSheetFragment.show(getParentFragmentManager(), "Update business");

    }



    private AskWhetherDeleteDialog askWhetherDeleteDialogBinBusiness;
    private void sendCurrentBusinessTobin(){
        askWhetherDeleteDialogBinBusiness = new AskWhetherDeleteDialog(getLayoutInflater(),
                this::handleCancelOrBinBusiness, getString(R.string.bin_business) );
        askWhetherDeleteDialogBinBusiness.show(getParentFragmentManager(), getString(R.string.bin_business));
    }

    public void handleCancelOrBinBusiness(boolean result){
        if (result){
            businessViewModel.binSingleBusiness(this::processResult);
        }else {
            askWhetherDeleteDialogBinBusiness.dismiss();
        }
    }

    private void processResult(Object result){
        boolean r = (Boolean) result;

        if (r){
           showToast(getString(R.string.business_binned_successfuly), Toast.LENGTH_SHORT);
           navigateBAck();
        }else {
            showToast(getString(R.string.error_to_bin_business), Toast.LENGTH_SHORT);
        }
    }

    private void showToast(String message, Integer lenght){
         new Handler(Looper.getMainLooper()).post(()->Toast.makeText(getContext(), message, lenght).show());

    }



    private void showProductDetails(String productId){
        createProductViewModel.setBusinessId(homeViewModel.getCurrentBusinessId());
        createProductViewModel.checkIfProductExists(productId);
        NavDirections navDirections = BusinessFragmentDirections.actionBusinessFragment2ToCreateProductFragment2();
        Navigation.findNavController(getView()).navigate(navDirections);
    }





}