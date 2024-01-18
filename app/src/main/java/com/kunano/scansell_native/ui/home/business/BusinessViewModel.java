package com.kunano.scansell_native.ui.home.business;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;
import com.kunano.scansell_native.repository.Repository;
import com.kunano.scansell_native.ui.home.HomeViewModel;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class BusinessViewModel extends HomeViewModel {
    private Repository repository;
    MutableLiveData<Business> currentBusiness;
    MutableLiveData<List<BusinessWithProduct>> productsList;
    private HashSet<Object> selectedProductsToDelete;
    private LinkedHashSet<Product> deletedProducts;

    public BusinessViewModel(@NonNull Application application) {
        super(application);
        System.out.println("Creado product view model");
    }
}