package org.billthefarmer.tuner;

import android.os.Handler;
import android.os.Looper;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;

public class SampleBackgroundThreadPermissionListener extends SamplePermissionListener {

  private Handler handler = new Handler(Looper.getMainLooper());

  public SampleBackgroundThreadPermissionListener(SplashScreen activity) {
    super(activity);
  }

  @Override public void onPermissionGranted(final PermissionGrantedResponse response) {
    handler.post(new Runnable() {
      @Override public void run() {
        SampleBackgroundThreadPermissionListener.super.onPermissionGranted(response);
      }
    });
  }

  @Override public void onPermissionDenied(final PermissionDeniedResponse response) {
    handler.post(new Runnable() {
      @Override public void run() {
        SampleBackgroundThreadPermissionListener.super.onPermissionDenied(response);
      }
    });
  }

  @Override public void onPermissionRationaleShouldBeShown(final PermissionRequest permission,
      final PermissionToken token) {
    handler.post(new Runnable() {
      @Override public void run() {
        SampleBackgroundThreadPermissionListener.super.onPermissionRationaleShouldBeShown(
            permission, token);
      }
    });
  }
}