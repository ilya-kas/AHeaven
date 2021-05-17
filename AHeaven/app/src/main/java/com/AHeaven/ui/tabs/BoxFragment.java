package com.AHeaven.ui.tabs;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.AHeaven.playing.Playlist;
import com.AHeaven.R;
import com.AHeaven.playing.QueueController;
import com.AHeaven.playing.Song;
import com.AHeaven.ui.tabs.DragNDrop.PlaylistAdapter;
import com.AHeaven.ui.tabs.DragNDrop.PlaylistTouchAdapter;

import static android.app.Activity.RESULT_OK;

//todo чтобы при изменении имени/автора песни менялись и данные в файле
/**
 * класс фрагмента выбора плейлистов
 */
public class BoxFragment extends Fragment {
    Playlist playlist;
    private final int ADD_SONG_CODE = 228;
    View fragment;

    private PlaylistAdapter playlistAdapter;

    public static BoxFragment newInstance(Playlist list) {
        BoxFragment fragment = new BoxFragment();
        fragment.updatePlaylist(list);
        return fragment;
    }

    void updatePlaylist(Playlist list){
        playlist = list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        fragment = inflater.inflate(R.layout.box_fragment, container, false); //кнопка добавить в плейлист
        Button add = fragment.findViewById(R.id.addToPlaylist);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //добавить в плейлист
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("audio/*");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(intent, ADD_SONG_CODE);
            }
        });
        Button toQ = fragment.findViewById(R.id.addPlaylistToQueue);     //кнопка добавить в очередь весь плейлист
        toQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueueController.addToQueue(playlist);
            }
        });

        /*
         * эта часть нужна для реализации drag-n-drop
         */
        playlistAdapter = new PlaylistAdapter(this,fragment);
        RecyclerView recyclerView = fragment.findViewById(R.id.playlist);
        recyclerView.setAdapter(playlistAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //привязываю свой обработчик нажатий к recycler view
        ItemTouchHelper.Callback callback = new PlaylistTouchAdapter(playlistAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        updateUI();
        return fragment;
    }

    public void updateUI(){
        playlistAdapter.update();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        switch (requestCode){
            case ADD_SONG_CODE:
                if (resultCode == RESULT_OK){
                    Uri path = data.getData();
                    Song song = new Song(path, "Noname", "Noname", 322);
                    setNameAuthor(song);
                    song.length = getDuration(song);
                    playlist.addSong(song);

                    final int takeFlags = data.getFlags()                  //флаги доступа
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContext().getContentResolver().takePersistableUriPermission(path, takeFlags);
                    updateUI();
                }
                break;
        }
    }

    private void setNameAuthor(Song x){
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(getContext(),x.source);
        x.author = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if (x.author == null)
            x.author = "Noname";
        x.name = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if (x.name == null)
            x.name = "Unknown";
    }

    private int getDuration(Song song){
        MediaPlayer player = MediaPlayer.create(getContext(),song.source);
        return player.getDuration()/1000;
    }

    public Playlist getPlaylist() {
        return playlist;
    }
}
