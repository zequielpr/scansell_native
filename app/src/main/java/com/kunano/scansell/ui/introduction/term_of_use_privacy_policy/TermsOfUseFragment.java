package com.kunano.scansell.ui.introduction.term_of_use_privacy_policy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.kunano.scansell.R;
import com.kunano.scansell.databinding.FragmentTermsOfUseBinding;

public class TermsOfUseFragment extends Fragment {

    private WebView webView;
    private FragmentTermsOfUseBinding binding;

    public TermsOfUseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentTermsOfUseBinding.inflate(getLayoutInflater());

        webView = binding.termsOfUseWebView;

        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set WebViewClient to handle loading within the WebView
        webView.setWebViewClient(new WebViewClient());

        // Load the website
        webView.loadUrl(getString(R.string.terms_of_service_link));


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                TermsOfUseFragment.this.handleOnBackPressed();

            }
        });


        return binding.getRoot();
    }

    private void handleOnBackPressed(){
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            navigateBack();
        }
    }

    private void navigateBack(){
        Navigation.findNavController(getView()).popBackStack();
    }
}