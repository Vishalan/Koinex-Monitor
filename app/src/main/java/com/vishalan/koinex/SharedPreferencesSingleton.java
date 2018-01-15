package com.vishalan.koinex;

import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Vishalan on 14-01-2018.
 */

public class SharedPreferencesSingleton {

    private static volatile SharedPreferencesSingleton sSoleInstance;
    public static final String[] thresholdStrings = new String[]{"BCH/INR_ThresholdValue","BTC/INR_ThresholdValue","XRP/INR_ThresholdValue","ETH/INR_ThresholdValue","LTC/INR_ThresholdValue"};
    public static final String[] notificationEnabledStrings = new String[]{"BCH/INR_NotificationEnabled","BTC/INR_NotificationEnabled","XRP/INR_NotificationEnabled","ETH/INR_NotificationEnabled","LTC/INR_NotificationEnabled"};


    private HashMap<String, Double> thresholdMap = new HashMap<>();
    private HashMap<String, Boolean> notificationEnabledMap = new HashMap<>();

    //private constructor.
    private SharedPreferencesSingleton(){

        //Prevent form the reflection api.
        if (sSoleInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SharedPreferencesSingleton getInstance() {
        //Double check locking pattern
        if (sSoleInstance == null) { //Check for the first time

            synchronized (SharedPreferencesSingleton.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSoleInstance == null) sSoleInstance = new SharedPreferencesSingleton();
            }
        }

        return sSoleInstance;
    }

    public static SharedPreferencesSingleton getsSoleInstance() {
        return sSoleInstance;
    }

    public static void setsSoleInstance(SharedPreferencesSingleton sSoleInstance) {
        SharedPreferencesSingleton.sSoleInstance = sSoleInstance;
    }

    public HashMap<String, Double> getThresholdMap() {
        return thresholdMap;
    }

    public void setThresholdMap(HashMap<String, Double> thresholdMap) {
        this.thresholdMap = thresholdMap;
    }

    public HashMap<String, Boolean> getNotificationEnabledMap() {
        return notificationEnabledMap;
    }

    public void setNotificationEnabledMap(HashMap<String, Boolean> notificationEnabledMap) {
        this.notificationEnabledMap = notificationEnabledMap;
    }
}
