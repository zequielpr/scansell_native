package com.kunano.scansell_native.ui.sell.receipts;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.components.ProcessItemsComponent;
import com.kunano.scansell_native.components.ViewModelListener;
import com.kunano.scansell_native.databinding.FragmentReceiptsBinding;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.ui.sell.SellViewModel;

import java.util.LinkedHashSet;

public class ReceiptsFragment extends Fragment implements MenuProvider {

    private ReceiptsViewModel receiptsViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private FragmentReceiptsBinding binding;
    private SellViewModel sellViewModel;
    private ReceiptAdapter receiptAdapter;
    private RecyclerView recyclerViewReceipt;
    private Toolbar toolbar;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private static final String DELETE_AFTER_15_DAYS = "DELETE_AFTER_15_DAYS";
    private static final String DELETE_AFTER_30_DAYS = "DELETE_AFTER_30_DAYS";
    private static final String NEVER = "NEVER";
    private static final String DELETE_MODE = "DELETE_MODE";
    private SearchView searchView;

    private  ProcessItemsComponent<Receipt> processItemsComponent;
    private Drawable checkedCircle;
    private Drawable unCheckedCircle;

    private View receiptEmptySectionLayout;

    public ReceiptsFragment() {
    }

    public static ReceiptsFragment newInstance() {
        return new ReceiptsFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        processItemsComponent = new ProcessItemsComponent<>(this);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        sellViewModel = new ViewModelProvider(requireActivity()).get(SellViewModel.class);
        receiptsViewModel = new ViewModelProvider(getActivity()).get(ReceiptsViewModel.class);
        binding = FragmentReceiptsBinding.inflate(inflater, container, false);


        checkedCircle = ContextCompat.getDrawable(getContext(), R.drawable.checked_circle);
        unCheckedCircle = ContextCompat.getDrawable(getContext(), R.drawable.unchked_circle);

        recyclerViewReceipt = binding.receiptRecycleView;
        receiptEmptySectionLayout = binding.notReceiptLayout.receiptEmptySection;
        recyclerViewReceipt.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReceipt.setHasFixedSize(true);
        receiptAdapter = new ReceiptAdapter();
        recyclerViewReceipt.setAdapter(receiptAdapter);
        toolbar = binding.receiptToolbar;

        sellViewModel.getReceipts().observe(getViewLifecycleOwner(), receiptAdapter::submitList);

        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                        handleBackPress();
                    }
                });

        setCardListener();
        return binding.getRoot();
    }


    private void handleBackPress(){
        navigateBack(getView());
    }

    private void navigateBack(View view){
        if (processItemsComponent.isProcessItemActive()){
            desActivateDeleteMode();
            return;
        }
       if(receiptsViewModel.getIsSearchModeActive().getValue()){
            receiptsViewModel.setIsSearchModeActive(false);
            if(searchView != null){
                searchView.onActionViewCollapsed();
            };
            return;
        }

        NavDirections navDirections = ReceiptsFragmentDirections.actionReceiptsFragment2ToSellFragment();
        Navigation.findNavController(getView()).navigate(navDirections);
    }

    private void navigateToSoldProducts(){
        NavDirections navDirections = ReceiptsFragmentDirections.actionReceiptsFragment2ToSoldProductFragment2(
                sellViewModel.getCurrentBusinessId(), sellViewModel.getCurrentReceiptId());
        Navigation.findNavController(getView()).navigate(navDirections);
    }



    private void setCardListener(){
        if (receiptAdapter == null) return;

        receiptAdapter.setListener(new  OnclickReceiptCardListener() {
            @Override
            public void onShortTap(Receipt receipt,ReceiptAdapter.CardHolder cardHolder) {

               if (processItemsComponent.isProcessItemActive()){
                   addOrRemoveItemToProcess(receipt);
                   checkIfReceiptIsChecked(receipt, cardHolder);
                   return;
               }
                sellViewModel.setCurrentReceiptId(receipt.getReceiptId());
                navigateToSoldProducts();
            }

            @Override
            public void onLongTap(Receipt receipt, ReceiptAdapter.CardHolder cardHolder) {

                if (processItemsComponent.isProcessItemActive()){
                    addOrRemoveItemToProcess(receipt);
                    checkIfReceiptIsChecked(receipt, cardHolder);
                    return;
                }
                activateDeleteMode();
                addOrRemoveItemToProcess(receipt);
                checkIfReceiptIsChecked(receipt, cardHolder);
                //addOrRemoveItemToProcess(receipt);
                //checkIfReceiptIsChecked(receipt, cardHolder);


            }

            @Override
            public void getCardHolderOnBind(ReceiptAdapter.CardHolder cardHolder, Receipt receipt) {
                if (processItemsComponent.isProcessItemActive()){
                    checkIfReceiptIsChecked(receipt, cardHolder);
                }
                String daysLeft = receiptsViewModel.calculateDaysTobeDeleted(receipt.getSellingDate());
                cardHolder.getDaysLeft().setText(daysLeft);

            }

            @Override
            public void reciveCardHol(ReceiptAdapter.CardHolder cardHolder) {

                receiptsViewModel.getReceiptCardBackgroundColor().observe(getViewLifecycleOwner(),
                        cardHolder.getCardView()::setCardBackgroundColor);

                ImageView imageView = cardHolder.getCheckIndicator();
                receiptsViewModel.getAllSelectedIconMutableLiveData().observe(getViewLifecycleOwner(),
                        imageView::setImageDrawable);
            }

            @Override
            public void onDelete(Receipt receipt) {

            }
        });
    }




    private void activateDeleteMode(){
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.toolbar_delete_mode_menu);
        selectAllMenuItem = toolbar.getMenu().findItem(R.id.select_all_button);
        deleteMenuItem = toolbar.getMenu().findItem(R.id.delete_button);
        processItemsComponent.setProcessItemActive(true);
        mainActivityViewModel.hideBottomNavBar();
        receiptsViewModel.getSelectedItemQuantityMutableLiveData().observe(getViewLifecycleOwner(),
                (i) ->{toolbar.setTitle(String.valueOf(i));});
    }

    private MenuItem selectAllMenuItem;
    private MenuItem deleteMenuItem;
    private void desActivateDeleteMode(){
        unSelectAllItems();
        processItemsComponent.setProcessItemActive(false);
        mainActivityViewModel.showBottomNavBar();
        receiptsViewModel.getSelectedItemQuantityMutableLiveData().removeObservers(getViewLifecycleOwner());
        toolbar.setTitle(getString(R.string.receipts));
        toolbar.addMenuProvider(this);
    }

    private void addOrRemoveItemToProcess(Receipt receipt){
        boolean isAdded = receiptsViewModel.checkIfReceiptIsChecked(receipt, processItemsComponent);
        System.out.println("Is added: " + isAdded);
        if(isAdded){
            processItemsComponent.removeItemToProcess(receipt);
        }else {
          processItemsComponent.addItemToProcess(receipt);
        }

        receiptsViewModel.setSelectedItemQuantityMutableLiveData(processItemsComponent.getItemsToProcess().size());
    }


    private void checkIfReceiptIsChecked(Receipt receipt, ReceiptAdapter.CardHolder cardHolder){
        boolean isChecked = receiptsViewModel.checkIfReceiptIsChecked(receipt, processItemsComponent);

        if(isChecked){
            checkCard(cardHolder);
        }else {
            unCheckCard(cardHolder);
        }

        if (receiptAdapter.getCurrentList().size() == processItemsComponent.getItemsToProcess().size()){
            processItemsComponent.setAllSelected(true);
            selectAllMenuItem.setIcon(checkedCircle);
        }else {
            processItemsComponent.setAllSelected(false);
            selectAllMenuItem.setIcon(unCheckedCircle);
        }

        if (processItemsComponent.getItemsToProcess().isEmpty()){
            deleteMenuItem.setVisible(false);
        }else {
            deleteMenuItem.setVisible(true);
        }


    }

    private void checkCard(ReceiptAdapter.CardHolder cardHolder){
        cardHolder.getCheckIndicator().setImageDrawable(checkedCircle);
        cardHolder.getCardView().setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_transparent));

    }
    private void unCheckCard(ReceiptAdapter.CardHolder cardHolder){
        cardHolder.getCheckIndicator().setImageDrawable(null);
        cardHolder.getCardView().setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardBackgroundColor));
    }

    private void selectAllItems(){
        receiptsViewModel.setAllSelectedIconMutableLiveData(checkedCircle);
        receiptsViewModel.setReceiptCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_transparent));
        LinkedHashSet<Receipt> receipts = new LinkedHashSet<>(receiptAdapter.getCurrentList());
        processItemsComponent.setItemsToProcess(receipts);
        selectAllMenuItem.setIcon(checkedCircle);
        processItemsComponent.setAllSelected(true);
        deleteMenuItem.setVisible(true);
        receiptsViewModel.setSelectedItemQuantityMutableLiveData(processItemsComponent.getItemsToProcess().size());
    }

    private void unSelectAllItems(){
        receiptsViewModel.setAllSelectedIconMutableLiveData(null);
        receiptsViewModel.setReceiptCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardBackgroundColor));
        processItemsComponent.clearItemsToProcess();
        selectAllMenuItem.setIcon(unCheckedCircle);
        processItemsComponent.setAllSelected(false);
        deleteMenuItem.setVisible(false);
        receiptsViewModel.setSelectedItemQuantityMutableLiveData(processItemsComponent.getItemsToProcess().size());
    }





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.addMenuProvider(this);
        sellViewModel.getEmptyReceiptSectionVisibility().observe(getViewLifecycleOwner(),
                receiptEmptySectionLayout::setVisibility);
    }


    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        toolbar.getMenu().clear();
        menuInflater.inflate(R.menu.receipt_tool_bar, menu);

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(this::navigateBack);
        searchView = (SearchView) toolbar.getMenu().findItem(R.id.search_action).getActionView();


        searchView.setOnSearchClickListener((v)->receiptsViewModel.setIsSearchModeActive(true));
        searchView.setOnCloseListener(()->{
            searchView.onActionViewCollapsed();
            receiptsViewModel.setIsSearchModeActive(false);
            return true;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sellViewModel.searchReceipt(newText);
                return false;
            }
        });

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        /*String deleteMode = sharedPref.getString(DELETE_MODE, NEVER);


        switch (deleteMode){
            case DELETE_AFTER_15_DAYS:
                toolbar.getMenu().findItem(R.id.delete_15_days).setChecked(true);
                break;
            case DELETE_AFTER_30_DAYS:
                toolbar.getMenu().findItem(R.id.delete_30_days).setChecked(true);
                break;
            default:
                toolbar.getMenu().findItem(R.id.never_delete).setChecked(true);

        }*/
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
      /*      case R.id.delete_15_days:
                setDeleteMode(DELETE_AFTER_15_DAYS);
                menuItem.setChecked(true);
                return true;
            case R.id.delete_30_days:
                setDeleteMode(DELETE_AFTER_30_DAYS);
                menuItem.setChecked(true);
                return true;
            case R.id.never_delete:
                setDeleteMode(NEVER);
                menuItem.setChecked(true);
                return true;*/
            case R.id.delete_button:
                processItemsComponent.deleteItems(new ViewModelListener<Void>() {
                    @Override
                    public void result(Void object) {
                        getActivity().runOnUiThread(ReceiptsFragment.this::desActivateDeleteMode);
                        receiptsViewModel.setSelectedItemQuantityMutableLiveData(processItemsComponent.getItemsToProcess().size());
                    }
                });
                return true;
            case R.id.select_all_button:
                if (processItemsComponent.isAllSelected()) {
                    unSelectAllItems();
                } else {
                    selectAllItems();
                }
                return true;
            case R.id.search_action:
                return true;
            default:

                return false;
        }
    }


   /* private void setDeleteMode(String deleteMode){
        if(editor != null){
            editor.putString(DELETE_MODE, deleteMode);
            editor.apply();
        }
    }*/
}