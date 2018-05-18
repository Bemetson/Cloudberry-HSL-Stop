package com.bemetson.cloudberryhslstop;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomStopSearchAdapter extends BaseAdapter implements Filterable {

    private ArrayList<BusStopData> original = new ArrayList<>();
    private ArrayList<BusStopData> filtered = new ArrayList<>();
    private Activity context;

    public CustomStopSearchAdapter(Activity context, ArrayList<BusStopData> busStops) {

        this.original = busStops;
        this.filtered = busStops;
        this.context = context;

    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public Object getItem(int position) {
        return filtered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View row = layoutInflater.inflate(R.layout.custom_stop_search_listview, null, true);

        TextView stopName = row.findViewById(R.id.stop_search_listview_textview);
        ImageView icon = row.findViewById(R.id.stop_search_listview_imageview);
        TextView iconDescription = row.findViewById(R.id.stop_search_listview_imageview_description);

        BusStopData busStop = filtered.get(position);
        stopName.setText(busStop.getStopName());

        if (busStop.getHasIcon()) {
            icon.setImageResource(R.drawable.ic_clear_black_24dp);
            iconDescription.setText("Remove icon");
        } else {
            icon.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
            iconDescription.setText("Add icon");
        }

        return row;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                ArrayList<BusStopData> filteredStops = new ArrayList<>();

                for (BusStopData busStop : original) {
                    String stopName = busStop.getStopName();
                    if (stopName.startsWith(charSequence.toString())) {
                        filteredStops.add(busStop);
                    }
                }

                results.count = filteredStops.size();
                results.values = filteredStops;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtered = (ArrayList<BusStopData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
