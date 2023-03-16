package com.kiramei.kikacomic.request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class WebsiteChooser {

    private final String url;
    private String href;
    private String pages;
    private String title;
    private Document doc;

    public final static String[] collector = {
            "http://m.531mk.com",
            "http://m.mxsan.com",
            "http://m.teemmm.com",
            "http://m.hlfssh.com",
            "http://m.moyunso.com",
            "http://ddd-smart.net"
    };
    public static final String[] sites_short = {
            "531mk",
            "mxsan",
            "teemm",
            "hlfss",
            "moyun",
            "smart"
    };
    public final static String[][] selector = {
            {".fanye1", "#nr234img", ".ptitle strong", ".txt", ".txt", ".pic img"},
            {".pagexej", "#imgString", ".ptitle strong", ".txt", ".txt", ".pic img"},
            {".content_page", ".content_cont", ".content_title h2", "span a", "span a", "ul li a img"},
            {".page11list", ".pic", ".wz-title", "ul span a", "ul span a", "a img"},
            {".page11list", ".pic", ".wz-title", "ul span a", "ul span a", "a img"},
            {"", "", "", ".package-list-text h3", ".package-list a", ".list-thumbnail img"}
    };

    public WebsiteChooser(String url) {
        this.url = url;
        if (url.startsWith("http://"))
            try {
                doc = Jsoup.parse(new URL(url).openStream(), "GBK", url);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        this.set();
    }

    private void set() {
        for (int t = 0; t < collector.length; t++) {
            if (url.contains(collector[t])) {
                if (t == 5) {
                    try {
                        Document doc_temp = Jsoup.parse(new URL(doc.select(".pagination a").attr("href")).openStream(), "utf-8", url);
                        pages = doc_temp.select("#comic-area img").size() + "";
                        href = doc.select(".show-relative img").attr("src");
                        title = doc_temp.select(".list-pickup-header").text();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    break;
                }
                int m = t == 3 || t == 4 ? 1 : 0;
                pages = doc.select(selector[t][0]).select("li").get(m).select("a").text();
                href = doc.select(selector[t][1]).select("img").attr("src");
                title = doc.select(selector[t][2]).text();
                break;
            }
        }
    }

    public static String baseURL(String url) {
        for (String s : collector) {
            if (url.contains(s)) {
                return s;
            }
        }
        return "";
    }

    public String getUrl() {
        return url;
    }

    public String getHref() {
        return href;
    }

    public int getPageNums() {
        return Integer.parseInt(pages.contains("页") ? pages.substring(1, pages.length() - 2) : pages);
    }

    public String getTitle() {
        return title;
    }
}