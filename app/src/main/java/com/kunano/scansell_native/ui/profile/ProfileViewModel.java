package com.kunano.scansell_native.ui.profile;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.model.sell.sold_products.MostSoldProducts;
import com.kunano.scansell_native.repository.sell.SellRepository;
import com.kunano.scansell_native.ui.profile.chart.line.LineChartData;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends AndroidViewModel {


    private SellRepository sellRepository;


    private MutableLiveData<LineChartData> sellsLineChartDataLive;
    private MutableLiveData<List<PieEntry>> mostSoldProductPieChartMLive;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        sellRepository = new SellRepository(application);
        sellsLineChartDataLive = new MutableLiveData<>();
        mostSoldProductPieChartMLive = new MutableLiveData<>();
    }

    private LocalDateTime startOfCurrentWeek;
    public void getCurrentWeekSells(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startOfCurrentWeek = LocalDate.now().
                    with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
            sellRepository.getCurrentWeekSells(new Long(1), startOfCurrentWeek).observeForever(sellObserver);
        }
    }



    Observer<List<Receipt>> sellObserver = new Observer<List<Receipt>>() {
        @Override
        public void onChanged(List<Receipt> receiptList) {
           LineChartData lineChartData = processReceipts(receiptList);
            for (LocalDateTime date : lineChartData.getDates()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    System.out.println("Dates: " + date);
                }
            }
           sellsLineChartDataLive.postValue(lineChartData);
        }
    };



    private LineChartData processReceipts(List<Receipt> receiptList){
        Float sells;
        LocalDateTime dateTime = startOfCurrentWeek;
        LineChartData lineChartData = new LineChartData();
        int x = 0;
        for (DayOfWeek dayOfWeek: DayOfWeek.values()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //The date of the current week is already set up on monday
                if (dayOfWeek != DayOfWeek.MONDAY){
                    dateTime = startOfCurrentWeek.with(TemporalAdjusters.next(dayOfWeek));
                }

                sells = receiptList.stream().filter((r)-> dayOfWeek == r.getSellingDate().getDayOfWeek()).
                        reduce(0.0, (a, r) -> a + r.getSpentAmount(), Double::sum).floatValue();

                lineChartData.setEntries(new Entry(x, sells));
                lineChartData.setDate(dateTime);
                x++;

            }

        }
        return lineChartData;


    }




    //Get most sold prodcuts


    public void getMostSoldProduct(){
        sellRepository.getMostSoldProduct(new Long(1)).observeForever(mostSoldProductsObserver);
    }


    private  List<PieEntry> processMostSoldPData(List<MostSoldProducts> mostSoldProductsList){
        List<PieEntry> entries = new ArrayList<>();
        int soldPercentage = 0;
        String productName = null;

        for (MostSoldProducts mostSoldProducts : mostSoldProductsList) {
            soldPercentage = mostSoldProducts.getSoldQuantity() * 100 / mostSoldProducts.getSoldProductsTotal();
            productName = mostSoldProducts.getProductName();
            entries.add(new PieEntry(soldPercentage, productName));

        }

        return entries;
    }


    Observer<List<MostSoldProducts>> mostSoldProductsObserver = (List<MostSoldProducts> mostSoldProductsList)->{
        List<PieEntry> pieEntryList = processMostSoldPData(mostSoldProductsList);
        mostSoldProductPieChartMLive.postValue(pieEntryList);
    };








    public MutableLiveData<LineChartData> getSellsLineChartDataLive() {
        return sellsLineChartDataLive;
    }

    public void setSellsLineChartDataLive(MutableLiveData<LineChartData> sellsLineChartDataLive) {
        this.sellsLineChartDataLive = sellsLineChartDataLive;
    }

    public MutableLiveData<List<PieEntry>> getMostSoldProductPieChartMLive() {
        return mostSoldProductPieChartMLive;
    }

    public void setMostSoldProductPieChartMLive(MutableLiveData<List<PieEntry>> mostSoldProductPieChartMLive) {
        this.mostSoldProductPieChartMLive = mostSoldProductPieChartMLive;
    }
}