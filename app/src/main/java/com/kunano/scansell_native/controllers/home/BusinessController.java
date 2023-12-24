package com.kunano.scansell_native.controllers.home;

import com.kunano.scansell_native.controllers.ValidateData;
import com.kunano.scansell_native.model.Home.Business;
import com.kunano.scansell_native.ui.home.HomeViewModel;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class BusinessController {
    Business businessesModel;

    public Business getBusinessesModel() {
        return businessesModel;
    }

    public void setBusinessesModel(Business businessesModel) {
        this.businessesModel = businessesModel;
    }

    HomeViewModel businessesView;


    public BusinessController(Business businessModel, HomeViewModel businessesView ){
        this.businessesModel = businessModel;
        this.businessesView = businessesView;
    }

    //Add business
    public CompletableFuture<Boolean> addBusiness(){
        //Verify data

        HashMap<String, Object> data = new HashMap<>();
        data.put("name", businessesModel.getName());
        data.put("address", businessesModel.getAddress());


        return businessesModel.addBusiness(data);
    }

    public void setName(String name){
        businessesModel.setName(name);
    }

    public void setAddress(String address){
        businessesModel.setAddress(address);
    }


    public String getName(){
        return businessesModel.getName();
    }

    public String getAddress(){
        return businessesModel.getAddress();
    }


    public boolean verifyName(){
        return ValidateData.validateName(businessesModel.getName());
    }

    public boolean verifyAddres(){
        return ValidateData.validateAddress(businessesModel.getAddress());
    }


    //Delete business
    public boolean deleteBusiness(String businessId){
       return businessesModel.deleteBusiness(businessId);
    }



    //Controll view

    public void showData(){
        businessesModel.getBusinessListDataAsync().thenAccept(data ->{
            if(!data.isEmpty()){
                businessesView.setBusinessList(data);
                System.out.println("data: " + data.size());
            }else{

                System.out.println("No data");
                //Show button to create a new business.
            }

        });

    }


}
