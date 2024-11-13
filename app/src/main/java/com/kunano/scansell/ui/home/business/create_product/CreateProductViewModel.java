package com.kunano.scansell.ui.home.business.create_product;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell.R;
import com.kunano.scansell.model.Home.product.Product;
import com.kunano.scansell.model.Home.product.ProductImg;
import com.kunano.scansell.repository.home.BinsRepository;
import com.kunano.scansell.repository.home.ProductRepository;
import com.kunano.scansell.components.ImageProcessor;
import com.kunano.scansell.components.ListenResponse;
import com.kunano.scansell.components.ViewModelListener;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateProductViewModel extends AndroidViewModel {

    private String productName;
    private String buyPrice;
    private String sellPrice;
    private String stock;

    private boolean productToUpdate;

    private MutableLiveData<Bitmap> bitmapImgMutableLiveData;
    private MutableLiveData<Boolean> handleSaveButtonClickLiveData;
    private MutableLiveData<String> warningName;
    private MutableLiveData<String> warningBuyinPrice;
    private MutableLiveData<String> warningSellingPrice;
    private MutableLiveData<String> warningStock;
    private MutableLiveData<Color> buttonColor;
    private MutableLiveData<Integer> cancelImageButtonVisibility;
    private MutableLiveData<String> barSubtitle;
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
        barSubtitle = new MutableLiveData<>();
        cancelImageButtonVisibility = new MutableLiveData<>(View.GONE);

        productNameLiveData = new MutableLiveData<>();
        buyingPriceLivedata = new MutableLiveData<>();
        sellingPriceLiveData = new MutableLiveData<>();
        stockLiveData = new MutableLiveData<>();
        buttonSaveTitle = new MutableLiveData<>();
    }

    public void checkIfProductExists(String productId){

        if (this.productId != null | this.productId != productId){
            clearOlddata();
        }
        barSubtitle.postValue(productId);

        this.productId = productId;
        productRepository.getProductByIds(productId, businessId, this::showData);
    }


    private void showData(Object result){
        Product product = (Product) result;

        if(product != null){
            productToUpdate = true;
            System.out.println("Product name: " + product.getProductName());
            productNameLiveData.postValue(product.getProductName());
            buyingPriceLivedata.postValue(String.valueOf(product.getBuying_price()));
            sellingPriceLiveData.postValue(String.valueOf(product.getSelling_price()));
            stockLiveData.postValue(String.valueOf(product.getStock()));
            buttonSaveTitle.postValue(this.getApplication().getResources().getString(R.string.update));
            productRepository.getProdductImage(productId, product.getBusinessIdFK(), this::showImage );
        }else {
            productToUpdate = false;
        }
    }

    private void showImage(ProductImg productImg){
        bitmapImg  = ImageProcessor.bytesToBitmap(productImg.getImg());

        if(bitmapImg == null){
            setBitmapImgMutableLiveData(ImageProcessor.parseDrawbleToBitmap(getApplication().getDrawable(R.drawable.add_image_ic_80dp)));
            cancelImageButtonVisibility.postValue(View.GONE);
            return;
        }

        setBitmapImgMutableLiveData(bitmapImg);
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
        setBitmapImgMutableLiveData( ImageProcessor.parseDrawbleToBitmap(this.getApplication().getDrawable(R.drawable.add_image_ic_80dp)));
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

    public void createProduct(String productId, String name, String buyingPrice, String sellingPrice, String stock,
                              LocalDateTime creatingDate, byte[] img, ListenResponse response) {

        double bPrice = Double.parseDouble(buyingPrice);
        double sPrice = Double.parseDouble(sellingPrice);
        int stck = Integer.parseInt(stock);
        Product product = new Product(productId, businessId, name, bPrice, sPrice, stck,
                creatingDate);

        productRepository.insertProduct(product, img, response::isSuccessfull);
    }

    public void updateProduct(String productId,  String name, String buyingPrice, String sellingPrice, String stock,
                              LocalDateTime creatingDate, byte[] img, ListenResponse response) {

        double bPrice = Double.parseDouble(buyingPrice);
        double sPrice = Double.parseDouble(sellingPrice);
        int stck = Integer.parseInt(stock);
        Product product = new Product(productId, businessId, name, bPrice, sPrice, stck,
                creatingDate);
        System.out.println("Upadate product: " + businessId);

        productRepository.updateProduct(product, img, response::isSuccessfull);
    }









    public MutableLiveData<Bitmap> getBitmapImgMutableLiveData() {
        return bitmapImgMutableLiveData;
    }

    public void setBitmapImgMutableLiveData(Bitmap bitmapImgMutableLiveData) {
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
        if (businessId == null)return;
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


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }


    public boolean isProductToUpdate() {
        return productToUpdate;
    }

    public void setProductToUpdate(boolean productToUpdate) {
        this.productToUpdate = productToUpdate;
    }

    public MutableLiveData<String> getBarSubtitle() {
        return barSubtitle;
    }

    public void setBarSubtitle(String barSubtitle) {
        this.barSubtitle.postValue(barSubtitle);
    }
}