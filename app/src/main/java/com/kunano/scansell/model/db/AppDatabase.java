package com.kunano.scansell.model.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kunano.scansell.components.Utils;
import com.kunano.scansell.model.Home.business.Business;
import com.kunano.scansell.model.Home.business.BusinessDao;
import com.kunano.scansell.model.Home.product.Product;
import com.kunano.scansell.model.Home.product.ProductDao;
import com.kunano.scansell.model.Home.product.ProductImg;
import com.kunano.scansell.model.Home.product.ProductImgDao;
import com.kunano.scansell.model.bins.business.BusinessBin;
import com.kunano.scansell.model.bins.business.BusinessBinDao;
import com.kunano.scansell.model.bins.user.UserBin;
import com.kunano.scansell.model.bins.user.UserBinDao;
import com.kunano.scansell.model.sell.Receipt;
import com.kunano.scansell.model.sell.ReceiptDao;
import com.kunano.scansell.model.sell.payment.Payment;
import com.kunano.scansell.model.sell.payment.PaymentDao;
import com.kunano.scansell.model.sell.payment.card.Card;
import com.kunano.scansell.model.sell.payment.card.CardDao;
import com.kunano.scansell.model.sell.payment.cash.Cash;
import com.kunano.scansell.model.sell.payment.cash.CashDao;
import com.kunano.scansell.model.sell.product_to_sel_draft.ProductToSellDraft;
import com.kunano.scansell.model.sell.product_to_sel_draft.ProductToSellDraftDao;
import com.kunano.scansell.model.sell.sold_products.SoldProduct;
import com.kunano.scansell.model.sell.sold_products.SoldProductDao;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


    // Populate the database
    private static ExecutorService executor = Executors.newCachedThreadPool();
    public static void populateDatabase(AppDatabase db) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BusinessDao businessDao = db.businessDao();
                LocalDateTime currentTime = Utils.getCurrentDate(Utils.YYYY_MM_DD_HH_MM_SS);
                Business business = new Business("demo", "", currentTime);

                businessDao.insertBusiness(business);
            }
        });
    }



}