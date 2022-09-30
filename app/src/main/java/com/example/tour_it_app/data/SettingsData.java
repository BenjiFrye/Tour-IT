package com.example.tour_it_app.data;

import java.util.HashMap;
import java.util.Map;

public class SettingsData {

    //This method contains default settings data.
    //Do not manipulate.
    public Map<String, Object> DefaultSystem() {

        Map<String, Object> map = new HashMap<>();

        map.put("System","Metric");

        return map;
    }

    public Map<String, Object> DefaultPreference() {

        Map<String, Object> map = new HashMap<>();

        map.put("Preference","Popular");

        return map;
    }



}
