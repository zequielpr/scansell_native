package com.kunano.scansell_native.ui.home;

public interface ListenHomeViewModel {
   abstract void activateWaitingMode();
   abstract void  desactivateWaitingMode();

   abstract void navigateToProducts(String businessId);



   abstract void showProgressBar();

   abstract void hideProgressBar();

   /** Update the progress bar shown in the deleting process**/
   abstract void askDeleteBusiness();


}
