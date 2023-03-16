package com.kiramei.kikacomic.ui.intent;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kiramei.kikacomic.R;
import com.kiramei.kikacomic.data.Config;
import com.kiramei.kikacomic.request.InformationGetter;
import com.kiramei.kikacomic.ui.adapter.LoadingAdapter;

public class SiteExplore extends Activity {

    String site;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.siteunit);
        Intent i = getIntent();
        site = i.getStringExtra("site");
        TextView textView = findViewById(R.id.sname);
        GridView gridView = findViewById(R.id.siteunits);
        ProgressBar progressBar = findViewById(R.id.progress);
        textView.setText(site);
        new Thread(() -> {
            InformationGetter j = InformationGetter.push().add(site);
            String[] d = j.getTitle();
            for (int t = 0; t < d.length; t++) {
                String ss = d[t];
                if (ss == null) {
                    d[t] = "";
                    continue;
                }
                d[t] = ss.length() > Config.TITLE_LENGTH_MAX ?
                        ss.substring(0, Config.TITLE_LENGTH_MAX) : ss;
            }

            textView.post(() -> gridView.setAdapter(new LoadingAdapter(this, j.getImg(), j.getHref(), d)));
            AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
            fadeOutAnimation.setDuration(500);
            fadeOutAnimation.setFillAfter(true);
            progressBar.startAnimation(fadeOutAnimation);
        }).start();
    }
}
