package com.kunano.scansell_native.ui.profile.chart.line;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.kunano.scansell_native.ui.components.Utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ClaimsXAxisValueFormatter extends ValueFormatter {

    List<LocalDateTime> datesList;

    public ClaimsXAxisValueFormatter(List<LocalDateTime> arrayOfDates) {
        this.datesList = arrayOfDates;
    }


    @Override
    public String getAxisLabel(float value, AxisBase axis) {
/*
Depends on the position number on the X axis, we need to display the label, Here, this is the logic to convert the float value to integer so that I can get the value from array based on that integer and can convert it to the required value here, month and date as value. This is required for my data to show properly, you can customize according to your needs.
*/
        Integer position = Math.round(value);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        if (position < datesList.size())
            return sdf.format(new Date((Utils.getDateInMilliSeconds(datesList.get(position), "yyyy-MM-dd"))));
        return "";
    }
}
