package com.kunano.scansell_native.ui.sell.collect_payment_method;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.databinding.FragmentCollectPaymentMethodBinding;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.components.ViewModelListener;
import com.kunano.scansell_native.ui.sell.SellFragmentDirections;
import com.kunano.scansell_native.ui.sell.SellViewModel;

public class CollectPaymentMethodFragment extends DialogFragment {
    private FragmentCollectPaymentMethodBinding binding;
    private TextView totalToPay;
    private RadioGroup radioGroup;
    private Button cancelButton;
    private Button payButton;

    public static int CASH = 0;
    public static int CARD = 1;


    /** 0 = cash, 1 = card**/
    private byte paymentMethod;
    private EditText cashTenderedEditText;
    private TextView cashDueTextView;
    private TextView cashDueTextViewLabel;
    private SellViewModel sellViewModel;
    private View parentView;
    private View cashDueLayout;

    private Activity parentActivity;

    public CollectPaymentMethodFragment(SellViewModel sellViewModel, View parentView, Activity parentActivity) {
        this.sellViewModel = sellViewModel;
        this.parentView = parentView;
        this.parentActivity = parentActivity;
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
        cashTenderedEditText = binding.cashTenderedEditText;
        cashDueTextView = binding.cashDueTextView;
        cashDueTextViewLabel = binding.cashDueTextViewLabel;
        paymentMethod = 0;
        cashDueLayout = binding.cashDueLayout;

        sellViewModel.getTotalToPay().observe(getViewLifecycleOwner(), (t)->totalToPay.setText(String.valueOf(t)));
        sellViewModel.getCashDue().observe(getViewLifecycleOwner(),(cd)-> {
            cashDueTextView.setText(String.valueOf(Utils.formatDecimal(cd)));
            if(cd.doubleValue() >= 0){
                activateOrdesacPayButton(true);
            }else {
                activateOrdesacPayButton(false);
            }
        });

        Double cashTendered = sellViewModel.getCashTendered();
        cashTenderedEditText.setText(cashTendered > 0? String .valueOf(cashTendered):null);
        sellViewModel.getCashTenderedAndDueVisibility().observe(getViewLifecycleOwner(), (v)->{
            cashTenderedEditText.setVisibility(v);
            cashDueLayout.setVisibility(v);
        });


        cashTenderedEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()){
                    sellViewModel.setCashTendered(0);
                    return;
                }
                sellViewModel.setCashTendered(Double.valueOf(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cancelButton.setOnClickListener(this::cancelPayment);
        payButton.setOnClickListener(this::pay);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                sellViewModel.setRadioButtonChecked(i);
               paymentMethod = Byte.parseByte(radioGroup.findViewById(i).getTag().toString());


               if(paymentMethod == 0){
                   sellViewModel.setCashTenderedAndDueVisibility(View.VISIBLE);
                   activateOrdesacPayButton(sellViewModel.getCashDue().getValue().doubleValue()>= 0);
               }else {
                   sellViewModel.setCashTenderedAndDueVisibility(View.GONE);
                   activateOrdesacPayButton(true);
               }
            }
        });

        return binding.getRoot();
    }

    private void cancelPayment(View view){
        this.dismiss();
    }

    private void pay(View view){
        PaymentInfo paymentInfo;
        if (paymentMethod == CASH){
            Double cashTendered = Double.valueOf(cashTenderedEditText.getText().toString());
            Double cashDue = Double.valueOf(cashDueTextView.getText().toString());
            paymentInfo = new PaymentInfo(paymentMethod, cashTendered, cashDue);
        }else {
            paymentInfo = new PaymentInfo(paymentMethod);
        }


        sellViewModel.finishSell(paymentInfo, new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean result) {
                if(result){

                    try {
                        sellViewModel.clearProductsToSell();
                        parentActivity.runOnUiThread(CollectPaymentMethodFragment.this::navigateToReceipt);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    // do something else
                }
            }
        });
        this.dismiss();
    }

    private void navigateToReceipt(){
        NavDirections receiptDirection = SellFragmentDirections.actionSellFragmentToSoldProductFragment22(sellViewModel.getCurrentBusinessId(),
                sellViewModel.getCurrentReceiptId());

        Navigation.findNavController(parentView).navigate(receiptDirection);
    }

    public void onResume(){
        super.onResume();
        if(sellViewModel.getRadioButtonChecked() != null){
            if (sellViewModel.getRadioButtonChecked()== CARD)activateOrdesacPayButton(true);
            radioGroup.check(sellViewModel.getRadioButtonChecked());
        }
    }


    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void activateOrdesacPayButton(Boolean state){
        payButton.setClickable(state);
    }

    public class PaymentInfo{
        byte method;
        double cashTendered;
        double cashDue;


        public PaymentInfo(byte method, double cashTendered, double cashDue) {
            this.method = method;
            this.cashTendered = cashTendered;
            this.cashDue = cashDue;
        }
        public PaymentInfo(byte method) {
            this.method = method;
        }

        public byte getMethod() {
            return method;
        }

        public void setMethod(byte method) {
            this.method = method;
        }

        public double getCashTendered() {
            return cashTendered;
        }

        public void setCashTendered(double cashTendered) {
            this.cashTendered = cashTendered;
        }

        public double getCashDue() {
            return cashDue;
        }

        public void setCashDue(double cashDue) {
            this.cashDue = cashDue;
        }
    }
}