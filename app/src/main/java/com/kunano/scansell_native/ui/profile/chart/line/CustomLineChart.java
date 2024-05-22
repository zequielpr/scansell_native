package com.kunano.scansell_native.ui.profile.chart.line;

import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.kunano.scansell_native.R;

public class CustomLineChart {
    LineChart lineChart;
    LineChartData lineChartData;
    int primaryColor;
    private String salesString;
    private String revenuesString;

    public CustomLineChart(LineChart lineChart, Fragment fragment) {
        this.lineChart = lineChart;
        this.revenuesString = fragment.getString(R.string.revenues);
        this.salesString = fragment.getString(R.string.sales);
        primaryColor = ContextCompat.getColor(fragment.getContext(), R.color.appPColor);
    }

    public void populateChart(LineChartData lineChartData) {
        this.lineChartData = lineChartData;

        LineDataSet revenuesDataSet = new LineDataSet(lineChartData.getRevenuesEntries(), revenuesString );
        revenuesDataSet.setColor(R.color.black); // Set a different color for the second line
        revenuesDataSet.setValueTextColor(Color.BLACK);
        revenuesDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        // Combine the datasets into LineData


        LineDataSet sellsDataSet = new LineDataSet(lineChartData.getSellsEntries(), salesString); // Label for the dataset
        sellsDataSet.setColor(primaryColor); // Set color for the line
        sellsDataSet.setValueTextColor(Color.BLACK); // Set color for the text

        // Use cubic bezier curves for smoother flow
        sellsDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        LineData lineData = new LineData(sellsDataSet, revenuesDataSet);
        ClaimsXAxisValueFormatter xAxisFormatter = new ClaimsXAxisValueFormatter(lineChartData.getDates());
        lineChart.getXAxis().setValueFormatter(xAxisFormatter);

        lineChart.setData(lineData); // Set the data and display the chart
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Set position of X-axis
        lineChart.getAxisRight().setEnabled(false); // Disable right Y-axis
        lineChart.getDescription().setText("");

        lineChart.invalidate();


    }

    public void setOnChartValueSelectedListener(OnChartValueSelectedListener onChartValueSelectedListener) {
        lineChart.setOnChartValueSelectedListener(onChartValueSelectedListener);
    }

    public LineChartData getLineChartData() {
        return lineChartData;
    }
}
