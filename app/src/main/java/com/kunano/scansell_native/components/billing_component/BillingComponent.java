package com.kunano.scansell_native.components.billing_component;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.ImmutableList;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.components.Utils;
import com.kunano.scansell_native.components.ViewModelListener;
import com.kunano.scansell_native.repository.firebase.PremiumRepository;

import java.util.List;

public class BillingComponent {
    private static BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private Context context;
    private Activity activity;
    public static final String PREMIUM_ID = "ads_free";
    private PremiumRepository premiumRepository;
    private ViewModelListener<Boolean> hasTheFunctionBeenBoughtListener;
    private Fragment fragment;


    public BillingComponent(Fragment fragment) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.context = activity.getBaseContext();
        this.premiumRepository = new PremiumRepository();

        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(context)
                    .setListener(this::onPurchasesUpdated).enablePendingPurchases()
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
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();

        if (billingClient.isReady()){
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
        }else {
            System.out.println("Client not ready");
        }
    }


    public void launchPurchaseFlow(ProductDetails productDetails) {
        String offerToken = productDetails.getSubscriptionOfferDetails().get(0).getOfferToken();

        // An activity reference from which the billing flow will be launched.
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder().
                                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                setProductDetails(productDetails).
                                setOfferToken(offerToken)
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

       if (billingClient.isReady()){
           billingClient.launchBillingFlow(activity, billingFlowParams);
       }else {
           System.out.println("Client not ready");
       }


    }


    public void launchAdminSubsFlow(String oldPurchaseToken) {
        // An activity reference from which the billing flow will be launched.
        BillingFlowParams.SubscriptionUpdateParams subscriptionUpdateParams =
                BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                //Subs token
                .setOldPurchaseToken(oldPurchaseToken).build();

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSubscriptionUpdateParams(subscriptionUpdateParams)
                .build();

        // Launch the billing flow
        if (billingClient.isReady()){
            billingClient.launchBillingFlow(activity, billingFlowParams);
        }else {
            System.out.println("Client not ready");
        }
    }


    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
                System.out.println("Purchase state: ok");
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            System.out.println("Purchase state: USER_CANCELED");
            // Handle an error caused by a user cancelling the purchase flow.
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            System.out.println("Purchase state: ITEM_ALREADY_OWNED");
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.NETWORK_ERROR) {
            System.out.println("Purchase state: NETWORK_ERROR");
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
            System.out.println("Purchase state: SERVICE_DISCONNECTED");
        } else {
            // Handle any other error codes.
            System.out.println("Purchase state: unknown");
        }
    }

    void handlePurchase(Purchase purchase) {
        Security.verifyPurchase(purchase.getOriginalJson(), purchase.getSignature()).addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean isVerified) {
                if (isVerified){
                    acknowledgePurchase(purchase);
                }else {
                  showUnsuccessfulSubscriptionMessage();
                    System.out.println("Purchase not verified");
                }
            }
        });
    }

    private void showUnsuccessfulSubscriptionMessage(){
        Utils.showAlertDialog(fragment, activity.getString(R.string.subscription_has_failed));
    }

    private void showToast(String message){
        Utils.showToast(activity, message, Toast.LENGTH_LONG);
    }

    public void consume() {
        // Check if billingClient is ready
        if (billingClient.isReady()) {
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(PREMIUM_ID)
                    .build();

            billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        // Consume successful
                        System.out.println("consumed");
                    } else {
                        // Consume failed
                        System.out.println("fail consumption");
                    }
                }
            });
        } else {
            // Billing client is not ready
        }
    }


    private void acknowledgePurchase(Purchase purchase){

        if (acknowledgePurchaseResponseListener == null) {
            acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
                @Override
                public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        showToast(activity.getString(R.string.subscription_successful));
                    } else{
                        showUnsuccessfulSubscriptionMessage();
                        System.out.println("Fail to acknowledge purchase");
                    }
                }
            };
        }


        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                System.out.println("Purchase acknowledgement: acknowledgement request sent ");
            }else{
                //showUnsuccessfulSubscriptionMessage();
                System.out.println("Purchase acknowledgement: purchase acknowledged");
            }
        }else {
            System.out.println("Purchase acknowledgement: not purchased");
        }
    }


    //Get purchases that the user has made
    public void queryPurchase(String productToken, ViewModelListener<Boolean> listener) {
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().
                setProductType(BillingClient.ProductType.SUBS).build(), new PurchasesResponseListener() {
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
                        listener.result(product.equalsIgnoreCase(productToken));
                    }
                }
            }
        });
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