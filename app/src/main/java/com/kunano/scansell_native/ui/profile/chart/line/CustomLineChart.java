package com.kunano.scansell_native.ui.profile.chart.line;

import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.kunano.scansell_native.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomLineChart {
    LineChart lineChart;
    LineChartData lineChartData;
    int primaryColor;
    private int revenuesColor;
    private String salesString;
    private String revenuesString;
    private String todayLabel;
    private static float dataTextSize = 8f;
    private static float LINE_WIDTH = 1.8f;
    private static float CIRCLE_SIZE = 3f;


    int limitLineColor;

    public CustomLineChart(LineChart lineChart, Fragment fragment) {
        this.lineChart = lineChart;
        this.revenuesString = fragment.getString(R.string.revenues);
        this.salesString = fragment.getString(R.string.sales);
        this.todayLabel = fragment.getString(R.string.today);
        this.limitLineColor = ContextCompat.getColor(fragment.getContext(), R.color.black_transparent);
        primaryColor = ContextCompat.getColor(fragment.getContext(), R.color.appPColor);
        revenuesColor = ContextCompat.getColor(fragment.getContext(), R.color.green);
    }

    public void populateChart(LineChartData lineChartData) {
        this.lineChartData = lineChartData;

        LineDataSet revenuesDataSet = new LineDataSet(lineChartData.getRevenuesEntries(), revenuesString );
        revenuesDataSet.setColor(revenuesColor); // Set a different color for the second line
        revenuesDataSet.setValueTextColor(Color.BLACK);
        revenuesDataSet.setValueTextSize(dataTextSize);
        revenuesDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        revenuesDataSet.setLineWidth(LINE_WIDTH);
        revenuesDataSet.setValueTextColor(Color.TRANSPARENT);
        revenuesDataSet.setCircleHoleColor(revenuesColor);
        revenuesDataSet.setCircleSize(CIRCLE_SIZE);
        revenuesDataSet.setCircleColor(revenuesColor);

        // Combine the datasets into LineData


        LineDataSet salesDataSet = new LineDataSet(lineChartData.getSellsEntries(), salesString); // Label for the dataset
        salesDataSet.setColor(primaryColor); // Set color for the line
        salesDataSet.setValueTextColor(Color.BLACK); // Set color for the text
        salesDataSet.setValueTextSize(dataTextSize);
        salesDataSet.setCircleColor(primaryColor);
        salesDataSet.setCircleHoleColor(primaryColor);
        salesDataSet.setCircleSize(CIRCLE_SIZE);
        salesDataSet.setValueTextColor(Color.TRANSPARENT);
        salesDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        salesDataSet.setLineWidth(LINE_WIDTH);

        LineData lineData = new LineData(salesDataSet, revenuesDataSet);
        ClaimsXAxisValueFormatter xAxisFormatter = new ClaimsXAxisValueFormatter(lineChartData.getDates());
        lineChart.getXAxis().setValueFormatter(xAxisFormatter);

        lineChart.setData(lineData); // Set the data and display the chart
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Set position of X-axis
        lineChart.getAxisRight().setEnabled(false); // Disable right Y-axis
        lineChart.getDescription().setText("");
        lineChart.setNoDataTextColor(Color.BLACK);


        highlightCurrentDay(lineChartData.getDates());

        lineChart.invalidate();


    }


    private void highlightCurrentDay(List<LocalDateTime> dates) {
        Date currentDate = new Date();
        int currentIndex = -1;
        for (int i = 0; i < dates.size(); i++) {


            // Convert LocalDateTime to Instant
            Instant instant = dates.get(i).atZone(ZoneId.systemDefault()).toInstant();

            if (isSameDay(Date.from(instant), currentDate)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex != -1) {
            LimitLine limitLine = new LimitLine(currentIndex, todayLabel);
            limitLine.setLineColor(limitLineColor);

            limitLine.setLineWidth(LINE_WIDTH);
            limitLine.setTextColor(Color.BLACK);
            limitLine.setTextSize(8f);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.addLimitLine(limitLine);
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        // Compare the two dates to check if they are the same day
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public void setOnChartValueSelectedListener(OnChartValueSelectedListener onChartValueSelectedListener) {
        lineChart.setOnChartValueSelectedListener(onChartValueSelectedListener);
    }

    public LineChartData getLineChartData() {
        return lineChartData;
    }
}
