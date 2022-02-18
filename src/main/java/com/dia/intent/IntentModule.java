package com.dia.intent;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.facebook.react.bridge.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class IntentModule extends ReactContextBaseJavaModule{

    final ReactApplicationContext _reactContext;
    final SparseArray<Promise> promises = new SparseArray<>();
    private static final int IMAGE_PICKER_REQUEST = 467081;
    private static final int PRINTER_PICKER_REQUEST = 10000;
    private static final int OPEN_FILE_DIALOG = 20000;
    private static final int OPEN_EXCEL_DIALOG = 30000;
    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String E_PICKER_CANCELLED = "E_PICKER_CANCELLED";
    private static final String E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER";
    private static final String E_NO_IMAGE_DATA_FOUND = "E_NO_IMAGE_DATA_FOUND";

    private Promise mPickerPromise;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        private @NonNull String getRealPathFromURI(@NonNull final Uri uri) {
            return pathFinder.getRealPathFromURI(_reactContext, uri);
        }

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent _intent) {
           if (requestCode == IMAGE_PICKER_REQUEST) {
                if (mPickerPromise != null) {

                    if (resultCode == Activity.RESULT_CANCELED) {
                        mPickerPromise.resolve("Image picker was cancelled");
                    } else if (resultCode == Activity.RESULT_OK) {
                        Uri uri = _intent.getData();

                        if (uri == null) {
                            mPickerPromise.resolve("No image data found");
                        } else {
                            String realPath = getRealPathFromURI(uri);
                            mPickerPromise.resolve(realPath);
                        }
                    }
                    mPickerPromise = null;
                }
            }else if(requestCode == PRINTER_PICKER_REQUEST){
               if (mPickerPromise != null) {

                   if (resultCode == Activity.RESULT_CANCELED) {
                       mPickerPromise.resolve("printer picker was cancelled");
                   } else if (resultCode == Activity.RESULT_OK) {
                      mPickerPromise.resolve("TAMAMLANDI");
                   }

                   mPickerPromise = null;
               }
           }else if(requestCode == OPEN_FILE_DIALOG){
               if (mPickerPromise != null) {

                   if (resultCode == Activity.RESULT_CANCELED) {
                       mPickerPromise.resolve("false");
                   } else if (resultCode == Activity.RESULT_OK) {
                       Uri selectedfile = _intent.getData();
                       if (selectedfile == null) {
                           mPickerPromise.resolve("false");
                       } else {
                           String realPath = getRealPathFromURI(selectedfile);
                           mPickerPromise.resolve(realPath);
                       }
                   }

                   mPickerPromise = null;
               }
           }else if(requestCode == OPEN_EXCEL_DIALOG){
               if (mPickerPromise != null) {

                   if (resultCode == Activity.RESULT_CANCELED) {

                       Log.d("excel11","canceled"+_intent);
                       mPickerPromise.resolve(-1);

                   } else if (resultCode == Activity.RESULT_OK) {

                       Log.d("excel11","ok"+_intent);
                       mPickerPromise.resolve(1);

                   }

                   mPickerPromise = null;
               }
           }


        }
    };


    public IntentModule(ReactApplicationContext reactContext) {
        super(reactContext);

        _reactContext = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "Intent";
    }

    @ReactMethod
    public void startActivityForResult(String packageNames, String FilePath, Promise promise) {
        mPickerPromise = promise;
        Activity currentActivity = getCurrentActivity();
        File _file = new File(Environment.getExternalStorageDirectory()+"/Android/data/"+getReactApplicationContext().getPackageName()+"/files/temp1pdf.pdf");
        Uri uriPath = Uri.fromFile(_file);

        if(Build.VERSION.SDK_INT >= 24){
            uriPath = FileProvider.getUriForFile(getReactApplicationContext(), "tr.com.dia.mobile.android.erp", _file);
        }

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uriPath, "application/pdf");

        try {
            currentActivity.startActivityForResult(intent,PRINTER_PICKER_REQUEST);
        } catch (Exception e) {
            Log.d("log 6",e.toString());
            mPickerPromise.resolve("error");
        }
    }

    @ReactMethod
    public void openFileDialog(Promise promise) {
        Log.d("choose File","0");
        Activity currentActivity = getCurrentActivity();
        mPickerPromise = promise;
        try {
            Intent intent = new Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT);
            currentActivity.startActivityForResult(Intent.createChooser(intent, "Select a file"), OPEN_FILE_DIALOG);

        } catch (Exception e) {
            Log.d("choose File","1"+e.toString());
            mPickerPromise.reject("Dosya Seçilemiyor", e);
            mPickerPromise = null;
        }
    }

    @ReactMethod
    public void triggerExcelApplications(String _path,Promise promise) {
        mPickerPromise = promise;
        Activity currentActivity = getCurrentActivity();
        Uri uriPath = Uri.fromFile(new File(_path));
        File _file = new File(URI.create(uriPath.toString()).getPath());
        if (_file.exists()) {
            Log.d("excel11","****true"+uriPath);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uriPath, "application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.d("excel11","1"+_path);
        Log.d("excel11","1"+uriPath);
        File file = new File(_path);
        if(file.exists())
            Log.d("excel11","1true"+_path);
            try {
                currentActivity.startActivityForResult(intent,OPEN_EXCEL_DIALOG);
            } catch (Exception e) {
                Log.d("excel11",e.toString());
                mPickerPromise.resolve("error");
            }
    }

    @ReactMethod
    public void pickImage(Promise promise) {
    Log.d("pickImage","0");
        Activity currentActivity = getCurrentActivity();
        mPickerPromise = promise;

     try {
            final Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType("image/*");
            final Intent chooserIntent = Intent.createChooser(galleryIntent, "Pick an image");
            currentActivity.startActivityForResult(chooserIntent, IMAGE_PICKER_REQUEST);
        } catch (Exception e) {
           Log.d("pickImage","1"+e.toString());
            mPickerPromise.reject("ERROR123123", e);
            mPickerPromise = null;
        }

        // promise.resolve(1);
    }

}
