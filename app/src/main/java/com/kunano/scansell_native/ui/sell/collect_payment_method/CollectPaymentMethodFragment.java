package com.kunano.scansell_native.ui.sell.collect_payment_method;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.kunano.scansell_native.databinding.FragmentCollectPaymentMethodBinding;
import com.kunano.scansell_native.ui.sell.SellViewModel;

public class CollectPaymentMethodFragment extends DialogFragment {
    private FragmentCollectPaymentMethodBinding binding;
    private TextView totalToPay;
    private RadioGroup radioGroup;
    private Button cancelButton;
    private Button payButton;

    /** 0 = cash, 1 = card**/
    private byte paymentMethod;

    private SellViewModel sellViewModel;

    public CollectPaymentMethodFragment(SellViewModel sellViewModel) {
        this.sellViewModel = sellViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCollectPaymentMethodBinding.inflate(inflater,container, false);

        totalToPay = binding.textViewTotalToPay;
        radioGroup = binding.paymentMethod;
        cancelButton = binding.cancelButton;
        payButton = binding.payButton;
        paymentMethod = 0;

        sellViewModel.getTotalToPay().observe(getViewLifecycleOwner(), (t)->totalToPay.setText(String.valueOf(t)));

        cancelButton.setOnClickListener(this::cancelPayment);
        payButton.setOnClickListener(this::pay);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
               paymentMethod = Byte.parseByte(radioGroup.findViewById(i).getTag().toString());
            }
        });

        return binding.getRoot();
    }

    private void cancelPayment(View view){
        this.dismiss();
    }

    private void pay(View view){
        sellViewModel.finishSell(paymentMethod);
        this.dismiss();
        sellViewModel.clearProductsToSell();
    }


    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}