package com.kiramei.kikacomic;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.kiramei.kikacomic.data.Config;
import com.kiramei.kikacomic.data.Service;
import com.kiramei.kikacomic.ui.adapter.ViewPagerAdapter;
import com.xuexiang.xupdate.aria.AriaDownloader;
import com.xuexiang.xupdate.easy.EasyUpdate;

public class MainActivity extends AppCompatActivity{

    BubbleNavigationLinearView bnv;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Deprecated
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newui);
        init();
        ViewPager2 content=findViewById(R.id.content);
        content.setAdapter(new ViewPagerAdapter(this));
        bnv=findViewById(R.id.top_nav);
        bnv.setNavigationChangeListener((view, position) -> content.setCurrentItem(position));
        content.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bnv.setCurrentActiveItem(position);
            }
        });
    }
    private void init() {
        EasyUpdate.create(this, Config.UPDATE_JSON_URL)
                .updateHttpService(AriaDownloader.getUpdateHttpService(this)).update();
        Service.init(getApplicationContext());
        if (!Service.isEighteen(getApplicationContext())) {
            WindowManager.LayoutParams l = getWindow().getAttributes();
            l.alpha = 0;
            getWindow().setAttributes(l);
            new AlertDialog.Builder(this, R.style.dialog).setTitle(R.string.confirm)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        Service.setEighteen(getApplicationContext());
                        dialog.dismiss();
                        l.alpha = 1;
                        getWindow().setAttributes(l);
                    }).setNegativeButton(R.string.no,
                    (dialog, which) -> {
                        android.os.Process.killProcess(android.os.Process.myUid());
                        System.exit(0);
                    }).create().show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}