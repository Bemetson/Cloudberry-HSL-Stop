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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class SelectionActivity extends AppCompatActivity {

    private TextView busNumberTextView;
    private TextView busRouteTextView;
    private LinearLayout navigationScrollLinearLayout;
    private ImageButton switchViewButton;
    private boolean testTravel = false;  // Bolean value for testing bus movement. On true ends loop
    SharedPreferences sharedPreferences_bool, sharedPreferences_stopname, sharedPreferences_firstTimeLaunch;
    private Runnable runnable;
    private Handler handler = new Handler();
    private ArrayList<BusStopLayout> busStopLayouts = new ArrayList<>();
    private Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private Ringtone r;
    private boolean stopVibration = false;
    private String[] busStopsForDemo = new String[14];
    private ArrayList<BusStopData> stopData = new ArrayList<>();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_list_of_stops:
                    return true;
                case R.id.navigation_contacts:
                    startContacts();
                    return false;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

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
                startMaps();
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
        View blank = new View(this);
        LinearLayout.LayoutParams blank_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 70);
        navigationScrollLinearLayout.addView(blank, blank_params);

        sharedPreferences_bool = getSharedPreferences("selectedBusStopBoolean", MODE_PRIVATE);
        sharedPreferences_stopname = getSharedPreferences("getStopNameString", MODE_PRIVATE);
        // Use these three lines below if you get logic error preventing selecting stops
        //SharedPreferences.Editor editor = sharedPreferences_bool.edit();
        //editor.putBoolean("busSelected", false);
        //editor.commit();
        Boolean activateSelection = sharedPreferences_bool.getBoolean("busSelected", false);
        if (activateSelection) {
            String stopname = sharedPreferences_stopname.getString("busStop", "none");
            Log.w("STOPNAME", stopname);
            for (BusStopLayout stop : busStopLayouts) {
                if (stopname.equals(stop.getStopname())) {
                    stop.pressButton();
                    break;
                }
            }
        }

        // For first time launch, create a new file for storing individual stop icon
        sharedPreferences_firstTimeLaunch = getPreferences(MODE_PRIVATE);
        Boolean firstTimeLunch = sharedPreferences_firstTimeLaunch.getBoolean("launch", true);
        if (firstTimeLunch) {
            new File(this.getFilesDir(), "savedIcons");
            SharedPreferences.Editor editor = sharedPreferences_firstTimeLaunch.edit();
            editor.putBoolean("launch", false);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem search = menu.add("Customize stops");
        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(SelectionActivity.this, StopSearchActivity.class);
                intent.putExtra("busStops", busStopLayouts);
                startActivity(intent);
                return true;
            }
        });

        MenuItem language = menu.add("Language");
        language.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                return true;
            }
        });

        MenuItem bus = menu.add("DEMO - Start the bus");
        bus.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                testBusMovement(1);
                return true;
            }
        });

        MenuItem item = menu.add("DEMO - Set first four passed");
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                testBusMovement(0);
                return true;
            }
        });

        MenuItem reset = menu.add("DEMO - Reset views");
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
        ArrayList<BusStopLayout> copy = new ArrayList<>(busStopLayouts);
        if (type == 0) {
            for (int i = 0; i < 4; i++) {
                BusStopLayout busStop = copy.get(i);
                busStop.busHasPassed();
            }
        }
        if (type == 1) {
            for (int i = 0; i < 4; i++) {
                BusStopLayout busStop = copy.get(0);
                busStop.busHasPassed();
                copy.remove(0);
            }
            startBus(copy);
        }
        if (type == 9999) {
            for (BusStopLayout busStop : copy) {
                busStop.debug_reset();
            }
            testTravel = false;
            stopVibration = false;
            LinearLayout estimateLayout = findViewById(R.id.navigation_estimate_of_arrival);
            estimateLayout.removeAllViews();
            estimateLayout.setBackgroundResource(0);
        }
    }


    private void startBus(final ArrayList<BusStopLayout> list) {
        runnable = new Runnable() {
            ArrayList<BusStopLayout> copy = list;

            @Override
            public void run() {
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
                .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
        busStopsForDemo[4] = "Kontiontie:1";
        busStopsForDemo[5] = "Otsolahdentie:2";
        busStopsForDemo[6] = "Itäranta:3";
        busStopsForDemo[7] = "Tekniikantie:4";
        busStopsForDemo[8] = "Vuorimies:5";
        busStopsForDemo[9] = "Alvar Aallon Puisto:6";
        busStopsForDemo[10] = "Dipoli:7";
        busStopsForDemo[11] = "Otaniemensilta:8";
        busStopsForDemo[12] = "Lehtisaarentie:9";
        busStopsForDemo[13] = "Kuusisaarenkuja:10";
    }

    private void createStopData(String[] busStopsForDemo) {
        for (BusStopLayout stopLayout : busStopLayouts) {

            //SelectionActivity.this.stopData.add(busStopData);
        }
    }

    private void startContacts() {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    private void startMaps() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
