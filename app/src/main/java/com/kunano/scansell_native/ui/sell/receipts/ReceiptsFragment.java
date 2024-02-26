package com.kunano.scansell_native.ui.sell.receipts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.databinding.FragmentReceiptsBinding;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.ui.sell.SellViewModel;

public class ReceiptsFragment extends Fragment {

    private ReceiptsViewModel mViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private FragmentReceiptsBinding binding;
    private SellViewModel sellViewModel;
    private ReceiptAdapter receiptAdapter;
    private RecyclerView recyclerViewReceipt;

    public static ReceiptsFragment newInstance() {
        return new ReceiptsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        sellViewModel = new ViewModelProvider(requireActivity()).get(SellViewModel.class);


        binding = FragmentReceiptsBinding.inflate(inflater, container, false);

        recyclerViewReceipt = binding.receiptRecycleView;
        recyclerViewReceipt.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReceipt.setHasFixedSize(true);
        receiptAdapter = new ReceiptAdapter();
        recyclerViewReceipt.setAdapter(receiptAdapter);

        sellViewModel.getReceipts().observe(getViewLifecycleOwner(), receiptAdapter::submitList);

        mainActivityViewModel.setHandleBackPress(this::handleBackPress);

        setCardListener();
        return binding.getRoot();
    }




    private void handleBackPress(){
        naVigateBack(getView());
    }

    private void naVigateBack(View view){
        NavDirections navDirections = ReceiptsFragmentDirections.actionReceiptsFragment2ToSellFragment();
        Navigation.findNavController(getView()).navigate(navDirections);
    }

    private void navigateToSoldProducts(){
        NavDirections navDirections = ReceiptsFragmentDirections.actionReceiptsFragment2ToSoldProductFragment2();
        Navigation.findNavController(getView()).navigate(navDirections);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReceiptsViewModel.class);
        // TODO: Use the ViewModel
    }


    private void setCardListener(){
        if (receiptAdapter == null) return;

        receiptAdapter.setListener(new OnclickProductCardListener() {
            @Override
            public void onShortTap(Receipt receipt, View cardHolder) {
                sellViewModel.setCurrentReceiptId(receipt.getReceiptId());
                navigateToSoldProducts();
            }

            @Override
            public void onLongTap(Receipt receipt, View cardHolder) {

            }

            @Override
            public void getCardHolderOnBind(View cardHolder, Receipt receipt) {

            }

            @Override
            public void reciveCardHol(View cardHolder) {

            }

            @Override
            public void onDelete(Receipt receipt) {
                sellViewModel.deleteReceipt(receipt);
            }
        });
    }

}