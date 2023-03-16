package com.kiramei.kikacomic.ui.intent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.kiramei.kikacomic.R;
import com.kiramei.kikacomic.request.InformationGetter;
import com.kiramei.kikacomic.request.WebsiteChooser;
import com.kiramei.kikacomic.ui.adapter.ScrollAdapter;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;

public class ScrollLoader extends Activity {

    String url;
    boolean isScroll;
    String[] p;

    @Deprecated
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        final GridView scroller = findViewById(R.id.scroller);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final TextView textView = findViewById(R.id.loading);
        final ImageView load = findViewById(R.id.load);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        final Intent i = getIntent();
        url = i.getStringExtra("url");

        isScroll = i.getBooleanExtra("isScroll", false);

        if (isScroll) {
            new Thread(() -> {
                try {
                    load.setVisibility(View.VISIBLE);
                    runOnUiThread(() -> Glide.with(this).asGif().load(R.drawable.load).into(load));
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    List<String> a = Jsoup.connect(Jsoup.connect(url).get().select(".pagination a").attr("href")).get().select("#comic-area img").eachAttr("src");
                    a.toArray(p = new String[a.size()]);
                    load.setVisibility(View.INVISIBLE);
                    runOnUiThread(() -> scroller.setAdapter(new ScrollAdapter(getApplicationContext(), p)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            return;
        }

        new Thread(() -> {
            WebsiteChooser c = new WebsiteChooser(url);
            scroller.post(() -> {
                progressBar.setMax(c.getPageNums());
                p = new String[c.getPageNums()];
                url = InformationGetter.realUrlGetter(url);
                p[0] = url + ".html";
                for (int j = 1; j < c.getPageNums(); j++) {
                    p[j] = url + "_" + (j + 1) + ".html";
                }


                new Thread() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        for (int i = 0; i < p.length; i++) {
                            p[i] = new WebsiteChooser(p[i]).getHref();
                            progressBar.setProgress(i + 1);
                            textView.setText("正在加载中(" + (i + 1) + "/" + c.getPageNums() + ")");
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        runOnUiThread(() -> scroller.setAdapter(new ScrollAdapter(getApplicationContext(), p)));
                    }
                }.start();
            });
        }).start();

    }
}
