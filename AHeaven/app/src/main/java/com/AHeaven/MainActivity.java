package com.AHeaven;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;

import com.AHeaven.playing.MyService;
import com.AHeaven.playing.QueueController;
import com.AHeaven.playing.User;
import com.AHeaven.ui.tabs.PlaylistsFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.TypedValue;

import com.AHeaven.ui.TabSelectionAdapter;

public class MainActivity extends FragmentActivity {
    static Resources r;
    ViewPager viewPager;
    public boolean wardrobeFragmentNow = true;
    boolean launch;
    MyService.PlayerServiceBinder serviceBinder;
    public static MediaControllerCompat mediaController;

    @Override
    protected void onStart() {
        super.onStart();

        if (launch){
            User.load(this); //загрузка данных о пользователе
            QueueController.init(getApplicationContext());
            launch = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        r = getResources();
        TabSelectionAdapter tabSelectionAdapter = new TabSelectionAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);//компонент, где показываются фрагменты вкладок
        viewPager.setAdapter(tabSelectionAdapter);

        TabLayout tabSelector = findViewById(R.id.tabs); //панель переключения вкладок
        tabSelector.setupWithViewPager(viewPager);
        launch = true;

        bindService(new Intent(this, MyService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                serviceBinder = (MyService.PlayerServiceBinder) service;
                try {
                    mediaController = new MediaControllerCompat(
                            MainActivity.this, serviceBinder.getMediaSessionToken());
                    mediaController.registerCallback(new MediaControllerCompat.Callback(){});
                }
                catch (RemoteException e) {
                    mediaController = null;
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                QueueController.stop();
                serviceBinder = null;
                mediaController = null;
            }
        }, BIND_AUTO_CREATE);
    }

    public void setWardrobeFragmentNow(boolean wardrobeFragmentNow) {
        this.wardrobeFragmentNow = wardrobeFragmentNow;
    }

    public static int DPtoPX(int dp){         //метод перевода px в dp
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return Math.round(px);
    }

    @Override
    public void onBackPressed() {
        if (wardrobeFragmentNow)
            super.onBackPressed();
        else{
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.container_first, PlaylistsFragment.newInstance()).commit();
            wardrobeFragmentNow = true;
        }
    }

    @Override
    protected void onStop() {
        User.save(this);
        super.onStop();
    }
}