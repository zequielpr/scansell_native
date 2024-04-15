package com.kunano.scansell_native.ui.sell.receipts.dele_component;

import android.app.Application;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.repository.home.BinsRepository;
import com.kunano.scansell_native.repository.home.BusinessRepository;
import com.kunano.scansell_native.repository.home.ProductRepository;
import com.kunano.scansell_native.repository.sell.SellRepository;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.ProgressBarDialog;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.components.ViewModelListener;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProcessItemsComponent<T> {

    public static String ASK_TO_DELETE_TAG = "ask to delete items";
    private static String ASK_TO_BIN_TAG = "ask to bin items";
    public static String SHOW_DELETING_PROGRESS = "ask to delete items";

    private Fragment fragment;
    private Application application;
    private BusinessRepository businessRepository;
    private ProductRepository productRepository;
    private SellRepository sellRepository;
    private BinsRepository binsRepository;


    private Toolbar toolbar;


    int itemsCounter;
    private Executor executor;
    private boolean isProcessToCancel;
    private boolean isAllSelected;
    private boolean isProcessItemActive;

    private LinkedHashSet<T> itemsToProcess;
    private MutableLiveData<String> processedItemsMutableLiveData;
    private MutableLiveData<Integer> processedPercentageMutableLiveData;
    private MutableLiveData<Integer> itemsToProcessMutableLIveDate;


    public ProcessItemsComponent(Fragment fragment) {
        this.fragment = fragment;
        this.application = fragment.getActivity().getApplication();

        itemsToProcess = new LinkedHashSet<>();

        businessRepository = new BusinessRepository(application);
        productRepository = new ProductRepository(application);
        sellRepository = new SellRepository(application);
        binsRepository = new BinsRepository(application);

        processedItemsMutableLiveData = new MutableLiveData<>();
        processedPercentageMutableLiveData = new MutableLiveData<>();
        itemsToProcessMutableLIveDate = new MutableLiveData<>();

        isProcessToCancel = false;
        isProcessItemActive = false;
        isAllSelected = false;
    }

    public void binItems(ViewModelListener<Void> listener) {
        this.listener = listener;
        new BinItems().binSelectedItems();

    }

    public void deleteItems(ViewModelListener<Void> listener) {
        this.listener = listener;
        new DeleteItems().deleteSelectedItems();
    }
    private ViewModelListener<Void> listener;

    public void restoreItems() {
        new RestoreItems();
    }


    private ProgressBarDialog progressBarDialog;
    public void showProcessProgress() {
        String title = fragment.getString(R.string.delete_selected_items);
        progressBarDialog =
                new ProgressBarDialog(title, processedPercentageMutableLiveData, processedItemsMutableLiveData);
        progressBarDialog.setAction(this::cancelProcess);
        progressBarDialog.show(fragment.getParentFragmentManager(), SHOW_DELETING_PROGRESS);
    }

    public void hideProgress(){
        if (progressBarDialog != null) progressBarDialog.dismiss();
    }

    private void updateProcessProgress(int processedItems) {
        processedItemsMutableLiveData.postValue(String.valueOf( processedItems));
        int processedPercentage =  processedItems * 100 / itemsToProcess.size();
        processedPercentageMutableLiveData.postValue(processedPercentage);
    }


    private void cancelProcess(boolean isToCancel) {
        this.isProcessToCancel = isToCancel;

    }


    public void addItemToProcess(T item) {
        itemsToProcess.add(item);
        itemsToProcessMutableLIveDate.postValue(itemsToProcess.size());
    }

    public void removeItemToProcess(T item) {
        itemsToProcess.remove(item);
        itemsToProcessMutableLIveDate.postValue(itemsToProcess.size());
        if (itemsToProcess.size() == 0) isAllSelected = false;
    }




    public void processFinished(String message) {
        itemsToProcess.clear();
        if( listener != null)listener.result(null);
        if (progressBarDialog == null) return;
        progressBarDialog.dismiss();
    }


    public HashSet<T> getItemsToProcess() {
        return itemsToProcess;
    }

    public void setItemsToProcess(LinkedHashSet<T> itemsToProcess) {
        this.itemsToProcess = itemsToProcess;
        if (itemsToProcess.size() == 0) isAllSelected = false;
    }

    public void clearItemsToProcess(){
        this.itemsToProcess.clear();
        isAllSelected = false;
    }

    public boolean isItemToBeProcess(T item){
        return itemsToProcess.contains(item);
    }

    public boolean isProcessToCancel() {
        return isProcessToCancel;
    }

    public void setProcessToCancel(boolean processToCancel) {
        isProcessToCancel = processToCancel;
    }

    public boolean isAllSelected() {
        return isAllSelected;
    }

    public void setAllSelected(boolean allSelected) {
        isAllSelected = allSelected;
    }

    public boolean isProcessItemActive() {
        return isProcessItemActive;
    }

    public void setProcessItemActive(boolean processItemActive) {
        isProcessItemActive = processItemActive;
        itemsToProcess.clear();
    }

    private class DeleteItems {
        public void deleteSelectedItems() {
            String title = fragment.getString(R.string.delete);
            String content = fragment.getString(R.string.delete_selected_items);

            AskForActionDialog askForActionDialog = new AskForActionDialog(title, content);
            askForActionDialog.setButtonListener(this::deleteSelectedItems);
            askForActionDialog.show(fragment.getParentFragmentManager(), ASK_TO_DELETE_TAG);
        }

        private void deleteSelectedItems(boolean isToDeletedItems) {
            if (!isToDeletedItems) return;


            executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                Integer result = 0;
                itemsCounter = 0;

                updateProcessProgress(itemsCounter);

                showProcessProgress();
                for (T i : itemsToProcess) {
                    if (isProcessToCancel) break;
                    try {
                        Thread.sleep(Math.round(1000 / itemsToProcess.size()));

                        if (i.getClass() == Business.class) {
                            deleteItem((Business) i);
                        } else if (i.getClass() == Product.class) {
                            deleteItem((Product) i);
                        } else if (i.getClass() == Receipt.class) {
                          result  =  deleteItem((Receipt) i).get();
                        }
                        if (result < 0) continue;
                        itemsCounter++;
                        updateProcessProgress(itemsCounter);
                    } catch (InterruptedException e) {
                        hideProgress();
                        Utils.showToast(fragment.getActivity(), fragment.getString(R.string.thera_has_been_an_error),
                                Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        hideProgress();
                        Utils.showToast(fragment.getActivity(), fragment.getString(R.string.thera_has_been_an_error),
                                Toast.LENGTH_SHORT);
                        throw new RuntimeException(e);
                    }catch (Exception e){
                        hideProgress();
                        Utils.showToast(fragment.getActivity(), fragment.getString(R.string.thera_has_been_an_error),
                                Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    }
                }

                //Show results

                //DesActivate process mode
                processFinished("holaa");
            });
        }


        private void deleteItem(Business business) {

        }

        private void deleteItem(Product product) {

        }

        private ListenableFuture<Integer> deleteItem(Receipt receipt) throws ExecutionException, InterruptedException {
            return sellRepository.deleteReceipt(receipt);
        }
    }

    private class BinItems{
        public void binSelectedItems() {
            String title = fragment.getString(R.string.bin);
            String content = fragment.getString(R.string.send_items_to_bin_warning);

            AskForActionDialog askForActionDialog = new AskForActionDialog(title, content);
            askForActionDialog.setButtonListener(this::binSelectedItems);
            askForActionDialog.show(fragment.getParentFragmentManager(), ASK_TO_BIN_TAG);
        }

        private void binSelectedItems(boolean isToDeletedItems) {
            if (!isToDeletedItems) return;

            executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                Integer result = 0;
                itemsCounter = 0;

                updateProcessProgress(itemsCounter);

                showProcessProgress();
                for (T i : itemsToProcess) {
                    if (isProcessToCancel) break;
                    try {
                        Thread.sleep(Math.round(1000 / itemsToProcess.size()));

                        if (i.getClass() == Business.class) {
                            binItem((Business) i).get();
                        } else if (i.getClass() == Product.class) {
                            binItem((Product) i).get();
                        } else if (i.getClass() == Receipt.class) {

                        }
                        if (result < 0) continue;
                        itemsCounter++;
                        updateProcessProgress(itemsCounter);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                //Show results

                //DesActivate process mode
                processFinished("holaa");
            });
        }


        private ListenableFuture<Long> binItem(Business business) {
            return binsRepository.sendBusinessTobin(business.getBusinessId());
        }

        private ListenableFuture<Long> binItem(Product product) {
            return binsRepository.sendProductTobin(product.getBusinessIdFK(), product.getProductId());
        }

        private void binItem(Receipt receipt) throws ExecutionException, InterruptedException {

        }

    }

    private class RestoreItems{

    }


}
