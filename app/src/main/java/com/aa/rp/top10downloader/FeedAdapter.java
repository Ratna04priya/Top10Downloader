package com.aa.rp.top10downloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    public FeedAdapter(Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;

    }

    @Override
    public int getCount() {
        return applications.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(layoutResource, parent,false);

        //Adding view because we are inflating the view , relating to layout resource and coming for list record.
        // We r actually looking in the constraint layout for different records


        TextView tvName = (TextView)  view.findViewById(R.id.tvName);
        TextView tvArtist = (TextView)  view.findViewById(R.id.tvArtist);
        TextView tvSummary = (TextView)  view.findViewById(R.id.tvSummary);

        FeedEntry currentApp = applications.get(position);

        tvName.setText(currentApp.getName());
        tvArtist.setText(currentApp.getArtist());
        tvSummary.setText(currentApp.getSummary());

        return view;
    }
}
