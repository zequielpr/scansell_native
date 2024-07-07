package com.kunano.scansell.home;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.kunano.scansell.components.Utils;
import com.kunano.scansell.model.Home.business.Business;
import com.kunano.scansell.model.db.AppDatabase;
import com.kunano.scansell.repository.home.BusinessRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.time.LocalDateTime;
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
        LocalDateTime localDateTime = Utils.getCurrentDate(Utils.YYYY_MM_DD_HH_MM_SS);
        businessRepository = new BusinessRepository(ApplicationProvider.getApplicationContext());
        Business business = new Business("name", "direction", localDateTime);

        businessRepository.insertBusiness(business,(r)-> {
            System.out.println("Result: " + r);
            assertEquals(false, r);
        });
    }

}
