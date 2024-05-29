package com.kunano.scansell.ui.sell;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Ads {



    public static void loadBanner(AdView adView, FrameLayout adContainer) {

        // Replace ad container with new ad view.
        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }

        adContainer.addView(adView);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
