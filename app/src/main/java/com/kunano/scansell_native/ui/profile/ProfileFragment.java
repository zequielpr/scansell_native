package com.kunano.scansell_native.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.kunano.scansell_native.databinding.ProfileFragmentBinding;
import com.kunano.scansell_native.ui.profile.chart.line.CustomLineChart;
import com.kunano.scansell_native.ui.profile.chart.pie.CustomPieChart;

public class ProfileFragment extends Fragment {

    private ProfileFragmentBinding binding;
    private LineChart lineChart;
    private CustomLineChart customLineChart;
    private ProfileViewModel profileViewModel;
    private PieChart pieChartMostSellProducts;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = ProfileFragmentBinding.inflate(inflater, container, false);


        lineChart = binding.lineChart;
        pieChartMostSellProducts = binding.pieChart;

        customLineChart = new CustomLineChart(lineChart);

        CustomPieChart customPieChart = new CustomPieChart(pieChartMostSellProducts);



        profileViewModel.getCurrentWeekSells();
        profileViewModel.getSellsLineChartDataLive().observe(getViewLifecycleOwner(), customLineChart::populateChart);






        // Create data entries for the chart
       /* ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(18.5f, "Label 1"));
        entries.add(new PieEntry(26.7f, "Label 2"));
        entries.add(new PieEntry(24.0f, "Label 3"));

        customPieChart.populatePieChart(entries);*/

        profileViewModel.getMostSoldProduct();
        profileViewModel.getMostSoldProductPieChartMLive().observe(getViewLifecycleOwner(), customPieChart::populatePieChart);



        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}