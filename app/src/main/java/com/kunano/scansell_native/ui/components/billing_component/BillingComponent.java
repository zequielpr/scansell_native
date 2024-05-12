package com.kunano.scansell_native.ui.components.billing_component;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.common.collect.ImmutableList;
import com.kunano.scansell_native.repository.firebase.Premium;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.components.ViewModelListener;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;

import java.util.List;

public class BillingComponent implements PurchasesUpdatedListener {
    private static PurchasesUpdatedListener purchasesUpdatedListener;
    private static BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private Context context;
    private Activity activity;
    public static final String PRODUCT_ID = "backup_f";
    private Premium premium;
    private AccountHelper accountHelper;
    private ViewModelListener<Boolean> hasTheFunctionBeenBoughtListener;


    public BillingComponent(Activity activity) {
        this.activity = activity;
        this.context = activity.getBaseContext();
        this.accountHelper = new AccountHelper();
        this.premium = new Premium();

        if (purchasesUpdatedListener == null) {
            purchasesUpdatedListener = new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                    // To be implemented in a later section.
                }
            };
        }

        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(context)
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases()
                    .build();
        }
        connectToGooglePlay();
    }

    public void connectToGooglePlay() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.NETWORK_ERROR) {
                    Utils.showToast(activity, "network error", Toast.LENGTH_LONG);
                } else {
                    Utils.showToast(activity, "There has been an error", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }


    public void queryProductsToBuy(String productId, ViewModelListener<ProductDetails> listener) {
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(productId)
                                                .setProductType(BillingClient.ProductType.INAPP)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {

                        if (productDetailsList.isEmpty()) {
                            listener.result(null);
                        } else {
                            listener.result(productDetailsList.get(0));
                        }

                        // check billingResult
                        // process returned productDetailsList
                        //BillingComponent.this.productDetailsList = productDetailsList;
                    }
                }
        );
    }


    public void launchPurchaseFlow(ProductDetails productDetails) {
        // An activity reference from which the billing flow will be launched.
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                .setProductDetails(productDetails)
                                // For one-time products, "setOfferToken" method shouldn't be called.
                                // For subscriptions, to get an offer token, call
                                // ProductDetails.subscriptionOfferDetails() for a list of offers
                                // that are available to the user.
                                //.setOfferToken(selectedOfferToken)
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        // Launch the billing flow
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {

        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.NETWORK_ERROR) {

        } else {
            // Handle any other error codes.
        }
    }

    void handlePurchase(Purchase purchase) {
        if (!isSignatureValid(purchase)) {
            if (hasTheFunctionBeenBoughtListener != null)hasTheFunctionBeenBoughtListener.result(false);
            return;
        }

        if (accountHelper.getCurrentUser() == null){
            //Notify user of thee error
            return;
        }

        if (acknowledgePurchaseResponseListener == null) {
            acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
                @Override
                public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {

                }
            };
        }

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            premium.setPremiumState(PRODUCT_ID, accountHelper.getUserId(), true,
                    new ViewModelListener<Boolean>() {
                        @Override
                        public void result(Boolean result) {
                            if (!purchase.isAcknowledged() && result) {
                                AcknowledgePurchaseParams acknowledgePurchaseParams =
                                        AcknowledgePurchaseParams.newBuilder()
                                                .setPurchaseToken(purchase.getPurchaseToken())
                                                .build();
                                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                            }
                        }
                    });
        }
    }




    //Get purchases that the user has made
    public void queryPurchase(String productName, ViewModelListener<Boolean> listener) {
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().
                setProductType(BillingClient.ProductType.INAPP).build(), new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                if (list.isEmpty()){
                    listener.result(false);
                    return;
                }

                for (Purchase purchase: list){
                    if (purchase.getProducts().isEmpty()){
                        listener.result(false);
                        return;
                    }
                    for (String product : purchase.getProducts()){
                        listener.result(product.equalsIgnoreCase(productName));
                    }
                }
            }
        });
    }


    private boolean isSignatureValid(Purchase purchase) {
        return Security.verifyPurchase(Security.BASE_64_ENCODED_PUBLIC_KEY, purchase.getOriginalJson(), purchase.getSignature());
    }

    public void endConnection() {
        if (billingClient != null && billingClient.isReady()) {
            billingClient.endConnection();
            billingClient = null;
        }
    }

    public void setHasTheFunctionBeenBoughtListener(ViewModelListener<Boolean> hasTheFunctionBeenBoughtListener) {
        this.hasTheFunctionBeenBoughtListener = hasTheFunctionBeenBoughtListener;
    }
}