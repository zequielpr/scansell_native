package com.kunano.scansell_native.ui.profile;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.model.sell.sold_products.MostSoldProducts;
import com.kunano.scansell_native.repository.home.BusinessRepository;
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
    private BusinessRepository businessRepository;


    private MutableLiveData<LineChartData> sellsLineChartDataLive;
    private MutableLiveData<List<PieEntry>> mostSoldProductPieChartMLive;
    private LiveData<List<Business>> businessListLivedata;
    private MutableLiveData<Integer> seletedBusiness;
    private LiveData<List<Receipt>> receiptListLiveData;
    private LiveData<List<MostSoldProducts>> soldProductsListLiveData;

    Observer<List<MostSoldProducts>> mostSoldProductsObserver;
    Observer<List<Receipt>> sellObserver;
    private LocalDateTime dateToSearch;
    private LocalDateTime currentDate;
    private LocalDateTime currentWeekDate;
    private MutableLiveData<String> selectedDateMutableLiveData;

    private Long currentBusinessId;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        sellRepository = new SellRepository(application);
        businessRepository = new BusinessRepository(application);

        businessListLivedata = businessRepository.getAllBusinesses();
        seletedBusiness = new MutableLiveData<>(0);
        receiptListLiveData = new MutableLiveData<>();
        soldProductsListLiveData = new MutableLiveData<>();

        sellsLineChartDataLive = new MutableLiveData<>();
        mostSoldProductPieChartMLive = new MutableLiveData<>();
        selectedDateMutableLiveData = new MutableLiveData<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDateTime.now();
            currentWeekDate =  LocalDate.now().
                    with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        }

        mostSoldProductsObserver = (List<MostSoldProducts> mostSoldProductsList)->{
            List<PieEntry> pieEntryList = processMostSoldPData(mostSoldProductsList);
            mostSoldProductPieChartMLive.postValue(pieEntryList);
        };
        sellObserver = (List<Receipt> receiptList)->{

            LineChartData lineChartData;
            lineChartData = processReceiptsGetWeeklySells(receiptList);
            sellsLineChartDataLive.postValue(lineChartData);
        };
    }


    Boolean processMonthlySells = false;
    public void handleDates(Integer spinnerIndex){
       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            switch (spinnerIndex) {
                case 0:
                    dateToSearch = LocalDate.now().
                            with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
                    fetchSellsCurrentWeek();
                    fetchMostSoldProductsInCurrentWeek();
                    break;
                case 1:
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
            receiptListLiveData.removeObserver(sellObserver);
            receiptListLiveData = sellRepository.getCurrentWeekSells(currentBusinessId, dateToSearch);
            receiptListLiveData.observeForever(sellObserver);
        }
    }

    public void fetchSellsLastWeek(){
        if (dateToSearch != null && currentBusinessId != null) {
            receiptListLiveData.removeObserver(sellObserver);
            receiptListLiveData = sellRepository.getLastWeekSells(currentBusinessId, dateToSearch, currentWeekDate);
            receiptListLiveData.observeForever(sellObserver);
        }
    }

    private LineChartData processReceiptsGetWeeklySells(List<Receipt> receiptList){
        selectedDateMutableLiveData.postValue("");
        Float sells;
        LocalDateTime dateTime = dateToSearch;
        LineChartData lineChartData = new LineChartData();
        int x = 0;



       for (DayOfWeek dayOfWeek: DayOfWeek.values()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //The date of the current week is already set up on monday
                if (dayOfWeek != DayOfWeek.MONDAY){
                    dateTime = dateToSearch.with(TemporalAdjusters.next(dayOfWeek));
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
                entries.add(new PieEntry(0, this.getApplication().getString(R.string.not_sold_product)));
                break;
            }
            soldPercentage = (double)mostSoldProducts.getSoldQuantity() * 100.0 / (double)mostSoldProducts.getSoldProductsTotal();
            System.out.println("Sold percentage: " + soldPercentage);
            productName = mostSoldProducts.getProductName();
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
        this.currentBusinessId = currentBusinessId;
        fetchSellsCurrentWeek();
        fetchMostSoldProductsInCurrentWeek();
    }

    public MutableLiveData<String> getSelectedDateMutableLiveData() {
        return selectedDateMutableLiveData;
    }

    public void setSelectedDateMutableLiveData(String selectedDateMutableLiveData) {
        this.selectedDateMutableLiveData.postValue(selectedDateMutableLiveData);
    }
}