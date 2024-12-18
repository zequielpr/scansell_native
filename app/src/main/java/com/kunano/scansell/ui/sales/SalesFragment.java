package com.kunano.scansell.ui.sales;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.kunano.scansell.components.Utils;
import com.kunano.scansell.model.Home.business.Business;
import com.kunano.scansell.ui.home.bottom_sheet.BottomSheetFragmentCreateBusiness;
import com.kunano.scansell.ui.sales.charts.line.CustomLineChart;
import com.kunano.scansell.ui.sales.charts.pie.CustomPieChart;
import com.kunano.scansell.ui.sell.adapters.BusinessSpinnerAdapter;
import com.kunano.scansell.R;
import com.kunano.scansell.databinding.SalesFragmentBinding;
import com.kunano.scansell.ui.sales.admin.AdminFragment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SalesFragment extends Fragment implements MenuProvider {

    private SalesFragmentBinding binding;
    private LineChart lineChart;
    private CustomLineChart customLineChart;
    private SalesViewModel salesViewModel;
    private PieChart pieChartMostSellProducts;
    private BusinessSpinnerAdapter pickBusinessSpinnerAdapter;
    private ArrayAdapter<String> pickPeriodSpinnerAdapter;
    private Spinner pickBusinessSpinner;
    private Spinner pickPeriodSpinner;
    private CustomPieChart customPieChart;
    private TextView selectedDateTextView;
    private Toolbar profileToolbar;
    private TextView mostSoldProductsTxtView;
    private TextView sellsTxtView;
    private View createNewBusinessView;
    private ImageButton createNewBusinessImgButton;
    private ScrollView businessStatsView;
    private Drawable userImage;
    private TextView sellsSumTotal;
    private TextView revenuesTextView;



    public SalesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Utils.handleLanguage(getActivity());

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        salesViewModel =
                new ViewModelProvider(this).get(SalesViewModel.class);

        binding = SalesFragmentBinding.inflate(inflater, container, false);


        lineChart = binding.lineChart;
        pieChartMostSellProducts = binding.pieChart;
        pickBusinessSpinner = binding.pickBusinessSpinner;
        pickPeriodSpinner = binding.pickPeriodSpinner;
        selectedDateTextView = binding.selecteddDateTextView;
        profileToolbar = binding.profileToolbar;
        mostSoldProductsTxtView = binding.mostSoldPTextView;
        sellsTxtView = binding.sellsTextView;
        createNewBusinessView = binding.createNewBusinessView.createNewBusinessView;
        createNewBusinessImgButton = binding.createNewBusinessView.createNewBusinessImgButton;
        businessStatsView = binding.businessStatsView;
        sellsSumTotal = binding.sellsSumTextView;
        revenuesTextView = binding.revenuesTextView;

        profileToolbar.addMenuProvider(this);



        //Select business spinner
        pickBusinessSpinnerAdapter = new BusinessSpinnerAdapter(getContext(),
                R.layout.custom_item_spinner, new ArrayList<>(), Color.BLACK);
        pickBusinessSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickBusinessSpinner.setAdapter(pickBusinessSpinnerAdapter);
        salesViewModel.getSeletedBusiness().observe(getViewLifecycleOwner(), pickBusinessSpinner::setSelection);
        salesViewModel.getBusinessListLivedata().observe(getViewLifecycleOwner(), this::setBusinessListInSpinner);
        pickBusinessSpinner.setOnItemSelectedListener(handleOnItemSelectedBusiness());



        //Select period spinner
        String[] periodList = {getString(R.string.this_week), getString(R.string.last_week)};

        pickPeriodSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, periodList);
        pickPeriodSpinnerAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        pickPeriodSpinner.setAdapter(pickPeriodSpinnerAdapter);
        pickPeriodSpinner.setOnItemSelectedListener(handleOnItemSelectedPeriod());




        customLineChart = new CustomLineChart(lineChart, this);
        customLineChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());
        customPieChart = new CustomPieChart(pieChartMostSellProducts);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.sell_navigation_graph);

            }
        });

        salesViewModel.getSellsLineChartDataLive().observe(getViewLifecycleOwner(), customLineChart::populateChart);

        salesViewModel.getMostSoldProductPieChartMLive().observe(getViewLifecycleOwner(), customPieChart::populatePieChart);
        salesViewModel.getSelectedDateMutableLiveData().observe(getViewLifecycleOwner(), selectedDateTextView::setText);
        salesViewModel.getMostSoldProductsTxtViewVisibility().observe(getViewLifecycleOwner(),
                mostSoldProductsTxtView::setVisibility);
        salesViewModel.getMostSoldProductsTxtViewVisibility().observe(getViewLifecycleOwner(),
                pieChartMostSellProducts::setVisibility);
        salesViewModel.getBusinessStatsVisibilityMutableData().observe(getViewLifecycleOwner(),
                businessStatsView::setVisibility);
        salesViewModel.getBusinessStatsVisibilityMutableData().observe(getViewLifecycleOwner(),
                pickBusinessSpinner::setVisibility);
        salesViewModel.getCreateBusinessButtonVisibility().observe(getViewLifecycleOwner(),
                createNewBusinessView::setVisibility);
        salesViewModel.getSalesSumMutableLiveDta().observe(getViewLifecycleOwner(), (t)->
                sellsSumTotal.setText(String.valueOf(BigDecimal.valueOf(t))));
        salesViewModel.getRevenuesMutableLiveData().observe(getViewLifecycleOwner(), t ->{
            revenuesTextView.setText(String.valueOf(BigDecimal.valueOf(t)));
        });


        createNewBusinessImgButton.setOnClickListener(this::createNewBusiness);


    }

    private void createNewBusiness(View view){

        BottomSheetFragmentCreateBusiness createBusiness = new BottomSheetFragmentCreateBusiness();
        createBusiness.setRequestResult(this::processCreateBusinessRequest);
        createBusiness.show(getParentFragmentManager(), BottomSheetFragmentCreateBusiness.TAG);
    }

    private void processCreateBusinessRequest(boolean result){

    }

    public OnChartValueSelectedListener getOnChartValueSelectedListener(){
        OnChartValueSelectedListener onChartValueSelectedListener;

        onChartValueSelectedListener = new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDateTime localDateTime = customLineChart.getLineChartData().getDates().get((int) e.getX());
                    BigDecimal soldAmount = Utils.formatDecimal(BigDecimal.valueOf(e.getY()));
                    salesViewModel.setSelectedDateMutableLiveData(localDateTime.format(formatter)
                    + " / " + soldAmount + getString(R.string.dollar_symbol));
                }
            }

            @Override
            public void onNothingSelected() {
                salesViewModel.setSelectedDateMutableLiveData("");
            }
        };

        return onChartValueSelectedListener;
    }






    private void setBusinessListInSpinner(List<Business> businessList){
        pickBusinessSpinnerAdapter.clear();
        pickBusinessSpinnerAdapter.addAll(businessList);
        try {
            salesViewModel.setCurrentBusinessId(pickBusinessSpinnerAdapter.getItem(0).getBusinessId());
        }catch (Exception e){
            System.out.println(e.getCause());
            salesViewModel.setCurrentBusinessId(null);
        }
    }


    public  AdapterView.OnItemSelectedListener handleOnItemSelectedBusiness(){
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int p, long l) {
                Business selectedBusiness = (Business) adapterView.getItemAtPosition(p);
                salesViewModel.setSeletedBusiness(p);
                salesViewModel.setCurrentBusinessId(selectedBusiness.getBusinessId());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                salesViewModel.setCurrentBusinessId(null);
            }
        };

        return onItemSelectedListener;
    }


    public  AdapterView.OnItemSelectedListener handleOnItemSelectedPeriod(){
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                salesViewModel.handleDates(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        return onItemSelectedListener;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.profile_tool_bar, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {


        switch(menuItem.getItemId()){
            case R.id.admin_options:
                showAdminOptions();
                return true;
        }

        return false;
    }


    private void showAdminOptions(){
        AdminFragment adminFragment = new AdminFragment(getView());
        adminFragment.show(getChildFragmentManager(), "AdminOptions");
    }
}