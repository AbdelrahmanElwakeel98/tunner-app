package org.billthefarmer.tuner;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

public class SplashScreen extends AppCompatActivity {
    private PermissionListener cameraPermissionListener;
    private PermissionRequestErrorListener errorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       /* PermissionListener snackbarPermissionListener =
                SnackbarOnDeniedPermissionListener.Builder
                        .with(getCurrentFocus(), "You need to give record permission to run this app.")
                        .withOpenSettingsButton("Settings")
                        .withCallback(new Snackbar.Callback() {
                            @Override
                            public void onShown(Snackbar snackbar) {

                                // Event handler for when the given Snackbar is visible
                            }

                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                // Event handler for when the given Snackbar has been dismissed

                            }
                        }).build();*/
        cameraPermissionListener = new SampleBackgroundThreadPermissionListener(this);

        errorListener = new SampleErrorListener();
        new Thread(new Runnable() {
            @Override public void run() {
                Dexter.withActivity(SplashScreen.this)
                        .withPermission(Manifest.permission.RECORD_AUDIO)
                        .withListener(cameraPermissionListener)
                        .withErrorListener(errorListener)
                        .onSameThread()
                        .check();
            }
        }).start();
        /*
        .withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                       *//* PermissionListener dialogPermissionListener =
                                DialogOnDeniedPermissionListener.Builder
                                        .withContext(SplashScreen.this)
                                        .withTitle("Record permission")
                                        .withMessage("You need to give record permission to run this app.")
                                        .withButtonText(android.R.string.ok)
                                        .withIcon(R.drawable.ic_launcher)
                                        .build();*//*



                //  Toast.makeText(SplashScreen.this, "You need to give record permission to run this app.", Toast.LENGTH_LONG).show();
                //finish();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
            }
        })*/
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(this).setTitle("Permissions Alert!")
                .setMessage("In Order to use this application, please give access to audio.")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

    public void showPermissionGranted(String permission) {
        startActivity(new Intent(SplashScreen.this, MainActivity.class));
        finish();
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
        Toast.makeText(SplashScreen.this, "You need to give record permission to run this app.", Toast.LENGTH_LONG).show();
        finish();
    }
}
