package com.kunano.scansell_native.ui.components;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static long getDateInMilliSeconds(LocalDateTime givenDateString, String format) {
        String DATE_TIME_FORMAT = format;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
        long timeInMilliseconds = 1;
        try {
            Date mDate = sdf.parse(givenDateString.toString());
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static void restartApp(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle extras = new Bundle();
            // Add any extra data you want to pass to the activity upon restart
            // For example:
            // extras.putString("key", "value");
            intent.putExtras(extras);
            context.startActivity(intent);
            System.exit(0); // This might be necessary to fully restart the app, but it's generally not recommended
        }
    }

    public static void showToast(Context context, String message, Integer duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static String  getFileNameFromUri(Uri uri){
        String path = uri.getPath();
        String[] segments = path.split("/");
        String name = segments[segments.length - 1];

        return name;
    }







}
