package com.vishalan.koinex;


import org.parceler.Parcel;

/**
 * Created by Matteo on 24/08/2015.
 */
@Parcel
public class Conversion {

    String conversion;
    String rates;
    int imageDrawable;
    boolean increase;
    double notificationThreshold;
    boolean notificationEnabled;
    public Conversion() {
    }

    public Conversion(String conversion, String rates, int imageDrawable, boolean increase) {
        this.conversion = conversion;
        this.rates = rates;
        this.imageDrawable = imageDrawable;
        this.increase = increase;
    }

    public String getConversion() {
        return conversion;
    }

    public void setConversion(String conversion) {
        this.conversion = conversion;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(int imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public boolean isIncrease() {
        return increase;
    }

    public void setIncrease(boolean change) {
        this.increase = change;
    }

    public double getNotificationThreshold() {
        return notificationThreshold;
    }

    public void setNotificationThreshold(double notificationThreshold) {
        this.notificationThreshold = notificationThreshold;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }
}