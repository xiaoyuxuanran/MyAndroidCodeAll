package com.example.myappmqtt;

public class SharedData {
    private static SharedData instance;
    public String year;
    public String month;
    public String day;
    public String hour;
    public String minute;
    public String weather;
    public String tempUp;
    public String tempDown;
    public String temperature;
    public String humidity;
    public String n1;
    public String n2;
    public String n3;

    private SharedData() {}

    public static synchronized SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }
}
