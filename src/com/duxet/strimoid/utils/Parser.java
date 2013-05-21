package com.duxet.strimoid.utils;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import android.util.Log;

import com.duxet.strimoid.models.*;

public class Parser {
    public static ArrayList<Content> getContents(String response) {
        ArrayList<Content> contents = new ArrayList<Content>();

        Document doc = Jsoup.parse(response);
        Elements elements = doc.getElementsByClass("content");

        for (Element el : elements) {
            String title = el.getElementsByClass("content_title").first().text().trim();
            String author = el.getElementsByClass("user_name").first().getElementsByTag("span").first().text().trim();
            String desc = el.getElementsByClass("content_info").text().trim();
            String url = el.getElementsByClass("content_title").first().attr("href").trim();

            String imageUrl = "";

            if (el.getElementsByClass("content_image").first() != null)
                imageUrl = el.getElementsByClass("content_image").first().getElementsByTag("img").first().attr("src").trim();

            int up = Integer.parseInt(el.getElementsByClass("like").first().getElementsByClass("content_vote_count").text());
            int down = Integer.parseInt(el.getElementsByClass("dislike").first().getElementsByClass("content_vote_count").text());

            Content content = new Content(title, author, desc, url, imageUrl, up, down);
            contents.add(content);
        }

        return contents;
    }

    public static ArrayList<Entry> getEntries(String response) {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        Document doc = Jsoup.parse(response);
        Elements elements = doc.getElementsByClass("entry");

        for (Element el : elements) {
            String author = el.getElementsByClass("entry_user").first().text().trim();
            String avatar = el.getElementsByClass("entry_image").first().getElementsByTag("img").first().attr("src").trim();
            String message = el.getElementsByClass("entry_text").first().text().trim();
            String time = el.getElementsByClass("entry_info").first().getElementsByAttribute("title").first().text().trim();
            String strim = el.getElementsByClass("entry_info").first().getElementsByTag("a").first().text().trim();

            Boolean isReply = el.hasClass("reply");
            Log.i("reply", isReply.toString());

            int up = Integer.parseInt(el.getElementsByClass("like").first().getElementsByClass("entry_vote_count").text());
            int down = Integer.parseInt(el.getElementsByClass("dislike").first().getElementsByClass("entry_vote_count").text());

            Entry entry = new Entry(author, avatar, message, time, strim, isReply, up, down);
            entries.add(entry);
        }

        return entries;
    }
}
