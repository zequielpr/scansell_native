package com.kunano.scansell_native.ui.profile.chart.line;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class CustomLineChart {
    LineChart lineChart;
    LineChartData lineChartData;

    public CustomLineChart(LineChart lineChart) {
        this.lineChart = lineChart;
    }

    public void populateChart(LineChartData lineChartData) {
        this.lineChartData = lineChartData;



        LineDataSet dataSet = new LineDataSet(lineChartData.getEntries(), "Sells"); // Label for the dataset
        dataSet.setColor(Color.GREEN); // Set color for the line
        dataSet.setValueTextColor(Color.BLACK); // Set color for the text
        // Use cubic bezier curves for smoother flow
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(dataSet);
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
