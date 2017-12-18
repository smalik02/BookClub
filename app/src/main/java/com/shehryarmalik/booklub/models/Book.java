package com.shehryarmalik.booklub.models;

/**
 * Created by shehryarmalik on 10/22/17.
 */

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Book extends RealmObject {

    public static final String FIELD_ID = "id";

    @Required
    @PrimaryKey
    private String id;
    @Required
    private String name;
    @Required
    private String author;
    private int length;
    @Required
    private String genre;
    private int release_year;
    private boolean read;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getRelease_year() {
        return release_year;
    }

    public void setRelease_year(int release_year) {
        this.release_year = release_year;
    }

    public static void delete(Realm realm, String id){
        Book book = realm.where(Book.class).equalTo(FIELD_ID, id).findFirst();
        if (book != null) {
            book.deleteFromRealm();
        }
    }
}
