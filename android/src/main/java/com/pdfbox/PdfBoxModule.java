package com.pdfbox;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.util.Log;
import java.io.File;
import java.io.IOException;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import javax.annotation.Nonnull;

public class PdfBoxModule extends ReactContextBaseJavaModule {
    public static ReactApplicationContext reactContext;

    PdfBoxModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Nonnull
    @Override
    public String getName() {
        return "PdfBox";
    }

    @ReactMethod
    public void unlockPdf(String filePath, String password) {
        try {
            // select a file for Decryption operation
            File file = new File(filePath);
      
            // Load the PDF file
            PDDocument pdd = PDDocument.load(file, password);
      
            // removing all security from PDF file
            pdd.setAllSecurityToBeRemoved(true);
      
            // Save the PDF file
            pdd.save(file);
      
            // Close the PDF file
            pdd.close();
        } catch (IOException e) {
            Log.e("PdfBox-React Native", "Exception thrown while creating PDF", e);
        }
    }
}
