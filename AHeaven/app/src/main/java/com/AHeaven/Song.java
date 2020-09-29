package com.AHeaven;

import android.net.Uri;

public class Song {
    public Uri source;
    public String name;
    public String author;
    public int length; //в секундах

    /**
     * @param source путь до песни
     * @param name имя песни
     * @param length длина песни в секундах
     */
    public Song(Uri source, String name, int length) {
        this.source = source;
        this.name = name;
        this.length = length;
        author = "Unknown";
        if (name.equals(""))
            this.name = "Noname";
    }

    /**
     * @param author исполнитель/автор
     */
    public Song(Uri source, String name, String author, int length) {
        this.source = source;
        this.name = name;
        this.author = author;
        this.length = length;
        if (name.equals(""))
            this.name = "Noname";
        if (author.equals(""))
            this.author = "Unknown";
    }
}
