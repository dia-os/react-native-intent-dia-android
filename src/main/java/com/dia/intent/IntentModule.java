package com.dia.intent;

import android.content.Intent;
import android.util.SparseArray;
import com.facebook.react.bridge.*;

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
    public void startActivityForResult(String action, int requestCode, Promise promise) {
    
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
     
    }
}
