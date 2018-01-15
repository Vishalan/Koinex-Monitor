package com.vishalan.koinex;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
/**
 * Created by Oclemy on 6/21/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 * 1.SAVE DATA TO FIREBASE
 * 2. RETRIEVE
 * 3.RETURN AN ARRAYLIST
 */
public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved;
    ArrayList<Conversion> conversions=new ArrayList<>();
    /*
 PASS DATABASE REFRENCE
  */
    public  FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }
    //WRITE IF NOT NULL
    public Boolean save(Conversion rates)
    {
        if(rates==null)
        {
            saved=false;
        }else
        {
            try
            {
                db.child("rates").child("conversions").push().setValue(rates);
                saved=true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }
    //IMPLEMENT FETCH DATA AND FILL ARRAYLIST
    private void fetchData(DataSnapshot dataSnapshot)
    {
        ArrayList<Conversion> history = new ArrayList<>();
        history.addAll(conversions);
        conversions.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Rates rates = ds.getValue(Rates.class);
            //rates.add(spacecraft);
            boolean[] increase = checkIncrease(history,rates);
            conversions.add(new Conversion("BCH/INR", "₹ " + rates.getBchINR(), R.mipmap.bitcoincash,increase[0]));
            conversions.add(new Conversion("BTC/INR", "₹ " + rates.getBtcINR(), R.mipmap.bitcoin,increase[1]));
            conversions.add(new Conversion("XRP/INR", "₹ " + rates.getXrpINR(), R.mipmap.ripple,increase[2]));
            conversions.add(new Conversion("ETH/INR", "₹ " + rates.getEthINR(), R.mipmap.etherium,increase[3]));
            conversions.add(new Conversion("LTC/INR", "₹ " + rates.getLtcINR(), R.mipmap.litecoin,increase[4]));
        }
        if(!CustomAdapter.editMode)
        {
            HomeActivity.adapter.notifyDataSetChanged();
        }
    }

    private boolean[] checkIncrease(ArrayList<Conversion> history, Rates rates) {
        boolean[] arr = null;
        if(history.size()==0)
        {
            arr = new boolean[5];
            return arr;
        }
        else
        {
            arr = new boolean[history.size()];
        }
        arr[0] = Double.parseDouble(history.get(0).getRates().replace("₹ ","")) > rates.getBchINR();
        arr[1] = Double.parseDouble(history.get(1).getRates().replace("₹ ","")) > rates.getBtcINR();
        arr[2] = Double.parseDouble(history.get(2).getRates().replace("₹ ","")) > rates.getXrpINR();
        arr[3] = Double.parseDouble(history.get(3).getRates().replace("₹ ","")) > rates.getEthINR();
        arr[4] = Double.parseDouble(history.get(4).getRates().replace("₹ ","")) > rates.getLtcINR();
        return arr;
    }

    //RETRIEVE
    public ArrayList<Conversion> retrieve()
    {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return conversions;
    }
}