package com.AHeaven.playing;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.AHeaven.DBHelper;
import com.AHeaven.ui.tabs.QueueFragment;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class User {
    public static int playlistCount;
    private static List<Playlist> playlists;
    public static QueueFragment q;

    public static void load(Context context){
        playlists = new ArrayList<>();
        playlistCount = 0;

        DBHelper db = new DBHelper(context);
        playlists = db.getPlaylists();
        playlistCount = playlists.size();
        for (Playlist x:playlists)
            db.loadSongsFromPlaylist(x);
    }

    public static void save(Context context){
        DBHelper db = new DBHelper(context);
        db.savePlaylists(playlists);
        db.saveSongs(playlists);
    }

    public static void updateQueueFragment(QueueFragment q) {
        User.q = q;
    }

    public static Playlist getPlaylist(int nom) {
        return playlists.get(nom);
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
