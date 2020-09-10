package com.AHeaven;

import com.AHeaven.ui.TabSelectionAdapter;
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
        playlists.add(new Playlist());
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
        q.stopSong();
    }
    public static void addToQueue(Song song){
        queue.add(song);
        q.updateUI();
        if (queue.size()==1){
            q.startSong();
            q.stopSong();
        }
    }
    public static void addToQueue(Song[] songs){
        queue.addAll(Arrays.asList(songs));
        q.updateUI();
        if (queue.size()==songs.length){
            q.startSong();
            q.stopSong();
        }
    }
    public static void addToQueue(Playlist list){
        queue.addAll(Arrays.asList(list.getSongs()));
        q.updateUI();
        if (queue.size()==list.length){
            q.startSong();
            q.stopSong();
        }
    }
    public static void removeFromQueue(int nom){
        queue.remove(nom);
        q.updateUI();
        if (nom==nomPlaying){
            q.startSong();
            q.stopSong();
        }
    }
    public static Song getFromQueue(int nom){
        return queue.get(nom);
    }
    public static int getQueueLength(){
        return queue.size();
    }
}
