package com.kunano.scansell.ui.sell.receipts.sold_products;

import static com.kunano.scansell.ui.sell.receipts.sold_products.SoldProductViewModel.CASH_CODE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell.MainActivityViewModel;
import com.kunano.scansell.components.AskForActionDialog;
import com.kunano.scansell.components.Utils;
import com.kunano.scansell.components.ViewModelListener;
import com.kunano.scansell.model.Home.product.Product;
import com.kunano.scansell.model.sell.Receipt;
import com.kunano.scansell.ui.sell.adapters.ProductToSellAdapter;
import com.kunano.scansell.R;
import com.kunano.scansell.databinding.FragmentSoldProductBinding;

import java.math.BigDecimal;

public class SoldProductFragment extends Fragment{
    private MainActivityViewModel mainActivityViewModel;
    private ProductToSellAdapter soldProductAdapter;
    private RecyclerView soldProductRecycleView;
    private Toolbar toolbar;
    private Receipt receipt;
    private SoldProductViewModel soldProductViewModel;

    private FragmentSoldProductBinding binding;
    private Long business_key;
    private String receipt_key;
    private View cashDueLayout;
    private View cashTenderedLayout;
    private TextView paymentMethod;
    private TextView soldItems;
    private TextView cashTendered;
    private TextView cashDue;
    private TextView cashTenderedLabel;


    public SoldProductFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        soldProductViewModel = new ViewModelProvider(this).get(SoldProductViewModel.class);

        Bundle args = getArguments();
        if (args != null){
            business_key = args.getLong("business_key");
            receipt_key = args.getString("receipt_key");

            soldProductViewModel.getReceiptByid(business_key, receipt_key).observe(getViewLifecycleOwner(), this::populateReceipt);
            soldProductViewModel.getSoldProducts(business_key, receipt_key).observe(getViewLifecycleOwner(), (soldProductsList)->{
                soldProductAdapter.submitList(soldProductsList);
                soldProductViewModel.setSoldItems(String.valueOf(soldProductsList.size()));
                double spentAmount = soldProductsList.stream().reduce(0.0, (c, sp) ->
                        c + sp.getSelling_price(), Double::sum);
                String spentAmountString = String.valueOf(Utils.formatDecimal(BigDecimal.valueOf(spentAmount))) + " ".concat(getString(R.string.dollar_symbol));
                toolbar.setSubtitle(spentAmountString);
            });
            soldProductViewModel.populatePaymentInfo(receipt_key);
        }else {
            business_key = new Long(0);
            receipt_key = "";
        }




        binding = FragmentSoldProductBinding.inflate(inflater, container, false);
        soldProductRecycleView = binding.soldProductRecycleView;
        toolbar = binding.soldToolbar;
        cashDueLayout = binding.cashDueLayout;
        cashTenderedLayout = binding.cashTenderedLayout;
        paymentMethod = binding.paymentMethod;
        soldItems = binding.soldItems;
        cashTendered = binding.cashTendered;
        cashDue = binding.casDue;
        cashTenderedLabel = binding.cashTenderedLabel;

        soldProductRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        soldProductRecycleView.setHasFixedSize(true);
        soldProductAdapter = new ProductToSellAdapter();
        soldProductAdapter.setActivityParent(getActivity());
        soldProductRecycleView.setAdapter(soldProductAdapter);

        setCardListener();
        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        handleBackPress();
                    }
                });


        return binding.getRoot();

    }

    private void handleBackPress(){
        navigateBack(getView());
    }

    private void navigateBack(View view){
        NavDirections navDirections = SoldProductFragmentDirections.actionSoldProductFragment2ToReceiptsFragment2();
        Navigation.findNavController(getView()).navigate(navDirections);
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

            @Override
            public void onListChanged() {

            }
        });
    }



    private void askTodeleteProduct(Product product){
        AskForActionDialog askForActionDialog = new AskForActionDialog(
                getString(R.string.delete));

        askForActionDialog.setButtonListener(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {
                if (object){
                    deleteSoldProduct(product);
                }
            }
        });
        askForActionDialog.show(getChildFragmentManager(), getString(R.string.delete));
    }

    private void deleteSoldProduct(Product product){
        if (receipt != null && product != null){
            soldProductViewModel.cancelProductSell(product, receipt, (result)->{
                System.out.println("Product deleted: " + result);
            });
        }
    }


    public void onViewCreated(@NonNull View view, @NonNull Bundle savedState){
        super.onViewCreated(view, savedState);

        cashTenderedLabel.setText(cashTenderedLabel.getText().toString().concat(":"));

        soldProductViewModel.getCashDueAndTenderedVisibility().observe(getViewLifecycleOwner(),
                cashTenderedLayout::setVisibility);
        soldProductViewModel.getCashDueAndTenderedVisibility().observe(getViewLifecycleOwner(),
                cashDueLayout::setVisibility);

        soldProductViewModel.getPaymentMethod().observe(getViewLifecycleOwner(),
                (paymentMethod)->{
                    if (paymentMethod == CASH_CODE) {
                        this.paymentMethod.setText(getString(R.string.cash));
                    } else {
                        this.paymentMethod.setText(getString(R.string.card));
                    }
                });
        soldProductViewModel.getSoldItems().observe(getViewLifecycleOwner(),
                soldItems::setText);
        soldProductViewModel.getCashTendered().observe(getViewLifecycleOwner(),
                cashTendered::setText);
        soldProductViewModel.getCashDue().observe(getViewLifecycleOwner(),
                cashDue::setText);


        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(this::navigateBack);
    }
}