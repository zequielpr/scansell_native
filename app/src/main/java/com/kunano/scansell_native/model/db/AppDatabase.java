package com.kunano.scansell_native.model.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.business.BusinessDao;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductDao;
import com.kunano.scansell_native.model.Home.product.ProductImg;
import com.kunano.scansell_native.model.Home.product.ProductImgDao;
import com.kunano.scansell_native.model.bins.business.BusinessBin;
import com.kunano.scansell_native.model.bins.business.BusinessBinDao;

@Database(entities = {Business.class, Product.class, ProductImg.class,
        BusinessBin.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static AppDatabase  instance;

    public abstract BusinessDao businessDao();

    public abstract ProductDao productDao();

    public abstract ProductImgDao productImgDao();

    public abstract BusinessBinDao businessBinDao();


    public static synchronized AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "kunano").
                    fallbackToDestructiveMigrationOnDowngrade().build();
        }
        return instance;
    }


}