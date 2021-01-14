package com.AHeaven.ui.tabs;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.AHeaven.playing.Playlist;
import com.AHeaven.R;
import com.AHeaven.playing.Song;
import com.AHeaven.playing.User;

import static android.app.Activity.RESULT_OK;

/**
 * класс фрагмента выбора плейлистов
 */
public class BoxFragment extends Fragment {
    Playlist playlist;
    private final int addSongCode = 228;
    View fragment;

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
                startActivityForResult(intent,addSongCode);
            }
        });
        Button toQ = fragment.findViewById(R.id.addPlaylistToQueue);     //кнопка добавить в очередь весь плейлист
        toQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.addToQueue(playlist);
            }
        });

        updateUI();

        return fragment;
    }

    private void addSongNameAuthor(Song x, LinearLayout layout){
        LinearLayout names = new LinearLayout(getContext());
        names.setOrientation(LinearLayout.VERTICAL);

        TextView TVsongName = new TextView(getContext());          //надпись конкретной песни
        TVsongName.setText(x.name);
        TVsongName.setTextSize(18);
        TVsongName.setTextColor(getResources().getColor(R.color.white));

        TextView author = new TextView(getContext());          //надпись конкретной песни
        author.setText(x.author);
        author.setTextSize(14);
        author.setTextColor(getResources().getColor(R.color.white));

        names.addView(TVsongName);
        names.addView(author);
        names.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,1f));

        layout.addView(names);
    }

    private void updateUI(){
        LinearLayout box = fragment.findViewById(R.id.song_list);
        box.removeAllViews();
        final Song[] songs = playlist.getSongs();
        for (int i=0;i<songs.length;i++){
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);

            addSongNameAuthor(songs[i],layout);

            final ImageButton plus = new ImageButton(getContext()); //кнопка добавить песню в конец очереди
            plus.setImageResource(R.drawable.plus);
            plus.setBackground(null);
            plus.setLayoutParams(new LinearLayout.LayoutParams(130, 110));
            plus.setScaleType(ImageView.ScaleType.FIT_XY);
            plus.setPadding(26,20,26,20);
            final int finalI = i;
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User.addToQueue(songs[finalI]);
                }
            });

            String text;
            TextView tv_Length = new TextView(getContext());
            if (songs[i].length%60<10)
                text = songs[i].length/60+":0"+songs[i].length%60;
            else
                text = songs[i].length/60+":"+songs[i].length%60;
            tv_Length.setText(text);
            tv_Length.setTextSize(22);
            tv_Length.setPadding(0,10,0,0);
            tv_Length.setTextColor(getResources().getColor(R.color.white));

            final ImageButton dots = new ImageButton(getContext());
            dots.setImageResource(R.drawable.dots);
            dots.setBackground(null);
            dots.setLayoutParams(new LinearLayout.LayoutParams(130, 110));
            dots.setScaleType(ImageView.ScaleType.FIT_XY);
            dots.setPadding(26,20,26,20);
            dots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(getContext(),dots);
                    popupMenu.inflate(R.menu.song_popupmenu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.remove_song:
                                    playlist.removeSong(finalI);
                                    updateUI();
                                    return true;
                                case R.id.add_song_to_another:
                                    PopupMenu playlistsMenu = new PopupMenu(getContext(),dots);
                                    playlistsMenu.inflate(R.menu.playlists_popupmenu);
                                    for (int i=0;i<User.playlistCount;i++)
                                        playlistsMenu.getMenu().add(User.getPlaylist(i).name);
                                    playlistsMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            for (int i=0;i<User.playlistCount;i++) {
                                                Playlist list = User.getPlaylist(i);
                                                if (list.name == item.getTitle()) {
                                                    list.addSong(playlist.getSong(finalI));
                                                    break;
                                                }
                                            }
                                            return true;
                                        }
                                    });
                                    playlistsMenu.show();
                                    return true;
                                case R.id.change_song:
                                    final Dialog addSong = new Dialog(getContext());         //диалог получения данных о песне
                                    addSong.setContentView(R.layout.sond_additing_dialog);
                                    final EditText et_name = addSong.findViewById(R.id.et_name);
                                    final EditText et_author = addSong.findViewById(R.id.et_author);

                                    final Song song = playlist.getSong(finalI);

                                    et_name.setText(song.name);
                                    et_author.setText(song.author);

                                    addSong.show();

                                    Button add = addSong.findViewById(R.id.b_add);
                                    add.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            song.name = et_name.getText().toString().trim();
                                            song.author = et_author.getText().toString().trim();

                                            updateUI();
                                            addSong.dismiss();
                                        }
                                    });
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });

            layout.addView(plus);
            layout.addView(tv_Length);
            layout.addView(dots);
            box.addView(layout);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        switch (requestCode){
            case addSongCode:
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
        x.name = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
    }

    private int getDuration(Song song){
        MediaPlayer player = MediaPlayer.create(getContext(),song.source);
        return player.getDuration()/1000;
    }
}
