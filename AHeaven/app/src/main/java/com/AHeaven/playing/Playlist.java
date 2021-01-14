package com.AHeaven.playing;

import java.util.ArrayList;
import java.util.List;

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
    public void addSong(List<Song> x){
        songs.addAll(x);
    }
    public void removeSong(int nom){
        songs.remove(nom);
    }
    public int getSize(){return songs.size();}
}
