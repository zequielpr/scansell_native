package com.kunano.scansell.components;

import static android.content.Context.MODE_PRIVATE;
import static com.kunano.scansell.repository.share_preference.SettingRepository.ENGLISH;
import static com.kunano.scansell.repository.share_preference.SettingRepository.SPANISH;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.ironsource.mediationsdk.IronSource;
import com.kunano.scansell.R;
import com.kunano.scansell.repository.share_preference.SettingRepository;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public  static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    public static void setIronSourcePermission(Context context){

        //IronSource permissions
        IronSource.setConsent(true);
        IronSource.setMetaData("do_not_sell","false");
        IronSource.setMetaData("is_child_directed","false");
    }


    public static LocalDateTime getCurrentDate(String pattern){
        // Create a DateTimeFormatter object with the desired pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime;
    }


    public static long getDateInMilliSeconds(LocalDateTime givenDateString, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
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


        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        // Call this on the main thread as it may require Activity.restart()
        AppCompatDelegate.setApplicationLocales(appLocale);
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


    public static void handleLanguage(Activity activity){
        SettingRepository settingRepository = new SettingRepository(activity, MODE_PRIVATE);
        String language = settingRepository.getLanguage();

        System.out.println("language: " + language);
        if (language.equals(ENGLISH)) {
            Utils.setLanguage(ENGLISH, activity);
        } else if (language.equals(SPANISH)) {
            Utils.setLanguage(SPANISH, activity);
        }else {
            Utils.setLanguageAutomatic(activity);
        }
    }



    public static void askToLeaveApp(Fragment fragment){
        String title = fragment.requireActivity().getString(R.string.leave_app);
        String message = fragment.getContext().getString(R.string.ask_to_leave_app);
        AskForActionDialog askForActionDialog = new AskForActionDialog(title, message);

        askForActionDialog.setButtonListener(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {
                if (object)fragment.getActivity().finish();
            }
        });
        askForActionDialog.show(fragment.getParentFragmentManager(), title);


    }


    public static double formatDecimal(double decimalToFormat){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        // Format the number using the DecimalFormat object
        String formattedNumber = decimalFormat.format(decimalToFormat).replace(",", ".");

        return Double.parseDouble(formattedNumber);
    }
    public static BigDecimal formatDecimal(BigDecimal decimalToFormat){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        // Format the number using the DecimalFormat object
        String formattedNumber = decimalFormat.format(decimalToFormat).replace(",", ".");

        return new BigDecimal(formattedNumber);
    }


    public static Palette getColorPaletteFromImage(Bitmap img){
        if (img == null) return  null;
        Palette p = Palette.from(img).generate();
        return p;
    }

    public static Integer getMutedColor(Palette palette){
        Palette.Swatch muted = palette.getMutedSwatch();
        if(muted != null){
            int color = muted.getTitleTextColor();
            return  color;
        }
        return null;
    }
    public static Integer getVibrantColor(Palette palette){
        if (palette == null) return null;
        Palette.Swatch vibrant= palette.getMutedSwatch();
        if(vibrant != null ){
            int color = vibrant.getRgb();
            return  color;
        }
        return null;
    }

    public static Integer getLightVibrantColor(Palette palette){
        if (palette == null) return  null;
        Palette.Swatch lightVibrant= palette.getMutedSwatch();
        if(lightVibrant != null){
            int color = lightVibrant.getRgb();
            return  color;
        }
        return null;
    }

    public static void setActionBarColor(Activity activity, Integer color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);

        }
    }

    public static GradientDrawable getGradientColor(int[] colors, Integer shape, GradientDrawable.Orientation orientation,
                                    float cornersRadius, Integer gradientType){
        // Create gradient drawable
        GradientDrawable gradientDrawable = new GradientDrawable(orientation, colors);

        // Set gradient shape (RECTANGLE, OVAL, LINE, RING)
        gradientDrawable.setShape(shape);

        // Set corner radius, if needed
        gradientDrawable.setCornerRadius(cornersRadius); // Optional

        // Set the gradient type (LINEAR_GRADIENT, RADIAL_GRADIENT, SWEEP_GRADIENT)
        gradientDrawable.setGradientType(gradientType);

        return gradientDrawable;

    }

    public static void startAnimationOfScanningLine(Fragment fragment, View line, View parentContainer){

        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f);
        animation.setDuration(1500); // Adjust duration as needed
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        line.startAnimation(animation);
    }

    public static void finishScanningLineAnim(View line){
        Animation animation = line.getAnimation();

        System.out.println("finish anim");
        if (animation != null){
            System.out.println("finish anim");
            line.getAnimation().cancel();
            line.clearAnimation();
        }

    }


    public static void showAlertDialog(Fragment fragment, String message){
        AskForActionDialog askForActionDialog = new AskForActionDialog(message, false, true);
        askForActionDialog.show(fragment.getParentFragmentManager(), message);
    }







}
