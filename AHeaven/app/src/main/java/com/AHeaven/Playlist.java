package com.AHeaven;

import java.util.ArrayList;

public class Playlist {
    public int length;
    private ArrayList<Song> songs;

    public Playlist(){
        songs = new ArrayList<>();
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
}
