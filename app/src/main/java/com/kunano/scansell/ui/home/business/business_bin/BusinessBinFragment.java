package com.kunano.scansell.ui.home.business.business_bin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import com.kunano.scansell.components.ProcessItemsComponent;
import com.kunano.scansell.components.ProgressBarDialog;
import com.kunano.scansell.components.ViewModelListener;
import com.kunano.scansell.model.Home.product.Product;
import com.kunano.scansell.ui.home.bin.DeleteOrRestoreOptions;
import com.kunano.scansell.ui.home.business.ProductCardAdapter;
import com.kunano.scansell.MainActivityViewModel;
import com.kunano.scansell.R;
import com.kunano.scansell.databinding.FragmentBusinessBinBinding;

import java.util.LinkedHashSet;
import java.util.List;

public class BusinessBinFragment extends Fragment {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProductCardAdapter productCardAdepter;

    private BusinessBinViewModel mViewModel;
    private View deleteOrRestoreOptions;
    private Drawable checkedCircle;
    private Drawable uncheckedCircle;
    private MenuItem selectAllIcon;

    private ProgressBarDialog progressBarDialog;

    MainActivityViewModel mainActivityViewModel;
    long currentBusinessId;
    private FragmentBusinessBinBinding binding;
    private View empty_bin_layout;
    private ImageButton deleteButton;
    private ImageButton restoreButton;

    private ProcessItemsComponent<Product> productProcessItemsComponent;


    public BusinessBinFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productProcessItemsComponent = new ProcessItemsComponent<>(this);
    }

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
        deleteOrRestoreOptions = binding.deleteOrRestoreOption.binBottomSheet;
        deleteButton = binding.deleteOrRestoreOption.deleteImageButton;
        restoreButton = binding.deleteOrRestoreOption.restoreImageButton;
        recyclerView = binding.recycledBusinessList;
        empty_bin_layout = binding.emptyBinLayout.emptyBinLayout;



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
                if (productProcessItemsComponent.isProcessItemActive()) {
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
            public void onShortTap(Product product, ProductCardAdapter.CardHolder cardHolder) {

                if (productProcessItemsComponent.isProcessItemActive()){
                    mViewModel.shortTap(product, productProcessItemsComponent);
                    checkCard(cardHolder, product);
                }
            }

            @Override
            public void onLongTap(Product product, ProductCardAdapter.CardHolder cardHolder) {
                if (!productProcessItemsComponent.isProcessItemActive()) {
                    activateDeleteMode();
                }
                mViewModel.longTap(product, productProcessItemsComponent);
                checkCard(cardHolder, product);


            }

            @Override
            public void getCardHolderOnBind(ProductCardAdapter.CardHolder cardHolder, Product prod) {
                //mViewModel.setDaysLeftToBeDeleted(prod.getProductId());
                if (productProcessItemsComponent.isProcessItemActive())checkCard(cardHolder, prod);
            }

            @Override
            public void reciveCardHol(ProductCardAdapter.CardHolder cardHolder) {
               mViewModel.getCheckedOrUncheckedCircleLivedata().observe(getViewLifecycleOwner(),
                        cardHolder.getUnCheckedCircle()::setBackground);

                ImageButton imageButton = cardHolder.getRestoreButton();


               mViewModel.getRestoreButtonVisibilityLiveData().observe(getViewLifecycleOwner(),
                        imageButton::setVisibility
                );


              /*TextView textViewDaysLeft = cardHolder.getCardView().findViewById(R.id.daysLeftProduct);
                textViewDaysLeft.setVisibility(View.VISIBLE);
                mViewModel.getDaysLeftTobeDeletedLiveDate().observe(getViewLifecycleOwner(), (d) -> {
                    textViewDaysLeft.setText((CharSequence) d);
                });*/

                //If it is all selected, then, the backgrounds of the product cards turn black transparent
                mViewModel.getCheckedOrUncheckedCircleLivedata().observe(getViewLifecycleOwner(),
                        (icon)->{
                            if (icon != null){
                                cardHolder.getCardView().setCardBackgroundColor(ContextCompat.
                                        getColor(getContext(), R.color.black_transparent));
                                return;
                            }
                            cardHolder.getCardView().setCardBackgroundColor(ContextCompat.getColor(getContext(),
                                    R.color.cardBackgroundColor));
                        });

                //cardHolder.getCardView().findViewById(R.id.dayLeftBackground).setVisibility(View.VISIBLE);
            }

            @Override
            public void onRestore(Product product) {
                mViewModel.restoreSingleProduct(product);
            }

            @Override
            public void onListChanged() {

            }

        });


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

    private void handlerBackPress(){
        if (productProcessItemsComponent.isProcessItemActive()) {
            desactivateDeleteMode();
            return;
        }


        mainActivityViewModel.showBottomNavBar();
        NavDirections action = BusinessBinFragmentDirections.actionBusinessBinFragment2ToBusinessFragment2(currentBusinessId);
        Navigation.findNavController(getView()).navigate(action);
    }

    public void showDeleteOrRestoreOptions() {
        DeleteOrRestoreOptions bottomSheetFragment = new DeleteOrRestoreOptions();
        bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
    }


    private void setToolbarSubtitle(List<Product> businessList) {
        toolbar.setSubtitle(Integer.toString(businessList.size()).
                concat(" ").
                concat(getString(R.string.current_products)));
    }


    public void activateDeleteMode() {

        mViewModel.setToolBarTitle("0");
        productProcessItemsComponent.setProcessItemActive(true);
        toolbar.setSubtitle("");
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.delete_mode_user_bin);
        selectAllIcon = toolbar.getMenu().findItem(R.id.all);
        mainActivityViewModel.hideBottomNavBar();
        mViewModel.setRestoreButtonVisibilityLiveData(View.GONE);

    }

    public void desactivateDeleteMode() {
        productProcessItemsComponent.setProcessItemActive(false);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.actions_toolbar_user_bin);
        deleteOrRestoreOptions.setVisibility(View.GONE);
        mainActivityViewModel.showBottomNavBar();
        mViewModel.setToolBarTitle(getString(R.string.recycle_bin));
        mViewModel.setCheckedOrUncheckedCircleLivedata(null);
        mViewModel.setRestoreButtonVisibilityLiveData(View.VISIBLE);

    }


    public void checkCard(ProductCardAdapter.CardHolder cardHolder, Product product) {
        if (productProcessItemsComponent.getItemsToProcess().isEmpty()){
            deleteOrRestoreOptions.setVisibility(View.GONE);
        }else {
            deleteOrRestoreOptions.setVisibility(View.VISIBLE);
        }

        if (productProcessItemsComponent.isItemToBeProcessed(product)) {
            cardHolder.getUnCheckedCircle().setBackground(checkedCircle);
            checkIfAllSelected();
            cardHolder.getCardView().setCardBackgroundColor(ContextCompat.
                    getColor(getContext(), R.color.black_transparent));
            return;
        }
        cardHolder.getCardView().setCardBackgroundColor(ContextCompat.getColor(getContext(),
                R.color.cardBackgroundColor));



        checkIfAllSelected();
        cardHolder.getUnCheckedCircle().setBackground(null);


    }

    public void empryBin(){
        activateDeleteMode();
        selectAll();
        askDeleteBusiness(getView());
    }


    public void checkIfAllSelected() {
        if (selectAllIcon == null)return;

        if (productProcessItemsComponent.getItemsToProcess().size() == productCardAdepter.getCurrentList().size()) {
            selectAllIcon.setIcon(R.drawable.checked_circle);
            productProcessItemsComponent.setAllSelected(true);
        } else {
            selectAllIcon.setIcon(R.drawable.unchked_circle);
            productProcessItemsComponent.setAllSelected(false);
        }
    }

    public void restoreBusinesses(View view) {

        productProcessItemsComponent.restoreItems(new ViewModelListener<Void>() {
            @Override
            public void result(Void object) {
                getActivity().runOnUiThread(()->desactivateDeleteMode());
            }
        });

    }


    public void askDeleteBusiness(View view) {
        productProcessItemsComponent.deleteItems(new ViewModelListener<Void>() {
            @Override
            public void result(Void object) {
                getActivity().runOnUiThread(()->desactivateDeleteMode());
            }
        });

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        deleteButton.setOnClickListener(this::askDeleteBusiness);
        restoreButton.setOnClickListener(this::restoreBusinesses);

        mViewModel.getRecycledProductLiveData(currentBusinessId).observe(getViewLifecycleOwner(), this::setToolbarSubtitle);
        mViewModel.getToolBarTitle().observe(getViewLifecycleOwner(), toolbar::setTitle);

        mViewModel.getRecycledProductLiveData(currentBusinessId).observe(getViewLifecycleOwner(),
                (l)->{empty_bin_layout.setVisibility(l.size()>0?View.GONE:View.VISIBLE);});

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit_bin:
                    activateDeleteMode();
                    return true;
                case R.id.all:
                    if (productProcessItemsComponent.isAllSelected()) {
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

    }


    public void selectAll() {
        selectAllIcon.setIcon(R.drawable.checked_circle);
        mViewModel.setCheckedOrUncheckedCircleLivedata(checkedCircle);
        productProcessItemsComponent.setItemsToProcess(new LinkedHashSet<>(productCardAdepter.getCurrentList()));
        productProcessItemsComponent.setAllSelected(true);
        deleteOrRestoreOptions.setVisibility(View.VISIBLE);
        mViewModel.setToolBarTitle(String.valueOf(productProcessItemsComponent.getItemsToProcess().size()));


    }


    public void unSelectAll() {
        selectAllIcon.setIcon(R.drawable.unchked_circle);
        mViewModel.setCheckedOrUncheckedCircleLivedata(null);
        productProcessItemsComponent.clearItemsToProcess();
        deleteOrRestoreOptions.setVisibility(View.GONE);
        mViewModel.setToolBarTitle(String.valueOf(productProcessItemsComponent.getItemsToProcess().size()));
    }

}