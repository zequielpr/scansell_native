package com.kunano.scansell_native.ui.sales;

import android.app.Application;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.kunano.scansell_native.components.Utils;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.sell.sold_products.MostSoldProducts;
import com.kunano.scansell_native.model.sell.sold_products.ProductWithReceiptDate;
import com.kunano.scansell_native.repository.home.BusinessRepository;
import com.kunano.scansell_native.repository.sell.SellRepository;
import com.kunano.scansell_native.ui.sales.chart.line.LineChartData;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class SalesViewModel extends AndroidViewModel {


    private SellRepository sellRepository;
    private BusinessRepository businessRepository;


    private MutableLiveData<LineChartData> sellsLineChartDataLive;
    private MutableLiveData<List<PieEntry>> mostSoldProductPieChartMLive;
    private LiveData<List<Business>> businessListLivedata;
    private MutableLiveData<Integer> seletedBusiness;
    private LiveData<List<ProductWithReceiptDate>> soldProductsListListLiveData;
    private LiveData<List<MostSoldProducts>> soldProductsListLiveData;

    Observer<List<MostSoldProducts>> mostSoldProductsObserver;
    Observer<List<ProductWithReceiptDate>> sellObserver;
    private LocalDateTime dateToSearch;
    private LocalDateTime currentDate;
    private LocalDateTime currentWeekDate;
    private MutableLiveData<String> selectedDateMutableLiveData;
    private MutableLiveData<Integer> mostSoldProductsTxtViewVisibility;
    private MutableLiveData<Integer> businessStatsVisibilityMutableData;
    private MutableLiveData<Integer> createBusinessButtonVisibility;
    private MutableLiveData<Double> salesSumMutableLiveDta;
    private MutableLiveData<Double> revenuesMutableLiveData;

    private Long currentBusinessId;

    public SalesViewModel(@NonNull Application application) {
        super(application);
        sellRepository = new SellRepository(application);
        businessRepository = new BusinessRepository(application);

        businessListLivedata = businessRepository.getAllBusinesses();
        seletedBusiness = new MutableLiveData<>(0);
        soldProductsListListLiveData = new MutableLiveData<>();
        soldProductsListLiveData = new MutableLiveData<>();
        salesSumMutableLiveDta = new MutableLiveData<>();

        sellsLineChartDataLive = new MutableLiveData<>();
        mostSoldProductPieChartMLive = new MutableLiveData<>();
        selectedDateMutableLiveData = new MutableLiveData<>();
        mostSoldProductsTxtViewVisibility = new MutableLiveData<>();
        businessStatsVisibilityMutableData = new MutableLiveData<>();
        createBusinessButtonVisibility = new MutableLiveData<>();
        revenuesMutableLiveData = new MutableLiveData<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDateTime.now();
            currentWeekDate =  LocalDate.now().
                    with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        }

        mostSoldProductsObserver = (List<MostSoldProducts> mostSoldProductsList)->{
            List<PieEntry> pieEntryList = processMostSoldPData(mostSoldProductsList);
            mostSoldProductsTxtViewVisibility.postValue(pieEntryList.isEmpty()? View.GONE:View.VISIBLE);
            mostSoldProductPieChartMLive.postValue(pieEntryList);
        };
        sellObserver = (List<ProductWithReceiptDate> soldProductsList)->{

            //System.out.println("receipts: " + soldProductsList.get(0).getReceiptDate());

            LineChartData lineChartData;
            lineChartData = processProductsSoldWeekly(soldProductsList);
            sellsLineChartDataLive.postValue(lineChartData);
        };
    }


    private Boolean searchCurrentWeek = true;
    public void handleDates(Integer spinnerIndex){
       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            switch (spinnerIndex) {
                case 0:
                    searchCurrentWeek = true;
                    dateToSearch = LocalDate.now().
                            with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
                    fetchSellsCurrentWeek();
                    fetchMostSoldProductsInCurrentWeek();
                    break;
                case 1:
                    searchCurrentWeek = false;
                    dateToSearch = LocalDate.now()
                            .with(TemporalAdjusters.previous(DayOfWeek.MONDAY)) // Get the previous Monday
                            .minusWeeks(1) // Move back one week to get to the previous week
                            .atStartOfDay();
                    fetchSellsLastWeek();
                    fetchMostSoldProductsInLastWeek();
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        }
    }



    public void fetchSellsCurrentWeek(){
        if (dateToSearch != null && currentBusinessId != null) {
            soldProductsListListLiveData.removeObserver(sellObserver);
            soldProductsListListLiveData = sellRepository.geSoldProductInCurrentWeek(currentBusinessId, dateToSearch);
            soldProductsListListLiveData.observeForever(sellObserver);
        }
    }

    public void fetchSellsLastWeek(){
        if (dateToSearch != null && currentBusinessId != null) {
            soldProductsListListLiveData.removeObserver(sellObserver);
            soldProductsListListLiveData = sellRepository.geSoldProductInLastWeek(currentBusinessId, dateToSearch, currentWeekDate);
            soldProductsListListLiveData.observeForever(sellObserver);
        }
    }

    private LineChartData processProductsSoldWeekly(List<ProductWithReceiptDate> productAndSellDate){

        Double salesSum = productAndSellDate.stream().reduce(0.0, (a, l)
                ->a + l.getProduct().getSelling_price(), Double::sum);
        Double revenuesSum = productAndSellDate.stream().reduce(0.0, (a, l)
                ->a + l.getProduct().getSelling_price() - l.getProduct().getBuying_price(), Double::sum);

        salesSumMutableLiveDta.postValue(Utils.formatDecimal(salesSum));
        revenuesMutableLiveData.postValue(Utils.formatDecimal(revenuesSum ));

        selectedDateMutableLiveData.postValue("");
        Float sells;
        Float spentOnSoldProducts;
        Float revenues;
        LocalDateTime dateTime = dateToSearch;
        LineChartData lineChartData = new LineChartData();
        int x = 0;



       for (DayOfWeek dayOfWeek: DayOfWeek.values()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //The date of the current week is already set up on monday
                if (dayOfWeek != DayOfWeek.MONDAY){
                    dateTime = dateToSearch.with(TemporalAdjusters.next(dayOfWeek));
                }

                sells = productAndSellDate.stream().filter((p)-> dayOfWeek == p.getReceiptDate().getDayOfWeek()).
                        reduce(0.0, (a, p) -> a + p.getProduct().getSelling_price(), Double::sum).floatValue();

                revenues =productAndSellDate.stream().filter((p)-> dayOfWeek == p.getReceiptDate().getDayOfWeek()).
                        reduce(0.0, (a, p) -> a + p.getProduct().getSelling_price() - p.getProduct().getBuying_price(), Double::sum).floatValue();;

                lineChartData.setSellsEntries(new Entry(x, sells));
                lineChartData.setRevenuesEntries(new Entry(x, revenues));
                lineChartData.setDate(dateTime);
                x++;

            }

        }
        return lineChartData;
    }




    //Get most sold prodcuts

    public void fetchMostSoldProductsInCurrentWeek(){
        soldProductsListLiveData.removeObserver(mostSoldProductsObserver);
        soldProductsListLiveData = sellRepository.getMostSoldProductsInCurrentWeek(currentBusinessId, dateToSearch);
        soldProductsListLiveData.observeForever(mostSoldProductsObserver);
    }

    public void fetchMostSoldProductsInLastWeek(){
        soldProductsListLiveData.removeObserver(mostSoldProductsObserver);
        soldProductsListLiveData = sellRepository.getMostSoldProductsInLastWeek(currentBusinessId, dateToSearch, currentWeekDate);
        soldProductsListLiveData.observeForever(mostSoldProductsObserver);
    }

    private  List<PieEntry> processMostSoldPData(List<MostSoldProducts> mostSoldProductsList){
        List<PieEntry> entries = new ArrayList<>();
        Double soldPercentage = 0.0;
        String productName = null;

        for (MostSoldProducts mostSoldProducts : mostSoldProductsList) {
            if (mostSoldProducts.getSoldProductsTotal() == 0){
                break;
            }
            soldPercentage = (double)mostSoldProducts.getSoldQuantity() * 100.0 / (double)mostSoldProducts.getSoldProductsTotal();
            productName = mostSoldProducts.getProductName();
            productName = productName.length()>15?productName.substring(0,15).concat("..."):productName;
            entries.add(new PieEntry(soldPercentage.floatValue(), productName));

        }

        return entries;
    }











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

    public LiveData<List<Business>> getBusinessListLivedata() {
        return businessListLivedata;
    }

    public void setBusinessListLivedata(LiveData<List<Business>> businessListLivedata) {
        this.businessListLivedata = businessListLivedata;
    }

    public MutableLiveData<Integer> getSeletedBusiness() {
        return seletedBusiness;
    }

    public void setSeletedBusiness(Integer seletedBusiness) {
        this.seletedBusiness.postValue(seletedBusiness);
    }

    public Long getCurrentBusinessId() {
        return currentBusinessId;
    }

    public void setCurrentBusinessId(Long currentBusinessId) {
        System.out.println("business Id: " + currentBusinessId);
        businessStatsVisibilityMutableData.postValue(currentBusinessId == null?View.GONE:View.VISIBLE);
        createBusinessButtonVisibility.postValue(currentBusinessId != null?View.GONE:View.VISIBLE);

        this.currentBusinessId = currentBusinessId;
        if (searchCurrentWeek){
            fetchSellsCurrentWeek();
            fetchMostSoldProductsInCurrentWeek();
            return;
        }

        fetchSellsLastWeek();
        fetchMostSoldProductsInLastWeek();

    }

    public MutableLiveData<String> getSelectedDateMutableLiveData() {
        return selectedDateMutableLiveData;
    }

    public void setSelectedDateMutableLiveData(String selectedDateMutableLiveData) {
        this.selectedDateMutableLiveData.postValue(selectedDateMutableLiveData);
    }

    public MutableLiveData<Integer> getMostSoldProductsTxtViewVisibility() {
        return mostSoldProductsTxtViewVisibility;
    }

    public void setMostSoldProductsTxtViewVisibility(Integer mostSoldProductsTxtViewVisibility) {
        this.mostSoldProductsTxtViewVisibility.postValue(mostSoldProductsTxtViewVisibility);
    }

    public MutableLiveData<Integer> getBusinessStatsVisibilityMutableData() {
        return businessStatsVisibilityMutableData;
    }

    public void setBusinessStatsVisibilityMutableData(Integer businessStatsVisibilityMutableData) {
        this.businessStatsVisibilityMutableData.postValue(businessStatsVisibilityMutableData);
    }

    public MutableLiveData<Integer> getCreateBusinessButtonVisibility() {
        return createBusinessButtonVisibility;
    }

    public void setCreateBusinessButtonVisibility(Integer createBusinessButtonVisibility) {
        this.createBusinessButtonVisibility.postValue(createBusinessButtonVisibility);
    }

    public MutableLiveData<Double> getSalesSumMutableLiveDta() {
        return salesSumMutableLiveDta;
    }

    public void setSalesSumMutableLiveDta(Double salesSumMutableLiveDta) {
        this.salesSumMutableLiveDta.postValue(salesSumMutableLiveDta);
    }

    public MutableLiveData<Double> getRevenuesMutableLiveData() {
        return revenuesMutableLiveData;
    }

    public void setRevenuesMutableLiveData(Double revenuesMutableLiveData) {
        this.revenuesMutableLiveData.postValue(revenuesMutableLiveData);
    }
}