package com.kunano.scansell_native.ui.profile.admin.back_up;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
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
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.ui.components.ViewModelListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BackUpViewModel extends AndroidViewModel {
    private static String EXPORT_FILE_NAME = "backup";
    private static final String TAG = "backups";

    private MutableLiveData<Integer> uploadFileToDriveProgress;
    private MutableLiveData<Integer> restoreProgress;
    private MutableLiveData<Integer> exportProgress;
    public BackUpViewModel(@NonNull Application application) {
        super(application);

        uploadFileToDriveProgress = new MutableLiveData<>(0);
        restoreProgress = new MutableLiveData<>(0);
        exportProgress = new MutableLiveData<>(0);
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

    public MutableLiveData<Integer> getRestoreProgress() {
        return restoreProgress;
    }

    public void setRestoreProgress(Integer restoreProgress) {
        this.restoreProgress.postValue(restoreProgress);
    }

    public MutableLiveData<Integer> getExportProgress() {
        return exportProgress;
    }

    public void setExportProgress(MutableLiveData<Integer> exportProgress) {
        this.exportProgress = exportProgress;
    }

    Executor executor;

    public void importDatabase(Context context, Uri sourceUri, ListenResponse listenResponse) {
        executor = Executors.newSingleThreadExecutor();

        executor.execute(()->{
            AppDatabase.closeDatabase();
            DocumentFile backup = DocumentFile.fromSingleUri(context, sourceUri);
            File dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME);

            if (backup.exists()) {

                try {

                    //Read content
                    InputStream inputStream = context.getContentResolver().openInputStream(backup.getUri());

                    //Write content
                    OutputStream outputStream = new FileOutputStream(dbFile);

                    // Transfer content from input stream to output stream
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        inputStream.transferTo(outputStream);
                        System.out.println("Tiramisu");
                    }else {
                        byte[] buffer = new byte[1024];
                        int totalBytesRead = 0;
                        Long totalBytes = backup.length();
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            if (bytesRead > 0) {
                                totalBytesRead += bytesRead;
                                outputStream.write(buffer, 0, bytesRead);

                                // Calculate progress
                                double progress = (double) totalBytesRead * 100 /totalBytes;;
                                restoreProgress.postValue((int)progress);
                                System.out.println("Progress: " + progress + "%");
                                if (progress == 100)listenResponse.isSuccessfull(true);
                            }

                        }
                    }


                    // Transfer content from input stream to output stream

                    outputStream.close();
                    inputStream.close();


                } catch (IOException e) {
                    e.printStackTrace();
                    listenResponse.isSuccessfull(false);
                    // Handle error
                }catch (Exception e){
                    Log.d(TAG, "Failure" + e.getCause());
                    listenResponse.isSuccessfull(false);
                }

            } else {
                Log.e(TAG, "Backup file not found!");
                listenResponse.isSuccessfull(false);
            }
        });
    }




    public void exportDatabaseToLocalFile(Context context, Uri uriToSaveBackUp,
                                          ViewModelListener<Boolean> listener) {
        executor = Executors.newSingleThreadExecutor();

        executor.execute(()->{
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
                    int totalBytesRead = 0;
                    Long totalBytes = dbFile.length();
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        if (bytesRead > 0) {
                            totalBytesRead += bytesRead;
                            outputStream.write(buffer, 0, bytesRead);

                            // Calculate progress
                            double progress = (double) totalBytesRead * 100 /totalBytes;;
                            exportProgress.postValue((int)progress);
                            System.out.println("Progress: " + progress + "%");
                        }

                    }
                    listener.result(true);

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

        });


    }

}
