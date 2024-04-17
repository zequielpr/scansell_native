package com.kunano.scansell_native.ui.profile.admin.account.password;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.core.text.HtmlCompat;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.ValidateData;

public class ValidatePasswd {
    PasswordViewModel passwordViewModel;


    public ValidatePasswd(PasswordViewModel passwordViewModel) {
        this.passwordViewModel = passwordViewModel;
    }

    public TextWatcher getPasswordEditTextWatcher(Context context){
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString().trim();
                passwordViewModel.setIsPasswordValid(ValidateData.validatePassword(password));
                boolean itContainsDigit = ValidateData.containsDigit(password.trim());
                boolean itContainsUpperAndLowerLtt = ValidateData.containsUpperAndLowerCase(password);
                boolean atLeastEightCharacters = password.trim().length() >= 8;

                checkIsContainsDigit(context, itContainsDigit);
                checkUpperAndLowerCase(context, itContainsUpperAndLowerLtt);
                checkEightCharacters(context, atLeastEightCharacters);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        return textWatcher;
    }

    private void checkIsContainsDigit(Context context, boolean itContainsDigit){
        if (itContainsDigit){
            passwordViewModel.setAtLeastOneDigit(
                    HtmlCompat.fromHtml(context.getString(R.string.at_least_one_digit),
                            HtmlCompat.FROM_HTML_MODE_LEGACY));

        }else {
            passwordViewModel.setAtLeastOneDigit(
                    HtmlCompat.fromHtml(
                            "<strike>"+context.getString(R.string.at_least_one_digit)+"</strike>",
                            HtmlCompat.FROM_HTML_MODE_LEGACY));

        }
    }

    private void checkUpperAndLowerCase(Context context, boolean itContainsUpperAndLowerLtt){
        if (itContainsUpperAndLowerLtt){
            passwordViewModel.setUpperAndLowerCaseMutableData(
                    HtmlCompat.fromHtml(context.getString(R.string.upper_and_lower_case),
                            HtmlCompat.FROM_HTML_MODE_LEGACY));

        }else {
            passwordViewModel.setUpperAndLowerCaseMutableData(
                    HtmlCompat.fromHtml(
                            "<strike>"+context.getString(R.string.upper_and_lower_case)+"</strike>",
                            HtmlCompat.FROM_HTML_MODE_LEGACY));

        }
    }

    private void checkEightCharacters(Context context, boolean atLeastEightCharacters){
        if (atLeastEightCharacters){
            passwordViewModel.setAtLeastEightCharacters(
                    HtmlCompat.fromHtml(context.getString(R.string.at_least_eight_characters),
                            HtmlCompat.FROM_HTML_MODE_LEGACY));

        }else {
            passwordViewModel.setAtLeastEightCharacters(
                    HtmlCompat.fromHtml(
                            "<strike>"+context.getString(R.string.at_least_eight_characters)+"</strike>",
                            HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
    }


    public TextWatcher getConfirmPasswordEditTextWatcher(EditText passwordEditText){
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String passwd = passwordEditText.getText().toString().trim();
                String passwdToConfirm = charSequence.toString().trim();

                checkIfPasswdMatch(passwdToConfirm, passwd);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        return textWatcher;
    }

    public boolean checkIfPasswdMatch(String passwdToConfirm, String passwd){
        boolean itMatches = passwdToConfirm.equals(passwd);
        if (itMatches){
            passwordViewModel.setPasswdNotMatchVisibility(View.GONE);
        }else {
            passwordViewModel.setPasswdNotMatchVisibility(View.VISIBLE);
        }
        return itMatches;
    }
}
