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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.AHeaven.R;
import com.AHeaven.playing.QueueController;
import com.AHeaven.playing.Song;
import com.AHeaven.playing.User;
import com.AHeaven.ui.tabs.DragNDrop.QueueAdapter;
import com.AHeaven.ui.tabs.DragNDrop.TouchAdapter;

import java.util.LinkedList;
import java.util.List;

//класс фрагмента, который отображает очередь воспроизведения
public class QueueFragment extends Fragment{
    View fragment;
    public Thread seekBarUpdate;

    //часть для drag-n-drop
    private QueueAdapter queueAdapter;
    private ItemTouchHelper mItemTouchHelper;

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

        /*
         * эта часть нужна для реализации drag-n-drop
         */
        queueAdapter = new QueueAdapter(this,fragment);
        RecyclerView recyclerView = fragment.findViewById(R.id.queue);
        recyclerView.setAdapter(queueAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //привязываю свой обработчик нажатий к recycler view очереди
        ItemTouchHelper.Callback callback = new TouchAdapter(queueAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

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

        if (QueueController.getQueueLength()>0){  //очистка
            FrameLayout frameLayout = fragment.findViewById(R.id.tv_song_name);
            frameLayout.removeAllViews();
            frameLayout.addView(createSongNameAuthor(QueueController.getSongFromQueue(QueueController.getNomPlaying())));
        }

        queueAdapter.update();
    }

    public String lengthToString(int length){  //длину в секундах в строку mm:ss
        String text;
        if (length%60<10)
            text = length/60+":0"+length%60;
        else
            text = length/60+":"+length%60;
        return text;
    }

    private LinearLayout createSongNameAuthor(Song x){ //создаёт слой с названием песни и группы
        LinearLayout names = new LinearLayout(getContext());
        names.setOrientation(LinearLayout.VERTICAL);

        TextView TVsongName = new TextView(getContext());          //надпись конкретной песни
        TVsongName.setText(x.name);
        TVsongName.setTextSize(18);

        TextView author = new TextView(getContext());          //надпись конкретной песни
        author.setText(x.author);
        author.setTextSize(14);

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