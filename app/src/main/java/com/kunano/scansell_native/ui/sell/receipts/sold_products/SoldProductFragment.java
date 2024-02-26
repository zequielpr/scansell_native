package com.kunano.scansell_native.ui.sell.receipts.sold_products;

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
import com.kunano.scansell_native.databinding.FragmentSoldProductBinding;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.ui.sell.SellViewModel;
import com.kunano.scansell_native.ui.sell.adapters.ProductToSellAdapter;

public class SoldProductFragment extends Fragment {

    private SoldProductViewModel mViewModel;

    private SellViewModel sellViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private ProductToSellAdapter soldProductAdapter;
    private RecyclerView soldProductRecycleView;

    private FragmentSoldProductBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        sellViewModel = new ViewModelProvider(requireActivity()).get(SellViewModel.class);


        binding = FragmentSoldProductBinding.inflate(inflater, container, false);
        soldProductRecycleView = binding.soldProductRecycleView;
        soldProductRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        soldProductRecycleView.setHasFixedSize(true);
        soldProductAdapter = new ProductToSellAdapter();
        soldProductAdapter.setActivityParent(getActivity());
        soldProductRecycleView.setAdapter(soldProductAdapter);

        sellViewModel.getSoldProducts().observe(getViewLifecycleOwner(), soldProductAdapter::submitList);
        mainActivityViewModel.setHandleBackPress(this::handleBackPress);
        setCardListener();

        return binding.getRoot();

    }

    private void handleBackPress(){
        navigateBack(getView());
    }

    private void navigateBack(View view){
        NavDirections navDirections = SoldProductFragmentDirections.actionSoldProductFragment2ToReceiptsFragment2();
        Navigation.findNavController(getView()).navigate(navDirections);
        mainActivityViewModel.setHandleBackPress(null);
    }



    private void setCardListener(){
        if(soldProductAdapter == null)return;

        soldProductAdapter.setListener(new ProductToSellAdapter.OnclickProductCardListener() {
            @Override
            public void onShortTap(Product product, View cardHolder) {

            }

            @Override
            public void onLongTap(Product product, View cardHolder) {

            }

            @Override
            public void getCardHolderOnBind(View cardHolder, Product prod) {

            }

            @Override
            public void reciveCardHol(View cardHolder) {

            }

            @Override
            public void onCancel(Product product) {

            }
        });
    }


}