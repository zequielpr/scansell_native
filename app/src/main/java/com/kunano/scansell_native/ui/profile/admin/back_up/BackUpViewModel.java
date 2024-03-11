package com.kunano.scansell_native.ui.profile.admin.back_up;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;
import com.kunano.scansell_native.model.db.AppDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackUpViewModel extends AndroidViewModel {
    private static String EXPORT_FILE_NAME = "backup";
    private static final String TAG = "backups";

    private MutableLiveData<Integer> uploadFileToDriveProgress;
    public BackUpViewModel(@NonNull Application application) {
        super(application);

        uploadFileToDriveProgress = new MutableLiveData<>(0);
    }

    public static GoogleSignInClient getGoogleSignInClientForDrive(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        return mGoogleSignInClient;
    }

    public MutableLiveData<Integer> getUploadFileToDriveProgress() {
        return uploadFileToDriveProgress;
    }

    public void setUploadFileToDriveProgress(Integer uploadFileToDriveProgress) {
        this.uploadFileToDriveProgress.postValue(uploadFileToDriveProgress);
    }









    public void exportDatabaseToLocalFile(Context context, Uri uriToSaveBackUp) {


        String fullName = EXPORT_FILE_NAME;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String pattern = "yyyy-MM-dd HH:mm:ss";

            // Create a DateTimeFormatter object with the desired pattern
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.now();
           fullName = EXPORT_FILE_NAME.concat(localDateTime.format(formatter).toString()).concat(".db");
        }

        java.io.File dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME);
        DocumentFile directory = DocumentFile.fromTreeUri(context, uriToSaveBackUp);
        DocumentFile backUpFile = directory.createFile("application/octet-stream", fullName);


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

}
