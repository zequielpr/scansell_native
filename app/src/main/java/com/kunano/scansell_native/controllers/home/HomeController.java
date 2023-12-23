package com.kunano.scansell_native.controllers.home;

import com.kunano.scansell_native.model.Home.Business;
import com.kunano.scansell_native.model.Home.Home;
import com.kunano.scansell_native.ui.home.HomeViewModel;

import java.util.HashMap;

public class HomeController {
    Home businessesModel;
    HomeViewModel businessesView;


    public HomeController(Home businessModel, HomeViewModel businessesView ){
        this.businessesModel = businessModel;
        this.businessesView = businessesView;
    }

    //Add business
    public boolean addBusiness(Business businessData){
        //Verify data


        HashMap<String, Object> data = new HashMap<>();
        data.put("name", businessData.getName());
        data.put("address", businessData.getAddress());
        return businessesModel.addBusiness(data);
    }

    //Delete business
    public boolean deleteBusiness(String businessId){
       return businessesModel.deleteBusiness(businessId);
    }

    public void showData(){
        businessesModel.getBusinessListDataAsync().thenAccept(data ->{
            if(!data.isEmpty()){
                businessesView.setBusinessList(data);
            }else{

                //Show button to create a new business.
            }

        });

    }


}
