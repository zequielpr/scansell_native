package com.kunano.scansell_native.ui.profile;

import android.graphics.Bitmap;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.ProfileFragmentBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.components.ImageProcessor;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragmentCreateBusiness;
import com.kunano.scansell_native.ui.profile.admin.AdminFragment;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;
import com.kunano.scansell_native.ui.profile.chart.line.CustomLineChart;
import com.kunano.scansell_native.ui.profile.chart.pie.CustomPieChart;
import com.kunano.scansell_native.ui.sell.adapters.BusinessSpinnerAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements MenuProvider {

    private ProfileFragmentBinding binding;
    private LineChart lineChart;
    private CustomLineChart customLineChart;
    private ProfileViewModel profileViewModel;
    private PieChart pieChartMostSellProducts;
    private BusinessSpinnerAdapter pickBusinessSpinnerAdapter;
    private ArrayAdapter<String> pickPeriodSpinnerAdapter;
    private Spinner pickBusinessSpinner;
    private Spinner pickPeriodSpinner;
    private CustomPieChart customPieChart;
    private TextView selectedDateTextView;
    private Toolbar profileToolbar;
    private AccountHelper accountHelper;
    private TextView mostSoldProductsTxtView;
    private TextView sellsTxtView;
    private View createNewBusinessView;
    private ImageButton createNewBusinessImgButton;
    private ScrollView businessStatsView;
    private Drawable userImage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountHelper = new AccountHelper();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = ProfileFragmentBinding.inflate(inflater, container, false);


        lineChart = binding.lineChart;
        pieChartMostSellProducts = binding.pieChart;
        pickBusinessSpinner = binding.pickBusinessSpinner;
        pickPeriodSpinner = binding.pickPeriodSpinner;
        selectedDateTextView = binding.selecteddDateTextView;
        profileToolbar = binding.profileToolbar;
        mostSoldProductsTxtView = binding.mostSoldPTextView;
        sellsTxtView = binding.sellsAndRevenuesTextView;
        createNewBusinessView = binding.createNewBusinessView.createNewBusinessView;
        createNewBusinessImgButton = binding.createNewBusinessView.createNewBusinessImgButton;
        businessStatsView = binding.businessStatsView;

        profileToolbar.addMenuProvider(this);



        //Select business spinner
        pickBusinessSpinnerAdapter = new BusinessSpinnerAdapter(getContext(),
                R.layout.custom_item_spinner, new ArrayList<>(), Color.BLACK);
        pickBusinessSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickBusinessSpinner.setAdapter(pickBusinessSpinnerAdapter);
        profileViewModel.getSeletedBusiness().observe(getViewLifecycleOwner(), pickBusinessSpinner::setSelection);
        profileViewModel.getBusinessListLivedata().observe(getViewLifecycleOwner(), this::setBusinessListInSpinner);
        pickBusinessSpinner.setOnItemSelectedListener(handleOnItemSelectedBusiness());



        //Select period spinner
        String[] periodList = {getString(R.string.this_week), getString(R.string.last_week)};

        pickPeriodSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, periodList);
        pickPeriodSpinnerAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        pickPeriodSpinner.setAdapter(pickPeriodSpinnerAdapter);
        pickPeriodSpinner.setOnItemSelectedListener(handleOnItemSelectedPeriod());




        customLineChart = new CustomLineChart(lineChart);
        customLineChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());
        customPieChart = new CustomPieChart(pieChartMostSellProducts);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileToolbar.setTitle(accountHelper.getUserName());
        profileViewModel.getSellsLineChartDataLive().observe(getViewLifecycleOwner(), customLineChart::populateChart);

        profileViewModel.getMostSoldProductPieChartMLive().observe(getViewLifecycleOwner(), customPieChart::populatePieChart);
        profileViewModel.getSelectedDateMutableLiveData().observe(getViewLifecycleOwner(), selectedDateTextView::setText);
        profileViewModel.getMostSoldProductsTxtViewVisibility().observe(getViewLifecycleOwner(),
                mostSoldProductsTxtView::setVisibility);
        profileViewModel.getMostSoldProductsTxtViewVisibility().observe(getViewLifecycleOwner(),
                pieChartMostSellProducts::setVisibility);
        profileViewModel.getBusinessStatsVisibilityMutableData().observe(getViewLifecycleOwner(),
                businessStatsView::setVisibility);
        profileViewModel.getCreateBusinessButtonVisibility().observe(getViewLifecycleOwner(),
                createNewBusinessView::setVisibility);


        createNewBusinessImgButton.setOnClickListener(this::createNewBusiness);


        ImageProcessor.ImageLoadTask(accountHelper.getProfilePic(), this::setUserImage);

    }

    private void setUserImage(Bitmap userImage){
        getActivity().runOnUiThread(()->{
            Bitmap roundImg = ImageProcessor.getRoundedBitmap(userImage);
            this.userImage = ImageProcessor.bitmapToDrawable(ProfileFragment.this.getContext(),
                    roundImg);
            profileToolbar.setNavigationIcon(this.userImage);
        });
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
                    Float soldAmount = e.getY();
                    profileViewModel.setSelectedDateMutableLiveData(localDateTime.format(formatter)
                    + " / " + soldAmount + "€");
                }
            }

            @Override
            public void onNothingSelected() {
                profileViewModel.setSelectedDateMutableLiveData("");
            }
        };

        return onChartValueSelectedListener;
    }






    private void setBusinessListInSpinner(List<Business> businessList){
        pickBusinessSpinnerAdapter.clear();
        pickBusinessSpinnerAdapter.addAll(businessList);
        try {
            profileViewModel.setCurrentBusinessId(pickBusinessSpinnerAdapter.getItem(0).getBusinessId());
        }catch (Exception e){
            System.out.println(e.getCause());
            profileViewModel.setCurrentBusinessId(null);
        }
    }


    public  AdapterView.OnItemSelectedListener handleOnItemSelectedBusiness(){
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int p, long l) {
                Business selectedBusiness = (Business) adapterView.getItemAtPosition(p);
                profileViewModel.setSeletedBusiness(p);
                profileViewModel.setCurrentBusinessId(selectedBusiness.getBusinessId());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                profileViewModel.setCurrentBusinessId(null);
            }
        };

        return onItemSelectedListener;
    }


    public  AdapterView.OnItemSelectedListener handleOnItemSelectedPeriod(){
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                profileViewModel.handleDates(i);
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