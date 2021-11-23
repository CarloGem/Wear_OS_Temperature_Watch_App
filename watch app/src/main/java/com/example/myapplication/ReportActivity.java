package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ReportActivity extends Activity {

    private TextView reportTextView;
    private ImageButton backButton;
    private Button filterButton;
    private HashMap<User, HashMap<String, String>> reports;
    private EditText inputCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        reportTextView = (TextView) findViewById(R.id.textView2);
        reportTextView.setMovementMethod(new ScrollingMovementMethod());
        backButton = (ImageButton) findViewById(R.id.imageButton);
        backButton.setOnClickListener(backListener);
        filterButton = (Button) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(filterListener);
        inputCity = (EditText) findViewById(R.id.editTextCity);

        reports = ((SharedResources) this.getApplication()).getReports();
        printReport();
    }

    public void printReport() {
        String content = "";
        for (User u : reports.keySet()) {
            content += " City: [ "+ u.getCity()+" ]\n Reported by: [ " + u.getName() + " " + u.getSurname() + " ]\n ";
            for (String key : reports.get(u).keySet()) {
                content += key + " [ " + reports.get(u).get(key) + " ]\n ";
            }
            content+="\n";
        }
        System.out.println("Print:\n"+content);
        reportTextView.setText(content);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //collegamento alla pagina home
            Intent intent = new Intent(ReportActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };


    private View.OnClickListener filterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String content = "";
            String city = inputCity.getText().toString();

            if ((city.equals("") || city==null)) {
                Toast.makeText(getApplicationContext(),
                        "PLEASE ENTER A STRING TO FILTER BY CITY", Toast.LENGTH_SHORT).show();
                for (User u : reports.keySet()) {
                    content += " City: [ "+ u.getCity()+" ]\n Reported by: [ " + u.getName() + " " + u.getSurname() + " ]\n ";
                    for (String key : reports.get(u).keySet()) {
                        content += key + " [ " + reports.get(u).get(key) + " ]\n ";
                    }
                    content+="\n";
                }
                return;
            }

            for (User u : reports.keySet()) {
                    if (u.getCity().equals(city)) {
                        content += u.getName() + " " + u.getSurname() + "[ " + u.getCity() + " ]\n";
                        for (String timestamp : reports.get(u).keySet()) {
                            content += timestamp + "|" + reports.get(u).get(timestamp) + "\n";
                        }
                        content+="\n";
                    }

            }
            System.out.println("Print:\n"+content);
            reportTextView.setText(content);
        }
    };
}
