package com.kunano.scansell_native.model.db;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.business.BusinessDao;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductDao;
import com.kunano.scansell_native.model.Home.product.ProductImg;
import com.kunano.scansell_native.model.Home.product.ProductImgDao;
import com.kunano.scansell_native.model.bins.business.BusinessBin;
import com.kunano.scansell_native.model.bins.business.BusinessBinDao;
import com.kunano.scansell_native.model.bins.user.UserBin;
import com.kunano.scansell_native.model.bins.user.UserBinDao;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.model.sell.ReceiptDao;
import com.kunano.scansell_native.model.sell.payment.Payment;
import com.kunano.scansell_native.model.sell.payment.PaymentDao;
import com.kunano.scansell_native.model.sell.payment.card.Card;
import com.kunano.scansell_native.model.sell.payment.card.CardDao;
import com.kunano.scansell_native.model.sell.payment.cash.Cash;
import com.kunano.scansell_native.model.sell.payment.cash.CashDao;
import com.kunano.scansell_native.model.sell.product_to_sel_draft.ProductToSellDraft;
import com.kunano.scansell_native.model.sell.product_to_sel_draft.ProductToSellDraftDao;
import com.kunano.scansell_native.model.sell.sold_products.SoldProduct;
import com.kunano.scansell_native.model.sell.sold_products.SoldProductDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Database(entities = {Business.class, Product.class, ProductImg.class,
        UserBin.class, BusinessBin.class, Receipt.class, SoldProduct.class,
        ProductToSellDraft.class, Payment.class, Card.class, Cash.class},  version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "DatabaseManager";
    private static String EXPORT_FILE_NAME = "backup";

    public static String DATABASE_NAME = "kunano";
    public static AppDatabase  instance;

    public abstract BusinessDao businessDao();

    public abstract ProductDao productDao();

    public abstract ProductImgDao productImgDao();

    public abstract UserBinDao userBinDao();

    public abstract BusinessBinDao businessBinDao();

    public abstract ReceiptDao receiptDao();
    public  abstract SoldProductDao soldProductDao();
    public abstract ProductToSellDraftDao productToSellDraftDao();
    public abstract PaymentDao paymentDao();
    public abstract CardDao cardDao();
    public abstract CashDao cashDao();



    public static synchronized AppDatabase getInstance(Context context){




        if(instance == null){
            instance =Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2) // Add your migrations here if any
                    .build();;
        }
        return instance;
    }



    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };



    public static void closeDatabase() {
        instance.close();
    }

    public static boolean importDatabase(Context context, Uri sourceUri) {
        instance.close();
        DocumentFile backup = DocumentFile.fromSingleUri(context, sourceUri);
        File dbFile = context.getDatabasePath(DATABASE_NAME);

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
                            double progress = (double) totalBytesRead * 100 /totalBytes;
                            System.out.println("Progress: " + progress + "%");
                        }
                    }
                }

                // Transfer content from input stream to output stream

                outputStream.close();
                inputStream.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
                // Handle error
            }catch (Exception e){
                Log.d(TAG, "Failure" + e.getCause());
                return false;
            }

        } else {
            Log.e(TAG, "Backup file not found!");
            return false;
        }
    }




}