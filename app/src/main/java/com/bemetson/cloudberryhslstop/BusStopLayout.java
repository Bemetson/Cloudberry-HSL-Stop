package com.bemetson.cloudberryhslstop;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BusStopLayout extends LinearLayout {

    Activity context;
    String stopname;
    Boolean isPressed;
    ImageView imageView;
    Button button;
    int darkGrey = getResources().getColor(R.color.darkGrey);
    int white = getResources().getColor(R.color.white);


    public BusStopLayout(Activity context, String stopname) {
        super(context);

        this.context = context;
        this.stopname = stopname;
        this.isPressed = false;

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
        button.setTextColor(darkGrey);
        button.setPadding(0, 12, 0, 12); // It may be bad practice to put direct values here
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPressed) {
                    imageView.setImageResource(R.drawable.ic_access_alarms_black_24dp);
                    isPressed = true;
                    button.setBackgroundResource(R.drawable.button_selected_layout);
                    button.setTextColor(white);
                } else {
                    resetSelection();
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

    public void resetSelection() {
        this.imageView.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        isPressed = false;
        this.button.setBackgroundResource(R.drawable.button_layout);
        this.button.setTextColor(this.darkGrey);
    }
}
