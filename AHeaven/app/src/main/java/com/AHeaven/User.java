package com.AHeaven;

import android.content.Context;
import android.util.Log;

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

    public static void load(Context context){
        playlists = new ArrayList<>();
        queue = new LinkedList<>();
        playlistCount = 0;

        DBHelper db = new DBHelper(context);
        playlists = db.getPlaylists();
        playlistCount = playlists.size();
        for (Playlist x:playlists){
            db.loadSongsFromPlaylist(x);
        }
    }

    public static void save(Context context){
        DBHelper db = new DBHelper(context);
        db.savePlaylists(playlists);
        for (Playlist x:playlists){
            db.saveSongsFromPlaylist(x.name,Arrays.asList(x.getSongs()));
        }
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
        if (list.getSize()==0)
            return;
        queue.addAll(Arrays.asList(list.getSongs()));
        q.updateUI();
        if (queue.size()==list.getSize())
            q.prepareSong();
    }
    public static void removeFromQueue(int nom){
        queue.remove(nom);
        if (nom<User.nomPlaying)
            User.nomPlaying=Math.max(User.nomPlaying-1,0);
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
        for (int i=0;i<playlistCount;i++){
            if (playlists.get(i).name.equals(list.name)) {
                Log.i("Same names", "Playlist with the same name");
                return;
            }
        }
        playlistCount++;
        playlists.add(list);
    }
}
