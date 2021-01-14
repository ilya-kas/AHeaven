package com.AHeaven.playing;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import com.AHeaven.MainActivity;
import com.AHeaven.R;

/**
 * https://habr.com/ru/post/339416/
 * https://github.com/SergeyVinyar/AndroidAudioExample/blob/master/app/src/main/java/ru/vinyarsky/androidaudioexample/service/PlayerService.java
 */

public class MyService extends Service {

    private static final int NOTIFICATION_ID = 123;
    final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(
                            PlaybackStateCompat.ACTION_PLAY
                            | PlaybackStateCompat.ACTION_PAUSE
                            | PlaybackStateCompat.ACTION_STOP
                            | PlaybackStateCompat.ACTION_PLAY_PAUSE
                            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);

    private AudioManager audioManager;
    MediaSessionCompat mediaSession;
    private final MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();

    MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        int currentState = PlaybackStateCompat.STATE_STOPPED;

        @Override
        public void onPlay() {
            startService(new Intent(getApplicationContext(), MyService.class));
            int audioFocusResult = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                return;

            updateMetadataFromSong(User.getFromQueue(User.nomPlaying));// Заполняем данные о треке
            mediaSession.setActive(true);// Указываем, что наше приложение теперь активный плеер и кнопки. на окне блокировки должны управлять именно нами

            mediaSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());  // Сообщаем новое состояние
            currentState = PlaybackStateCompat.STATE_PLAYING;

            if (!User.q.isNowPlaying)
                User.q.playButtonClick();
        }

        @Override
        public void onPause() {
            mediaSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build()); // Сообщаем новое состояние
            currentState = PlaybackStateCompat.STATE_PAUSED;

            if (User.q.isNowPlaying)
                User.q.playButtonClick();
        }

        @Override
        public void onStop() {
            User.q.pauseSong();

            audioManager.abandonAudioFocus(audioFocusChangeListener);

            mediaSession.setActive(false);
            mediaSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            currentState = PlaybackStateCompat.STATE_STOPPED;

            refreshNotificationAndForegroundStatus(currentState);

            stopSelf();
        }


        @Override
        public void onSkipToNext() {
            User.q.moveToNext();
            updateMetadataFromSong(User.getFromQueue(User.nomPlaying));

            refreshNotificationAndForegroundStatus(currentState);
        }

        @Override
        public void onSkipToPrevious() {
            User.q.moveToPrev();
            updateMetadataFromSong(User.getFromQueue(User.nomPlaying));

            refreshNotificationAndForegroundStatus(currentState);
        }

        private void updateMetadataFromSong(Song song) {
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.name);
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.author);
            metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.length*1000);
            mediaSession.setMetadata(metadataBuilder.build());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mediaSession = new MediaSessionCompat(this, "PlayerService");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(mediaSessionCallback);

        Context appContext = getApplicationContext();
        Intent activityIntent = new Intent(appContext, MainActivity.class);
        mediaSession.setSessionActivity(PendingIntent.getActivity(appContext, 0, activityIntent, 0));
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null,  appContext, MediaButtonReceiver.class);
        mediaSession.setMediaButtonReceiver(PendingIntent.getBroadcast(appContext, 0, mediaButtonIntent, 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        User.q.pauseSong();
        mediaSession.release();
        audioManager.abandonAudioFocus(audioFocusChangeListener);
    }

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (User.q.isNowPlaying)
                        mediaSessionCallback.onPlay(); // Не очень красиво
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: //уведомление или другое "выделение"
                    mediaSessionCallback.onPause();
                    break;
                default:  //забрали доступ
                    mediaSessionCallback.onPause();
                    break;
            }
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerServiceBinder();
    }

    public class PlayerServiceBinder extends Binder {
        public MediaSessionCompat.Token getMediaSessionToken() {
            return mediaSession.getSessionToken();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    void refreshNotificationAndForegroundStatus(int playbackState) {
        switch (playbackState) {
            case PlaybackStateCompat.STATE_PLAYING:
                startForeground(NOTIFICATION_ID, getNotification(playbackState));
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                // На паузе мы перестаем быть foreground, однако оставляем уведомление, чтобы пользователь мог нажать play
                NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, getNotification(playbackState));
                stopForeground(false);
                break;
            default:
                stopForeground(true); // Все, можно прятать уведомление
                break;
        }
    }

    Notification getNotification(int playbackState) {
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mediaSession);

        // Добавляем кнопки
        // ...на предыдущий трек
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_previous, "Previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));

        // ...play/pause
        if (playbackState == PlaybackStateCompat.STATE_PLAYING)
            builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        else
            builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));

        // ...на следующий трек
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_next, "Next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));

        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                // В компактном варианте показывать Action с данным порядковым номером.
                // В нашем случае это play/pause.
                .setShowActionsInCompactView(1)
                // Отображать крестик в углу уведомления для его закрытия.
                // Это связано с тем, что для API < 21 из-за ошибки во фреймворке
                // пользователь не мог смахнуть уведомление foreground-сервиса даже после вызова stopForeground(false).
                // Так что это костыль. На API >= 21 крестик не отображается, там просто смахиваем уведомление.
                .setShowCancelButton(true)
                // Указываем, что делать при нажатии на крестик или смахивании
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP))
                // Передаем токен. Это важно для Android Wear. Если токен не передать,
                // кнопка на Android Wear будет отображаться, но не будет ничего делать
                .setMediaSession(mediaSession.getSessionToken()));

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        // Не отображать время создания уведомления. В нашем случае это не имеет смысла
        builder.setShowWhen(false);
        // Это важно. Без этой строчки уведомления не отображаются на Android Wear и криво отображаются на самом телефоне.
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setOnlyAlertOnce(true);// Не надо каждый раз вываливать уведомление на пользователя

        return builder.build();
    }
}
