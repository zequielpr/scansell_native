package com.kunano.scansell_native.ui.introduction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kunano.scansell_native.databinding.IntroductionActivityBinding;


public class IntroductionActivity extends AppCompatActivity {

    private IntroductionActivityBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = IntroductionActivityBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

    }
}
