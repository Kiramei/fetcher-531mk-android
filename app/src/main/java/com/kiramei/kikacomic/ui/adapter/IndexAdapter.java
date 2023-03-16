package com.kiramei.kikacomic.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kiramei.kikacomic.R;
import com.kiramei.kikacomic.ui.intent.SiteExplore;

public class IndexAdapter extends BaseAdapter {
    Context context;

    final String[] s = new String[]{"531MK", "MXSAN", "HLFSSH", "TEEMM"};
    final String[] site = new String[]{"http://m.531mk.com", "http://m.mxsan.com", "http://m.hlfssh.com", "http://m.teemmm.com"};
    final int[] s1 = new int[]{R.drawable.site1, R.drawable.site2, R.drawable.site3, R.drawable.site1};


    public IndexAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return s.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.sites_unit, null);
        } else {
            view = convertView;
        }
        final ImageView img = view.findViewById(R.id.site_back);
        final TextView txt = view.findViewById(R.id.site_name);
        txt.setText(s[position]);
        Glide.with(context).load(s1[position]).into(img);
        txt.setOnClickListener(view1 -> {
            Intent i = new Intent();
            i.putExtra("site", site[position]);
            i.setClass(context, SiteExplore.class);
            context.startActivity(i);
        });
        return view;
    }
}
