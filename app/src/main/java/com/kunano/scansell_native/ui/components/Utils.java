package com.kunano.scansell_native.ui.components;

import static android.content.Context.MODE_PRIVATE;
import static com.kunano.scansell_native.repository.share_preference.SettingRepository.ENGLISH;
import static com.kunano.scansell_native.repository.share_preference.SettingRepository.SPANISH;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.kunano.scansell_native.repository.share_preference.SettingRepository;

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

    public static void showToast(Activity activity, String message, Integer duration) {
        activity.runOnUiThread(()-> Toast.makeText(activity, message, duration).show());
    }

    public static String  getFileNameFromUri(Uri uri){
        String path = uri.getPath();
        String[] segments = path.split("/");
        String name = segments[segments.length - 1];

        return name;
    }


    public static void setLanguage(String languageCode, Activity activity) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = activity.getResources();

        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
    }

    public static void saveLanguage(String languageCode, Activity activity){
        SettingRepository settingRepository = new SettingRepository(activity,  MODE_PRIVATE);
        settingRepository.setLanguage(languageCode);
    }

    public static String getDeviceLanguage(Activity activity){
        Locale locale;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = activity.getResources().getConfiguration().getLocales().get(0);
        } else {
            // For older versions
            locale = activity.getResources().getConfiguration().locale;
        }

        return locale.getLanguage();
    }

    public static void setLanguageAutomatic(Activity activity){
        String language = getDeviceLanguage(activity);


        if (language.equals(ENGLISH)){
            setLanguage(ENGLISH, activity);
        } else if (language.equals(SPANISH)) {
            setLanguage(SPANISH, activity);
        }else {
            setLanguage(ENGLISH, activity);
        }

    }







}
