package com.kunano.scansell_native.ui.introduction;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kunano.scansell_native.databinding.IntroductionActivityBinding;


public class IntroductionActivity extends AppCompatActivity {

    private IntroductionActivityBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = IntroductionActivityBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

    }
}
