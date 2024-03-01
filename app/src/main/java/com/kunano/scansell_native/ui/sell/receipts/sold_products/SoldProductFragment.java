package com.kunano.scansell_native.ui.sell.receipts.sold_products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentSoldProductBinding;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.ui.sell.SellViewModel;
import com.kunano.scansell_native.ui.sell.adapters.ProductToSellAdapter;

public class SoldProductFragment extends Fragment{

    private SoldProductViewModel mViewModel;

    private SellViewModel sellViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private ProductToSellAdapter soldProductAdapter;
    private RecyclerView soldProductRecycleView;
    private Toolbar toolbar;
    private Receipt receipt;
    private SoldProductViewModel soldProductViewModel;

    private FragmentSoldProductBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        sellViewModel = new ViewModelProvider(requireActivity()).get(SellViewModel.class);
        soldProductViewModel = new ViewModelProvider(this).get(SoldProductViewModel.class);


        binding = FragmentSoldProductBinding.inflate(inflater, container, false);
        soldProductRecycleView = binding.soldProductRecycleView;
        toolbar = binding.soldToolbar;
        soldProductRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        soldProductRecycleView.setHasFixedSize(true);
        soldProductAdapter = new ProductToSellAdapter();
        soldProductAdapter.setActivityParent(getActivity());
        soldProductRecycleView.setAdapter(soldProductAdapter);

        sellViewModel.getCurrentReceiptId();
        sellViewModel.getReceiptByid().observe(getViewLifecycleOwner(), this::populateReceipt);
        sellViewModel.getSoldProducts().observe(getViewLifecycleOwner(), (soldProductsList)->{
            soldProductAdapter.submitList(soldProductsList);
            double spentAmount = soldProductsList.stream().reduce(0.0, (c, sp) ->
                    c + sp.getSelling_price(), Double::sum);
            toolbar.setSubtitle(String.valueOf(spentAmount));
        });
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

    private void populateReceipt(Receipt receipt){
        if (receipt != null){
            this.receipt = receipt;
            toolbar.setTitle(receipt.getReceiptId());
        }
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
                askTodeleteProduct(product);
            }
        });
    }



    private void askTodeleteProduct(Product product){
        AskForActionDialog askForActionDialog = new AskForActionDialog(getLayoutInflater(),
                getString(R.string.delete));

        askForActionDialog.setButtonListener(new ListenResponse() {
            @Override
            public void isSuccessfull(boolean resultado) {
                if (resultado){
                    deleteSoldProduct(product);
                }
            }
        });
        askForActionDialog.show(getChildFragmentManager(), getString(R.string.delete));
    }

    private void deleteSoldProduct(Product product){
        if (receipt != null && product != null){
            soldProductViewModel.cancelProductSell(product, receipt, (resulst)->{
                System.out.println("Product deleted: " + resulst);
            });
        }
    }


    public void onViewCreated(@NonNull View view, @NonNull Bundle savedState){
        super.onViewCreated(view, savedState);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(this::navigateBack);
    }
}