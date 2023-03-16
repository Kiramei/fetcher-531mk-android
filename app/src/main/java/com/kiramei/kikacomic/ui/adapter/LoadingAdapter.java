package com.kiramei.kikacomic.ui.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.kiramei.kikacomic.R;
import com.kiramei.kikacomic.ui.intent.DetailPanel;

public class LoadingAdapter extends BaseAdapter {
    private final Context context;
    private final String[] imageUrls;
    private final String[] href;
    private final String[] title;


    public LoadingAdapter(Context context, String[] imageUrls, String[] href, String[] title) {
        super();
        this.context = context;
        this.imageUrls = imageUrls;
        this.href = href;
        this.title = title;
    }

    @Override
    public int getCount() {
        return imageUrls.length;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comic_item, null);
        } else {
            view = convertView;
        }
        final ImageView image = view.findViewById(R.id.image);
        TextView textView = view.findViewById(R.id.st);

        image.setOnClickListener(v -> {
            Intent i = new Intent();
            i.putExtra("origin", href[position]);
            i.setClass(context, DetailPanel.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });
        textView.setText(title[position]);
        Glide.with(context)
                .load(imageUrls[position])
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object o, Target<Drawable> target, boolean b) {
                        image.setOnClickListener(null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        return false;
                    }
                })
                .thumbnail(Glide.with(context).load(R.drawable.load))
                .apply(new RequestOptions().fitCenter())
                .transition(withCrossFade())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(image);
        return view;
    }
}
