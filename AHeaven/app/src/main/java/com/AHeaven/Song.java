package com.AHeaven;

public class Song {
    public String source;
    public String name;
    public String author;
    public int length; //в секундах

    /**
     * @param source путь до песни
     * @param name имя песни
     * @param length длина песни в секундах
     */
    public Song(String source, String name, int length) {
        this.source = source;
        this.name = name;
        this.length = length;
        author = "Unknown";
    }

    /**
     * @param author исполнитель/автор
     */
    public Song(String source, String name, String author, int length) {
        this.source = source;
        this.name = name;
        this.author = author;
        this.length = length;
    }
}
