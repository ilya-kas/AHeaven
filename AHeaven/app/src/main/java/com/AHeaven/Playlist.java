package com.AHeaven;

import java.util.ArrayList;

public class Playlist {
    public String name;
    private ArrayList<Song> songs;

    public Playlist(String _name){
        songs = new ArrayList<>();
        name = _name;
    }

    public Song[] getSongs() {
        Song[] res = new Song[songs.size()];
        for (int i=0;i<songs.size();i++)
            res[i] = songs.get(i);
        return res;
    }
    public Song getSong(int i){
        return songs.get(i);
    }

    public void addSong(Song x){
        songs.add(x);
    }
    public void removeSong(Song x){
        songs.remove(x);
    }
    public int getLength(){return songs.size();}
}
