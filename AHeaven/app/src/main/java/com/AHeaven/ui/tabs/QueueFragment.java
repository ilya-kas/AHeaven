package com.AHeaven.ui.tabs;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.AHeaven.R;
import com.AHeaven.Song;
import com.AHeaven.User;

//класс фрагмента, который отображает очередь воспроизведения
public class QueueFragment extends Fragment implements MediaPlayer.OnCompletionListener {
    View fragment;
    boolean isNowPlaying;
    MediaPlayer player;
    Thread seekBarUpdate;

    public static QueueFragment newInstance() {       //создаём экземпляр вкладки
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

        isNowPlaying = false;                                        //кнопка проиграть внизу
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

        Button clear = fragment.findViewById(R.id.b_clear_queue); //кнопка очистки очереди
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.clearQueue();
                play.setBackgroundResource(R.drawable.play_button);
                isNowPlaying = false;
                updateUI();
            }
        });

        Button shuffle = fragment.findViewById(R.id.b_shuffle); //кнопка перемешивания очереди
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.shuffle();
                updateUI();
                prepareSong();
            }
        });

        ImageButton prev = fragment.findViewById(R.id.prev);    //выбрать предыдущую песню
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPrev();
            }
        });

        ImageButton next = fragment.findViewById(R.id.next);    //выбрать следующую песню
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNext();
            }
        });

        final TextView currentTime = fragment.findViewById(R.id.tv_song_time);
        SeekBar seekBar = fragment.findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {  //полоса проигранной части песни
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTime.setText(lengthToString(player.getCurrentPosition()/1000));
                if (!fromUser) return;
                if (User.getQueueLength()==0) return;
                player.seekTo(Math.round(player.getDuration()*(seekBar.getProgress()/100f)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return fragment;
    }

    private void moveToPrev() {
        User.nomPlaying--;
        if (User.nomPlaying<0)
            User.nomPlaying=User.getQueueLength()-1;
        updateUI();
        prepareSong();
        fragment.findViewById(R.id.play).setBackgroundResource(R.drawable.pause_button);
        isNowPlaying = true;
        player.start();
    }

    private void moveToNext() {
        User.nomPlaying++;
        User.nomPlaying%=User.getQueueLength();
        updateUI();
        prepareSong();
        fragment.findViewById(R.id.play).setBackgroundResource(R.drawable.pause_button);
        isNowPlaying = true;
        player.start();
    }

    public void updateUI(){
        LinearLayout queue = fragment.findViewById(R.id.queue);
        queue.removeAllViews();
        if (User.getQueueLength()>0){  //очистка
            FrameLayout frameLayout = fragment.findViewById(R.id.tv_song_name);
            frameLayout.removeAllViews();
            frameLayout.addView(createSongNameAuthor(User.getFromQueue(User.nomPlaying),18));
        }
        for (int i=0;i<User.getQueueLength();i++){ //отрисовка строчек песен
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

            ImageButton minus = new ImageButton(getContext());  //кнопка удаления из плейлиста
            minus.setImageResource(R.drawable.minus);
            minus.setBackground(null);
            minus.setLayoutParams(new LinearLayout.LayoutParams(130, 110));
            minus.setScaleType(ImageView.ScaleType.FIT_XY);
            final int finalI = i;
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean flag = finalI==User.nomPlaying; //если удаляли песню, которая игралась
                    User.removeFromQueue(finalI);
                    if (User.getQueueLength()==0) {
                        player.stop();
                        fragment.findViewById(R.id.play).setBackgroundResource(R.drawable.pause_button);
                        isNowPlaying = false;
                        return;
                    }
                    if (flag)
                        prepareSong();
                    if (flag && isNowPlaying)
                        player.start();
                }
            });

            TextView tv_Length = new TextView(getContext());
            tv_Length.setText(lengthToString(current.length));
            tv_Length.setTextSize(22);
            tv_Length.setPadding(0,10,0,0);

            line.addView(minus);
            line.addView(tv_Length);

            line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //при нажатии проигрывать именно эту
                    User.nomPlaying= finalI;
                    updateUI();
                    prepareSong();
                    player.start();
                    fragment.findViewById(R.id.play).setBackgroundResource(R.drawable.pause_button);
                    isNowPlaying = true;
                }
            });
            queue.addView(line);
        }
    }

    public void pauseSong(){
        player.pause();
    }

    public void prepareSong(){ //подготавливает следующую песню к запуску
        if (User.nomPlaying<0)
            return;
        player.release();
        player = MediaPlayer.create(getContext(),User.getFromQueue(User.nomPlaying).source);
        player.setOnCompletionListener(this);

        TextView songLength = fragment.findViewById(R.id.tv_song_length);
        songLength.setText(lengthToString(player.getDuration()/1000));
        seekBarUpdate = new Thread(new Runnable() {    //обновление прогресса песни. Чтобы ползунок двигался
            @Override
            public void run() {     //обновление полосы проигранной части песни
                SeekBar sb = fragment.findViewById(R.id.seek_bar);
                Song song = User.getFromQueue(User.nomPlaying);
                sb.setProgress(0);
                while (sb.getProgress()<100){
                    try {
                        if (User.nomPlaying>=User.getQueueLength())
                            return;
                        if (User.getFromQueue(User.nomPlaying)!=song)
                            return;
                        sb.setProgress(Math.round(((float)player.getCurrentPosition())/player.getDuration()*100));
                    }catch (Exception e){
                        e.printStackTrace();
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBarUpdate.start();
            }
        });
    }

    private String lengthToString(int length){  //длину в секундах в строку mm:ss
        String text;
        if (length%60<10)
            text = length/60+":0"+length%60;
        else
            text = length/60+":"+length%60;
        return text;
    }

    private LinearLayout createSongNameAuthor(Song x, float size){ //создаёт слой с названием песни и группы
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

    /**
     * implementing parts for MediaPlayer
     */

    @Override
    public void onCompletion(MediaPlayer mp) {
        User.nomPlaying++;
        User.nomPlaying%=User.getQueueLength();
        updateUI();
        prepareSong();
        player.start();
    }
}