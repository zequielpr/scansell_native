package com.kunano.scansell_native.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.controllers.PruebaController;
import com.kunano.scansell_native.controllers.negocio.NegociosController;
import com.kunano.scansell_native.databinding.FragmentHomeBinding;
import com.kunano.scansell_native.databinding.ToolBarHomeBinding;
import com.kunano.scansell_native.model.negocio.Negocios;

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

        ToolBarHomeBinding toolBarHomeBinding = binding.includeToolbar;
        Toolbar toolbarHoma = toolBarHomeBinding.toolbar;
        TextView title = toolbarHoma.findViewById(R.id.prueba);
        title.setText(getString(R.string.businesses_title));
       // toolBarHomeBinding.toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        RecyclerView businessLIst = binding.businessList;


        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        homeViewModel.getListBusinessApader().observe(getViewLifecycleOwner(), businessLIst::setAdapter);
        businessLIst.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        return root;
    }

    public void actualizar_nombre(){
        System.out.println("nombre: "+new_name);
    }





  public void   showInterfTocreatBusiness(){}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}