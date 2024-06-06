package com.kunano.scansell;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kunano.scansell.components.Utils;
import com.kunano.scansell.databinding.ActivityMainBinding;
import com.kunano.scansell.repository.share_preference.ShareRepository;
import com.kunano.scansell.ui.introduction.IntroductionActivity;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    MainActivityViewModel mainActivityViewModel;

    BottomNavigationView navView;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);


        boolean isFirstStart = new ShareRepository(this, MODE_PRIVATE).isFirstStart();
        if (isFirstStart)navigateToIntroduction();

        Utils.handleLanguage(this);

       /* AccountHelper accountHelper = new AccountHelper();
        if (accountHelper.getCurrentUser()== null){
            navigateToLogIn();
        }else {
           if(!accountHelper.isEmailVerified()) accountHelper.signOut(this);
        };*/


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = binding.navView;


        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        mainActivityViewModel.setHostComponentListener(new MainActivityViewModel.HostComponentListener() {
            @Override
            public void hideBottomNavBar() {
                navView.setVisibility(View.GONE);
            }

            @Override
            public void showBottomNavBar() {
                navView.setVisibility(View.VISIBLE);
            }
        });



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();*/
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.getContext();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }
    NavController navController;

    private void initializeAds(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }


    private void navigateToIntroduction(){
        Intent intent = new Intent(MainActivity.this, IntroductionActivity.class);
        startActivity(intent);
        finish();
    }


   /* private void navigateToLogIn(){
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }*/





}