package com.kunano.scansell_native.ui.home.bin;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentUserBinBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.components.ProgressBarDialog;
import com.kunano.scansell_native.ui.components.ViewModelListener;
import com.kunano.scansell_native.ui.home.BusinessCardAdepter;
import com.kunano.scansell_native.ui.home.HomeViewModel;
import com.kunano.scansell_native.ui.sell.receipts.dele_component.ProcessItemsComponent;

import java.util.LinkedHashSet;
import java.util.List;

public class UserBinFragment extends Fragment {
    private FragmentUserBinBinding binding;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private BusinessCardAdepter businessCardAdepter;

    private UserBinViewModel mViewModel;
    View deleteOrRestoreOptions;
    private Drawable checkedCircle;
    private Drawable uncheckedCircle;
    private MenuItem selectAllIcon;

    private ProgressBarDialog progressBarDialog;

    MainActivityViewModel mainActivityViewModel;
    HomeViewModel homeViewModel;

    private ImageButton deleteButton;
    private ImageButton restoreButton;
    private View empty_bin_layout;

    private ProcessItemsComponent<Business> businessProcessItemsComponent;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        businessProcessItemsComponent = new ProcessItemsComponent<>(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinBinding.inflate(inflater, container, false);


        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mViewModel = new ViewModelProvider(this).get(UserBinViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        toolbar = binding.binToolbar;
        deleteOrRestoreOptions = binding.deleteOrRestoreOption.binBottomSheet;
        deleteButton = binding.deleteOrRestoreOption.deleteImageButton;
        restoreButton = binding.deleteOrRestoreOption.restoreImageButton;
        recyclerView = binding.recycledBusinessList;
        empty_bin_layout = binding.emptyBinLayout.emptyBinLayout;


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
            public void onShortTap(Business business, BusinessCardAdepter.CardHolder cardHolder) {
                if (businessProcessItemsComponent.isProcessItemActive()) {
                    mViewModel.shortTap(business, businessProcessItemsComponent);
                    checkCard(cardHolder, business);
                }
            }

            @Override
            public void onLongTap(Business business, BusinessCardAdepter.CardHolder cardHolder) {
                if (!businessProcessItemsComponent.isProcessItemActive()) {
                    actcivateDeleteMode();
                }
                mViewModel.longTap(business, businessProcessItemsComponent);
                checkCard(cardHolder, business);


            }

            @Override
            public void getCardHolderOnBind(BusinessCardAdepter.CardHolder cardHolder, Business business) {

                if (businessProcessItemsComponent.isProcessItemActive())
                    checkCard(cardHolder, business);

                TextView quantity = cardHolder.getNumProducts();

                homeViewModel.getQuantityOfProductsInBusiness(business.getBusinessId()).observe(
                        getViewLifecycleOwner(), (q) -> {

                            String label = getString(R.string.current_products);
                            label = q < 2 ? label.substring(0, label.length() - 1) : label;
                            quantity.setText(String.valueOf(q) + " " + label);
                        }
                );
            }

            @Override
            public void reciveCardHol(BusinessCardAdepter.CardHolder cardHolder) {
                mViewModel.getCheckedOrUncheckedCircleLivedata().observe(getViewLifecycleOwner(),
                        cardHolder.getUnCheckedCircle()::setBackground);

                mViewModel.getRestoreButtonVisibilityLiveData().observe(getViewLifecycleOwner(),
                        cardHolder.getImageButtonRestore()::setVisibility
                );
                mViewModel.getCardBackgroundColor().observe(getViewLifecycleOwner(),
                        cardHolder.getCard()::setCardBackgroundColor);

                /*TextView textViewDaysLeft = cardHolder.getCard().findViewById(R.id.textViewDaysLeftProduct);
                textViewDaysLeft.setVisibility(View.VISIBLE);
                mViewModel.getDaysLeftTobeDeletedLiveDate().observe(getViewLifecycleOwner(), (d) -> {
                    textViewDaysLeft.setText((CharSequence) d);
                });*/
            }

            @Override
            public void onRestore(Business business) {
                mViewModel.restoreSingleBusiness(business);
            }
        });

        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                        handleBackPressed();
                    }
                });


        return binding.getRoot();
    }

    private void handleBackPressed() {
        if (businessProcessItemsComponent.isProcessItemActive()) {
            desactivateDeleteMode();
            return;
        }


        mainActivityViewModel.showBottomNavBar();
        NavDirections action = UserBinFragmentDirections.actionUserBinFragmentToNavigationHome();
        Navigation.findNavController(getView()).navigate(action);
    }


    private void setToolbarSubtitle(List<Business> businessList) {
        toolbar.setSubtitle(Integer.toString(businessList.size()).
                concat(" ").
                concat(getString(R.string.businesses_title)));
    }


    public void actcivateDeleteMode() {

        mViewModel.setSelectedItemsNumbLiveData("0");
        businessProcessItemsComponent.setProcessItemActive(true);
        mViewModel.getSelectedItemsNumbLiveData().observe(getViewLifecycleOwner(), toolbar::setTitle);
        toolbar.setSubtitle("");
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.delete_mode_user_bin);
        selectAllIcon = toolbar.getMenu().findItem(R.id.all);
        mainActivityViewModel.hideBottomNavBar();
        mViewModel.setRestoreButtonVisibilityLiveData(View.GONE);

    }

    public void desactivateDeleteMode() {
        getActivity().runOnUiThread(() -> {
            businessProcessItemsComponent.setProcessItemActive(false);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.actions_toolbar_user_bin);
            mViewModel.getRecycledBusinessLiveData().observe(getViewLifecycleOwner(), this::setToolbarSubtitle);
            deleteOrRestoreOptions.setVisibility(View.GONE);
            mainActivityViewModel.showBottomNavBar();
            mViewModel.setSelectedItemsNumbLiveData(getString(R.string.recycle_bin));
            mViewModel.setCheckedOrUncheckedCircleLivedata(null);
            mViewModel.setCardBackgroundColor(Color.WHITE);
            mViewModel.setRestoreButtonVisibilityLiveData(View.VISIBLE);
            businessProcessItemsComponent.clearItemsToProcess();
        });

    }


    public void checkCard(BusinessCardAdepter.CardHolder cardHolder, Business business) {
        if (businessProcessItemsComponent.getItemsToProcess().isEmpty()) {
            deleteOrRestoreOptions.setVisibility(View.GONE);
        } else {
            deleteOrRestoreOptions.setVisibility(View.VISIBLE);
        }

        if (businessProcessItemsComponent.isItemToBeProcessed(business)) {
            cardHolder.getUnCheckedCircle().setBackground(checkedCircle);
            cardHolder.getCard().setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_transparent));
            System.out.println("Seleccionada" + cardHolder.getCard().getTag());
            checkIfAllSelected();
            return;
        }


        checkIfAllSelected();
        cardHolder.getCard().setCardBackgroundColor(Color.WHITE);
        cardHolder.getUnCheckedCircle().setBackground(null);


    }

    public void empryBin() {
        actcivateDeleteMode();
        selectAll();
        askDeleteBusiness();
    }


    public void checkIfAllSelected() {
        if (selectAllIcon == null) return;

        if (businessProcessItemsComponent.getItemsToProcess().size() == businessCardAdepter.getCurrentList().size()) {
            businessProcessItemsComponent.setAllSelected(true);
            selectAllIcon.setIcon(R.drawable.checked_circle);
        } else {
            businessProcessItemsComponent.setAllSelected(false);
            selectAllIcon.setIcon(R.drawable.unchked_circle);
        }
    }

    public void restoreBusinesses() {
        businessProcessItemsComponent.restoreItems(new ViewModelListener<Void>() {
            @Override
            public void result(Void object) {
                desactivateDeleteMode();
            }
        });

    }


    public void askDeleteBusiness() {
        businessProcessItemsComponent.deleteItems(new ViewModelListener<Void>() {
            @Override
            public void result(Void object) {
                desactivateDeleteMode();
            }
        });

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel.getRecycledBusinessLiveData().observe(getViewLifecycleOwner(), (l) -> {
            empty_bin_layout.setVisibility(l.size() > 0 ? View.GONE : View.VISIBLE);
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (businessProcessItemsComponent.isProcessItemActive()) {
                    desactivateDeleteMode();
                    return;
                }


                mainActivityViewModel.showBottomNavBar();
                NavDirections action = UserBinFragmentDirections.actionUserBinFragmentToNavigationHome();
                Navigation.findNavController(getView()).navigate(action);
            }
        });


        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit_bin:
                    actcivateDeleteMode();
                    return true;
                case R.id.all:
                    if (businessProcessItemsComponent.isAllSelected()) {
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


        mViewModel.getRecycledBusinessLiveData().observe(getViewLifecycleOwner(), (list) -> {
            if (list.isEmpty()) {
                toolbar.getMenu().clear();
            } else {
                if (toolbar.getMenu() != null) return;
                toolbar.inflateMenu(R.menu.actions_toolbar_user_bin);
            }
        });

        deleteButton.setOnClickListener((v) -> askDeleteBusiness());
        restoreButton.setOnClickListener((v) -> restoreBusinesses());
    }


    public void selectAll() {
        selectAllIcon.setIcon(R.drawable.checked_circle);
        mViewModel.setCheckedOrUncheckedCircleLivedata(checkedCircle);
        mViewModel.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_transparent));
        LinkedHashSet<Business> businessList = new LinkedHashSet<>(businessCardAdepter.getCurrentList());
        businessProcessItemsComponent.setItemsToProcess(businessList);
        mViewModel.setSelectedItemsNumbLiveData(String.valueOf(businessList.size()));
        businessProcessItemsComponent.setAllSelected(true);
        deleteOrRestoreOptions.setVisibility(View.VISIBLE);


    }


    public void unSelectAll() {
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        mViewModel.setCheckedOrUncheckedCircleLivedata(null);
        mViewModel.setSelectedItemsNumbLiveData("0");
        mViewModel.setCardBackgroundColor(Color.WHITE);
        businessProcessItemsComponent.clearItemsToProcess();
        deleteOrRestoreOptions.setVisibility(View.GONE);
    }


}