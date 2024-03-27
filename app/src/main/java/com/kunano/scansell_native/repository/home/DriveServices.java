package com.kunano.scansell_native.repository.home;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.model.db.SharePreferenceHelper;
import com.kunano.scansell_native.ui.components.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriveServices{
    Fragment fragment;
    GoogleSignInAccount googleAccount;
    SharePreferenceHelper sharePreferenceHelper;
    String parentFolderId;
    Drive drive;
    private String fileFullName;
    private static String EXPORT_FILE_NAME = "backup";
    private MediaHttpUploader uploader;

    public DriveServices(Fragment fragment, GoogleSignInAccount googleAccount,
                         SharePreferenceHelper sharePreferenceHelper) {
        this.fragment = fragment;
        this.googleAccount = googleAccount;
        this.sharePreferenceHelper = sharePreferenceHelper;
        parentFolderId = sharePreferenceHelper.getDriveFolderId();

        drive = getDriveInstance();
    }

    public ExecutorService executor;


    public void saveBAckUpInFolderDrive(MediaHttpUploaderProgressListener mediaHttpUploaderProgressListener){
        executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            if(parentFolderId == null)parentFolderId = createFolder();

            fileFullName = getFileFullName();

            File fileMetadata = new File();
            fileMetadata.setName(fileFullName);
            fileMetadata.setMimeType("application/octet-stream");
            java.io.File dbFile = fragment.getContext().getDatabasePath(AppDatabase.DATABASE_NAME);
            Content content = new Content(null, dbFile);
            ArrayList parent = new ArrayList<>();
            parent.add(parentFolderId);
            fileMetadata.setParents(parent);

            try {
                Drive.Files.Create fileUploaded = drive.files().create(fileMetadata, content);

                //set uploader listener to track down the upload process
                uploader = fileUploaded.getMediaHttpUploader();
                setUploadProcess(mediaHttpUploaderProgressListener);

                // Execute the upload.
                fileUploaded.execute();

            } catch (IOException e) {
                if (e.getLocalizedMessage().contains("Unable to resolve host")){
                    fragment.getActivity().runOnUiThread(()->{
                        Utils.showToast(fragment.getActivity(),
                                fragment.getContext().
                                        getString(R.string.faile_to_create_backup_verify_network),
                                Toast.LENGTH_LONG);
                    });
                }else{
                    //If the file is not found, then create a new one an save its id
                    parentFolderId = null;
                    saveBAckUpInFolderDrive(mediaHttpUploaderProgressListener);
                }
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
                fragment.getActivity().runOnUiThread(()->{
                    Utils.showToast(fragment.getActivity(),
                            fragment.getContext().
                                    getString(R.string.faile_to_create_backup),
                            Toast.LENGTH_LONG);
                });
            }
        });
    }



    public void setUploadProcess(MediaHttpUploaderProgressListener mediaHttpUploaderProgressListener){
        uploader.setProgressListener(mediaHttpUploaderProgressListener);
    }


    private String createFolder(){

        if (parentFolderId == null){
            System.out.println("Create folder");
            File fileMetadata = new File();
            fileMetadata.setName(fragment.getContext().getString(R.string.scan_sell_backup));
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            try {
                File folder = drive.files().create(fileMetadata).execute();
                sharePreferenceHelper.setDriveFolderId(folder.getId());
                return folder.getId();
            } catch (IOException e) {
                return null;
            }
        }else{
            return sharePreferenceHelper.getDriveFolderId();
        }
    }


    private String  getFileFullName(){
        String fullName = EXPORT_FILE_NAME;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String pattern = "yyyy-MM-dd HH:mm:ss";

            // Create a DateTimeFormatter object with the desired pattern
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.now();
            fullName = EXPORT_FILE_NAME.concat(localDateTime.format(formatter).toString()).concat(".db");
        }
        return fullName;
    }


    public Drive getDriveInstance(){
        Drive drive = null;
        if (googleAccount != null) {
            // Get credentials
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    fragment.getContext(), Collections.singletonList(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(googleAccount.getAccount());
            // Get Drive instance
            drive = new Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName(fragment.getContext().getString(R.string.app_name))
                    .build();
        }
        return drive;
    }





    private class Content extends AbstractInputStreamContent {
        java.io.File file;

        /**
         * @param type Content type or {@code null} for none
         * @since 1.5
         */
        public Content(@Nullable String type, java.io.File file) {
            super(type);
            this.file = file;
        }

        @Override
        public InputStream getInputStream() throws IOException {

            return new FileInputStream(file);
        }

        @Override
        public long getLength() throws IOException {
            return file.length();
        }

        @Override
        public boolean retrySupported() {
            return true;
        }
    }

}
