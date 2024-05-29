package com.kunano.scansell.ui.sales.admin.back_up;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.kunano.scansell.MainActivityViewModel;
import com.kunano.scansell.components.AdminPermissions;
import com.kunano.scansell.components.AskForActionDialog;
import com.kunano.scansell.components.ProgressBarDialog;
import com.kunano.scansell.components.Utils;
import com.kunano.scansell.components.ViewModelListener;
import com.kunano.scansell.model.db.AppDatabase;
import com.kunano.scansell.model.db.SharePreferenceHelper;
import com.kunano.scansell.R;
import com.kunano.scansell.components.media_picker.CustomMediaPicker;
import com.kunano.scansell.databinding.FragmentBackUpBinding;
import com.kunano.scansell.repository.home.DriveServices;

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
    private AdminPermissions adminPermissions;

    public BackUpFragment() {
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminPermissions = new AdminPermissions(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        backUpViewModel = new ViewModelProvider(this).get(BackUpViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentBackUpBinding.inflate(inflater, container, false);

        signInForDriveResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::receiveSignInForDriveResult);
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::loadBackUpFilePath);
        customMediaPicker = CustomMediaPicker.fromPickVisualMediaLauncher(pickMedia);

        backupToolbar = binding.backupToolbar;
        createBackupSection = binding.createBackupSection;
        restoreBackUpSection = binding.restoreBackupSection;

        backupToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        backupToolbar.setNavigationOnClickListener(this::navigateBack);

        restoreBackUpSection.setOnClickListener(this::setRestoreBackUpSection);
        createBackupSection.setOnClickListener(this::setCreateBackupSectionAction);

        directoryPickerLauncher = registerForActivityResult(new
                ActivityResultContracts.StartActivityForResult(), this::receiveDirSelcted);


        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                        handleBackPressed();
                    }
                });

        return binding.getRoot();
    }


    private void handleBackPressed() {
        navigateBack(getView());
    }


    private void navigateBack(View view) {

        Navigation.findNavController(getView()).popBackStack();
    }


    //Generate backup___________________________________________________________
    private BackupDestinationFragment backupDestinationFragment;

    private void setCreateBackupSectionAction(View view) {
        backupDestinationFragment = new BackupDestinationFragment(getString(R.string.backup_destination));
        backupDestinationFragment.setBackUpDestinationListener(new BackupDestinationFragment.BackUpDestinationListener() {
            @Override
            public void onDevice(View view) {
                adminPermissions.setResultListener(new ViewModelListener<Boolean>() {
                    @Override
                    public void result(Boolean object) {
                        if (object){
                            chooseDir();
                        }else {
                            askToGoToSettings();
                        }
                    }
                });
                adminPermissions.checkMediaPermission();
                backupDestinationFragment.dismiss();

            }

            @Override
            public void onDrive(View view) {
                uploadBackUpToDrive();
                backupDestinationFragment.dismiss();
            }
        });
        backupDestinationFragment.show(getParentFragmentManager(), "Select fragment destination");
    }


    //Generate backup in local___________________________________________________________
    private void chooseDir() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        directoryPickerLauncher.launch(intent);
    }

    private void receiveDirSelcted(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                Uri treeUri = data.getData();
                createBackUp(treeUri);
            } else {
                showResults(getString(R.string.there_has_been_an_error));
            }
        }
    }

    private void createBackUp(Uri dirTosaveBackUp) {
        progressBarDialog = new
                ProgressBarDialog(getString(R.string.saving_file_in_local),
                backUpViewModel.getExportProgress());
        progressBarDialog.show(getParentFragmentManager(), "restore progress");

        AppDatabase.closeDatabase();
        backUpViewModel.exportDatabaseToLocalFile(getContext(), dirTosaveBackUp, new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {

                progressBarDialog.dismiss();
                if (object) {
                    System.out.println("create success");
                    showResults(getString(R.string.backup_created_success));

                } else {
                    showResults(getString(R.string.there_has_been_an_error));
                }

            }
        });
    }

    private void showResults(String message) {
        Utils.showToast(getActivity(), message, Toast.LENGTH_LONG);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Utils.restartApp(getContext());
    }


    //Upload backup to drive_______________________________-
    private void uploadBackUpToDrive() {
        GoogleSignInClient googleSignInClient = BackUpViewModel.getGoogleSignInClientForDrive(getContext());

        if (googleSignInClient != null){
            googleSignInClient.signOut();
            signInForDriveResult.launch(googleSignInClient.getSignInIntent());
        }

    }

    private void receiveSignInForDriveResult(ActivityResult activityResult) {
        if (activityResult.getResultCode() == Activity.RESULT_OK) {

            if (activityResult.getData() != null) {
                Task<GoogleSignInAccount> task =
                        GoogleSignIn.getSignedInAccountFromIntent(activityResult.getData());
                task.addOnSuccessListener(this::saveBackUpInDrive).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        System.out.println("error: " + e.getMessage());
                    }
                });


            } else {
                Toast.makeText(getContext(), "Google Login Error!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveBackUpInDrive(GoogleSignInAccount googleSignInAccount) {
        SharePreferenceHelper sharePreferenceHelper =
                new SharePreferenceHelper(getActivity(), Context.MODE_PRIVATE);

        DriveServices driveInstance = new DriveServices(BackUpFragment.this,
                googleSignInAccount, sharePreferenceHelper);
        AppDatabase.closeDatabase();
        driveInstance.saveBAckUpInFolderDrive(resultOfsaveBackUpInDrive());

    }

    ProgressBarDialog progressBarDialog;

    private MediaHttpUploaderProgressListener resultOfsaveBackUpInDrive() {
        progressBarDialog = new
                ProgressBarDialog(getString(R.string.uploading_file_to_drive),
                backUpViewModel.getUploadFileToDriveProgress());
        MediaHttpUploaderProgressListener mediaHttpUploaderProgressListener = new MediaHttpUploaderProgressListener() {

            public void progressChanged(MediaHttpUploader uploader) throws IOException {
                System.out.println();
                switch (uploader.getUploadState()) {

                    case INITIATION_STARTED:
                        break;
                    case INITIATION_COMPLETE:
                        if (!progressBarDialog.isAdded()) {
                            progressBarDialog.show(getParentFragmentManager(), "uploading progress");
                        }

                        break;
                    case MEDIA_IN_PROGRESS:
                        Integer progress = (int) (uploader.getProgress() * 100);
                        backUpViewModel.setUploadFileToDriveProgress(progress);
                        break;
                    case MEDIA_COMPLETE:
                        showResults(getString(R.string.backup_created_success));
                        getActivity().runOnUiThread(() -> {
                            progressBarDialog.dismiss();
                        });


                        break;
                }
            }
        };

        return mediaHttpUploaderProgressListener;

    }


    //Restore database___________________________________________________________
    private void setRestoreBackUpSection(View view) {
        adminPermissions.setResultListener(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {
                if (object){
                    customMediaPicker.lunchImagePicker(new
                            ActivityResultContracts.PickVisualMedia.SingleMimeType("application/octet-stream"));
                }else {
                    askToGoToSettings();
                }
            }
        });
        adminPermissions.checkMediaPermission();

    }

    private void askToGoToSettings(){
        String title = getString(R.string.enable_backup_func);
        String message = getString(R.string.file_and_media_permission_required);
        adminPermissions.showDialogToGotoSettings(title, message);
    }


    private void loadBackUpFilePath(Uri backUpFileUri) {
        if (backUpFileUri != null) askToRestore(backUpFileUri);

    }

    private void askToRestore(@NonNull Uri backUpFileUri) {
        System.out.println("File uri: " + backUpFileUri);
        String fileName = backUpFileUri.toString().
                contains("externalstorage") ? Utils.getFileNameFromUri(backUpFileUri) : "From drive";
        askForActionDialog = new AskForActionDialog(
                getString(R.string.restore_back_up), fileName,
                getString(R.string.cancel), getString(R.string.restore));

        askForActionDialog.show(getParentFragmentManager(), "RestoreBAckUk");

        askForActionDialog.setButtonListener((resultado) -> restore(resultado, backUpFileUri));
    }


    private void restore(Boolean isToRestore, Uri BackUpFileUri) {
        if (isToRestore) {
            if (askForActionDialog != null) askForActionDialog.dismiss();
            progressBarDialog = new
                    ProgressBarDialog(getString(R.string.restoring),
                    backUpViewModel.getRestoreProgress());
            progressBarDialog.show(getParentFragmentManager(), "restore progress");


            backUpViewModel.importDatabase(getContext(), BackUpFileUri, this::processResult);
        }
    }

    private void processResult(boolean result) {
        if (result) {
            showResults(getString(R.string.data_restored_success));
            return;
        }
        showResults(getString(R.string.there_has_been_an_error));

    }


}