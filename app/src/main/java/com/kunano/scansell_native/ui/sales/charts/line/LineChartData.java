package com.kunano.scansell_native.ui.sales.charts.line;

import com.github.mikephil.charting.data.Entry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LineChartData {
    private ArrayList<Entry> sellsEntries;
    private ArrayList<Entry> revenuesEntries;
    private List<LocalDateTime> dates;


    public LineChartData() {
        sellsEntries = new ArrayList<>();
        revenuesEntries = new ArrayList<>();
        dates = new ArrayList<>();
    }


    public LineChartData(ArrayList<Entry> sellsEntries) {
        this.sellsEntries = sellsEntries;
    }

    public ArrayList<Entry> getSellsEntries() {
        return sellsEntries;
    }

    public void setSellsEntries(Entry sellsEntries) {
        this.sellsEntries.add(sellsEntries);
    }

    public void setRevenuesEntries(Entry revenuesEntries){
        this.revenuesEntries.add(revenuesEntries);
    }

    public ArrayList<Entry> getRevenuesEntries(){
        return this.revenuesEntries;
    }


    public List<LocalDateTime> getDates() {
        return dates;
    }

    public void setDate(LocalDateTime dates) {
        this.dates.add(dates);
    }
}
