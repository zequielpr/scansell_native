package com.kunano.scansell_native.ui.home.bin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentUserBinBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.home.BusinessCardAdepter;

import java.util.List;

public class UserBinFragment extends Fragment {
    private FragmentUserBinBinding binding;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private BusinessCardAdepter businessCardAdepter;

    private UserBinViewModel mViewModel;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinBinding.inflate(inflater, container, false);


        mViewModel = new ViewModelProvider(this).get(UserBinViewModel.class);


        toolbar = binding.binToolbar;

        recyclerView = binding.recycledBusinessList;


        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);

        businessCardAdepter = new BusinessCardAdepter();
        recyclerView.setAdapter(businessCardAdepter);

        mViewModel.getRecycledBusinessLiveData().observe(getViewLifecycleOwner(), this::setToolbarSubtitle);
        mViewModel.getRecycledBusinessLiveData().observe(getViewLifecycleOwner(), businessCardAdepter::submitList);


        toolbar.setNavigationIcon(R.drawable.back_arrow);



        toolbar.inflateMenu(R.menu.actions_toolbar_user_bin);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = UserBinFragmentDirections.actionUserBinFragmentToNavigationHome();
                Navigation.findNavController(getView()).navigate(action);
            }
        });
        mViewModel.setListenUserBinViewModel(new UserBinViewModel.ListenUserBinViewModel() {
            @Override
            public void requestResult(String message) {
                getActivity().runOnUiThread(()->{
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                });

            }
        });


        businessCardAdepter.setListener(new BusinessCardAdepter.OnclickBusinessCardListener() {
            @Override
            public void onShortTap(Business business, View cardHolder) {

            }

            @Override
            public void onLongTap(Business business, View cardHolder) {

            }

            @Override
            public void getCardHolderOnBind(View cardHolder, Business business) {

            }

            @Override
            public void reciveCardHol(View cardHolder) {

            }

            @Override
            public void onRestore(Business business) {
                mViewModel.restoreSingleBusiness(business);
            }
        });



        return binding.getRoot();
    }

    private void setToolbarSubtitle(List<Business> businessList){
        toolbar.setSubtitle(Integer.toString(businessList.size()).
                concat(" ").
                concat(getString(R.string.businesses_title)));
    }






    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserBinViewModel.class);
        // TODO: Use the ViewModel
    }

}