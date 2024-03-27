package com.kunano.scansell_native.ui.home.business.business_bin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentBusinessBinBinding;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.ui.components.ProgressBarDialog;
import com.kunano.scansell_native.ui.home.bin.DeleteOrRestoreOptions;
import com.kunano.scansell_native.ui.home.business.ProductCardAdapter;

import java.util.List;

public class BusinessBinFragment extends Fragment {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProductCardAdapter productCardAdepter;

    private BusinessBinViewModel mViewModel;
    BottomNavigationView deleteOrRestoreOptions;
    private Drawable checkedCircle;
    private Drawable uncheckedCircle;
    private MenuItem selectAllIcon;

    private ProgressBarDialog progressBarDialog;

    MainActivityViewModel mainActivityViewModel;
    long currentBusinessId;
    private FragmentBusinessBinBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBusinessBinBinding.inflate(inflater, container, false);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mViewModel = new ViewModelProvider(this).get(BusinessBinViewModel.class);

        if (getArguments() != null) {
            currentBusinessId = getArguments().getLong("business_key");
        }

        toolbar = binding.binToolbar;
        deleteOrRestoreOptions = binding.deleteOrRestoreOption;
        recyclerView = binding.recycledBusinessList;


        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);

        productCardAdepter = new ProductCardAdapter();
        recyclerView.setAdapter(productCardAdepter);
        productCardAdepter.setActivityParent(getActivity());

        mViewModel.getRecycledProductLiveData(currentBusinessId).observe(getViewLifecycleOwner(), this::setToolbarSubtitle);
        mViewModel.getRecycledProductLiveData(currentBusinessId).observe(getViewLifecycleOwner(), productCardAdepter::submitList);


        checkedCircle = ContextCompat.getDrawable(getContext(), R.drawable.checked_circle);
        uncheckedCircle = ContextCompat.getDrawable(getContext(), R.drawable.unchked_circle);


        toolbar.inflateMenu(R.menu.actions_toolbar_user_bin);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        deleteOrRestoreOptions.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewModel.isDeleteModeActive()) {
                    desactivateDeleteMode();
                    return;
                }


                mainActivityViewModel.showBottomNavBar();
                NavDirections action = BusinessBinFragmentDirections.
                        actionBusinessBinFragment2ToBusinessFragment2(currentBusinessId);
                Navigation.findNavController(getView()).navigate(action);
            }
        });
        mViewModel.setListenBusinessBinViewModel(new BusinessBinViewModel.ListenBusinessBinViewModel() {
            @Override
            public void requestResult(String message) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                });

            }
        });



        productCardAdepter.setListener(new ProductCardAdapter.OnclickProductCardListener(){


            @Override
            public void onShortTap(Product product, View cardHolder) {
                mViewModel.shortTap(product);
                if (mViewModel.isDeleteModeActive()) checkCard(cardHolder, product);
            }

            @Override
            public void onLongTap(Product product, View cardHolder) {
                if (!mViewModel.isDeleteModeActive()) {
                    actcivateDeleteMode();
                }
                mViewModel.longTap(product);
                checkCard(cardHolder, product);


            }

            @Override
            public void getCardHolderOnBind(View cardHolder, Product prod) {
                mViewModel.setDaysLeftToBeDeleted(prod.getProductId());
                checkCard(cardHolder, prod);
            }

            @Override
            public void reciveCardHol(View cardHolder) {
               mViewModel.getCheckedOrUncheckedCirclLivedata().observe(getViewLifecycleOwner(),
                        cardHolder.findViewById(R.id.uncheckedCircle)::setBackground);

                ImageButton imageButton = cardHolder.findViewById(R.id.restoreButton);


               mViewModel.getRestoreButtonVisibilityLiveData().observe(getViewLifecycleOwner(),
                        imageButton::setVisibility
                );


              TextView textViewDaysLeft = cardHolder.findViewById(R.id.daysLeftProduct);
                textViewDaysLeft.setVisibility(View.VISIBLE);
                mViewModel.getDaysLeftTobeDeletedLiveDate().observe(getViewLifecycleOwner(), (d) -> {
                    textViewDaysLeft.setText((CharSequence) d);
                });
            }

            @Override
            public void onRestore(Product product) {
                mViewModel.restoreSingleProduct(product);
            }

        });


        mainActivityViewModel.setHandleBackPress(new MainActivityViewModel.HandleBackPress() {
            @Override
            public void backButtonPressed() {
                if (mViewModel.isDeleteModeActive()) {
                    desactivateDeleteMode();
                    return;
                }


                mainActivityViewModel.showBottomNavBar();
                NavDirections action = BusinessBinFragmentDirections.actionBusinessBinFragment2ToBusinessFragment2(currentBusinessId);
                Navigation.findNavController(getView()).navigate(action);
                mainActivityViewModel.setHandleBackPress(null);
            }
        });


        return binding.getRoot();
    }

    public void showDeleteOrRestoreOptions() {
        DeleteOrRestoreOptions bottomSheetFragment = new DeleteOrRestoreOptions();
        bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
    }


    private void setToolbarSubtitle(List<Product> businessList) {
        toolbar.setSubtitle(Integer.toString(businessList.size()).
                concat(" ").
                concat(getString(R.string.businesses_title)));
    }


    public void actcivateDeleteMode() {

        mViewModel.setSelectedItemsNumbLiveData("0");
        mViewModel.setDeleteModeActive(true);
        mViewModel.getSelectedItemsNumbLiveData().observe(getViewLifecycleOwner(), toolbar::setTitle);
        toolbar.setSubtitle("");
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.delete_mode_user_bin);
        selectAllIcon = toolbar.getMenu().findItem(R.id.all);
        mainActivityViewModel.hideBottomNavBar();
        mViewModel.setRestoreButtonVisibilityLiveData(View.GONE);

    }

    public void desactivateDeleteMode() {
        mViewModel.setDeleteModeActive(false);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.actions_toolbar_user_bin);
        mViewModel.getRecycledProductLiveData(currentBusinessId).observe(getViewLifecycleOwner(), this::setToolbarSubtitle);
        deleteOrRestoreOptions.setVisibility(View.GONE);
        mainActivityViewModel.showBottomNavBar();
        mViewModel.desactivateDeleteMod(getString(R.string.recycle_bin));
        mViewModel.setCheckedOrUncheckedCirclLivedata(null);
        mViewModel.setRestoreButtonVisibilityLiveData(View.VISIBLE);

    }


    public void checkCard(View cardHolder, Product product) {
        if (mViewModel.getItemsToDelete().isEmpty()){
            deleteOrRestoreOptions.setVisibility(View.GONE);
        }else {
            deleteOrRestoreOptions.setVisibility(View.VISIBLE);
        }

        if (mViewModel.getItemsToDelete().contains(product)) {
            cardHolder.findViewById(R.id.uncheckedCircle).setBackground(checkedCircle);
            checkIfAllSelected();
            return;
        }



        checkIfAllSelected();
        cardHolder.findViewById(R.id.uncheckedCircle).setBackground(null);


    }

    public void empryBin(){
        actcivateDeleteMode();
        selectAll();
        askDeleteBusiness();
    }


    public void checkIfAllSelected() {
        if (selectAllIcon == null)return;

        if (mViewModel.isAllSelected()) {
            selectAllIcon.setIcon(R.drawable.checked_circle);
        } else {
            selectAllIcon.setIcon(R.drawable.unchked_circle);
        }
    }

    public void restoreBusinesses() {

        showProgressBar(getString(R.string.restoring));
        mViewModel.restoreItems(this::hideProgressBar, getString(R.string.recycle_bin));

    }





    public void askDeleteBusiness() {
        System.out.println("Ask whether delete businiesses");
        String title = getString(R.string.delete_businesses_warn);
        AskForActionDialog askWhetherDeleteDialog = new
                AskForActionDialog(title);
        askWhetherDeleteDialog.setButtonListener(this::deleteOrCancel);
        askWhetherDeleteDialog.show(getActivity().getSupportFragmentManager(), "ask to delete business");

    }


    public void deleteOrCancel(boolean response){
        if(response){
            showProgressBar(getString(R.string.deleting));
            mViewModel.deleteItems(this::hideProgressBar, getString(R.string.recycle_bin));
        }else {
            desactivateDeleteMode();
        }
    }


    public void showProgressBar(String title) {


        MutableLiveData<Integer> progress =mViewModel.getDeleteProgressLiveData();
        MutableLiveData<String> deletedBusiness = mViewModel.getDeletedItemsLiveData();

        progressBarDialog = new ProgressBarDialog(
                title, getViewLifecycleOwner(), progress, deletedBusiness);
        progressBarDialog.setAction(new ListenResponse() {
            @Override
            public void isSuccessfull(boolean cancelDeleteProcess) {
                if(cancelDeleteProcess){
                    mViewModel.cancelDeleteProcess();
                    desactivateDeleteMode();
                }
            }
        });

        progressBarDialog.show(getParentFragmentManager(), "progress bar");

    }


    public void hideProgressBar(boolean result) {
        if(!result){
            //Show result
            return;
        }

        if(progressBarDialog != null){
            progressBarDialog.dismiss();

            getActivity().runOnUiThread(()->{
                desactivateDeleteMode();
            });
        }

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        selectAllIcon = toolbar.getMenu().findItem(R.id.select_all);

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit_bin:
                    actcivateDeleteMode();
                    return true;
                case R.id.all:
                    if (mViewModel.isAllSelected()) {
                        unSelectAll();
                    } else {
                        selectAll();
                    }
                    // Save profile changes.
                    return true;
                case R.id.empty_bin:
                    empryBin();
                    return true;
                default:
                    return false;
            }
        });


        mViewModel.getRecycledProductLiveData(currentBusinessId).observe(getViewLifecycleOwner(),(list)->{
            if (list.isEmpty()){
                toolbar.getMenu().clear();
            }else {
                if (toolbar.getMenu()!= null)return;
                toolbar.inflateMenu(R.menu.actions_toolbar_user_bin);
            }
        });

        deleteOrRestoreOptions.setOnItemSelectedListener((item)->{
            switch (item.getItemId()){
                case R.id.delete:
                    askDeleteBusiness();
                    return true;
                case R.id.restore:
                    restoreBusinesses();
                    return true;
                default :
                    return false;
            }
        });
    }


    public void selectAll() {
        selectAllIcon.setIcon(R.drawable.checked_circle);
        mViewModel.setCheckedOrUncheckedCirclLivedata(checkedCircle);
        mViewModel.selectAll(mViewModel.parseBusinessListToGeneric());
        deleteOrRestoreOptions.setVisibility(View.VISIBLE);


    }


    public void unSelectAll() {
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        mViewModel.setCheckedOrUncheckedCirclLivedata(null);
        mViewModel.unSelectAll();
        deleteOrRestoreOptions.setVisibility(View.GONE);
    }

}