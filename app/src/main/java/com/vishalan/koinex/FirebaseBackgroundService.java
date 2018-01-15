package com.vishalan.koinex;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseBackgroundService extends Service {

	final static String FB_BACKGROUND_SERVICE = "FirebaseBackgroundService";
	DatabaseReference db = FirebaseDatabase.getInstance().getReference();;
	FirebaseHelper helper = new FirebaseHelper(db);
	public static final String[] thresholdStrings = new String[]{"BCH/INR_ThresholdValue","BTC/INR_ThresholdValue","XRP/INR_ThresholdValue","ETH/INR_ThresholdValue","LTC/INR_ThresholdValue"};
	public static final String[] notificationEnabledStrings = new String[]{"BCH/INR_NotificationEnabled","BTC/INR_NotificationEnabled","XRP/INR_NotificationEnabled","ETH/INR_NotificationEnabled","LTC/INR_NotificationEnabled"};
	private HashMap<String, Double> thresholdMap = new HashMap<>();
	private HashMap<String, Boolean> notificationEnabledMap = new HashMap<>();
	public static final double threshold = 0.5;
	private NotificationUtils notificationUtils;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		for(int i=0; i<thresholdStrings.length; i++)
		{
			thresholdMap.put(thresholdStrings[i],intent.getDoubleExtra(thresholdStrings[i],0.0));
			notificationEnabledMap.put(notificationEnabledStrings[i],intent.getBooleanExtra(notificationEnabledStrings[i],true));
		}
		db.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//				fetchData(dataSnapshot);
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
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onDestroy() {
		db.getDatabase().goOffline();
		super.onDestroy();
	}

	private void fetchData(DataSnapshot dataSnapshot)
	{
		for (DataSnapshot ds : dataSnapshot.getChildren()) {
			Rates rates = ds.getValue(Rates.class);
			//rates.add(spacecraft);
			for(int i=0;i<thresholdStrings.length;i++)
			{
				if(!notificationEnabledMap.get(notificationEnabledStrings[i])) continue;
				double upperValue = thresholdMap.get(thresholdStrings[i]) + threshold;
				double lowervalue = thresholdMap.get(thresholdStrings[i]) - threshold;
				double currentValue = returnOrderedValue(rates, i);
				if(currentValue>= lowervalue && currentValue <= upperValue)
				{
					postNotif(i,thresholdMap.get(thresholdStrings[i]),currentValue);
				}

			}
		}
	}
	private double returnOrderedValue(Rates rates, int i)
	{
		switch (i)
		{
			case 0: return rates.getBchINR();
			case 1: return rates.getBtcINR();
			case 2: return rates.getXrpINR();
			case 3: return rates.getEthINR();
			case 4: return rates.getLtcINR();
			default: return -1;
		}
	}
	private String returnOrderedNames(int i)
	{
		switch (i)
		{
			case 0: return "Bitcoin Cash";
			case 1: return "Bitcoin";
			case 2: return "Ripple";
			case 3: return "Etherium";
			case 4: return "Litecoin";
			default: return "Unbounded";
		}
	}
	private int returnOrderedImages(int i)
	{
		switch (i)
		{
			case 0: return R.mipmap.bitcoincash;
			case 1: return R.mipmap.bitcoin;
			case 2: return R.mipmap.ripple;
			case 3: return R.mipmap.etherium;
			case 4: return R.mipmap.litecoin;
			default: return -1;
		}
	}
	private void postNotif(int position, Double aDouble, double currentValue) {
		String name = returnOrderedNames(position);
		int drawableId =  returnOrderedImages(position);
		if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
			// app is in foreground, broadcast the push message
			Intent pushNotification = new Intent("pushNotification");
			pushNotification.putExtra("message", name+" is "+currentValue+", really close to your target: "+aDouble);
			LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

			// play notification sound
			NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
			notificationUtils.playNotificationSound();
		} else {
			// app is in background, show the notification in notification tray
			Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
			resultIntent.putExtra("message", name+" is "+currentValue+", really close to your target: "+aDouble);

			// check for image attachment
			notificationUtils = new NotificationUtils(getApplicationContext());
			showNotificationMessage(position,getApplicationContext(), name, name+" is "+currentValue+", really close to your target: "+aDouble, ""+System.currentTimeMillis(), resultIntent);
			notificationUtils.playNotificationSound();
		}


	}
	private void showNotificationMessage(int position,Context context, String title, String message, String timeStamp, Intent intent) {
		notificationUtils = new NotificationUtils(context);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		notificationUtils.showNotificationMessage(position,title, message, timeStamp, intent);
	}

	@Override
	public boolean stopService(Intent name) {
		stopSelf();
		return super.stopService(name);
	}
}