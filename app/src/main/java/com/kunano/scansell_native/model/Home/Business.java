package com.kunano.scansell_native.model.Home;

import com.google.firebase.firestore.DocumentReference;

public class Business extends Home {

    String name;
    String address;

    DocumentReference business;


    public Business(){
        super();
    }

    //It obtains the business with the given id.
    public Business(String businessId){
        business = this.getBusinessCollection().document(businessId);
    }

    public Business(String name, String address){
        this.name = name;
        this.address = address;
    }


    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address.trim();
    }

    public void setDireccion(String direccion) {
        this.address = direccion;
    }


    //get products list



}
