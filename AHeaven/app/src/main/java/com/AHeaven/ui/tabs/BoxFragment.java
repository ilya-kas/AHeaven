package com.AHeaven.ui.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.AHeaven.Playlist;
import com.AHeaven.R;
import com.AHeaven.Song;
import com.AHeaven.User;

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
        fragment.playlist = list;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        fragment = inflater.inflate(R.layout.box_fragment, container, false);
        Button add = fragment.findViewById(R.id.addToPlaylist);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent,addSongCode);
            }
        });
        Button toQ = fragment.findViewById(R.id.addPlaylistToQueue);
        toQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.addToQueue(playlist);
            }
        });

        updateUI();

        return fragment;
    }

    private void addSongNameAuthor(int i, Song x, LinearLayout layout){
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

            addSongNameAuthor(i,songs[i],layout);

            ImageButton plus = new ImageButton(getContext());
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
            if (songs[i].length<60)
                text = String.valueOf(songs[i].length);
            else
            if (songs[i].length%60<10)
                text = songs[i].length/60+":0"+songs[i].length%60;
            else
                text = songs[i].length/60+":"+songs[i].length%60;
            tv_Length.setText(text);
            tv_Length.setTextSize(22);
            tv_Length.setPadding(0,10,0,0);
            tv_Length.setTextColor(getResources().getColor(R.color.white));

            ImageButton dots = new ImageButton(getContext());
            dots.setImageResource(R.drawable.dots);
            dots.setBackground(null);
            dots.setLayoutParams(new LinearLayout.LayoutParams(130, 110));
            dots.setScaleType(ImageView.ScaleType.FIT_XY);
            dots.setPadding(26,20,26,20);
            dots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo
                }
            });

            layout.addView(plus);
            layout.addView(tv_Length);
            layout.addView(dots);
            box.addView(layout);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case addSongCode:
                if (resultCode == RESULT_OK){
                    String path = data.getData().getPath();
                    playlist.addSong(new Song(path,"Name",322));
                    updateUI();
                }
                break;
        }
    }
}
