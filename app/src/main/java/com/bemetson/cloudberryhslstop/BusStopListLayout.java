package com.bemetson.cloudberryhslstop;

import android.app.Activity;
import android.widget.LinearLayout;

public class BusStopListLayout extends LinearLayout {

    Activity context;

    public BusStopListLayout(Activity context, String[] listOfBusStops) {
        super(context);
        this.context = context;


        for (String busStopName : listOfBusStops) {

        }

        LinearLayout.LayoutParams classParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        //classParams.setMargins(0, 24, 0, 0);
        this.setLayoutParams(classParams);
        this.setOrientation(VERTICAL);
    }

}
