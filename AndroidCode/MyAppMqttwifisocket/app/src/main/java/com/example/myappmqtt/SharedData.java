package com.example.myappmqtt;

public class SharedData {
    private static SharedData instance;
    public String ec;
    public String ph;
    public String temp;
    public String water;
    public String dry;
    public String text;

    private SharedData() {}

    public static synchronized SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }
}
