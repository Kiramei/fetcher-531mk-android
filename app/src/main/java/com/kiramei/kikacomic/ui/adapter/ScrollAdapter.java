package com.kiramei.kikacomic.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kiramei.kikacomic.R;

public class ScrollAdapter extends BaseAdapter {

    public Context context;
    public String[] href;

    public ScrollAdapter(Context context, String[] href) {
        this.context = context;
        this.href = href;
    }

    @Override
    public int getCount() {
        return href.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.scroll_item, null);
        } else {
            view = convertView;
        }
        ImageView im = view.findViewById(R.id.imgss);
        Glide.with(context)
                .load(href[position])
                .apply(new RequestOptions().fitCenter())
                .thumbnail(Glide.with(context).load(R.drawable.load))
                .into(im);

        return view;
    }
}
