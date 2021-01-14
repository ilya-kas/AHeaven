package com.AHeaven.playing;

import android.content.Context;
import android.util.Log;

import com.AHeaven.DBHelper;
import com.AHeaven.ui.tabs.QueueFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class User {
    public static int playlistCount;
    private static List<Playlist> playlists;
    private static List<Song> queue;
    public static QueueFragment q;
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
        db.saveSongs(playlists);
    }

    public static Playlist getPlaylist(int nom) {
        return playlists.get(nom);
    }

    public static void setQueueFragment(QueueFragment q1){
        q = q1;
    }

    public static void clearQueue(){
        queue.clear();
        nomPlaying = -1;
        q.pauseSong();
    }

    public static void addToQueue(Song song){
        if (nomPlaying<0)
            nomPlaying = 0;
        queue.add(song);
        q.updateUI();
        if (queue.size()==1)
            q.prepareSong();
    }

    public static void addToQueue(Song[] songs){
        if (songs.length==0)
            return;
        if (nomPlaying<0)
            nomPlaying = 0;
        queue.addAll(Arrays.asList(songs));
        q.updateUI();
        if (queue.size()==songs.length)
            q.prepareSong();
    }

    public static void addToQueue(Playlist list){
        if (list.getSize()==0)
            return;
        if (nomPlaying<0)
            nomPlaying = 0;
        queue.addAll(Arrays.asList(list.getSongs()));
        q.updateUI();
        if (queue.size()==list.getSize())
            q.prepareSong();
    }

    public static void removeFromQueue(int nom){
        queue.remove(nom);
        if (nom<User.nomPlaying)
            nomPlaying = nomPlaying-1;
        if (nomPlaying>=queue.size()){
            nomPlaying = queue.size()-1;
            q.prepareSong();
        }
        q.updateUI();
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

    public static void shuffle(){ //перетасовка очереди
        Random rand = new Random();
        int j;
        for (int i=1;i<queue.size();i++){
            j = i-rand.nextInt(i+1);
            queue.add(j,queue.remove(i));
        }
    }
}
