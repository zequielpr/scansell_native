package com.kunano.scansell_native.home;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.repository.home.BusinessRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.Executor;

@RunWith(RobolectricTestRunner.class)
public class BusinessRepositoryTest{
    Executor executor;
    BusinessRepository businessRepository;
    Application application;
    private AppDatabase appDatabase;
    public BusinessRepositoryTest() {

    }

    @Test
    public void insertBusiness(){
        businessRepository = new BusinessRepository(ApplicationProvider.getApplicationContext());
        Business business = new Business("name", "direction", "creatingDate");

        businessRepository.insertBusiness(business,(r)-> {
            System.out.println("Result: " + r);
            assertEquals(false, r);
        });
    }

}
