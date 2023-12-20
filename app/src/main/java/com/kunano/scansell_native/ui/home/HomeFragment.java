package com.kunano.scansell_native.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.controllers.PruebaController;
import com.kunano.scansell_native.controllers.negocio.NegociosController;
import com.kunano.scansell_native.databinding.FragmentHomeBinding;
import com.kunano.scansell_native.model.negocio.Negocios;
import com.kunano.scansell_native.model.negocio.Prueba;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    private PruebaController controller;
    String new_name;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new HomeViewModel(inflater);
        Negocios businessesModel = new Negocios();

        NegociosController businessesController = new NegociosController(businessesModel, homeViewModel);

        businessesController.showData();


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView businessLIst = binding.businessList;


        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        homeViewModel.getListBusinessApader().observe(getViewLifecycleOwner(), businessLIst::setAdapter);
        businessLIst.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        return root;
    }

    public void actualizar_nombre(){
        System.out.println("nombre: "+new_name);
    }





    public Prueba getPruebaFromDataBase(){
        Prueba prueba = new Prueba();
        prueba.setNombre("zequiel");
        return  prueba;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}