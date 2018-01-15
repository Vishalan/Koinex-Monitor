package com.vishalan.koinex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements ForceUpdateChecker.OnUpdateNeededListener {
    final static String spNotificationEnabled = "_NotificationEnabled";
    final static String spThresholdValue = "_ThresholdValue";
    private static final String TAG = HomeActivity.class.getSimpleName();
    DatabaseReference db;
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    FirebaseHelper helper;
    static CustomAdapter adapter;
    private  ProgressDialog progress;
    ListView lv;
    Button edit,save;
    Context context;
    SharedPreferences sharedPref;
    final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lv = (ListView) findViewById(R.id.listViewRates);
        edit = (Button) findViewById(R.id.editButton);
        save = (Button) findViewById(R.id.saveButton);
        checkVersionAppVersionStatus();
        toggleButtons();
        initializeAds();
        intializeRemoteConfig();
        context = getApplicationContext();
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        saveIntoSharedPreferencesSingleton();
        progress = new ProgressDialog(this);
        progress.setMessage("Initializing Parameters");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelper(db);
        adapter = new CustomAdapter(this, helper.retrieve());
        lv.setAdapter(adapter);

        if(adapter.getCount()<=0) {
            new Thread(new Runnable() {
                public void run() {
                    while (adapter.getCount() == 0) {

                    }
                    progress.dismiss();
                    startServiceAtBoot();
                }
            }).start();
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                CustomAdapter.editMode = false;
                toggleButtons();
                saveSharedPreferences();
                adapter.notifyDataSetChanged();
                //db.getDatabase().goOffline();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                CustomAdapter.editMode = true;
                toggleButtons();
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void intializeRemoteConfig() {
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "1.0.0");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=com.vishalan.koinex");
        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activateFetched();
                            ForceUpdateChecker.with(getApplicationContext()).onUpdateNeeded(HomeActivity.this).check();
                        }
                    }
                });
    }

    private void checkVersionAppVersionStatus() {
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
    }

    private void initializeAds() {
        MobileAds.initialize(this,
                getResources().getString(R.string.homeactivity_ad_client));

        mAdView = (AdView) findViewById(R.id.adViewBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        loadInterstitialAds();

    }
    public void loadInterstitialAds()
    {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.homeactivity_ad_interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.show();
    }

    private void saveIntoSharedPreferencesSingleton()
    {
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        for(int i =0 ;i < SharedPreferencesSingleton.thresholdStrings.length ; i++)
        {
            double thresholdValue = Double.parseDouble(sharedPref.getString(SharedPreferencesSingleton.thresholdStrings[i].toString(), "0.0"));
            boolean notificationEnabledVaue = sharedPref.getBoolean(SharedPreferencesSingleton.notificationEnabledStrings[i].toString(), false);
            HashMap<String,Double> temp1 = SharedPreferencesSingleton.getInstance().getThresholdMap();
            temp1.put(SharedPreferencesSingleton.thresholdStrings[i],thresholdValue);
            SharedPreferencesSingleton.getInstance().setThresholdMap(temp1);
            HashMap<String,Boolean> temp2 = SharedPreferencesSingleton.getInstance().getNotificationEnabledMap();
            temp2.put(SharedPreferencesSingleton.notificationEnabledStrings[i],notificationEnabledVaue);
            SharedPreferencesSingleton.getInstance().setNotificationEnabledMap(temp2);
        }

    }
    private void saveSharedPreferences() {
        Intent mIntent = new Intent(HomeActivity.this, FirebaseBackgroundService.class);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        boolean checkOneorMoreTrue = false;
        for(int i =0 ;i < adapter.getCount() ; i++)
        {
            Conversion conversion = (Conversion)adapter.getItem(i);
            View listView = getViewByPosition(i,lv);
            ToggleButton tb = (ToggleButton) listView.findViewById(R.id.toggleButton);
            EditText et = (EditText) listView.findViewById(R.id.editTextThreshold);
            boolean notificationEnabledValue = tb.isChecked();
            if(notificationEnabledValue)
            {
                checkOneorMoreTrue = true;
            }
            Double thresholdValue = Double.parseDouble(et.getText().toString());
            editor.putBoolean(conversion.getConversion()+spNotificationEnabled,tb.isChecked() );
            editor.putString(conversion.getConversion()+spThresholdValue,et.getText().toString());
            HashMap<String,Double> temp1 = SharedPreferencesSingleton.getInstance().getThresholdMap();
            temp1.put(SharedPreferencesSingleton.thresholdStrings[i],thresholdValue);
            SharedPreferencesSingleton.getInstance().setThresholdMap(temp1);
            HashMap<String,Boolean> temp2 = SharedPreferencesSingleton.getInstance().getNotificationEnabledMap();
            temp2.put(SharedPreferencesSingleton.notificationEnabledStrings[i],notificationEnabledValue);
            SharedPreferencesSingleton.getInstance().setNotificationEnabledMap(temp2);
            mIntent.putExtra(SharedPreferencesSingleton.notificationEnabledStrings[i],notificationEnabledValue);
            mIntent.putExtra(SharedPreferencesSingleton.thresholdStrings[i],thresholdValue);
        }
        editor.commit();
        adapter.notifyDataSetChanged();
        if(checkOneorMoreTrue)
        {
            stopService(new Intent(HomeActivity.this, FirebaseBackgroundService.class));
            startService(mIntent);
        }
        else
        {
            stopService(new Intent(HomeActivity.this, FirebaseBackgroundService.class));
        }

    }
    private void startServiceAtBoot()
    {
        Intent mIntent = new Intent(HomeActivity.this, FirebaseBackgroundService.class);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean checkOneorMoreTrue = false;
        for(int i =0 ;i < SharedPreferencesSingleton.thresholdStrings.length ; i++)
        {
            double thresholdValue = Double.parseDouble(sharedPref.getString(SharedPreferencesSingleton.thresholdStrings[i].toString(), "0.0"));
            boolean notificationEnabledVaue = sharedPref.getBoolean(SharedPreferencesSingleton.notificationEnabledStrings[i].toString(), false);
            HashMap<String,Double> temp1 = SharedPreferencesSingleton.getInstance().getThresholdMap();
            temp1.put(SharedPreferencesSingleton.thresholdStrings[i],thresholdValue);
            SharedPreferencesSingleton.getInstance().setThresholdMap(temp1);
            HashMap<String,Boolean> temp2 = SharedPreferencesSingleton.getInstance().getNotificationEnabledMap();
            temp2.put(SharedPreferencesSingleton.notificationEnabledStrings[i],notificationEnabledVaue);
            SharedPreferencesSingleton.getInstance().setNotificationEnabledMap(temp2);
            mIntent.putExtra(SharedPreferencesSingleton.notificationEnabledStrings[i],notificationEnabledVaue);
            mIntent.putExtra(SharedPreferencesSingleton.thresholdStrings[i],thresholdValue);
        }
        if(checkOneorMoreTrue)
        {
            stopService(new Intent(HomeActivity.this, FirebaseBackgroundService.class));
            startService(mIntent);
        }
        else
        {
            stopService(new Intent(HomeActivity.this, FirebaseBackgroundService.class));
        }
    }
    private void toggleButtons() {
        save.setEnabled(CustomAdapter.editMode);
        edit.setEnabled(!CustomAdapter.editMode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setCancelable(false)
                .setMessage("Please, update app to new version to continue reposting.")
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore(updateUrl);
                            }
                        }).setNegativeButton("Exit",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }
                        }).create();
        dialog.show();
    }
    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
