package com.dia.intent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import com.facebook.react.bridge.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class IntentModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    final ReactApplicationContext reactContext;
    final SparseArray<Promise> promises = new SparseArray<>();

    public IntentModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "Intent";
    }

    @ReactMethod
    public void startActivityForResult(String packageNames, String FilePath, Promise promise) {

        List<Intent> targetIntents = new ArrayList<Intent>();
        PackageManager manager = this.reactContext.getPackageManager();

        try {
            JSONArray jsonApps = new JSONArray(packageNames);
            for (int i=0;i<jsonApps.length();i++)
            {
                JSONObject jsonObj = jsonApps.getJSONObject(i);
                String packageName = jsonObj.getString("packageName");
                Log.d("Log123",packageName);
                Intent targetIntent = manager.getLaunchIntentForPackage(packageName);
                if(targetIntent!=null) {
                    targetIntents.add(targetIntent);
                }


            }

            if(targetIntents.size()==0) {
              promise.resolve(-1);
              return;
            }
            Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), "Yazdır");
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[] {}));
            this.reactContext.startActivity(chooserIntent);
            promise.resolve(1);

        }catch (Exception e){
        promise.resolve(-1);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        WritableNativeMap obj = new WritableNativeMap();

        obj.putInt("requestCode", requestCode);
        obj.putInt("resultCode", resultCode);
        obj.putString("data", data != null ? data.getDataString() : null);

        promises.get(requestCode).resolve(obj);
        promises.remove(requestCode);
    }
}
