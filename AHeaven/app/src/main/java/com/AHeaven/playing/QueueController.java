package com.AHeaven.playing;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.fragment.app.FragmentManager;

import com.AHeaven.MainActivity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *класс, отвечающий за работу с бэкэндом очереди
 */
public class QueueController implements MediaPlayer.OnCompletionListener {
    private static List<Song> queue;
    private static int nomPlaying;
    private static boolean isNowPlaying,prepared;
    private static MediaPlayer player;
    private static Context context;

    public static void init(Context context){
        queue = new LinkedList<>();
        player = new MediaPlayer();

        nomPlaying = -1;
        isNowPlaying = false;
        prepared = false;

        QueueController.context = context;
    }

    /**
     * геттеры и сеттеры
     */
    public static int getNomPlaying() {
        return nomPlaying;
    }

    public static boolean isNowPlaying() {
        return isNowPlaying;
    }

    //вернёт в ms
    public static int getCurrentPosition(){
        if (nomPlaying==-1)
            return 0;
        else
            return player.getCurrentPosition();
    }

    public static Song getSongFromQueue(int nom){
        return queue.get(nom);
    }

    public static int getQueueLength(){
        return queue.size();
    }

    /**
     * методы управления воспроизведением
     */
    public static void play(){
        if (isNowPlaying) return;
        if (MainActivity.mediaController != null)
            MainActivity.mediaController.getTransportControls().play();  //состояние для уведомлений
        if (nomPlaying==-1) return;
        if (!prepared)
            prepareSong();
        player.start();
        isNowPlaying = true;
    }

    public static void pause(){
        if (!isNowPlaying) return;
        if (MainActivity.mediaController != null)
            MainActivity.mediaController.getTransportControls().pause();
        if (nomPlaying==-1) return;
        player.pause();
        isNowPlaying = false;
    }

    public static void stop(){
        pause();
        player.stop();
        prepared = false;
    }

    public static void moveToPrev(){
        if (nomPlaying==-1) return;
        nomPlaying--;
        if (nomPlaying<0)
            nomPlaying=getQueueLength()-1;
        prepareSong();
        isNowPlaying = false;
        play();
    }

    public static void moveToNext(){
        if (nomPlaying==-1) return;
        nomPlaying++;
        nomPlaying%=getQueueLength();
        prepareSong();
        isNowPlaying = false;
        play();
    }

    public static void moveTo(int i){
        nomPlaying = i;
        prepareSong();
        isNowPlaying = false;
        play();
        if (MainActivity.mediaController != null)
            MainActivity.mediaController.getTransportControls().play();
    }

    public static void clear(){
        queue.clear();
        nomPlaying = -1;
        pause();
        prepared = false;
    }

    public static void shuffle(){ //перетасовка очереди
        Random rand = new Random();
        int j;
        for (int i=1;i<queue.size();i++){
            j = i-rand.nextInt(i+1);
            queue.add(j,queue.remove(i));
        }
        pause();
        prepareSong();
    }

    /**
     * перематывает песню, которая сейчас играется
     * @param part процент от всей длины трека, на который надо поставить
     */
    public static void seekTo(float part){
        player.seekTo(Math.round(player.getDuration()*part));
    }

    public static void prepareSong(){ //подготавливает следующую песню к запуску
        if (nomPlaying==-1)
            return;
        player.release();
        player = MediaPlayer.create(context,queue.get(nomPlaying).source);
        player.setOnCompletionListener(new QueueController());

        //Fragment queueFragment = fragmentManager.findFragmentById(QueueFragment.ID);
        User.q.prepareSong();

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                User.q.seekBarUpdate.start();
            }
        });
        prepared = true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nomPlaying++;
        if (queue.size()>0)
            nomPlaying%=queue.size();
        else
            nomPlaying = -1;
        User.q.updateUI();
        if (nomPlaying==-1) return;
        prepareSong();
        player.start();
        if (MainActivity.mediaController != null)
            MainActivity.mediaController.getTransportControls().play();
    }

    /**
     * функции изменения содержимого очереди
     */
    public static void addToQueue(Song song){
        if (nomPlaying<0)
            nomPlaying = 0;
        queue.add(song);
        User.q.updateUI();
        if (queue.size()==1)
            User.q.prepareSong();
    }

    public static void addToQueue(Song[] songs){
        if (songs.length==0)
            return;
        if (nomPlaying<0)
            nomPlaying = 0;
        queue.addAll(Arrays.asList(songs));
        User.q.updateUI();
        if (queue.size()==songs.length)
            User.q.prepareSong();
    }

    public static void addToQueue(Playlist list){
        if (list.getSize()==0)
            return;
        if (nomPlaying<0)
            nomPlaying = 0;
        queue.addAll(Arrays.asList(list.getSongs()));
        User.q.updateUI();
        if (queue.size()==list.getSize())
            User.q.prepareSong();
    }

    public static void removeFromQueue(int nom){
        boolean flag = nom==nomPlaying;

        queue.remove(nom);
        if (nom<QueueController.nomPlaying)
            nomPlaying = nomPlaying-1;
        if (nomPlaying>=queue.size())
            nomPlaying = queue.size()-1;
        if (queue.size()==0){
            player.stop();
            isNowPlaying = false;
        }

        if (flag) {
            prepareSong();
            play();
        }
    }
}
