package com.example.sahilbansal.barcode;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkpremissions())
            {
                Toast.makeText(this, "permissiongranted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                 requestpermission();
            }
        }
    }
    private boolean checkpremissions()
    {
         return (ContextCompat.checkSelfPermission(MainActivity.this , Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }
    private void requestpermission()
    {
        ActivityCompat.requestPermissions(this,new String[]{CAMERA} , REQUEST_CAMERA);
    }
    public void onRequestPermissionResult(int Requestcode , String permissions[] , int grantresults[])
    {
         switch (Requestcode)
         {
             case REQUEST_CAMERA:
                 if(grantresults.length>0)
                 {
                      boolean cameraAccepted = grantresults[0]==PackageManager.PERMISSION_GRANTED;
                      if(cameraAccepted)
                      {
                          Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                      }
                      else
                      {
                          Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                          if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
                          {
                               if(shouldShowRequestPermissionRationale(CAMERA))
                               {
                                   displayalert("You need to give permission to both",
                                   new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialogInterface, int i) {
                                           if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
                                           {
                                                requestPermissions(new String[]{CAMERA} , REQUEST_CAMERA);
                                           }

                                       }
                                   });
                                   return;
                               }
                          }
                      }
                 }
                 break;
         }
    }
   @Override
    public void onResume()
    {
         super.onResume();
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
        {
             if(checkpremissions())
             {
                 if(scannerView==null)
                 {
                      scannerView = new ZXingScannerView(this);
                      setContentView(scannerView);
                 }
                 scannerView.setResultHandler(this);
                 scannerView.startCamera();
             }
        }

    }
    @Override
    public void onDestroy()
    {
         super.onDestroy();
         scannerView.stopCamera();
    }
    public void displayalert(String message , DialogInterface.OnClickListener listener)
    {
         new AlertDialog.Builder(MainActivity.this)
                 .setMessage(message)
                 .setPositiveButton("OK" , listener)
                 .setNegativeButton("Cancel"  , null)
                 .create()
                 .show();
    }


    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                scannerView.resumeCameraPreview(MainActivity.this);
            }
        });
 builder.setMessage(scanResult);
 AlertDialog alert = builder.create();
 alert.show();


    }
}
