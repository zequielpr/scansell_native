package com.kunano.scansell_native.ui.home.business;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunano.scansell_native.ListenResponse;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentBusinessBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.ui.AskWhetherDeleteDialog;
import com.kunano.scansell_native.ui.ProgressBarDialog;
import com.kunano.scansell_native.ui.home.HomeViewModel;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragment;

import java.util.LinkedHashSet;


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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {




        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

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
        }
        navigateBAck();
    }

    public void navigateBAck(){
        NavDirections action = BusinessFragmentDirections.actionProductsFragment2ToNavigationHome();
        Navigation.findNavController(getView()).navigate(action);
        mainActivityViewModel.setHandleBackPress(null);
    }





    private void  setCardListener(){
        productCardAdapter.setListener(new ProductCardAdapter.OnclickProductCardListener() {
            @Override
            public void onShortTap(Product product, View cardHolder) {
                businessViewModel.shortTap(product);
                if (businessViewModel.isDeleteModeActive()) checkCard(cardHolder, product);
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
        NavDirections action = BusinessFragmentDirections.actionBusinessFragmentToScannProductCreateFragment();
        Navigation.findNavController(getView()).navigate(action);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updateToolbar();

    }

    public void navigateBusinessBin(){
        NavDirections directions = BusinessFragmentDirections.actionBusinessFragmentToBusinessBinFragment();

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
        toolbar.setNavigationIcon(null);
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
                default:
                    return true;
            }

        });
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



    private void sendCurrentBusinessTobin(){
        sendingBusinessToBin = true;
        Business business = businessViewModel.getCurrentBusiness();
        System.out.println("Business name: " +business.getBusinessName());
        LinkedHashSet businessList = new LinkedHashSet<>();

        businessList.add(((Object) business));
        businessViewModel.setItemsToDelete(businessList);
        askToSendProductsBin();


    }






}