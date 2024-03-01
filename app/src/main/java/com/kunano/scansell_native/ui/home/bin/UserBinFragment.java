package com.kunano.scansell_native.ui.home.bin;

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
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentUserBinBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.ProgressBarDialog;
import com.kunano.scansell_native.ui.home.BusinessCardAdepter;

import java.util.List;

public class UserBinFragment extends Fragment {
    private FragmentUserBinBinding binding;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private BusinessCardAdepter businessCardAdepter;

    private UserBinViewModel mViewModel;
    BottomNavigationView deleteOrRestoreOptions;
    private Drawable checkedCircle;
    private Drawable uncheckedCircle;
    private MenuItem selectAllIcon;

    private  ProgressBarDialog progressBarDialog;

    MainActivityViewModel mainActivityViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinBinding.inflate(inflater, container, false);


        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mViewModel = new ViewModelProvider(this).get(UserBinViewModel.class);


        toolbar = binding.binToolbar;
        deleteOrRestoreOptions = binding.deleteOrRestoreOption;
        recyclerView = binding.recycledBusinessList;


        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);

        businessCardAdepter = new BusinessCardAdepter();
        recyclerView.setAdapter(businessCardAdepter);

        mViewModel.getRecycledBusinessLiveData().observe(getViewLifecycleOwner(), this::setToolbarSubtitle);
        mViewModel.getRecycledBusinessLiveData().observe(getViewLifecycleOwner(), businessCardAdepter::submitList);


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
                NavDirections action = UserBinFragmentDirections.actionUserBinFragmentToNavigationHome();
                Navigation.findNavController(getView()).navigate(action);
            }
        });
        mViewModel.setListenUserBinViewModel(new UserBinViewModel.ListenUserBinViewModel() {
            @Override
            public void requestResult(String message) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                });

            }
        });


        businessCardAdepter.setListener(new BusinessCardAdepter.OnclickBusinessCardListener() {
            @Override
            public void onShortTap(Business business, View cardHolder) {
                mViewModel.shortTap(business);
                if (mViewModel.isDeleteModeActive()) checkCard(cardHolder, business);
            }

            @Override
            public void onLongTap(Business business, View cardHolder) {
                if (!mViewModel.isDeleteModeActive()) {
                    actcivateDeleteMode();
                }
                mViewModel.longTap(business);
                checkCard(cardHolder, business);


            }

            @Override
            public void getCardHolderOnBind(View cardHolder, Business business) {
                mViewModel.setDaysLeftToBeDeleted(business.getBusinessId());
            }

            @Override
            public void reciveCardHol(View cardHolder) {
                mViewModel.getCheckedOrUncheckedCirclLivedata().observe(getViewLifecycleOwner(),
                        cardHolder.findViewById(R.id.checked_unchecked_image_view)::setBackground);

                ImageButton imageButton = cardHolder.findViewById(R.id.imageButtonRestoreFromTrash);


                mViewModel.getRestoreButtonVisibilityLiveData().observe(getViewLifecycleOwner(),
                        imageButton::setVisibility
                );


                TextView textViewDaysLeft = cardHolder.findViewById(R.id.textViewDaysLeftProduct);
                textViewDaysLeft.setVisibility(View.VISIBLE);
                mViewModel.getDaysLeftTobeDeletedLiveDate().observe(getViewLifecycleOwner(), (d) -> {
                    textViewDaysLeft.setText((CharSequence) d);
                });
            }

            @Override
            public void onRestore(Business business) {
                mViewModel.restoreSingleBusiness(business);
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
                NavDirections action = UserBinFragmentDirections.actionUserBinFragmentToNavigationHome();
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


    private void setToolbarSubtitle(List<Business> businessList) {
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
        mViewModel.getRecycledBusinessLiveData().observe(getViewLifecycleOwner(), this::setToolbarSubtitle);
        deleteOrRestoreOptions.setVisibility(View.GONE);
        mainActivityViewModel.showBottomNavBar();
        mViewModel.desactivateDeleteMod(getString(R.string.recycle_bin));
        mViewModel.setCheckedOrUncheckedCirclLivedata(null);
        mViewModel.setRestoreButtonVisibilityLiveData(View.VISIBLE);

    }


    public void checkCard(View cardHolder, Business business) {
        if (mViewModel.getItemsToDelete().isEmpty()){
            deleteOrRestoreOptions.setVisibility(View.GONE);
        }else {
            deleteOrRestoreOptions.setVisibility(View.VISIBLE);
        }

        if (mViewModel.getItemsToDelete().contains(business)) {
            cardHolder.findViewById(R.id.checked_unchecked_image_view).setBackground(checkedCircle);
            System.out.println("Seleccionada" + cardHolder.getTag());
            checkIfAllSelected();
            return;
        }



        checkIfAllSelected();
        cardHolder.findViewById(R.id.checked_unchecked_image_view).setBackground(null);


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
        String title = getString(R.string.delete_businesses_warn);
        AskForActionDialog askWhetherDeleteDialog = new
                AskForActionDialog(getLayoutInflater(), title);
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
        ListenResponse action = (cancelDeleteProcess)->{
            if(cancelDeleteProcess){
                mViewModel.cancelDeleteProcess();
                desactivateDeleteMode();
            }
        };


        MutableLiveData<Integer> progress =mViewModel.getDeleteProgressLiveData();
        MutableLiveData<String> deletedBusiness = mViewModel.getDeletedItemsLiveData();

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

            getActivity().runOnUiThread(()->{
                desactivateDeleteMode();
            });
        }

    }







    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserBinViewModel.class);
        // TODO: Use the ViewModel
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


        mViewModel.getRecycledBusinessLiveData().observe(getViewLifecycleOwner(),(list)->{
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