package com.example.newsaggregator;

import android.graphics.drawable.Drawable;

public class Article {
    private final String headline;
    private final String date;
    private final String author;
    private final Drawable image;
    private final String body;
    private final String url;



    public Article(String headline, String date, String author, Drawable image, String body, String url) {
        this.headline = headline;
        this.date = date;
        this.author = author;
        this.image = image;
        this.body = body;
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public String getUrl() {
        return url;
    }

    public String getHeadline() {
        return headline;
    }

    public Drawable getImage() {
        return image;
    }
}
