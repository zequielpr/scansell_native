package com.kunano.scansell_native.controllers;

import com.kunano.scansell_native.model.negocio.Prueba;
import com.kunano.scansell_native.ui.home.HomeViewModel;

public class PruebaController
{
    public PruebaController(HomeViewModel view, Prueba model) {
        this.view = view;
        this.model = model;
    }

    HomeViewModel view;
    Prueba model;


    //Controll model object
    public void setNombre(String nombre){
        model.setNombre(nombre);
    }

    public String getNombre(){
        return model.getNombre();
    }

    //Controll view
    public void updateView(){
        //view.mostrar_data(model.getNombre());
        //view.setBusinessList();
    }
}
