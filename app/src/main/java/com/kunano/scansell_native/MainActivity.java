package com.kunano.scansell_native;

import static com.kunano.scansell_native.repository.share_preference.SettingRepository.ENGLISH;
import static com.kunano.scansell_native.repository.share_preference.SettingRepository.SPANISH;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kunano.scansell_native.databinding.ActivityMainBinding;
import com.kunano.scansell_native.repository.share_preference.SettingRepository;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.login.LogInActivity;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    MainActivityViewModel mainActivityViewModel;

    BottomNavigationView navView;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccountHelper accountHelper = new AccountHelper();

        handleLanguage();

        if (accountHelper.getCurrentUser()== null){
            navigateToLogIn();
        }else {
           if(!accountHelper.isEmailVerified()) accountHelper.signOut(this);
        };


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


    private void navigateToLogIn(){
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       mainActivityViewModel.notifyBackPressed();
    }

    @Override
    public void onDestroy(){
        System.out.println("main activiti on destroy");
        super.onDestroy();
    }

    public void handleLanguage(){
        SettingRepository settingRepository = new SettingRepository(this, MODE_PRIVATE);
        String language = settingRepository.getLanguage();

        if (language.equals(ENGLISH)) {
            Utils.setLanguage(ENGLISH, this);
        } else if (language.equals(SPANISH)) {
            Utils.setLanguage(SPANISH, this);
        }else {
            Utils.setLanguageAutomatic(this);
        }
    }



}