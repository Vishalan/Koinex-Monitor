package com.vishalan.koinex;


public class Rates {
    public double btcINR;
    public double xrpINR;
    public double ethINR;
    public double ltcINR;
    public double bchINR;
    public Rates() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Rates(double btcINR, double xrpINR, double ethINR, double ltcINR, double bchINR) {
        super();
        this.btcINR = btcINR;
        this.xrpINR = xrpINR;
        this.ethINR = ethINR;
        this.ltcINR = ltcINR;
        this.bchINR = bchINR;
    }
    public double getBtcINR() {
        return btcINR;
    }
    public void setBtcINR(double btcINR) {
        this.btcINR = btcINR;
    }
    public double getXrpINR() {
        return xrpINR;
    }
    public void setXrpINR(double xrpINR) {
        this.xrpINR = xrpINR;
    }
    public double getEthINR() {
        return ethINR;
    }
    public void setEthINR(double ethINR) {
        this.ethINR = ethINR;
    }
    public double getLtcINR() {
        return ltcINR;
    }
    public void setLtcINR(double ltcINR) {
        this.ltcINR = ltcINR;
    }
    public double getBchINR() {
        return bchINR;
    }
    public void setBchINR(double bchINR) {
        this.bchINR = bchINR;
    }

}

