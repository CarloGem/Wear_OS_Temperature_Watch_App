package com.example.myapplication;

import android.app.Application;

import java.util.HashMap;

public class SharedResources extends Application {
    private HashMap<User, HashMap<String,String>> reports = new HashMap<>();
    public HashMap<User, HashMap<String, String>> getReports() {
        return this.reports;
    }
     public void setReports(HashMap<User, HashMap<String, String>> reports) {
        this.reports=reports;
     }
}
