package com.AHeaven.ui.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.AHeaven.R;
import com.AHeaven.playing.QueueController;
import com.AHeaven.playing.Song;
import com.AHeaven.playing.User;

//класс фрагмента, который отображает очередь воспроизведения
public class QueueFragment extends Fragment{
    View fragment;
    public Thread seekBarUpdate;

    public static QueueFragment newInstance() {       //создаём экземпляр вкладки
        QueueFragment fragment = new QueueFragment();
        User.updateQueueFragment(fragment);
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
        fragment = inflater.inflate(R.layout.queue_fragment, container, false);

        final ImageButton play = fragment.findViewById(R.id.play);//кнопка проиграть внизу
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonClick();
            }
        });

        Button clear = fragment.findViewById(R.id.b_clear_queue); //кнопка очистки очереди
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueueController.clear();
                play.setBackgroundResource(R.drawable.play_button);
                updateUI();
            }
        });

        Button shuffle = fragment.findViewById(R.id.b_shuffle); //кнопка перемешивания очереди
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueueController.shuffle();
                updateUI();
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

        SeekBar seekBar = fragment.findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {  //полоса проигранной части песни
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                if (QueueController.getNomPlaying()==-1) return;
                QueueController.seekTo(seekBar.getProgress()/100f);
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

    public void playButtonClick(){
        ImageButton play = fragment.findViewById(R.id.play);
        if (QueueController.getNomPlaying()==-1)
            return;
        if (!QueueController.isNowPlaying()) {
            QueueController.play();
            play.setBackgroundResource(R.drawable.pause_button);
        }else {
            QueueController.pause();
            play.setBackgroundResource(R.drawable.play_button);
        }
    }

    public void moveToPrev() {
        QueueController.moveToPrev();
        updateUI();
        if (QueueController.isNowPlaying())
            fragment.findViewById(R.id.play).setBackgroundResource(R.drawable.pause_button);
    }

    public void moveToNext() {
        QueueController.moveToNext();
        updateUI();
        if (QueueController.isNowPlaying())
            fragment.findViewById(R.id.play).setBackgroundResource(R.drawable.pause_button);
    }

    public void updateUI(){
        if (QueueController.isNowPlaying())
            fragment.findViewById(R.id.play).setBackgroundResource(R.drawable.pause_button);
        else
            fragment.findViewById(R.id.play).setBackgroundResource(R.drawable.play_button);
        LinearLayout queue = fragment.findViewById(R.id.queue);
        queue.removeAllViews();
        if (QueueController.getQueueLength()>0){  //очистка
            FrameLayout frameLayout = fragment.findViewById(R.id.tv_song_name);
            frameLayout.removeAllViews();
            frameLayout.addView(createSongNameAuthor(QueueController.getSongFromQueue(QueueController.getNomPlaying()),18));
        }
        for (int i=0;i<QueueController.getQueueLength();i++){ //отрисовка строчек песен
            final Song current = QueueController.getSongFromQueue(i);
            LinearLayout line = new LinearLayout(getContext());
            line.setOrientation(LinearLayout.HORIZONTAL);
            if (i==QueueController.getNomPlaying())
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
                    QueueController.removeFromQueue(finalI);
                    if (QueueController.getQueueLength()==0)
                        fragment.findViewById(R.id.play).setBackgroundResource(R.drawable.play_button);
                    updateUI();
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
                    QueueController.moveTo(finalI);
                    updateUI();
                    fragment.findViewById(R.id.play).setBackgroundResource(R.drawable.pause_button);
                }
            });
            queue.addView(line);
        }
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

    Runnable changeCurrentTime = new Runnable() {
        @Override
        public void run() {
            TextView currentTime = fragment.findViewById(R.id.tv_song_time);
            currentTime.setText(lengthToString(QueueController.getCurrentPosition()/1000));
        }
    };

    public void prepareSong(){ //подготавливает следующую песню к запуску
        TextView songLength = fragment.findViewById(R.id.tv_song_length);
        songLength.setText(lengthToString(
                QueueController.getSongFromQueue(QueueController.getNomPlaying()).length));
        seekBarUpdate = new Thread(new Runnable() {    //обновление прогресса песни. Чтобы ползунок двигался
            @Override
            public void run() {     //обновление полосы проигранной части песни
                SeekBar sb = fragment.findViewById(R.id.seek_bar);
                Song song = QueueController.getSongFromQueue(QueueController.getNomPlaying());
                sb.setProgress(0);
                while (sb.getProgress()<100){
                    try {
                        if (QueueController.getNomPlaying()>=QueueController.getQueueLength())
                            return;
                        if (QueueController.getSongFromQueue(QueueController.getNomPlaying())!=song)
                            return;
                        sb.setProgress(Math.round((QueueController.getCurrentPosition()/1000f)/song.length*sb.getMax()));
                        getActivity().runOnUiThread(changeCurrentTime);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}