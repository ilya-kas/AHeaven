package com.AHeaven.ui.tabs;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.AHeaven.R;
import com.AHeaven.Song;
import com.AHeaven.User;

//класс фрагмента, который отображает очередь воспроизведения
public class QueueFragment extends Fragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    View fragment;
    boolean isNowPlaying;
    MediaPlayer player;

    public static QueueFragment newInstance() {
        QueueFragment fragment = new QueueFragment();
        User.setQueueFragment(fragment);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        player = new MediaPlayer();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.queue_fragment, container, false);
        User.nomPlaying = 0;

        isNowPlaying = false;
        final ImageButton play = fragment.findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.getQueueLength()==0)
                    return;
                if (!isNowPlaying){
                    play.setBackgroundResource(R.drawable.pause_button);
                    player.start();
                    isNowPlaying = true;
                }else{
                    play.setBackgroundResource(R.drawable.play_button);
                    player.pause();
                    isNowPlaying = false;
                }
            }
        });

        ImageButton prev = fragment.findViewById(R.id.prev);    //выбрать предыдущую песню
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.nomPlaying--;
                if (User.nomPlaying<0)
                    User.nomPlaying=User.getQueueLength()-1;
                updateUI();
                prepareSong();
                play.setBackgroundResource(R.drawable.pause_button);
                isNowPlaying = true;
                player.start();
            }
        });

        ImageButton next = fragment.findViewById(R.id.next);    //выбрать следующую песню
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.nomPlaying++;
                User.nomPlaying%=User.getQueueLength();
                updateUI();
                prepareSong();
                play.setBackgroundResource(R.drawable.pause_button);
                isNowPlaying = true;
                player.start();
            }
        });
        return fragment;
    }

    public void updateUI(){
        LinearLayout queue = fragment.findViewById(R.id.queue);
        queue.removeAllViews();
        if (User.getQueueLength()>0){
            FrameLayout frameLayout = fragment.findViewById(R.id.tv_song_name);
            frameLayout.removeAllViews();
            frameLayout.addView(createSongNameAuthor(User.getFromQueue(User.nomPlaying),18));
        }
        for (int i=0;i<User.getQueueLength();i++){
            final Song current = User.getFromQueue(i);
            LinearLayout line = new LinearLayout(getContext());
            line.setOrientation(LinearLayout.HORIZONTAL);
            if (i==User.nomPlaying)
                line.setBackgroundColor(getResources().getColor(R.color.lightBlue));

            TextView tv_nom = new TextView(getContext());          //надпись конкретной песни
            String text = (i+1) + ":";
            tv_nom.setText(text);
            tv_nom.setTextSize(22);
            tv_nom.setPadding(0,10,10,0);
            line.addView(tv_nom);

            line.addView(createSongNameAuthor(current,18));

            ImageButton minus = new ImageButton(getContext());
            minus.setImageResource(R.drawable.minus);
            minus.setBackground(null);
            minus.setLayoutParams(new LinearLayout.LayoutParams(130, 110));
            minus.setScaleType(ImageView.ScaleType.FIT_XY);
            final int finalI = i;
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User.removeFromQueue(finalI);
                    if (finalI==User.nomPlaying && isNowPlaying)
                        player.start();
                }
            });

            TextView tv_Length = new TextView(getContext());
            if (current.length<60)
                text = "0:"+ current.length;
            else
            if (current.length%60<10)
                text = current.length/60+":0"+current.length%60;
            else
                text = current.length/60+":"+current.length%60;
            tv_Length.setText(text);
            tv_Length.setTextSize(22);
            tv_Length.setPadding(0,10,0,0);

            line.addView(minus);
            line.addView(tv_Length);

            line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User.nomPlaying= finalI;
                    updateUI();
                    prepareSong();
                    player.start();
                }
            });
            queue.addView(line);
        }
    }

    public void pauseSong(){
        player.pause();
    }

    public void prepareSong(){
        player.release();
        player = MediaPlayer.create(getContext(),User.getFromQueue(User.nomPlaying).source);
        player.setOnCompletionListener(this);
    }

    private LinearLayout createSongNameAuthor(Song x, float size){
        LinearLayout names = new LinearLayout(getContext());
        names.setOrientation(LinearLayout.VERTICAL);

        TextView TVsongName = new TextView(getContext());          //надпись конкретной песни
        TVsongName.setText(x.name);
        TVsongName.setTextSize(size);

        TextView author = new TextView(getContext());          //надпись конкретной песни
        author.setText(x.author);
        author.setTextSize(size-4);

        names.addView(TVsongName);
        names.addView(author);
        names.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,1f));

        return names;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.seekTo(0);
        player.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        User.nomPlaying++;
        User.nomPlaying%=User.getQueueLength();
        updateUI();
        prepareSong();
        player.start();
    }
}