package com.kunano.scansell_native.ui.components;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageProcessor {
    public static final int PICK_IMAGE_REQUEST = 1;
    ContentResolver contentResolver;
    Fragment fragment;


    public ImageProcessor(Fragment fragment) {
        this.contentResolver = fragment.getActivity().getContentResolver();
        this.fragment = fragment;
    }


    // Convert Bitmap to byte array
    public byte[] bitmapToBytes(Bitmap bitmap) {
        if (bitmap == null)return new byte[0];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // Convert byte array to Bitmap
    public static Bitmap bytesToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static void ImageLoadTask(Uri imageUrl, ViewModelListener<Bitmap> listener){
        if (imageUrl == null) return;
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            listener.result(bitmap);
        });
    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;

        // Create a rounded Bitmap
        Bitmap roundedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Create a BitmapShader to draw the original Bitmap
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        // Create a RectF with the same size as the Bitmap
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // Draw the original Bitmap onto the canvas with a round shape
        canvas.drawRoundRect(rect, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f, paint);

        return roundedBitmap;
    }


    //Select Image from multimedia



    //Rotate

    public Bitmap handleOrientation(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap; // No rotation needed
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }



    public Bitmap decodeUri(Uri selectedImage) {
        try {
            // Decode the image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImage), null, options);
            options.inJustDecodeBounds = false;

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 400, 400); // Adjust the target size as needed

            return BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImage), null, options);
        } catch (Exception e) {
            System.out.println("problema: " + e.getMessage());
            return null;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of the image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, so the final image will be smaller or equal to the requested size
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    public int getOrientation(Uri imageUri) {
        try {
            InputStream inputStream = fragment.getContext().getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                ExifInterface exifInterface = new ExifInterface(inputStream);

                int orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                inputStream.close();

                return orientation;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ExifInterface.ORIENTATION_UNDEFINED;
    }


    public static Bitmap parseDrawbleToBitmap(Drawable drawable ){
        int widthPixels = 300;
        int heightPixels = 300;
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

}


