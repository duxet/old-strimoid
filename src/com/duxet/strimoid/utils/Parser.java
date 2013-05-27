package com.duxet.strimoid.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import android.graphics.Color;

import com.duxet.strimoid.models.*;
import com.duxet.strimoid.models.Comment;

public class Parser {
    private String html;
    private Document doc;

	public Parser(String html) {
        super();
        this.html = html;
        this.doc = Jsoup.parse(html);
    }

    public boolean checkIsLogged() {
		return html.contains("page_template.logged_in = true");
	}
	
	public String getUsername() {
	    return doc.getElementById("top_user_menu").getElementsByClass("user_name").first().text().trim();
	}
	
    public String getToken(){
        /*Document doc = Jsoup.parse(response);
        return doc.getElementsByAttributeValue("name", "token").first().attr("value").toString();*/
        
        Pattern p = Pattern.compile("page_template\\.token = '([a-z0-9]+)';");
        Matcher m = p.matcher(html);
        m.find();
        return m.group(1);
    }
    
    public String getFirstValue(String v){
        return doc.getElementsByAttributeValue("name", v).first().attr("value").toString();
    }
    
    public ArrayList<Comment> getComments() {
        ArrayList<Comment> comments = new ArrayList<Comment>();

        for (Element el : doc.getElementsByClass("content_comment")) {
            if (el.hasClass("hidden"))
                continue;
            
            String id = el.getElementsByTag("a").first().attr("id").trim();
            String author = el.getElementsByClass("user_name").first().text().trim();
            String avatar = el.getElementsByClass("content_comment_image").first().getElementsByTag("img").first().attr("src").trim();
            String text = el.getElementsByClass("content_comment_text").first().text().trim();
            String time = el.getElementsByClass("content_comment_info").first().getElementsByAttribute("title").first().text().trim();

            boolean isReply = el.hasClass("reply");
            boolean isUpvoted = el.getElementsByClass("like").first().hasClass("selected");
            boolean isDownvoted = el.getElementsByClass("dislike").first().hasClass("selected");
            
            String likeUrl = el.getElementsByClass("like").first().attr("href");
            String dislikeUrl = el.getElementsByClass("dislike").first().attr("href");
            
            int up = Integer.parseInt(el.getElementsByClass("like").first().getElementsByClass("content_comment_vote_count").text());
            int down = Integer.parseInt(el.getElementsByClass("dislike").first().getElementsByClass("content_comment_vote_count").text());

            int color = getColorUserByString(el.getElementsByClass("user_name").first().attr("class"));
            
            Comment comment = new Comment(id, author, avatar, text, time, likeUrl, dislikeUrl,
                    up, down, isUpvoted, isDownvoted, isReply, color);
            comments.add(comment);
        }

        return comments;
    }
    
    public ArrayList<Content> getContents() {
        ArrayList<Content> contents = new ArrayList<Content>();

        for (Element el : doc.getElementsByClass("content")) {
            String id = el.getElementsByTag("a").first().attr("id").trim();
            String title = el.getElementsByClass("content_title").first().text().trim();
            String author = el.getElementsByClass("user_name").first().getElementsByTag("span").first().text().trim();
            String desc = el.getElementsByClass("content_info").text().trim();
            
            String time = el.getElementsByClass("content_info").first().getElementsByClass("color_gray").first().getElementsByAttribute("title").first().text();
            String strim = el.getElementsByClass("content_info").first().getElementsByClass("color_gray").first().getElementsByTag("a").last().text();
            
            String url = el.getElementsByClass("content_title").first().attr("href").trim();
            String commentsUrl = el.getElementsByClass("content_info_actions").first().getElementsByTag("a").first().attr("href").trim();

            String imageUrl = "";

            if (el.getElementsByClass("content_image").first() != null)
                imageUrl = el.getElementsByClass("content_image").first().getElementsByTag("img").first().attr("src").trim();
            
            boolean isUpvoted = el.getElementsByClass("like").first().hasClass("selected");
            boolean isDownvoted = el.getElementsByClass("dislike").first().hasClass("selected");
            
            String likeUrl = el.getElementsByClass("like").first().attr("href");
            String dislikeUrl = el.getElementsByClass("dislike").first().attr("href");

            int up = Integer.parseInt(el.getElementsByClass("like").first().getElementsByClass("content_vote_count").text());
            int down = Integer.parseInt(el.getElementsByClass("dislike").first().getElementsByClass("content_vote_count").text());
            
            int color = getColorUserByString(el.getElementsByClass("user_name").first().attr("class"));
            
            Content content = new Content(id, title, author, desc, time, strim, url, imageUrl, commentsUrl,
                    likeUrl, dislikeUrl, up, down, isUpvoted, isDownvoted, color);
            contents.add(content);
        }

        return contents;
    }
    
    

    public ArrayList<Entry> getEntries() {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        Elements liElements = doc.getElementsByClass("entries").first().getElementsByTag("li");
        
        for (Element li : liElements) {
            if(!li.parent().hasClass("entries"))
                continue;
            
            String firstId = "";
            
            for (Element el : li.getElementsByClass("entry")) {
                if (el.hasClass("hidden"))
                    continue;
                
                String id = el.getElementsByTag("a").first().attr("id").trim();
                String author = el.getElementsByClass("entry_user").first().text().trim();
                String avatar = el.getElementsByClass("entry_image").first().getElementsByTag("img").first().attr("src").trim();
                String message = el.getElementsByClass("entry_text").first().text().trim();
                String time = el.getElementsByClass("entry_info").first().getElementsByAttribute("title").first().text().trim();
                String strim = el.getElementsByClass("entry_info").first().getElementsByTag("a").first().text().trim();

                boolean isReply = el.hasClass("reply");
                boolean isUpvoted = el.getElementsByClass("like").first().hasClass("selected");
                boolean isDownvoted = el.getElementsByClass("dislike").first().hasClass("selected");
                
                String likeUrl = el.getElementsByClass("like").first().attr("href");
                String dislikeUrl = el.getElementsByClass("dislike").first().attr("href");
                String moreUrl = "";
                
                int up = Integer.parseInt(el.getElementsByClass("like").first().getElementsByClass("entry_vote_count").text());
                int down = Integer.parseInt(el.getElementsByClass("dislike").first().getElementsByClass("entry_vote_count").text());
                
                int color = getColorUserByString(el.getElementsByClass("entry_user").first().getElementsByTag("a").first().attr("class"));
                
                if (!isReply)
                    firstId = id;
                
                Entry entry = new Entry(id, author, avatar, message, time, strim, likeUrl, dislikeUrl, moreUrl,
                        up, down, color, isUpvoted, isDownvoted, isReply);
                entries.add(entry);
            }
            
            if (li.getElementsByClass("entries_more").first() != null) {
                String moreUrl = "ajax/w/" + firstId + "/odpowiedzi";
                Entry entry = new Entry("", "", "", "", "", "", "", "", moreUrl, 0, 0, 0, false, false, false);
                entries.add(entry);
            }
        }

        return entries;
    }
    
    public ArrayList<Entry> getMoreEntries() {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (Element el : doc.getElementsByClass("entry")) {
            if (el.hasClass("hidden"))
                continue;
            
            String id = el.getElementsByTag("a").first().attr("id").trim();
            String author = el.getElementsByClass("entry_user").first().text().trim();
            String avatar = el.getElementsByClass("entry_image").first().getElementsByTag("img").first().attr("src").trim();
            String message = el.getElementsByClass("entry_text").first().text().trim();
            String time = el.getElementsByClass("entry_info").first().getElementsByAttribute("title").first().text().trim();
            String strim = el.getElementsByClass("entry_info").first().getElementsByTag("a").first().text().trim();

            boolean isReply = el.hasClass("reply");
            boolean isUpvoted = el.getElementsByClass("like").first().hasClass("selected");
            boolean isDownvoted = el.getElementsByClass("dislike").first().hasClass("selected");
            
            String likeUrl = el.getElementsByClass("like").first().attr("href");
            String dislikeUrl = el.getElementsByClass("dislike").first().attr("href");
            String moreUrl = "";
            
            int up = Integer.parseInt(el.getElementsByClass("like").first().getElementsByClass("entry_vote_count").text());
            int down = Integer.parseInt(el.getElementsByClass("dislike").first().getElementsByClass("entry_vote_count").text());
            
            int color = getColorUserByString(el.getElementsByClass("entry_user").first().getElementsByTag("a").first().attr("class"));

            Entry entry = new Entry(id, author, avatar, message, time, strim, likeUrl, dislikeUrl, moreUrl,
                    up, down, color, isUpvoted, isDownvoted, isReply);
            entries.add(entry);
        }    

        return entries;
    }
    
    public ArrayList<Strim> getStrims() {
        ArrayList<Strim> strims = new ArrayList<Strim>();
        
        String content = "";
        
        try {
            JSONObject json = new JSONObject(html);
            content = json.getString("content");
        } catch (JSONException e) {
            return null;
        }
        
        Document doc = Jsoup.parse(content);

        for (Element el : doc.getElementsByTag("li")) {
            String name = el.getElementsByTag("a").first().attr("href").trim();
            String title = el.getElementsByClass("name").first().text().trim();
            String desc = "";
            
            Strim strim = new Strim(name, title, desc);
            strims.add(strim);
        }

        return strims;
    }
    
    public static int getColorUserByString(String userStatus){
        int color;
       
        if (userStatus.contains("new")){
         color = Color.parseColor("#2e9b2d");
        }else if(userStatus.contains("admin")){
         color = Color.parseColor("#c4181b");
        }else if(userStatus.contains("advanced")){
         color = Color.parseColor("#0075dc");
        }else{
         color = Color.parseColor("#3272aa");
        }
        
        return color;
    }
    
    public static NotificationStatus getNotifications(String response) {

		try {
	        JSONObject mainObject = new JSONObject(response);
	        JSONObject uniObject = mainObject.getJSONObject("content");
	        String m_c = uniObject.getJSONObject("messages_count").toString();
	        String n_c = uniObject.getJSONObject("notifications_count").toString();
			return new NotificationStatus(Integer.parseInt(m_c),Integer.parseInt(n_c));
		} catch (JSONException e) {
			return null;
		}
        
    }
}
