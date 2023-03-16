package com.kiramei.kikacomic.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.kiramei.kikacomic.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Service {
    public final static int STAR_MODE = 0;
    public final static int HISTORIES_MODE = 1;
    public final static int MXSAN_MIN = 14600;
    public static String stars;
    public static String histories;
    public static int mxsan_max = 19305;

    public static void init(Context context) {
        stars = context.getSharedPreferences("starInfo", 0).getString("starInfo", "");
        histories = context.getSharedPreferences("historyInfo", 0).getString("name", "");
        new Thread(()->{
            try {
                JSONObject jb=new JSONObject(
                        Jsoup.connect(Config.CONFIG_JSON_URL)
                        .ignoreContentType(true)
                        .get()
                        .text()
                );
                mxsan_max =jb.getInt("m1");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static boolean isEighteen(Context context){
        return context.getSharedPreferences("appInfo", 0).getBoolean("eighteen", false);
    }

    public static void setEighteen(Context context){
        context.getSharedPreferences("appInfo",0).edit().putBoolean("eighteen",true).apply();
    }

    public static boolean saveStar(String content, Context context) {
        if (stars.contains(content)) {
            stars = stars.replace(content, "");
            save(STAR_MODE, context);
            Toast.makeText(context, R.string.stars, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            stars = content + stars;
            save(STAR_MODE, context);
            Toast.makeText(context, R.string.unstars, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public static void saveHistories(String content, Context context) {
        if (histories.contains(content)) {
            histories = histories.replace(content, "");
        }
        histories = content + histories;
        save(HISTORIES_MODE, context);
    }

    private static void save(int mode, Context context) {
        if (mode == STAR_MODE) {
            context.getSharedPreferences("starInfo", 0)
                    .edit()
                    .putString("starInfo", stars)
                    .apply();
        } else {
            context.getSharedPreferences("historyInfo", 0)
                    .edit()
                    .putString("name", histories)
                    .apply();
        }
    }


    @SuppressLint("NewApi")
    public static String getStarEncryption() {
        return Base64.getEncoder().encodeToString(stars.getBytes(StandardCharsets.UTF_8));
    }

    @SuppressLint("NewApi")
    public static void saveWholeStar(String content, Context context) {
        try {
            String after = new String(Base64.getDecoder().decode(content));
            if (after.contains("~`~") && after.contains("http://")) {
                stars = after;
                save(STAR_MODE, context);
                Toast.makeText(context, R.string.rs, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.ru, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.ru, Toast.LENGTH_SHORT).show();
        }
    }
}
