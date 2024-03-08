package com.kunano.scansell_native.ui.profile.admin.back_up;

import android.app.Activity;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentBackUpBinding;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.ui.components.media_picker.CustomMediaPicker;

public class BackUpFragment extends Fragment {
    private FragmentBackUpBinding binding;
    private static final int REQUEST_CODE_PICK_DIRECTORY = 123;
    private MainActivityViewModel mainActivityViewModel;
    private Toolbar backupToolbar;
    private View createBackupSection;
    private View restoreBackUpSection;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<Intent> directoryPickerLauncher;
    private CustomMediaPicker customMediaPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentBackUpBinding.inflate(inflater, container, false);
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::loadFilePath);
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


    private void setCreateBackupSectionAction(View view){
        chooseDir();
       //AppDatabase.exportDatabase(getContext());
    }

    private void setRestoreBackUpSection(View view){
        //AppDatabase.importDatabase(getContext());
        customMediaPicker.lunchImagePicker(new
                ActivityResultContracts.PickVisualMedia.SingleMimeType("application/octet-stream"));
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
                inTroduceFileName(treeUri);
                // Use the selected URI to save your file
                Toast.makeText(getContext(), "Directory picked: " + treeUri.toString(), Toast.LENGTH_SHORT).show();
                // You can use this treeUri to create new files or directories within the selected directory
            }
        }
    }

    private void inTroduceFileName(Uri dirTosaveBackUp){
        System.out.println("Directory: " + dirTosaveBackUp.getPath());
        AppDatabase.exportDatabase(getContext(), dirTosaveBackUp);
    }





    private void loadFilePath(Uri fileUri){
        String fileName = fileUri.getLastPathSegment();
        AskForActionDialog askForActionDialog = new AskForActionDialog(getLayoutInflater(),
                getString(R.string.restore_back_up), fileName,
                getString(R.string.cancel), getString(R.string.restore));

        askForActionDialog.show(getParentFragmentManager(), "RestoreBAckUk");

        askForActionDialog.setButtonListener(new ListenResponse() {
            @Override
            public void isSuccessfull(boolean resultado) {
                if (resultado){
                    AppDatabase.importDatabase(getContext(), fileUri);
                    askForActionDialog.dismiss();
                }
            }
        });

    }



}