package com.kunano.scansell_native.model.db;

import android.content.Context;

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

@Database(entities = {Business.class, Product.class, ProductImg.class,
        UserBin.class, BusinessBin.class, Receipt.class, SoldProduct.class,
        ProductToSellDraft.class},  version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

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
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "kunano").
                    fallbackToDestructiveMigrationOnDowngrade().build();
        }
        return instance;
    }


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            // Migration code to handle changes from version 1 to version 2
        }
    };


}