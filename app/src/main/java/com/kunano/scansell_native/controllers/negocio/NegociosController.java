package com.kunano.scansell_native.controllers.negocio;

import com.kunano.scansell_native.model.negocio.Negocios;
import com.kunano.scansell_native.ui.home.HomeViewModel;

import java.util.HashMap;

public class NegociosController {
    Negocios businessesModel;
    HomeViewModel businessesView;

    public NegociosController(Negocios businessModel, HomeViewModel businessesView ){
        this.businessesModel = businessModel;
        this.businessesView = businessesView;
    }

    //Add business
    public boolean addBusiness(HashMap<String, Object> data){
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
