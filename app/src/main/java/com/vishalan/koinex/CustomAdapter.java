package com.vishalan.koinex;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
/**
 * Created by Oclemy on 6/21/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 * 1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */
public class CustomAdapter extends BaseAdapter{
    Context c;
    SharedPreferences sharedPreferences;
    static boolean editMode = false;
    ArrayList<Conversion> conversions;
    public CustomAdapter(Context c, ArrayList<Conversion> conversions) {
        this.c = c;
        this.conversions = conversions;
    }
    @Override
    public int getCount() {
        return conversions.size();
    }
    @Override
    public Object getItem(int position) {
        return conversions.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView= LayoutInflater.from(c).inflate(R.layout.rates_row,parent,false);
        }
        TextView conversion= (TextView) convertView.findViewById(R.id.conversion);
        TextView rate= (TextView) convertView.findViewById(R.id.rate);
        ImageView conversionImage= (ImageView) convertView.findViewById(R.id.conversionImage);
        ToggleButton toggleButton= (ToggleButton) convertView.findViewById(R.id.toggleButton);
        EditText editText = (EditText) convertView.findViewById(R.id.editTextThreshold);


        final Conversion s= (Conversion) this.getItem(position);
        if(s.isIncrease())
        {
            conversion.setTextColor(Color.RED);
        }
        else
        {
            conversion.setTextColor(Color.GREEN);
        }
        conversion.setText(s.getConversion());
        rate.setText(s.getRates());
        conversionImage.setImageResource(s.getImageDrawable());
        toggleButton.setChecked(SharedPreferencesSingleton.getInstance().getNotificationEnabledMap().get(s.getConversion()+HomeActivity.spNotificationEnabled));
        editText.setText(SharedPreferencesSingleton.getInstance().getThresholdMap().get(s.getConversion()+HomeActivity.spThresholdValue).toString());
        toggleButton.setEnabled(editMode);
        editText.setEnabled(editMode);
        return convertView;
    }
}