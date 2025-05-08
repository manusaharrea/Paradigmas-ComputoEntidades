package feed;

import java.io.Serializable;

public class Article implements Serializable {
    private String title;
    private String description;
    private String url;
    private String pubDate;

    public Article(String title, String description, String url, String pubDate) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.pubDate = pubDate;
    }

    public void print() {
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Publication Date: " + pubDate);
        System.out.println("Link: " + url);
        System.out.println("************************************\n");
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getPubDate() {
        return pubDate;
    }
}