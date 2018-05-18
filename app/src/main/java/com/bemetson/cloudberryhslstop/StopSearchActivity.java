package com.bemetson.cloudberryhslstop;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StopSearchActivity extends AppCompatActivity {

    ArrayList<BusStopLayout> busStopLayouts;
    CustomStopSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_search);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        String[] busStops = (String[]) intent.getSerializableExtra("busStops");
        for (String stopname : busStops) {
            String[] stop = stopname.split(":");

        }

    }

    private void search() {
        Collections.sort(busStopLayouts, new Comparator<BusStopLayout>() {
            @Override
            public int compare(BusStopLayout left, BusStopLayout right) {
                return left.getStopname().compareToIgnoreCase(right.getStopname());
            }
        });

        adapter = new CustomStopSearchAdapter(this, busStopLayouts);

        ListView listView = findViewById(R.id.stop_search_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.w("LISTVIEW", "YOU JUST CLICKED AN ITEM");
            }
        });

        EditText editText = findViewById(R.id.stop_search_edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                StopSearchActivity.this.adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
