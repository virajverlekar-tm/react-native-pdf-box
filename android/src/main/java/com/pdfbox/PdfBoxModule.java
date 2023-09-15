package com.pdfbox;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.util.Log;
import java.io.File;
// import android.net.Uri;
import androidx.core.content.FileProvider;
import java.io.IOException;
import com.turtlemint.pdf_box.PDDocument;

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
    public void unlockPdf(String filePath, String password, Promise promise) {
        try {
            // select the file for Decryption operation
            File file = new File(filePath);
      
            // Load the PDF file
            PDDocument pdd = PDDocument.load(file, password);
      
            // removing all security from PDF file
            pdd.setAllSecurityToBeRemoved(true);
      
            // Save the PDF file
            pdd.save(file);
      
            // Close the PDF file
            pdd.close();

            // promise.resolve(Uri.fromFile(file).toString());
            promise.resolve(FileProvider.getUriForFile(reactContext, reactContext.getPackageName() + ".provider", file).toString());
        } catch (IOException e) {
            promise.reject(e);
        }
    }
}
