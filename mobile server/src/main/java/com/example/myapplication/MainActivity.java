package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements DataClient.OnDataChangedListener{

    private static final String WEARABLE_DATA_PATH = "/wearable_data";
    private HashMap<User, HashMap<String, String>> reports = new HashMap<>();
    private boolean found =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEvents) {
        String name, surname, city, timestamp, sensorValue;
        DataMap dataMap;

        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_CHANGED) {

                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.v("myTag", "DataMap received on phone: " + dataMap);
                    name = dataMap.getString("NAME");
                    if (name.equals("REQUEST")) {
                        //data request from client, output existing entries
                        dataMap = new DataMap();

                        for (Map.Entry<User, HashMap<String,String>> entry : reports.entrySet()) {
                            dataMap = new DataMap();
                            dataMap.putString("NAME", entry.getKey().getName());
                            dataMap.putString("SURNAME", entry.getKey().getSurname());
                            dataMap.putString("CITY", entry.getKey().getCity());
                            for (String timestmp : entry.getValue().keySet()) {
                                dataMap.putString(timestmp, entry.getValue().get(timestmp));
                            }

                            new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap, getApplicationContext()).start();

                        }
                    }
                    else { //inserting new entry
                        surname = dataMap.getString("SURNAME");
                        city = dataMap.getString("CITY");
                        timestamp = dataMap.getString("TIMESTAMP");
                        sensorValue = dataMap.getString("TEMPERATURE");
                        insert(name, surname, city, timestamp, sensorValue);
                    }
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    public void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    public void insert(String name, String surname, String city, String timestamp, String sensorValue) {
        for (User u : reports.keySet()) {
            if (u.getName().equals(name)) {
                reports.get(u).put(timestamp, sensorValue);
                found =true;
            }
        }
        if (!found) {
            HashMap<String, String> data = new HashMap<>();
            data.put(timestamp, sensorValue);
            User u = new User(name, surname, city);
            reports.put(u, data);
        }
    }
}