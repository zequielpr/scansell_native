package com.kunano.scansell_native.ui.sales.admin.premium;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.billingclient.api.ProductDetails;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.components.billing_component.BillingComponent;
import com.kunano.scansell_native.databinding.FragmentPremiumBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PremiumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PremiumFragment extends Fragment {

    private Button subscribeOrUnsubscribeButton;
    private FragmentPremiumBinding binding;
    private PremiumViewModel premiumViewModel;
    private Toolbar toolbar;
    private BillingComponent billingComponent;

    public PremiumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        premiumViewModel = new ViewModelProvider(this).get(PremiumViewModel.class);
        billingComponent = new BillingComponent(this);

        billingComponent.queryPurchase(BillingComponent.PREMIUM_ID, premiumViewModel::verifySubscription);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentPremiumBinding.inflate(inflater, container, false);


        subscribeOrUnsubscribeButton = binding.subscribeOrUnsubscribeButton;
        toolbar = binding.premiumToolbar;


        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                        //navigateBack(getView());
                    }
                });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subscribeOrUnsubscribeButton.setOnClickListener(this::handleSubscribeOrUnsubscribeButton);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        //toolbar.setNavigationOnClickListener(this::navigateBack);

        premiumViewModel.getSubscriptionState().observe(getViewLifecycleOwner(), (s)->{
            subscribeOrUnsubscribeButton.setText(s?getString(R.string.cancel_subscription):getString(R.string.subscribe));
        });
    }

    public void onDestroy() {
        super.onDestroy();
        if (billingComponent != null) billingComponent.endConnection();
    }

    private void handleSubscribeOrUnsubscribeButton(View view){
      boolean subscriptionState =  premiumViewModel.getSubscriptionState().getValue();
      System.out.println("Subscription state: " + subscriptionState);

      if (subscriptionState){
          cancelSubscription(getView());
      }else {
          subscribe(getView());
      }
    }


    public void subscribe(View view){
        billingComponent.queryProductsToBuy(BillingComponent.PREMIUM_ID, this::launchPurchaseFlow);
    }

    private void launchPurchaseFlow(ProductDetails productDetails){
        System.out.println("product details: " + productDetails.getProductId());
        if (productDetails == null)return;
        //billingComponent.launchPurchaseFlow(productDetails);
        billingComponent.consume();
    }





    private void cancelSubscription(View view){
        billingComponent.launchAdminSubsFlow(BillingComponent.PREMIUM_ID);
    }

    /*private void navigateBack(View view){
        NavDirections navProfileDirection = PremiumFragmentDirections.actionPremiumFragmentToProfileFragment();
        Navigation.findNavController(getView()).navigate(navProfileDirection);
    }*/
}