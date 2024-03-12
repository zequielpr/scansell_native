package com.kunano.scansell_native.ui.profile.admin.back_up;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentBackUpBinding;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.model.db.SharePreferenceHelper;
import com.kunano.scansell_native.repository.home.DriveServices;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.ProgressBarDialog;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.components.media_picker.CustomMediaPicker;

import java.io.IOException;

public class BackUpFragment extends Fragment {
    private FragmentBackUpBinding binding;
    private static final int REQUEST_CODE_PICK_DIRECTORY = 123;
    private MainActivityViewModel mainActivityViewModel;
    private Toolbar backupToolbar;
    private View createBackupSection;
    private View restoreBackUpSection;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<Intent> directoryPickerLauncher;
    private ActivityResultLauncher<Intent> signInForDriveResult;
    private CustomMediaPicker customMediaPicker;
    private BackUpViewModel backUpViewModel;
    private AskForActionDialog askForActionDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        backUpViewModel = new ViewModelProvider(this).get(BackUpViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentBackUpBinding.inflate(inflater, container, false);

        signInForDriveResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),this::receiveSignInForDriveResult);
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::loadBackUpFilePath);
        customMediaPicker = new CustomMediaPicker(pickMedia);

        backupToolbar = binding.backupToolbar;
        createBackupSection = binding.createBackupSection;
        restoreBackUpSection = binding.restoreBackupSection;

        backupToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        backupToolbar.setNavigationOnClickListener(this::navigateBack);

        mainActivityViewModel.setHandleBackPress(this::handlePressBack);


        restoreBackUpSection.setOnClickListener(this::setRestoreBackUpSection);
        createBackupSection.setOnClickListener(this::setCreateBackupSectionAction);

        directoryPickerLauncher = registerForActivityResult(new
                ActivityResultContracts.StartActivityForResult(),this::receiveDirSelcted);


        return binding.getRoot();
    }


    private void handlePressBack(){
        navigateBack(getView());
    }


    private void navigateBack(View view){
        NavDirections profileNavDirections = BackUpFragmentDirections.actionBackUpFragmentToProfileFragment();
        Navigation.findNavController(getView()).navigate(profileNavDirections);
        mainActivityViewModel.setHandleBackPress(null);
    }


    //Generate backup___________________________________________________________
    private  BackupDestinationFragment backupDestinationFragment;
    private void setCreateBackupSectionAction(View view){
        backupDestinationFragment = new BackupDestinationFragment(getString(R.string.backup_destination));
        backupDestinationFragment.setBackUpDestinationListener(new BackupDestinationFragment.BackUpDestinationListener() {
            @Override
            public void onDevice(View view) {
                backupDestinationFragment.dismiss();
                chooseDir();
            }

            @Override
            public void onDrive(View view) {
                uploadBackUpToDrive();
                backupDestinationFragment.dismiss();
            }
        });
        backupDestinationFragment.show(getParentFragmentManager(), "Select fragment destination");
    }
    private void chooseDir(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        directoryPickerLauncher.launch(intent);
    }

    private void receiveDirSelcted(ActivityResult result){
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                Uri treeUri = data.getData();
                createBackUp(treeUri);
            }
        }
    }

    private void createBackUp(Uri dirTosaveBackUp){
        AppDatabase.closeDatabase();
        backUpViewModel.exportDatabaseToLocalFile(getContext(), dirTosaveBackUp);
    }




    //Upload backup to drive_______________________________-
    private void uploadBackUpToDrive(){
        GoogleSignInClient googleSignInClient = BackUpViewModel.getGoogleSignInClientForDrive(getContext());

        signInForDriveResult.launch(googleSignInClient.getSignInIntent());

    }

    private void receiveSignInForDriveResult(ActivityResult activityResult){
        if (activityResult.getResultCode() == Activity.RESULT_OK) {

            if (activityResult.getData() != null) {
                Task<GoogleSignInAccount> task =
                GoogleSignIn.getSignedInAccountFromIntent(activityResult.getData());
                task.addOnSuccessListener(this::saveBackUpInDrive);

            } else {
                Toast.makeText(getContext(), "Google Login Error!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveBackUpInDrive(GoogleSignInAccount googleSignInAccount){
        SharePreferenceHelper sharePreferenceHelper =
                new SharePreferenceHelper(getActivity(), Context.MODE_PRIVATE);

        DriveServices driveInstance = new DriveServices(BackUpFragment.this,
                googleSignInAccount, sharePreferenceHelper);
        AppDatabase.closeDatabase();
       driveInstance.saveBAckUpInFolderDrive(resultOfsaveBackUpInDrive());

    }

    ProgressBarDialog progressBarDialog;
    private MediaHttpUploaderProgressListener resultOfsaveBackUpInDrive(){
        progressBarDialog = new
                ProgressBarDialog(getString(R.string.uploading_file_to_drive), getViewLifecycleOwner(),
                backUpViewModel.getUploadFileToDriveProgress());
        MediaHttpUploaderProgressListener mediaHttpUploaderProgressListener = new   MediaHttpUploaderProgressListener() {

            public void progressChanged(MediaHttpUploader uploader) throws IOException {
                System.out.println();
                switch (uploader.getUploadState()) {

                    case INITIATION_STARTED:
                        break;
                    case INITIATION_COMPLETE:
                        if(!progressBarDialog.isAdded()){
                            progressBarDialog.show(getParentFragmentManager(), "uploading progress");
                        }

                        break;
                    case MEDIA_IN_PROGRESS:
                        Integer progress = (int)(uploader.getProgress() * 100);
                        backUpViewModel.setUploadFileToDriveProgress(progress);
                        break;
                    case MEDIA_COMPLETE:
                        getActivity().runOnUiThread(()->{
                            progressBarDialog.dismiss();
                            Utils.showToast(getContext(), getString(R.string.backup_created_success), Toast.LENGTH_LONG);
                        });


                        break;
                }
            }
        };

        return mediaHttpUploaderProgressListener;

    }








    //Restore database___________________________________________________________
    private void setRestoreBackUpSection(View view){
        customMediaPicker.lunchImagePicker(new
                ActivityResultContracts.PickVisualMedia.SingleMimeType("application/octet-stream"));

    }


    private void loadBackUpFilePath(Uri backUpFileUri){
        if (backUpFileUri != null)askToRestore(backUpFileUri);

    }

    private void askToRestore(@NonNull Uri backUpFileUri){
        System.out.println("File uri: " + backUpFileUri);
        String fileName = backUpFileUri.toString().
                contains("externalstorage")?Utils.getFileNameFromUri(backUpFileUri):"From drive";
        askForActionDialog = new AskForActionDialog(getLayoutInflater(),
                getString(R.string.restore_back_up), fileName,
                getString(R.string.cancel), getString(R.string.restore));

        askForActionDialog.show(getParentFragmentManager(), "RestoreBAckUk");

        askForActionDialog.setButtonListener((resultado)->restore(resultado, backUpFileUri));
    }



    private void restore(Boolean isToRestore, Uri BackUpFileUri){
        if (isToRestore){
           if (  askForActionDialog != null)askForActionDialog.dismiss();
            progressBarDialog = new
                    ProgressBarDialog(getString(R.string.uploading_file_to_drive), getViewLifecycleOwner(),
                    backUpViewModel.getRestoreProgress());
            progressBarDialog.show(getParentFragmentManager(), "restore progress");



           backUpViewModel.importDatabase(getContext(), BackUpFileUri, this::processResult);
        }
    }

    private void processResult(boolean result){
        getActivity().runOnUiThread(()->{
            if (result){
                Utils.showToast(getContext(), getString(R.string.data_restored_success), Toast.LENGTH_SHORT);
                Utils.restartApp(getContext());
                return;
            }
            Utils.showToast(getContext(), getString(R.string.thera_has_been_an_error), Toast.LENGTH_SHORT);
        });



    }



}