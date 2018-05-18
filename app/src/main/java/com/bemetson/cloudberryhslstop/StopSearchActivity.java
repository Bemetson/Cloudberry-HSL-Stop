package com.bemetson.cloudberryhslstop;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StopSearchActivity extends AppCompatActivity {

    ArrayList<BusStopData> busStopdatas;
    CustomStopSearchAdapter adapter;
    String busStop = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_search);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        busStopdatas = (ArrayList<BusStopData>) intent.getSerializableExtra("busStops");
        for (BusStopData data : busStopdatas) {

        }

        search();
    }

    private void search() {
        Collections.sort(busStopdatas, new Comparator<BusStopData>() {
            @Override
            public int compare(BusStopData left, BusStopData right) {
                return left.getStopName().compareToIgnoreCase(right.getStopName());
            }
        });

        adapter = new CustomStopSearchAdapter(this, busStopdatas);

        ListView listView = findViewById(R.id.stop_search_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Log.w("LISTVIEW", "YOU JUST CLICKED AN ITEM");
                TextView description = view.findViewById(R.id.stop_search_listview_imageview_description);
                TextView stopName = view.findViewById(R.id.stop_search_listview_textview);
                busStop = stopName.getText().toString();
                if (description.getText().toString().equals("Add icon")) {
                    Intent intent = new Intent(StopSearchActivity.this, AddIconDialogActivity.class);
                    //intent.putExtra("stopName", stopName.getText().toString());
                    startActivityForResult(intent, 1000);
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("result", -1);
                    intent.putExtra("stopname", busStop);
                    setResult(RESULT_OK, intent);
                    finish();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1000) {
            int icon_value = data.getIntExtra("icon_value", 1500);
            //Log.w("RETURNING ICON", Integer.toString(icon_value));
            //Log.w("STOPNAME", busStop);

            BusStopData stopData = new BusStopData(busStop, true, false, icon_value);

            Intent intent = getIntent();
            intent.putExtra("result", 1);
            intent.putExtra("stopname", busStop);
            intent.putExtra("iconvalue", icon_value);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}

/*
Collections.sort(busStopdatas, new Comparator<BusStopData>() {
            @Override
            public int compare(BusStopLayout left, BusStopLayout right) {
                return left.getStopname().compareToIgnoreCase(right.getStopname());
            }
        });

        adapter = new CustomStopSearchAdapter(this, busStopdatas);

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
 */