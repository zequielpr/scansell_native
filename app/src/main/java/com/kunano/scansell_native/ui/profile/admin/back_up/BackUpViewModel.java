package com.kunano.scansell_native.ui.profile.admin.back_up;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.model.db.SharePreferenceHelper;
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.ui.components.ViewModelListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackUpViewModel extends AndroidViewModel {
    private static String EXPORT_FILE_NAME = "backup";
    private static final String TAG = "backups";
    public BackUpViewModel(@NonNull Application application) {
        super(application);
    }

    public static GoogleSignInClient getGoogleSignInClientForDrive(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        return mGoogleSignInClient;
    }

    public Drive getDriveInstance(Context context,  GoogleSignInAccount googleAccount){
        Drive drive = null;
        if (googleAccount != null) {
            // Get credentials
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    context, Collections.singletonList(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(googleAccount.getAccount());
            // Get Drive instance
            drive = new Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName(context.getString(R.string.app_name))
                    .build();
        }
        return drive;
    }

    public ExecutorService executor;
    public void createFolder(Drive drive, SharePreferenceHelper sharePreferenceHelper, ViewModelListener viewModelListener){

        if (sharePreferenceHelper.getDriveFolderId() == null){
            executor = Executors.newSingleThreadExecutor();
            executor.execute(()->{
                File fileMetadata = new File();
                fileMetadata.setName(this.getApplication().getString(R.string.scan_sell_backup));
                fileMetadata.setMimeType("application/vnd.google-apps.folder");
                try {
                    File folder = drive.files().create(fileMetadata).execute();
                    sharePreferenceHelper.setDriveFolderId(folder.getId());
                    viewModelListener.result(folder.getId());
                } catch (IOException e) {
                    viewModelListener.result(null);
                }
            });
        }
        viewModelListener.result(null);
    }

    public void saveBAckUpInFolderDrive(Drive drive, Context context, String parentFolderId, ListenResponse listenResponse){

       if(parentFolderId != null){
           executor = Executors.newSingleThreadExecutor();
           executor.execute(()->{
               if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                   String pattern = "yyyy-MM-dd HH:mm:ss";

                   // Create a DateTimeFormatter object with the desired pattern
                   DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                   LocalDateTime localDateTime = LocalDateTime.now();
                   EXPORT_FILE_NAME = EXPORT_FILE_NAME.concat(localDateTime.format(formatter).toString()).concat(".db");
               }


               File fileMetadata = new File();
               fileMetadata.setName(EXPORT_FILE_NAME);
               fileMetadata.setMimeType("application/octet-stream");
               java.io.File dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME);
               Content content = new Content(null, dbFile);
               ArrayList parent = new ArrayList<>();
               parent.add(parentFolderId);
               fileMetadata.setParents(parent);

               try {
                   drive.files().create(fileMetadata, content).execute();
                   listenResponse.isSuccessfull(true);
               } catch (IOException e) {
                   listenResponse.isSuccessfull(false);
                   e.printStackTrace();
               }
           });
       }
    }



    public void exportDatabaseToLocalFile(Context context, Uri uriToSaveBackUp) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String pattern = "yyyy-MM-dd HH:mm:ss";

            // Create a DateTimeFormatter object with the desired pattern
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.now();
            EXPORT_FILE_NAME = EXPORT_FILE_NAME.concat(localDateTime.format(formatter).toString()).concat(".db");
        }

        java.io.File dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME);
        DocumentFile directory = DocumentFile.fromTreeUri(context, uriToSaveBackUp);
        DocumentFile backUpFile = directory.createFile("application/octet-stream", EXPORT_FILE_NAME);


        if (backUpFile != null) {
            try {
                //read
                InputStream inputStream = new FileInputStream(dbFile);

                // Write your content to outputStream
                OutputStream outputStream = context.getContentResolver().openOutputStream(backUpFile.getUri());



                // Transfer content from input stream to output stream
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                Log.d(TAG, "Database path " + dbFile);
                Log.d(TAG, "Database exported to " + backUpFile.getUri().getPath());
                outputStream.close();
                inputStream.close();
                // File saved successfully
            } catch (IOException e) {
                e.printStackTrace();
                // Handle error
            }
        } else {
            // Failed to create the file
        }


    }

    public static class Content extends AbstractInputStreamContent{
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
