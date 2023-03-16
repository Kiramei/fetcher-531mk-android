package com.kiramei.kikacomic.request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class InformationGetter {
    private final ArrayList<String> href = new ArrayList<>();
    private final ArrayList<String> img = new ArrayList<>();
    private final ArrayList<String> title = new ArrayList<>();

    public InformationGetter search(String content) {
        for (int i=1;i<3;i++)
        this.add(WebsiteChooser.collector[i] + "/plus/search.php?kwtype=0&q=" + encode(content));
        return this;
    }

    private static String encode(String url) {
        StringBuilder sb = new StringBuilder();
        try {
            for (byte b : url.getBytes("gbk")) {
                String p = Integer.toHexString(b);
                sb.append("%").append(p.substring(p.length() - 2).toUpperCase());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public InformationGetter add(String url) {
        Document doc;
        try {
//            doc = Jsoup.parse(new URL(url).openStream(), "GBK", url);
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return this;
        }
        for (int t = 0; t < WebsiteChooser.collector.length; t++) {
            if (url.contains(WebsiteChooser.collector[t])) {

                List<String> m = new ArrayList<>();
                List<String> n = new ArrayList<>();
                List<String> o = new ArrayList<>();

                System.out.println(t);

                for (Element x : doc.select(WebsiteChooser.selector[t][5])) {
                    m.add(x.attr("src"));
                }
                for (Element x : doc.select(WebsiteChooser.selector[t][4])) {
                    n.add(x.attr("href"));
                }
                for (Element x : doc.select(WebsiteChooser.selector[t][3])) {
                    o.add(x.text());
                }

                for (int i = 0; i < m.size(); i++) {
                    String m0 = m.get(i);
                    if (m0.startsWith("http")) {
                        try {
                            Jsoup.connect(m0).ignoreContentType(true).get();
                            img.add(m0);
                        } catch (IOException ignored) {
                            continue;
                        }
                        href.add(WebsiteChooser.baseURL(url) + n.get(i));
                        title.add(o.get(i));
                    }
                }
            }
        }
        return this;
    }


    public String[] getHref() {
        String[] arr = new String[href.size()];
        for (int i = 0; i < href.size(); i++)
            arr[i] = href.get(i);
        return arr;
    }

    public String[] getImg() {
        String[] arr = new String[img.size()];
        for (int i = 0; i < img.size(); i++)
            arr[i] = img.get(i);
        return arr;
    }

    public String[] getTitle() {
        String[] arr = new String[title.size()];
        for (int i = 0; i < title.size(); i++)
            arr[i] = title.get(i);
        return arr;
    }

    public static String realUrlGetter(String before) {
        if (before.contains("_"))
            return before.substring(0, before.indexOf("_"));
        else
            return before.substring(0, before.indexOf(".html"));
    }

    public static InformationGetter push() {
        return new InformationGetter();
    }

}
