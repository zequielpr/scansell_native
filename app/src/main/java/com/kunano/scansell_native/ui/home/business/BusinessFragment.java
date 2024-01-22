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
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentBusinessBinding;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;
import com.kunano.scansell_native.ui.ProgressBarDialog;
import com.kunano.scansell_native.ui.notifications.AskWhetherDeleteDialog;


public class BusinessFragment extends Fragment {


    private BusinessViewModel businessViewModel;
    private FragmentBusinessBinding binding;

    private Toolbar toolbar;

    private FloatingActionButton fButton;

    private RecyclerView recyclerViewProduct;
    private ProductCardAdapter productCardAdapter;
    private Drawable checkedCircle;
    private  Drawable uncheckedCircle;
    private  String businessKey;

    private MenuItem deleteIcon;
    private MenuItem selectAllIcon;
    private FragmentManager suportFmanager;
    private  ProgressBarDialog progressBarDialog;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        businessKey = BusinessFragmentArgs.fromBundle(getArguments()).getBusinessKey();


        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);

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


        businessViewModel.setCurrentBusinessId(Long.parseLong(businessKey));
        //businessViewModel.getBusinessListWithProductsList().observe(getViewLifecycleOwner(), this::updateProductsList);

        businessViewModel.queryAllProducts().observe(getViewLifecycleOwner(), this::updateProductsList);

        businessViewModel.getCurrentBusinessLiveData().observe(getViewLifecycleOwner(), businessViewModel::updateCurrentBusiness);
        businessViewModel.getSelectedItemsNumbLiveData().observe(getViewLifecycleOwner(), toolbar::setTitle);


        fButton.setOnClickListener(this::navigateToCreateProduct);

        productCardAdapter.setLifecycleOwner(getViewLifecycleOwner());
        productCardAdapter.setActivityParent(getActivity());


        setCardListener();
        return binding.getRoot();
    }

    public void updateProductsList(BusinessWithProduct businessWithProducts){
        businessViewModel.setCurrentBusinessLiveData(businessWithProducts.business);
        businessViewModel.setProductList(businessWithProducts.productsList);
        productCardAdapter.submitList(businessWithProducts.productsList);
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
        });
    }


    private void navigateToCreateProduct(View view ) {
        NavDirections action = BusinessFragmentDirections.actionProductsFragment2ToCreateProductFragment();
        Navigation.findNavController(getView()).navigate(action);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        if (businessViewModel.isDeleteModeActive()) {
            toolbar.setNavigationIcon(R.drawable.cancel_24);
            toolbar.setNavigationOnClickListener(this::desactivateDeleteMode);
        }
        updateToolbar();

    }





    public void updateToolbar() {
        boolean isDeleteModeActivate = businessViewModel.isDeleteModeActive();
        toolbar.getMenu().clear();

        if(isDeleteModeActivate){
        inflateDeleteMenu();
        }else{
            inflateNormarMenu();
            return;
        }

        if (businessViewModel.isAllSelected()){
            selectAllIcon.setIcon(R.drawable.checked_circle);
        }else {
            selectAllIcon.setIcon(R.drawable.unchked_circle);
        }

    }


    public void inflateNormarMenu(){
        toolbar.setNavigationIcon(null);
        toolbar.inflateMenu(R.menu.actions_toolbar_business_screen);
    }

    public void inflateDeleteMenu(){
        toolbar.setNavigationIcon(R.drawable.cancel_24);
        toolbar.inflateMenu(R.menu.toolbar_delete_mode_menu);
        deleteIcon = toolbar.getMenu().findItem(R.id.delete_button);
        selectAllIcon = toolbar.getMenu().findItem(R.id.select_all_button);

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete_button:
                    askDeleteBusiness();
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
        toolbar.setNavigationIcon(R.drawable.cancel_24);
        toolbar.setNavigationOnClickListener(this::desactivateDeleteMode);
        updateToolbar();
    }

    public void desactivateDeleteMode(View view) {
        businessViewModel.setCheckedOrUncheckedCirclLivedata(null);
        businessViewModel.desactivateDeleteMod(businessViewModel.getBusinessName());
        toolbar.setNavigationIcon(null);
        updateToolbar();
    }





    public void askDeleteBusiness() {
        System.out.println("Ask whether delete businiesses");
        ListenResponse action = this::deleteOrCancel;
        String title = getString(R.string.delete_businesses_warn);
        AskWhetherDeleteDialog askWhetherDeleteDialog = new
                AskWhetherDeleteDialog(getLayoutInflater(),action, title);
        askWhetherDeleteDialog.show(suportFmanager, "ask to delete business");

    }


    public void deleteOrCancel(boolean response){
        if(response){
            showProgressBar();
            businessViewModel.deletetItems(this::hideProgressBar, businessViewModel.getBusinessName());
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





    public void showProgressBar() {
        ListenResponse action = (cancelDeleteProcess)->{
            if(cancelDeleteProcess){
                businessViewModel.cancelDeleteProcess();
            }
        };


        String title =  getString(R.string.delete);
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
            progressBarDialog.dismiss();
        }

    }





}