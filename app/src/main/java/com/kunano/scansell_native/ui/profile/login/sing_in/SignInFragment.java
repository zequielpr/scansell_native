package com.kunano.scansell_native.ui.profile.login.sing_in;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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

public class SignInFragment extends Fragment {
    private static String TAG = "results";

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button sigInButton;
    private CardView continueWithGoogleCard;
    private Button forgottenPasswd;
    private Button signUpButton;


    private FragmentLogInBinding binding;
    private GoogleSignInClient mGoogleSignInClient;

    ActivityResultLauncher<Intent> singInActivityResult;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLogInBinding.inflate(inflater, container, false);

        emailEditText = binding.emailEditText;
        passwordEditText = binding.passdEditText;
        sigInButton = binding.singUpButton;
        continueWithGoogleCard = binding.logInGoogleCard;
        forgottenPasswd = binding.forgottenPassButton;
        signUpButton = binding.singUpButton;


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        singInActivityResult = registerForActivityResult(new
                ActivityResultContracts.StartActivityForResult(),this::receiveRequeste);


        sigInButton.setOnClickListener(this::setSigInButtonAction);
        continueWithGoogleCard.setOnClickListener(this::continueGoogleAction);
        return binding.getRoot();
    }

    private void setSigInButtonAction(View view) {


    }

    public void continueGoogleAction(View view) {
        signIn();
    }


    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mGoogleSignInClient.signOut();
        singInActivityResult.launch(signInIntent);
    }


    private void receiveRequeste(ActivityResult activityResult){

        if (activityResult.getResultCode() == Activity.RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.getData());
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = (GoogleSignInAccount) ((Task<?>) task).getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
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
                            //updateUI(null);
                        }
                    }
                });
    }

    public void navigateToMainActivity(){
        Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }




}