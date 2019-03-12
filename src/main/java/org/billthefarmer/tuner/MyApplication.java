package org.billthefarmer.tuner;

import android.Manifest;
import android.app.Application;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();


    }
}
