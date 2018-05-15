package com.bemetson.cloudberryhslstop;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class BusStopLayout extends LinearLayout {

    Activity context;
    String stopname;
    String estimate;
    boolean isPressed;
    ImageView imageView;
    Button button;
    int darkerGrey = getResources().getColor(R.color.darkerGrey);
    int white = getResources().getColor(R.color.white);
    boolean isClickable = true;
    SharedPreferences sharedPreferences_bool, sharedPreferences_stopname;
    SharedPreferences.Editor editor_bool, editor_stopname;
    TextView estimateTextView1, estimateTextView2, estimateTextView3;
    LinearLayout estimateLayout;


    public BusStopLayout(final Activity context, final String stopname, final String estimate) {
        super(context);

        this.context = context;
        this.stopname = stopname;
        this.isPressed = false;
        this.estimate = estimate;

        sharedPreferences_bool = context.getSharedPreferences("selectedBusStopBoolean", Context.MODE_PRIVATE);
        editor_bool = sharedPreferences_bool.edit();
        //Log.w("Sharedpref value: ", String.valueOf(isBusStopSelected));
        sharedPreferences_stopname = context.getSharedPreferences("getStopNameString", Context.MODE_PRIVATE);
        editor_stopname = sharedPreferences_stopname.edit();

        final ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        LinearLayout.LayoutParams imageviewParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        imageviewParams.gravity = Gravity.CENTER;
        this.imageView = imageView;
        this.addView(imageView, imageviewParams);

        final Button button = new Button(context);
        this.button = button;
        button.setText(stopname);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(0, DPConverter(32), 3.0f);
        buttonParams.gravity = Gravity.CENTER_VERTICAL;
        buttonParams.setMargins(DPConverter(8), 0, DPConverter(16), 0);
        button.setTextSize(12.0f);
        button.setBackgroundResource(R.drawable.button_layout);
        button.setTextColor(darkerGrey);
        button.setPadding(0, 12, 0, 12); // It may be bad practice to put direct values here
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPressed && isClickable) {
                    boolean anotherBusStopSelected = sharedPreferences_bool.getBoolean("busSelected", true);
                    if (!anotherBusStopSelected) {
                        pressButton();
                    }
                } else {
                    if (isClickable) {
                        resetSelection();
                        estimateLayout.removeAllViews();
                        estimateLayout.setVisibility(INVISIBLE);
                    }
                }
            }
        });
        this.addView(button, buttonParams);

        LinearLayout.LayoutParams classParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        classParams.setMargins(0, 24, 0, 0);
        this.setLayoutParams(classParams);

    }

    private int DPConverter(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int value = (int) (dp * scale + 0.5f);
        return value;
    }

    // Reset the button selection
    public void resetSelection() {
        isPressed = false;
        this.imageView.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        this.button.setBackgroundResource(R.drawable.button_layout);
        this.button.setTextColor(this.darkerGrey);
        editor_bool.putBoolean("busSelected", false);
        editor_bool.commit();
        editor_stopname.putString("busStop", "none");
        editor_stopname.commit();
    }


    public void busHasPassed() {
        this.isClickable = false; // Set the buttons to be not clickable after bus has passed the stop
        this.imageView.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
        this.button.setBackgroundResource(R.drawable.button_disabled);
        this.button.setTextColor(this.darkerGrey);
    }

    public void debug_reset() {
        this.isClickable = true;
        resetSelection();
    }

    public void pressButton() {
        imageView.setImageResource(R.drawable.ic_access_alarms_black_24dp);
        isPressed = true;
        button.setBackgroundResource(R.drawable.button_selected_layout);
        button.setTextColor(white);

        editor_bool.putBoolean("busSelected", true);
        editor_bool.commit();
        editor_stopname.putString("busStop", stopname);
        editor_stopname.commit();

        int white = getResources().getColor(R.color.white);

        estimateLayout = ((SelectionActivity) context).findViewById(R.id.navigation_estimate_of_arrival);
        estimateTextView1 = new TextView(context);
        estimateTextView1.setText("Estimated Arrival: ");
        estimateTextView1.setGravity(Gravity.CENTER);
        estimateTextView1.setTextColor(white);
        estimateTextView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        estimateTextView2 = new TextView(context);
        estimateTextView2.setText(estimate);
        estimateTextView2.setGravity(Gravity.CENTER);
        estimateTextView2.setId(R.id.estimate_time);
        estimateTextView2.setTypeface(null, Typeface.BOLD);
        estimateTextView2.setTextColor(white);
        estimateTextView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        estimateTextView3 = new TextView(context);
        estimateTextView3.setText(" min");
        estimateTextView3.setGravity(Gravity.CENTER);
        estimateTextView3.setTextColor(white);
        estimateTextView3.setTypeface(null, Typeface.BOLD);
        estimateTextView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        //LinearLayout.LayoutParams estimateParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        estimateLayout.addView(estimateTextView1);
        estimateLayout.addView(estimateTextView2);
        estimateLayout.addView(estimateTextView3);
        estimateLayout.setVisibility(VISIBLE);

    }

    public String getStopname() {
        return this.stopname;
    }
}
