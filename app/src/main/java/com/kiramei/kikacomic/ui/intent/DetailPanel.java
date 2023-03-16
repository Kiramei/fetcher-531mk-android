package com.kiramei.kikacomic.ui.intent;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kiramei.kikacomic.R;
import com.kiramei.kikacomic.data.Service;
import com.kiramei.kikacomic.request.WebsiteChooser;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import java.text.MessageFormat;

public class DetailPanel extends Activity {

    AppCompatImageView image;
    AppCompatButton read, back;
    AppCompatTextView title, page_num;

    FloatingActionButton star;

    @Deprecated
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        image = findViewById(R.id.image_d);
        read = findViewById(R.id.read);
        star = findViewById(R.id.star);
        back = findViewById(R.id.back1);
        title = findViewById(R.id.title_s);
        page_num = findViewById(R.id.page_num);
        final Intent i = getIntent();
        new Thread(() -> {
            WebsiteChooser c = new WebsiteChooser(i.getStringExtra("origin"));
            set(c);
        }).start();

        back.setOnClickListener(v -> DetailPanel.this.finish());
    }

    private void set(WebsiteChooser websiteChooser) {
        final String url = websiteChooser.getUrl();
        final String title = websiteChooser.getTitle();
        final String cover = websiteChooser.getHref();
        int max = websiteChooser.getPageNums();
        image.post(() -> {
            loadCoverImg(cover);
            this.title.setText(title);
            this.title.setMovementMethod(ScrollingMovementMethod.getInstance());
            if (Service.histories.contains(url)) {
                page_num.setText(MessageFormat.format("{0}{1}{2}\n{3}", getString(R.string.zzz), max, getString(R.string.page), getString(R.string.readed)));
                read.setText(R.string.conti);
            } else {
                page_num.setText(MessageFormat.format("{0}{1}{2}\n{3}", getString(R.string.zzz), max, getString(R.string.page), getString(R.string.to_read)));
                read.setText(R.string.start);
            }
            if (Service.stars.contains(url)) {
                star.setImageResource(R.drawable.star);
            } else
                star.setImageResource(R.drawable.star_);

            star.setOnClickListener(v -> new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    if (url.equals("")) return;
                    WebsiteChooser c = new WebsiteChooser(url);
                    if (c.getHref().equals("")) return;
                    if (Service.saveStar(c.getUrl() + "," + c.getTitle() + "," + c.getHref() + "~`~", getApplicationContext())) {
                        star.post(() -> star.setImageResource(R.drawable.star));
                    } else {
                        star.post(() -> star.setImageResource(R.drawable.star_));
                    }
                    Looper.loop();
                }
            }.start());
            read.setOnClickListener(v -> new Thread() {
                @Override
                public void run() {
                    Intent i1 = new Intent();
                    i1.putExtra("url", url);
                    i1.putExtra("isScroll", url.contains("smart"));

                    i1.setClass(getApplicationContext(), ScrollLoader.class);
                    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(i1);
                    page_num.setText(MessageFormat.format("{0}{1}{2}\n{3}", getString(R.string.zzz), max, getString(R.string.page), getString(R.string.readed)));
                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            if (url.equals("")) return;
                            WebsiteChooser c = new WebsiteChooser(url);
                            if (c.getHref().equals("")) return;
                            Service.saveHistories(c.getUrl() + "," + c.getTitle() + "," + c.getHref() + "~`~", getApplicationContext());
                            Looper.loop();
                        }
                    }.start();
                }
            }.start());
        });

    }

    public void loadCoverImg(String url) {
        Glide.with(this).load(url)
                .thumbnail(Glide.with(this).load(R.drawable.load))
                .apply(new RequestOptions().fitCenter())
                .transition(withCrossFade())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(image);
    }
}
