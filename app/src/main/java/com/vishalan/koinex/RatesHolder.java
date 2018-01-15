package com.vishalan.koinex;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Created by Vishalan on 14-01-2018.
 */

public class RatesHolder extends RecyclerView.ViewHolder{
    private static final String TAG = RatesHolder.class.getSimpleName();
    public TextView conversion;
    public TextView rate;
    public ImageView conversionImage;
    public RatesHolder(View itemView) {
        super(itemView);
        conversion = (TextView)itemView.findViewById(R.id.conversion);
        rate = (TextView)itemView.findViewById(R.id.rate);
        conversionImage = (ImageView)itemView.findViewById(R.id.conversionImage);
    }
}
