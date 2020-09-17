package com.AHeaven;

import com.AHeaven.ui.tabs.QueueFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class User {
    public static int playlistCount;
    private static List<Playlist> playlists;
    private static List<Song> queue;
    private static QueueFragment q;
    public static int nomPlaying;

    public static void load(MainActivity activity){
        playlists = new ArrayList<>();
        queue = new LinkedList<>();
        playlistCount = 0;

        playlistCount+=1;              //пока что инициализация 2 плейлистами
        playlists.add(new Playlist("hello"));
        playlistCount+=1;              //пока что инициализация 2 плейлистами
        playlists.add(new Playlist("world"));
    }

    public static Playlist getPlaylist(int nom) {
        return playlists.get(nom);
    }

    public static void setQueueFragment(QueueFragment q1){
        q = q1;
    }
    public static void clearQueue(){
        queue.clear();
        q.updateUI();
        q.pauseSong();
    }
    public static void addToQueue(Song song){
        queue.add(song);
        q.updateUI();
        if (queue.size()==1)
            q.prepareSong();
    }
    public static void addToQueue(Song[] songs){
        queue.addAll(Arrays.asList(songs));
        q.updateUI();
        if (queue.size()==songs.length)
            q.prepareSong();
    }
    public static void addToQueue(Playlist list){
        if (list.getLength()==0)
            return;
        queue.addAll(Arrays.asList(list.getSongs()));
        q.updateUI();
        if (queue.size()==list.getLength())
            q.prepareSong();
    }
    public static void removeFromQueue(int nom){
        queue.remove(nom);
        q.updateUI();
        if (queue.size()==0)
            return;
        if (nom==nomPlaying)
            q.prepareSong();
    }
    public static Song getFromQueue(int nom){
        return queue.get(nom);
    }
    public static int getQueueLength(){
        return queue.size();
    }

    public static void addPlaylist(Playlist list){
        playlistCount++;
        playlists.add(list);
    }
}
