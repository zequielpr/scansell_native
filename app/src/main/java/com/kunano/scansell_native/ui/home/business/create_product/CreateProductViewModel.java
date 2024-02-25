package com.kunano.scansell_native.ui.home.business.create_product;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductImg;
import com.kunano.scansell_native.repository.home.BinsRepository;
import com.kunano.scansell_native.repository.home.ProductRepository;
import com.kunano.scansell_native.ui.ImageProcessor;
import com.kunano.scansell_native.ui.components.ViewModelListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateProductViewModel extends AndroidViewModel {

    private MutableLiveData<Drawable> bitmapImgMutableLiveData;
    private MutableLiveData<Boolean> handleSaveButtonClickLiveData;
    private MutableLiveData<String> warningName;
    private MutableLiveData<String> warningBuyinPrice;
    private MutableLiveData<String> warningSellingPrice;
    private MutableLiveData<String> warningStock;
    private MutableLiveData<Color> buttonColor;
    private MutableLiveData<Integer> cancelImageButtonVisibility;
    private String productId;
    private Long businessId;
    private Bitmap bitmapImg;

    private MutableLiveData<String> productNameLiveData;
    private MutableLiveData<String> buyingPriceLivedata;
    private MutableLiveData<String> sellingPriceLiveData;
    private MutableLiveData<String> stockLiveData;
    private ProductRepository productRepository;
    private MutableLiveData<String> buttonSaveTitle;
    private BinsRepository binsRepository;
    private ViewModelListener viewModelListener;
    public CreateProductViewModel(@NonNull Application application){
        super(application);
        productRepository = new ProductRepository(application);
        binsRepository = new BinsRepository(application);
        bitmapImgMutableLiveData = new MutableLiveData<>();
        bitmapImg = null;
        handleSaveButtonClickLiveData = new MutableLiveData<>();
        warningName = new MutableLiveData<>();
        warningBuyinPrice = new MutableLiveData<>();
        warningSellingPrice = new MutableLiveData<>();
        warningStock = new MutableLiveData<>();
        cancelImageButtonVisibility = new MutableLiveData<>(View.GONE);

        productNameLiveData = new MutableLiveData<>();
        buyingPriceLivedata = new MutableLiveData<>();
        sellingPriceLiveData = new MutableLiveData<>();
        stockLiveData = new MutableLiveData<>();
        buttonSaveTitle = new MutableLiveData<>();
    }

    private ExecutorService executors;


    public void checkIfProductExists(String productId){

        if (this.productId != null & this.productId != productId){
            clearOlddata();
        }

        this.productId = productId;
        productRepository.getProductByIds(productId, businessId, this::showData);
        System.out.println("Businessid create product: " + businessId + " ProductId: " + productId);
    }

    private void showData(Object result){
        Product product = (Product) result;

        if(product != null){
            System.out.println("Product name: " + product.getProductName());
            productNameLiveData.postValue(product.getProductName());
            buyingPriceLivedata.postValue(String.valueOf(product.getBuying_price()));
            sellingPriceLiveData.postValue(String.valueOf(product.getSelling_price()));
            stockLiveData.postValue(String.valueOf(product.getStock()));
            buttonSaveTitle.postValue(this.getApplication().getResources().getString(R.string.update));
            productRepository.getProdductImage(productId, this::showImage );

        }
    }

    private void showImage(ProductImg productImg){
        bitmapImg  = ImageProcessor.bytesToBitmap(productImg.getImg());

        if(bitmapImg == null){
            setDrawableImgMutableLiveData(getApplication().getDrawable(R.drawable.add_image_ic_80dp));
            cancelImageButtonVisibility.postValue(View.GONE);
            return;
        }

        setDrawableImgMutableLiveData(new BitmapDrawable(this.getApplication().getResources(), bitmapImg));
        cancelImageButtonVisibility.postValue(View.VISIBLE);

    }

    //Clear olddata
    private void  clearOlddata(){
        setButtonSaveTitle(new MutableLiveData<>(this.getApplication().getString(R.string.save)));
        setProductNameLiveData("");
        setWarningName("");
        setBuyingPriceLivedata("");
        setWarningBuyinPrice("");
        setSellingPriceLiveData("");
        setWarningSellingPrice("");
        setStockLiveData("");
        setWarningStock("");
        setBitmapImg(null);
        setCancelImageButtonVisibility(View.GONE);
        setDrawableImgMutableLiveData( this.getApplication().getDrawable(R.drawable.add_image_ic_80dp));
    }


    ExecutorService executor;
    public void sendProductToBin(ViewModelListener viewModelListener){
        executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            try {
               Long result = binsRepository.sendProductTobin(businessId, productId).get();

                viewModelListener.result(result > 0);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void shotDownExecutors(){
        if (executor != null){
            executor.shutdown();
        }
    }









    public MutableLiveData<Drawable> getBitmapImgMutableLiveData() {
        return bitmapImgMutableLiveData;
    }

    public void setDrawableImgMutableLiveData(Drawable bitmapImgMutableLiveData) {
        this.bitmapImgMutableLiveData.postValue(bitmapImgMutableLiveData);
    }

    public void setBitmapImg(Bitmap bitmapImgMutableLiveData) {
        bitmapImg = bitmapImgMutableLiveData;
    }


    public MutableLiveData<Boolean> getHandleSaveButtonClickLiveData() {
        return handleSaveButtonClickLiveData;
    }

    public void setHandleSaveButtonClickLiveData(Boolean isClickcable) {
        this.handleSaveButtonClickLiveData.postValue(isClickcable);
    }


    public MutableLiveData<String> getWarningName() {
        return warningName;
    }

    public void setWarningName(String warningName) {
        this.warningName.postValue(warningName);
    }

    public MutableLiveData<String> getWarningBuyinPrice() {
        return warningBuyinPrice;
    }

    public void setWarningBuyinPrice(String warningBuyinPrice) {
        this.warningBuyinPrice.postValue(warningBuyinPrice);
    }

    public MutableLiveData<String> getWarningSellingPrice() {
        return warningSellingPrice;
    }

    public void setWarningSellingPrice(String warningSellingPrice) {
        this.warningSellingPrice.postValue(warningSellingPrice);
    }

    public MutableLiveData<String> getWarningStock() {
        return warningStock;
    }

    public void setWarningStock(String warningStock) {
        this.warningStock.postValue(warningStock);
    }

    public Bitmap getBitmapImg() {
        return bitmapImg;
    }

    public MutableLiveData<Integer> getCancelImageButtonVisibility() {
        return cancelImageButtonVisibility;
    }

    public void setCancelImageButtonVisibility(Integer cancelImageButtonVisibility) {
        this.cancelImageButtonVisibility.postValue(cancelImageButtonVisibility);
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public MutableLiveData<String> getProductNameLiveData() {
        return productNameLiveData;
    }

    public void setProductNameLiveData(String productNameLiveData) {
        this.productNameLiveData.postValue( productNameLiveData);
    }

    public MutableLiveData<String> getBuyingPriceLivedata() {
        return buyingPriceLivedata;
    }

    public void setBuyingPriceLivedata(String buyingPriceLivedata) {
        this.buyingPriceLivedata.postValue( buyingPriceLivedata);
    }

    public MutableLiveData<String> getSellingPriceLiveData() {
        return sellingPriceLiveData;
    }

    public void setSellingPriceLiveData(String sellingPriceLiveData) {
        this.sellingPriceLiveData.postValue(sellingPriceLiveData);
    }

    public MutableLiveData<String> getStockLiveData() {
        return stockLiveData;
    }

    public void setStockLiveData(String stockLiveData) {
        this.stockLiveData.postValue(stockLiveData);
    }


    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public MutableLiveData<String> getButtonSaveTitle() {
        return buttonSaveTitle;
    }

    public void setButtonSaveTitle(MutableLiveData<String> buttonSaveTitle) {
        this.buttonSaveTitle = buttonSaveTitle;
    }

    public ViewModelListener getFragmentListener() {
        return viewModelListener;
    }

    public void setFragmentListener(ViewModelListener viewModelListener) {
        this.viewModelListener = viewModelListener;
    }
}