package com.kunano.scansell_native.model.db;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
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
import com.kunano.scansell_native.model.sell.product_to_sel_draft.ProductToSellDraft;
import com.kunano.scansell_native.model.sell.product_to_sel_draft.ProductToSellDraftDao;
import com.kunano.scansell_native.model.sell.sold_products.SoldProduct;
import com.kunano.scansell_native.model.sell.sold_products.SoldProductDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

@Database(entities = {Business.class, Product.class, ProductImg.class,
        UserBin.class, BusinessBin.class, Receipt.class, SoldProduct.class,
        ProductToSellDraft.class},  version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "DatabaseManager";
    private static final String EXPORT_FILE_NAME = "backup.db";

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



    public static void exportDatabase(Context context) {
        try {
            instance.close();
            File dbFile = context.getDatabasePath(DATABASE_NAME);
            File exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File backup = new File(exportDir, EXPORT_FILE_NAME);
            FileChannel src = new FileInputStream(dbFile).getChannel();
            FileChannel dst = new FileOutputStream(backup).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            Log.d(TAG, "Database path " + dbFile);
            Log.d(TAG, "Database exported to " + backup.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Error exporting database: " + e.getCause());
        }
    }

    public static void importDatabase(Context context) {
        try {
            instance.close();
            File exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (!exportDir.exists()){
                exportDir.mkdirs();
            }
            File backup = new File(exportDir, EXPORT_FILE_NAME);
            if (backup.exists()) {
                File dbFile = context.getDatabasePath(DATABASE_NAME);
                FileChannel src = new FileInputStream(backup).getChannel();
                FileChannel dst = new FileOutputStream(dbFile).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.d(TAG, "Database imported from " + backup.getAbsolutePath());
            } else {
                Log.e(TAG, "Backup file not found!");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error importing database: " + e.getMessage());
        }
    }

}