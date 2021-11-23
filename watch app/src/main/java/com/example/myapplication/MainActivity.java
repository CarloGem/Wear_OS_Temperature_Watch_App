package com.example.myapplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class MainActivity extends Activity implements DataClient.OnDataChangedListener, SensorEventListener {

    private Button updateButton;
    private Button reportButton;
    private TextView temperatureTextView;
    private EditText nameInput;
    private EditText surnameInput;
    private EditText cityInput;
    private HashMap<User, HashMap<String,String>> reports;
    private SensorManager sensorManager;
    private Sensor mTemperatureSensor;
    private String sensorValue="DEFAULT";
    private String name;
    private String surname;
    private String city;
    private String WEARABLE_DATA_PATH = "/wearable_data";
    private String tmpTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameInput = (EditText) findViewById(R.id.editTextName);
        surnameInput = (EditText) findViewById(R.id.editTextSurname);
        cityInput = (EditText) findViewById(R.id.editTextCity);

        //TAKE THE INSTANCE OF THE TEMPERATURE SENSOR AND THEN ASSOCIATE IT WITH THE HANDLER
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorManager.registerListener(this, mTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);

        reports = ((SharedResources) this.getApplication()).getReports();
        updateButton = (Button) findViewById(R.id.button);
        updateButton.setOnClickListener(updateListener);
        reportButton = (Button) findViewById(R.id.button2);
        reportButton.setOnClickListener(reportListener);
    }

    public static boolean isInt(String s) {
        try
        {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }

    private View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("UPDATE");
            //CHECK OF INPUT STRINGS
            name = nameInput.getText().toString();
            System.out.println("Name: "+name);
            if (name == null || name.equals("")) {
                System.err.println("Name not provided!");
                return;
            }
            surname = surnameInput.getText().toString();
            System.out.println("Surname: "+ surname);
            if (surname == null || surname.equals("")) {
                System.err.println("Surname not provided!");
                return;
            }
            city = cityInput.getText().toString();
            System.out.println("City: "+ city);
            if (city == null || city.equals("")) {
                System.err.println("City not provided!");
                return;
            }


            System.out.println("Sensor value: " + sensorValue);
            if (! sensorValue.equals("DEFAULT")) {

                LocalDateTime dateTime = LocalDateTime.now();
                String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
                System.out.println("Current timestamp: " + timestamp);

                // Create a DataMap object and send it to the data layer
                DataMap dataMap = new DataMap();
                dataMap.putString("NAME", name);
                dataMap.putString("SURNAME", surname);
                dataMap.putString("CITY", city);
                dataMap.putString("TIMESTAMP", timestamp);
                dataMap.putString("TEMPERATURE", sensorValue);

                //NEW THREAD
                new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap, getApplicationContext()).start();

                Toast.makeText(getApplicationContext(), "TEMPERATURE FOR " + city + " SENT: "+ tmpTemperature+"°", Toast.LENGTH_LONG).show();

            }
        }
    };

    private View.OnClickListener reportListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //collegamento alla pagina di report
            onPause();
            DataMap dataMap = new DataMap();
            dataMap.putString("USERNAME", "REQUEST");
            new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap, getApplicationContext()).start();

            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
        }
    };

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Wearable.getDataClient(this).addListener(this);
    }

    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        Wearable.getDataClient(this).removeListener(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
            DataMap dataMap;
            User user;
            HashMap<String, String> temp;
            for (DataEvent event : dataEventBuffer) {
                // Check the data type
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    // Check the data path
                    String path = event.getDataItem().getUri().getPath();
                    if (path.equals(WEARABLE_DATA_PATH)) {
                        dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                        Log.v("myTag", "DataMap received on watch: " + dataMap);
                        user = new User(dataMap.getString("NAME"),
                                dataMap.getString("SURNAME"),
                                dataMap.getString("CITY"));
                        temp = new HashMap<>();
                        // -- SAVING USER DATA IN THE MAP
                        for (String key : dataMap.keySet()) {
                            if (!key.equals("NAME") &&  !key.equals("SURNAME") && !key.equals("CITY")) {
                                //TIMESTAMP CHECK
                                temp.put(key, dataMap.getString(key));
                            }
                        }
                        reports.put(user, temp);
                    }
                }
            }
            ((SharedResources) this.getApplication()).setReports(reports);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        System.out.println("On sensor changed");
        if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            tmpTemperature = Float.toString(sensorEvent.values[0]);
            System.out.println("Temperature value has changed");
            String msg = "" + tmpTemperature;
            Log.d("SENSOR", "Sensor changed value: "+msg);
            this.sensorValue = msg;
        }
        Log.d("SENSOR", "Unknown sensor type");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d("SENSOR", "onAccuracyChanged - accuracy: "+i);
    }
}