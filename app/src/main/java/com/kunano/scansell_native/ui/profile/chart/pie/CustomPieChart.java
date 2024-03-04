package com.kunano.scansell_native.ui.profile.chart.pie;

import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.List;

public class CustomPieChart {
    PieChart pieChart;
    public CustomPieChart(PieChart pieChart) {
        this.pieChart = pieChart;
    }

    public void populatePieChart(List<PieEntry> entries){

        // Create a PieDataSet
        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        // Create a PieData object from the PieDataSet
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter( this.pieChart));
        this.pieChart.setData(data);

        // Customize the chart
        this.pieChart.getDescription().setEnabled(false);
        this.pieChart.setDrawHoleEnabled(true);
        this.pieChart.setHoleColor(Color.WHITE);
        this.pieChart.setTransparentCircleRadius(61f);
        this.pieChart.setHoleRadius(58f);
        this.pieChart.setEntryLabelColor(Color.TRANSPARENT);

        // Refresh the chart
        this.pieChart.invalidate();
    }
}
