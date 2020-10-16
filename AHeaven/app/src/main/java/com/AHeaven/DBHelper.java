package com.AHeaven;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private final String tableName = "SongList";
    private final String mainTableName = "Playlists";

    private final static String dbName = "User";
    private final static int VERSION = 1;

    private final String ROW_ID = "Row_id";
    private final String URI = "Uri";
    private final String NAME = "Name";
    private final String AUTHOR = "Author";
    private final String PLAYLIST = "Playlist";
    private final String LENGTH = "Length";

    private final String PLAYLIST_NAME = "Name";

    public DBHelper(Context context){
        super(context, dbName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + tableName + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                URI + " TEXT," + NAME + " TEXT," + AUTHOR + " TEXT," + LENGTH + " INTEGER," + PLAYLIST +" TEXT)");
        db.execSQL("CREATE TABLE " + mainTableName + "(" + PLAYLIST_NAME + " TEXT PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void loadSongsFromPlaylist(Playlist list){
        /*String[] projection = new String[] {media-database-columns-to-retrieve};
        String selection = sql-where-clause-with-placeholder-variables;
        String[] selectionArgs = new String[] {
                values-of-placeholder-variables
        };
        String sortOrder = sql-order-by-clause;
        Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.media-type.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        while (cursor.moveToNext()) {
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
        }*/

        String query = "SELECT * FROM " + tableName +" WHERE "+ PLAYLIST+" = '"+list.name.replace(' ','_')+"'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst()){
            do{
                Song x = new Song(null,"",0);
                x.source = Uri.parse(cursor.getString(1));
                x.name = cursor.getString(2);
                x.author = cursor.getString(3);
                x.length = cursor.getInt(4);
                list.addSong(x);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    public List<Playlist> getPlaylists(){
        LinkedList<Playlist> list = new LinkedList<>();

        String query = "SELECT * FROM " + mainTableName;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst()){
            do{
                Playlist x = new Playlist(cursor.getString(0));
                list.add(x);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void saveSongs(List<Playlist> lists){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE "+tableName);
        db.execSQL("CREATE TABLE " + tableName + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                URI + " TEXT," + NAME + " TEXT," + AUTHOR + " TEXT," + LENGTH + " INTEGER," + PLAYLIST +" TEXT)");

        for (Playlist list:lists)
            for (Song x:list.getSongs()){
                ContentValues values = new ContentValues();
                values.put(URI,x.source.toString());
                values.put(NAME,x.name);
                values.put(AUTHOR,x.author);
                values.put(LENGTH,x.length);
                String name = list.name.replace(' ','_');
                values.put(PLAYLIST,name);

                db.insert(tableName, null,values);
            }
    }

    public void savePlaylists(List<Playlist> list){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE "+mainTableName);
        db.execSQL("CREATE TABLE " + mainTableName + "(" + PLAYLIST_NAME + " TEXT PRIMARY KEY)");

        for (Playlist x:list){
            ContentValues values = new ContentValues();
            values.put(PLAYLIST_NAME,x.name);

            db.insert(mainTableName, null,values);
        }
    }
}
