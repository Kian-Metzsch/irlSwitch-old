package com.nordicnerds.irlswitch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK;
import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT;

public class QRSyncActivity extends Activity
{
    SurfaceView cameraPreview;

    BarcodeDetector barcodeDetector;

    CameraSource cameraSource;

    final int RequestCameraPermissionID = 1001;

    FirebaseDatabase database;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case RequestCameraPermissionID:
                {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            return;
                        }
                        try
                        {
                            cameraSource.start(cameraPreview.getHolder());
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    public void sync_process(String pcname)
    {
        Intent intent_edit = new Intent(this, EditActivity.class);
        Bundle bundle_EditInfo = new Bundle();

        DatabaseReference myRef = database.getReference("FirebaseSync").child(pcname);
        myRef.setValue("Jw7H3kLo91f");

        myRef = database.getReference("Jw7H3kLo91f/AuthenticatedPCS").child(pcname);
        myRef.child("icon").setValue(2);
        myRef.child("name").setValue("New pc");

        bundle_EditInfo.putString("title", "New pc");
        bundle_EditInfo.putInt("icon", R.drawable.custom_icon_2);
        bundle_EditInfo.putString("key", pcname);
        bundle_EditInfo.putInt("current", 1);

        intent_edit.putExtras(bundle_EditInfo);
        startActivity(intent_edit);

        finish();
    }

    boolean qrSwitch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrsync);

        database = FirebaseDatabase.getInstance();

        cameraPreview = findViewById(R.id.cameraPreview);

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder)
            {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(QRSyncActivity.this, new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try
                {
                    cameraSource.start(cameraPreview.getHolder());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2){}

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder)
            {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>()
        {
            @Override
            public void release() {}

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections)
            {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0)
                {
                    if (qrSwitch)
                    {
                        Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(50);
                        System.out.println(qrcodes.valueAt(0).displayValue);
                        sync_process(qrcodes.valueAt(0).displayValue);

                        qrSwitch = false;
                    }
                }
            }
        });
    }
}
