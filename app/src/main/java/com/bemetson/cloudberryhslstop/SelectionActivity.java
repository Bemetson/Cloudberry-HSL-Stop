package com.bemetson.cloudberryhslstop;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectionActivity extends AppCompatActivity {

    private TextView busNumberTextView;
    private TextView busRouteTextView;
    private LinearLayout navigationScrollLinearLayout;
    private ImageButton switchViewButton;
    private boolean testTravel = false;  // Bolean value for testing bus movement. On true ends loop
    SharedPreferences sharedPreferences;
    private Runnable runnable;
    private Handler handler = new Handler();
    ArrayList<BusStopLayout> busStopLayouts = new ArrayList<>();
    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    Ringtone r;
    boolean stopVibration = false;
    String[] busStopsForDemo = new String[14];


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_list_of_stops:
                    return true;
                case R.id.navigation_contacts:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        sharedPreferences = getSharedPreferences("selectedBusStopBoolean", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("busSelected", false);
        editor.commit();

        populateDemoStops();

        r = RingtoneManager.getRingtone(getApplicationContext(), notification);

        // Placeholder text for actual fetched data
        busNumberTextView = findViewById(R.id.bus_number_textview);
        busNumberTextView.setText(getString(R.string.bus_195));

        busRouteTextView = findViewById(R.id.bus_route_textview);
        busRouteTextView.setText(getString(R.string.bus_195_text));

        switchViewButton = findViewById(R.id.switch_view_imagebutton);
        switchViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testBusMovement(0);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigationScrollLinearLayout = (LinearLayout) findViewById(R.id.navigation_scrollview_linearlayout);
        for (String demoStops : busStopsForDemo) {
            String stopName = demoStops.split(":")[0];
            String estimate = demoStops.split(":")[1];
            BusStopLayout stop = new BusStopLayout(this, stopName, estimate);
            navigationScrollLinearLayout.addView(stop);
            busStopLayouts.add(stop);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.add("Set first five passed");
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                testBusMovement(1);
                return true;
            }
        });

        MenuItem reset = menu.add("Reset views");
        reset.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                testBusMovement(9999);
                return true;
            }
        });
        return true;

    }

    // TODO: This method is an absolute balllake and has lots of technical debt. Will fix later
    private void testBusMovement(final int type) {
        runnable = new Runnable() {
            ArrayList<BusStopLayout> copy = new ArrayList<>(busStopLayouts);

            @Override
            public void run() {
                if (type == 0) {
                    if (copy.size() > 0) {
                        BusStopLayout nextStop = copy.get(0);
                        if (!nextStop.isPressed) {
                            nextStop.busHasPassed();
                            copy.remove(0);
                            try {
                                TextView estimateTextView2 = findViewById(R.id.estimate_time);
                                String time = String.valueOf(estimateTextView2.getText());
                                String newTime = String.valueOf(Integer.parseInt(time) - 1);
                                estimateTextView2.setText(newTime);
                                Log.w("time", newTime);
                            } catch (NullPointerException e) {
                                Log.e("No textview found", e.getLocalizedMessage());
                            }

                            Log.w("Testing moving bus", nextStop.stopname);
                        } else {
                            handler.removeCallbacks(runnable);
                            testTravel = true;
                            Log.w("Testing moving bus", "Bus has come to a stop");
                            alarm();
                            return;
                        }
                    } else {
                        handler.removeCallbacks(runnable);
                        testTravel = true;
                        Log.w("Testing moving bus", "Bus has come to a stop");
                    }
                    if (!testTravel) {
                        handler.postDelayed(runnable, 1000);
                    } else {
                        handler.removeCallbacks(runnable);
                    }
                }
                if (type == 1) {
                    for (int i = 0; i < 5; i++) {
                        BusStopLayout busStop = copy.get(i);
                        busStop.busHasPassed();
                    }
                }
                if (type == 9999) {
                    for (BusStopLayout busStop : copy) {
                        busStop.debug_reset();
                    }
                    testTravel = false;
                    stopVibration = false;
                    LinearLayout estimateLayout = findViewById(R.id.navigation_estimate_of_arrival);
                    estimateLayout.removeAllViews();
                }
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    private void alarm() {
        /* scheduleNotification(getNotification("Alarm"), 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, SelectionActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pendingIntent);
        */
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(300);
                if (!stopVibration) {
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);
        r.play();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Next Bus Stop");

        // set dialog message
        alertDialogBuilder
                .setMessage("Remember to exit when the bus stops at the next stop!")
                .setCancelable(false)
                .setPositiveButton("Okay!",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        r.stop();
                        stopVibration = true;
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("This is an alarm");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_access_alarms_black_24dp);
        return builder.build();
    }

    private void populateDemoStops() {
        busStopsForDemo[0] = "Latokartano:1";
        busStopsForDemo[1] = "Hakarinne:2";
        busStopsForDemo[2] = "Tuuliniitty:3";
        busStopsForDemo[3] = "Tapiola (M):4";
        busStopsForDemo[4] = "Kontiontie:5";
        busStopsForDemo[5] = "Otsolahdentie:6";
        busStopsForDemo[6] = "Itäranta:7";
        busStopsForDemo[7] = "Tekniikantie:8";
        busStopsForDemo[8] = "Vuorimies:9";
        busStopsForDemo[9] = "Alvar Aallon Puisto:10";
        busStopsForDemo[10] = "Dipoli:11";
        busStopsForDemo[11] = "Otaniemensilta:12";
        busStopsForDemo[12] = "Lehtisaarentie:13";
        busStopsForDemo[13] = "Kuusisaarenkuja:14";
    }

    public void showEstimateForArrival() {
        Log.w("Estimate", "Button was clicked");
    }
}
