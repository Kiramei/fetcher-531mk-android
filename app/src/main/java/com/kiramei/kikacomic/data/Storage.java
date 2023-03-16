package com.kiramei.kikacomic.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Storage {

    private static String unit = "";
    private static String storage = "";

    public Storage(){
        JSONArray arr=new JSONArray();

    }

    public String fetchUnit() {
        return unit;
    }

    public String fetchStorage() {
        return storage;
    }

    class Unit {

        @JSONField(name = "WEBPAGE")
        public String webPage;
        @JSONField(name = "TITLE")
        public String title;
        @JSONField(name = "IMGKEYSTRING")
        public String imgKeyString;

        public Context context;

        public ImgData imgKey;

        public Unit(String webPage, String title, String imgUrl, Context context) {
            this.title = title;
            this.webPage = webPage;

            this.context = context;

        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setWebPage(String webPage) {
            this.webPage = webPage;
        }


        public String getTitle() {
            return title;
        }

        public String getWebPage() {
            return webPage;
        }

        public String toJson() {
            return JSONObject.toJSONString(getClass());
        }

        public void apply() {

        }
    }

    static class ImgData {
        @JSONField(name="INDEX")
        int index;
        @JSONField(name = "IMAGEKEY")
        public String imgKey;
        @JSONField(name = "URL")
        public String url;

        public SharedPreferences storage;

        public ImgData(String imgUrl, Context context,int index) {
            url = imgUrl;
            this.index=index;
            Glide.with(context).load(imgUrl).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    drawable2Bitmap(resource).compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] img = baos.toByteArray();
                    MessageDigest ms = null;
                    try {
                        ms = MessageDigest.getInstance("MD5");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    assert ms != null;
                    byte[] after = ms.digest(img);

                    StringBuilder hexValue = new StringBuilder();
                    for (byte b : after) {

                        int val = ((int) b) & 0xff;
                        if (val < 16)
                            hexValue.append("0");
                        hexValue.append(Integer.toHexString(val));
                    }
                    String hexMD5 = hexValue.toString();
                    imgKey = hexMD5;
                    File pic = new File(Environment.getDataDirectory().getAbsolutePath() + "/kiramei/" + hexMD5);
                    try {
                        if (!pic.exists()) System.out.println(pic.createNewFile());
                        FileOutputStream fos = new FileOutputStream(pic);
                        fos.write(img);
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }).preload();
        }


        Bitmap drawable2Bitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof NinePatchDrawable) {
                Bitmap bitmap = Bitmap
                        .createBitmap(
                                drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(),
                                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                        : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                drawable.draw(canvas);
                return bitmap;
            } else {
                return null;
            }
        }

        public void add(String md5) {
            imgKey += "," + md5;
            storage.edit().putString("ImgKey", imgKey).apply();
        }

        public boolean contains(String test) {
            return imgKey.contains(test);
        }

        public File getDownloadedImage(String md5) {
            return new File(Environment.getDataDirectory().getAbsolutePath() + "/kiramei/" + imgKey);
        }
    }

}
