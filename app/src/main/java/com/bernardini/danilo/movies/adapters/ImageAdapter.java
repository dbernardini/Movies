package com.bernardini.danilo.movies.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.entries.Entry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private final static String URL_IMAGE_API = "http://image.tmdb.org/t/p/";
    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";
    private Context context;
    private ArrayList<Entry> entries;


    public ImageAdapter(Context context, ArrayList<Entry> entries){
        this.context = context;
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Entry entry = (Entry) ((GridView) parent).getItemAtPosition(position);
        String path = entry.getImagePath();

        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(context);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, context.getResources().getDisplayMetrics());
            int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112, context.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else
            imageView = (ImageView) convertView;

        Picasso.with(context)
                .load(path)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.no_image)
                .into(imageView);

        return imageView;

    }
}
