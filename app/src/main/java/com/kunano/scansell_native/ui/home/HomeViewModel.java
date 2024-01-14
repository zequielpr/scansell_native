package com.kunano.scansell_native.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kunano.scansell_native.controllers.home.BusinessController;
import com.kunano.scansell_native.model.db.Business;

import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<BusinessCardAdepter>  listBusinessApader;
    private MutableLiveData<Integer> addButtonVisibility;
    private MutableLiveData<Integer> cancelDeleteModeButtonVisibility;
    private MutableLiveData<Integer> selectAllButtonVisibility;
    private MutableLiveData<Integer> deletButtonVisibility;
    private MutableLiveData<ViewGroup.LayoutParams> titleWidth;
    private MutableLiveData<Integer> unchedCircleVisibility;
    private MutableLiveData<Map<String, Object>> imageAndBusinessId;
    private  MutableLiveData<Drawable> imageCheckedOrUnchecked;

    private MutableLiveData<String> itemsToDelete;
    private MutableLiveData<Integer> progress;
    private MutableLiveData<Drawable> imageForSeletAllButton;
    private HomeFragment homeFragment;
    private MutableLiveData<Integer> deletingProgresDialogVisibility;

    public MutableLiveData<String> getSelectedBusinesses() {
        return selectedBusinesses;
    }

    public void setSelectedBusinesses(String selectedBusinesses) {
        this.selectedBusinesses.postValue(selectedBusinesses);
    }

    private MutableLiveData<String> selectedBusinesses;

    public AlertDialog getProgressBarDialog() {
        return progressBarDialog;
    }

    public void setProgressBarDialog(AlertDialog progressBarDialog) {
        this.progressBarDialog = progressBarDialog;
    }

    AlertDialog progressBarDialog;


    public MutableLiveData<String> getItemsToDelete() {
        return itemsToDelete;
    }



    public HomeViewModel() {
        this.listBusinessApader = new MutableLiveData<>();
        this.addButtonVisibility = new MutableLiveData<>();
        this.cancelDeleteModeButtonVisibility = new MutableLiveData<>();
        this.selectAllButtonVisibility = new MutableLiveData<>();
        this.deletButtonVisibility = new MutableLiveData<>();
        this.titleWidth = new MutableLiveData<>();
        this.unchedCircleVisibility = new MutableLiveData<>();
        this.imageAndBusinessId = new MutableLiveData<>();
        this.imageCheckedOrUnchecked = new MutableLiveData<>();
        this.imageForSeletAllButton = new MutableLiveData<>();
        this.itemsToDelete = new MutableLiveData<>();
        this.progress = new MutableLiveData<>();
        this.deletingProgresDialogVisibility = new MutableLiveData<>();
        this.selectedBusinesses = new MutableLiveData<>();


    }




    public MutableLiveData<Integer> getDeletingProgresDialogVisibility() {
        return deletingProgresDialogVisibility;
    }

    public void setDeletingCustomViewVisibility(int visibility) {
        this.deletingProgresDialogVisibility.postValue(visibility);
    }




    public void setItemsToDelete(String itemsToDelete) {
        this.itemsToDelete.postValue(itemsToDelete);
    }

    public MutableLiveData<Integer> getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress.postValue(progress);
    }

    public MutableLiveData<Drawable> getImageForSeletAllButton() {
        return imageForSeletAllButton;
    }

    public void setImageForSeletAllButton(Drawable imageForSeletAllButton) {
        this.imageForSeletAllButton.postValue(imageForSeletAllButton);
    }



    public void setBusinessList(List<Business> businessesListData, BusinessController businessController, LifecycleOwner homeLifecycleOwner, Context context){
        this.listBusinessApader.postValue(new BusinessCardAdepter(businessesListData, businessController,homeLifecycleOwner, context));
    }

    public LiveData<BusinessCardAdepter> getListBusinessApader(){return listBusinessApader;}



    public MutableLiveData<Integer> getAddButtonVisibility() {
        return addButtonVisibility;
    }

    public void setAddButtonVisibility(int visibility) {
        this.addButtonVisibility.postValue(visibility);
    }

    public MutableLiveData<Integer> getCancelDeleteModeButtonVisibility() {
        return cancelDeleteModeButtonVisibility;
    }

    public void setCancelDeleteModeButtonVisibility(int cancelDeleteModeButtonVisibility) {
        this.cancelDeleteModeButtonVisibility.postValue(cancelDeleteModeButtonVisibility);
    }

    public MutableLiveData<Integer> getSelectAllButtonVisibility() {
        return selectAllButtonVisibility;
    }

    public void setSelectAllButtonVisibility(int selectAllButtonVisibility) {
        this.selectAllButtonVisibility.postValue(selectAllButtonVisibility);
    }

    public MutableLiveData<Integer> getDeletButtonVisibility() {
        return deletButtonVisibility;
    }

    public void setDeletButtonVisibility(int deletButtonVisibility) {
        this.deletButtonVisibility.postValue(deletButtonVisibility);
    }

    public MutableLiveData<ViewGroup.LayoutParams> getTitleWidth() {
        return titleWidth;
    }

    public void setTitleWidth(ViewGroup.LayoutParams titleWidth) {
        this.titleWidth.postValue(titleWidth);
    }

    public MutableLiveData<Integer> getUncheckedCircleVisibility() {
        return unchedCircleVisibility;
    }

    public void setUncheckedCircleVisibility(int unchedCircleVisibility) {
        this.unchedCircleVisibility.postValue(unchedCircleVisibility);
    }

    public MutableLiveData<Map<String, Object>> getImageAndBusinessId() {
        return imageAndBusinessId;
    }

    public void setCircleForTouchedCard(Map<String, Object>imageAndBusinessId) {
        this.imageAndBusinessId.postValue(imageAndBusinessId);
    }

    public MutableLiveData<Drawable> getCircleForAllCards() {
        return imageCheckedOrUnchecked;
    }

    public void setCircleForAllCards(Drawable imageCheckedOrUnchecked) {
        this.imageCheckedOrUnchecked.postValue(imageCheckedOrUnchecked);
    }

}