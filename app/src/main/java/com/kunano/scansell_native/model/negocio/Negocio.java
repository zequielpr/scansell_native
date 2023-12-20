package com.kunano.scansell_native.model.negocio;

import com.google.firebase.firestore.DocumentReference;

public class Negocio extends Negocios {


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    String nombre;
    String direccion;

    DocumentReference business;
    Negocio(String businessId){
        business = this.getBusinessCollection().document(businessId);
    }


}
