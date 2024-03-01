package com.kunano.scansell_native.ui.sell.receipts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentReceiptsBinding;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.ui.sell.SellViewModel;

public class ReceiptsFragment extends Fragment{

    private ReceiptsViewModel receiptsViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private FragmentReceiptsBinding binding;
    private SellViewModel sellViewModel;
    private ReceiptAdapter receiptAdapter;
    private RecyclerView recyclerViewReceipt;
    private Toolbar toolbar;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private static String DELETE_AFTER_15_DAYS = "DELETE_AFTER_15_DAYS";
    private SearchView searchView;

    public static ReceiptsFragment newInstance() {
        return new ReceiptsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        sellViewModel = new ViewModelProvider(requireActivity()).get(SellViewModel.class);
        receiptsViewModel = new ViewModelProvider(getActivity()).get(ReceiptsViewModel.class);
        binding = FragmentReceiptsBinding.inflate(inflater, container, false);

        recyclerViewReceipt = binding.receiptRecycleView;
        recyclerViewReceipt.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReceipt.setHasFixedSize(true);
        receiptAdapter = new ReceiptAdapter();
        recyclerViewReceipt.setAdapter(receiptAdapter);
        toolbar = binding.receiptToolbar;

        sellViewModel.getReceipts().observe(getViewLifecycleOwner(), receiptAdapter::submitList);

        mainActivityViewModel.setHandleBackPress(this::handleBackPress);

        setCardListener();
        return binding.getRoot();
    }


    private void handleBackPress(){
        naVigateBack(getView());
    }

    private void naVigateBack(View view){
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
            public void onShortTap(Receipt receipt, View cardHolder) {
                sellViewModel.setCurrentReceiptId(receipt.getReceiptId());
                navigateToSoldProducts();
            }

            @Override
            public void onLongTap(Receipt receipt, View cardHolder) {

            }

            @Override
            public void getCardHolderOnBind(ReceiptAdapter.CardHolder cardHolder, Receipt receipt) {
                String daysLeft = receiptsViewModel.calculateDaysTobeDeleted(receipt.getSellingDate());
                cardHolder.getDaysLeft().setText(daysLeft);

            }

            @Override
            public void reciveCardHol(View cardHolder, Receipt receipt) {

            }

            @Override
            public void onDelete(Receipt receipt) {
                askToDelete(receipt);
            }
        });
    }


    public void askToDelete(Receipt receipt){
        AskForActionDialog askForActionDialog = new AskForActionDialog(getLayoutInflater(), getString(R.string.delete));
        askForActionDialog.setButtonListener(new ListenResponse() {
            @Override
            public void isSuccessfull(boolean resultado) {
                if (resultado){
                    sellViewModel.deleteReceipt(receipt);
                }
            }
        });
        askForActionDialog.show(getParentFragmentManager(), getString(R.string.delete));
    }





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.inflateMenu(R.menu.receipt_tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(this::naVigateBack);
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

        boolean isDeleteAfter15Active = sharedPref.getBoolean(DELETE_AFTER_15_DAYS, true);

        if (isDeleteAfter15Active){
            toolbar.getMenu().findItem(R.id.delete_15_days).setChecked(true);
        }else {
            toolbar.getMenu().findItem(R.id.delete_30_days).setChecked(true);
        }




        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_15_days:
                        deletePeridio15Days(true);
                        item.setChecked(true);
                        return true;
                    case R.id.delete_30_days:
                        deletePeridio15Days(false);
                        item.setChecked(true);
                        return true;
                    default:

                        return false;
                }
            }
        });

    }

    private void deletePeridio15Days(Boolean delete_after_15_days){
        if(editor != null){
            editor.putBoolean(DELETE_AFTER_15_DAYS,delete_after_15_days);
            editor.apply();
        }
    }

}