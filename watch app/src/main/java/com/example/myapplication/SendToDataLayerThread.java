package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.ExecutionException;

public class SendToDataLayerThread extends Thread {
    private String path;
    private DataMap dataMap;
    private Context context;

    public SendToDataLayerThread(String path, DataMap dataMap, Context context) {
        this.path=path;
        this.dataMap=dataMap;
        this.context=context;
    }

    public void run() {
        // Construct a DataRequest and send over the data layer
        PutDataMapRequest putDMR = PutDataMapRequest.create(path);
        putDMR.getDataMap().putAll(dataMap);
        PutDataRequest request = putDMR.asPutDataRequest();
        Task<DataItem> dataItemTask = Wearable.getDataClient(context).putDataItem(request);
        dataItemTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d("TAG", "Sending data was successful: " + dataItem);
                    }
                });
    }
}
