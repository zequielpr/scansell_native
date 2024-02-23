package com.kunano.scansell_native.ui.components;

import android.annotation.SuppressLint;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class BarcodeScannerCustom implements ImageAnalysis.Analyzer {
    BarcodeScannerCustomListenner barcodeScannerCustomListenner;

    public BarcodeScannerCustom() {

    }

    @SuppressLint("UnsafeOptInUsageError")
    public void analyze(@NonNull ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();



        if (imageProxy != null) {
            InputImage inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScannerOptions barcodeScannerOptions = new BarcodeScannerOptions.Builder().
                    setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_EAN_13,
                            Barcode.FORMAT_CODE_39, Barcode.FORMAT_CODE_128, Barcode.FORMAT_EAN_8 ). enableAllPotentialBarcodes().build();


            BarcodeScanner scanner = BarcodeScanning.getClient(barcodeScannerOptions);



          Task<List<Barcode>> result =  scanner.process(inputImage).
                  addOnSuccessListener(barcodes -> {

                      for (Barcode barcode : barcodes) {

                          barcodeScannerCustomListenner.receiveBarCodeData(barcode.getRawValue());

                          //System.out.println("Resultado: " + barcode.getFormat());
                      }
                  }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //System.out.println("Fallo: " + e.getCause().getMessage());
                }
            });

        }

        imageProxy.close();
    }




    @FunctionalInterface
    public interface BarcodeScannerCustomListenner{

        abstract void receiveBarCodeData(String data);
    }


    public BarcodeScannerCustomListenner getBarcodeScannerCustomListenner() {
        return barcodeScannerCustomListenner;
    }

    public void setBarcodeScannerCustomListenner(BarcodeScannerCustomListenner barcodeScannerCustomListenner) {
        this.barcodeScannerCustomListenner = barcodeScannerCustomListenner;
    }
}
