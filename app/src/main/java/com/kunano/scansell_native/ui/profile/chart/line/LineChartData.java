package com.kunano.scansell_native.ui.profile.chart.line;

import com.github.mikephil.charting.data.Entry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LineChartData {
    private ArrayList<Entry> entries;
    private List<LocalDateTime> dates;


    public LineChartData() {
        entries = new ArrayList<>();
        dates = new ArrayList<>();
    }


    public LineChartData(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void setEntries(Entry entries) {
        this.entries.add(entries);
    }

    public List<LocalDateTime> getDates() {
        return dates;
    }

    public void setDate(LocalDateTime dates) {
        this.dates.add(dates);
    }
}
