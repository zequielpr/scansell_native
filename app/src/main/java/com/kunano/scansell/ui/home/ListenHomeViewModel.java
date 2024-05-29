package com.kunano.scansell.ui.home;

public interface ListenHomeViewModel {
   abstract void activateWaitingMode();
   abstract void  desactivateWaitingMode();

   abstract void navigateToProducts(Long businessId);



}
