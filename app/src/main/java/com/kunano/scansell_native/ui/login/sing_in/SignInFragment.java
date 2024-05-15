package com.kunano.scansell_native.ui.login.sing_in;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kunano.scansell_native.MainActivity;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentLogInBinding;
import com.kunano.scansell_native.components.ImageProcessor;
import com.kunano.scansell_native.components.SpinningWheel;
import com.kunano.scansell_native.components.Utils;
import com.kunano.scansell_native.ui.profile.admin.account.password.PasswordViewModel;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;
import com.kunano.scansell_native.ui.profile.auth.Auth;

public class SignInFragment extends Fragment {
    private static String TAG = "results";
    private static String ACCOUNT_DISABLE_BY_THE_ADMINISTRATOR = "account has been disabled by an administrator";
    private static String NETWORK_ERROR = "network error";
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView emailWarnTextView;
    private TextView passwdWarnTextView;
    private Button sigInButton;
    private CardView continueWithGoogleCard;
    private Button forgottenPasswdButton;
    private Button signUpButton;
    private SignInViewModel signInViewModel;
    private CheckBox showOrHidePasswdCheckBox;
    private PasswordViewModel passwordViewModel;
    private FragmentLogInBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView googleLogo;
    private ActivityResultLauncher<Intent> singInActivityResult;

    public SignInFragment() {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        passwordViewModel = new ViewModelProvider(this).get(PasswordViewModel.class);

        mAuth = FirebaseAuth.getInstance();


        singInActivityResult = registerForActivityResult(new
                ActivityResultContracts.StartActivityForResult(),this::receiveRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLogInBinding.inflate(inflater, container, false);

        emailEditText = binding.emailEditText;
        passwordEditText = binding.passdEditText;
        sigInButton = binding.signInbutton;
        continueWithGoogleCard = binding.logInGoogleCard;
        forgottenPasswdButton = binding.forgottenPassButton;
        signUpButton = binding.singUpButton;
        emailWarnTextView = binding.emailWarnTextViewSignIn;
        passwdWarnTextView = binding.passwdWarnTextViewSignIn;
        showOrHidePasswdCheckBox = binding.checkBoxShowPassword;
        googleLogo = binding.googleLogo;


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               Utils.askToLeaveApp(SignInFragment.this);

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpButton.setOnClickListener(this::signUp);
        sigInButton.setOnClickListener(this::signInWithEmailAndPasswdRequest);
        continueWithGoogleCard.setOnClickListener(this::continueGoogleAction);
        signInViewModel.getEmailWarn().observe(getViewLifecycleOwner(), emailWarnTextView::setText);
        signInViewModel.getPasswdWarn().observe(getViewLifecycleOwner(), passwdWarnTextView::setText);
        showOrHidePasswdCheckBox.setOnCheckedChangeListener(passwordViewModel.getShowOrHidePasswordListener());
        passwordViewModel.getHideOrShowPasswordMutableData().observe(getViewLifecycleOwner(),
                passwordEditText::setInputType);
        forgottenPasswdButton.setOnClickListener(this::passwdForgotten);

        Bitmap googleLogoBitmap = ImageProcessor.
                drawableToBitmap(getResources(), R.drawable.google_logo, 100, 100);
        googleLogo.setImageBitmap(googleLogoBitmap);
    }



    public void continueGoogleAction(View view) {
        signInWithGoogle();
    }


    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mGoogleSignInClient.signOut();
        singInActivityResult.launch(signInIntent);
    }


    private void receiveRequest(ActivityResult activityResult){

        if (activityResult.getResultCode() == Activity.RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.getData());
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = (GoogleSignInAccount) ((Task<?>) task).getResult(ApiException.class);


                firebaseAuthWithGoogle(account.getIdToken());
                spinningWheel = new SpinningWheel();
                spinningWheel.show(getChildFragmentManager(), SpinningWheel.TAG);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            navigateToMainActivity();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String error = task.getException().toString();
                            if(error.contains(NETWORK_ERROR)){
                                Utils.showToast(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT);
                            } else if (error.contains(ACCOUNT_DISABLE_BY_THE_ADMINISTRATOR)) {
                                Utils.showToast(getActivity(), getString(R.string.account_disabled), Toast.LENGTH_SHORT);
                            }

                            //updateUI(null);
                        }
                        spinningWheel.dismiss();
                    }
                });
    }

    public void navigateToMainActivity(){
        AccountHelper accountHelper = new AccountHelper();

        if (accountHelper.isEmailVerified()){
            Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }else {
            NavDirections navDirections = SignInFragmentDirections.actionLogInFragmentToVerifyEmailFragment();
            Navigation.findNavController(getView()).navigate(navDirections);
        }
    }

    private void passwdForgotten(View view){
        NavDirections navDirectionToRecoverPasswd =
                SignInFragmentDirections.actionLogInFragmentToRecoverPasswdFragment();
        Navigation.findNavController(getView()).navigate(navDirectionToRecoverPasswd);
    }

    private void signUp(View view){
        NavDirections navDirectionsToSigUp = SignInFragmentDirections.actionLogInFragmentToCollectNameAndSurnameFragment();
        Navigation.findNavController(getView()).navigate(navDirectionsToSigUp);
    }

    private SpinningWheel spinningWheel;
    private void signInWithEmailAndPasswdRequest(View view){

        String email = emailEditText.getText().toString().trim();
        String passwd = passwordEditText.getText().toString().trim();

        if (!signInViewModel.validateEmailAndPasswd(email, passwd)) return;

        Auth auth = new Auth();
        spinningWheel = new SpinningWheel();
        spinningWheel.show(getChildFragmentManager(), SpinningWheel.TAG);

        auth.signInWithEmailAndPasswd(email, passwd, this::processSignInRequest);
    }

    private void processSignInRequest(Auth.SIGN_IN_REQUEST_RESULT result){
        spinningWheel.dismiss();
        String message;

        switch (result){
            case SUCCESS:
                navigateToMainActivity();
                break;
            case NETWORK_ERROR:
                message = getString(R.string.network_error);
                showResult(message);
                break;
            case EMAIL_OR_PASSWD_INCORRECT:
                message = getString(R.string.email_or_passwd_incorrect);
                showResult(message);
                break;
            default:
                message = getString(R.string.there_has_been_an_error);
                showResult(message);
                break;
        }

    }

    private void showResult(String message){
        Utils.showToast(getActivity(), message, Toast.LENGTH_SHORT);
    }




}