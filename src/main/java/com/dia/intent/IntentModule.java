package com.dia.intent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import com.facebook.react.bridge.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class IntentModule extends ReactContextBaseJavaModule {

    final ReactApplicationContext _reactContext;
    final SparseArray<Promise> promises = new SparseArray<>();
    private static final int IMAGE_PICKER_REQUEST = 467081;
    private static final int PRINTER_PICKER_REQUEST = 10000;
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
           }
           /* WritableNativeMap obj = new WritableNativeMap();

            obj.putInt("requestCode", requestCode);
            obj.putInt("resultCode", resultCode);
            obj.putString("data", _intent != null ? _intent.getDataString() : null);

            mPickerPromise.resolve(obj);
            //mPickerPromise.get(requestCode).resolve(obj);
           // promises.remove(requestCode);
           */
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
        Log.d("pickImage","------");
        if(true) {

            List<Intent> targetIntents = new ArrayList<Intent>();
            PackageManager manager = _reactContext.getPackageManager();
            Activity currentActivity = getCurrentActivity();
            mPickerPromise = promise;

            try {
                JSONArray jsonApps = new JSONArray(packageNames);
                for (int i = 0; i < jsonApps.length(); i++) {
                    JSONObject jsonObj = jsonApps.getJSONObject(i);
                    String packageName = jsonObj.getString("packageName");
                    Log.d("Log123", packageName);
                    Intent targetIntent = manager.getLaunchIntentForPackage(packageName);
                    if (targetIntent != null) {
                        targetIntents.add(targetIntent);
                    }
                }

                if (targetIntents.size() == 0) {
                    promise.resolve(-1);
                    return;
                }
                Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), "Yazdır");
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[]{}));
                //this.reactContext.startActivity(chooserIntent);
                currentActivity.startActivityForResult(chooserIntent, PRINTER_PICKER_REQUEST);
            } catch (Exception e) {
                //mPickerPromise.reject(E_FAILED_TO_SHOW_PICKER, e);
                //mPickerPromise = null;
                mPickerPromise.resolve(-1);
            }
        }else{
            Activity currentActivity = getCurrentActivity();
            mPickerPromise = promise;

            try {
                final Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Pick an image");
                currentActivity.startActivityForResult(chooserIntent, IMAGE_PICKER_REQUEST);
            } catch (Exception e) {
                Log.d("Log12333", e.toString());
                mPickerPromise.resolve("ERROR123123");
                mPickerPromise = null;
            }

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


   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        WritableNativeMap obj = new WritableNativeMap();

        obj.putInt("requestCode", requestCode);
        obj.putInt("resultCode", resultCode);
        obj.putString("data", data != null ? data.getDataString() : null);

        promises.get(requestCode).resolve(obj);
        promises.remove(requestCode);
    }*/
}
