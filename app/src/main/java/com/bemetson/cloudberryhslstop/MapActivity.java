package com.bemetson.cloudberryhslstop;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

public class MapActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_list_of_stops:
                    kill();
                    return false;
                case R.id.navigation_contacts:
                    return true;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = this.getSharedPreferences("getStopNameString", Context.MODE_PRIVATE);
        String busStopName = sharedPreferences.getString("busStop", "none");
        ImageView view = findViewById(R.id.map_picture);

        switch (busStopName) {
            case "Kontiontie":
                view.setImageResource(R.drawable.kontiontie);
                break;
            case "Otsolahdentie":
                view.setImageResource(R.drawable.otsolahdentie);
                break;
            case "Itäranta":
                view.setImageResource(R.drawable.itaranta);
                break;
            case "Tekniikantie":
                view.setImageResource(R.drawable.tekniikantie);
                break;
            case "Vuorimies":
                view.setImageResource(R.drawable.vuorimies);
                break;
            case "Alvar Aallon Puisto":
                view.setImageResource(R.drawable.alvar_aallon_puisto);
                break;
            case "Dipoli":
                view.setImageResource(R.drawable.dipoli);
                break;
            case "Otaniemensilta":
                view.setImageResource(R.drawable.otaniemensilta);
                break;
            default:
                view.setImageResource(R.drawable.ei_valintaa);
                break;

        }
    }

    private void kill() {
        this.finish();
    }
}
