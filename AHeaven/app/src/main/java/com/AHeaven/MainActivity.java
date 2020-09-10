package com.AHeaven;

import android.content.res.Resources;
import android.os.Bundle;

import com.AHeaven.ui.tabs.PlaylistFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.util.TypedValue;

import com.AHeaven.ui.TabSelectionAdapter;

public class MainActivity extends FragmentActivity {
    static Resources r;
    ViewPager viewPager;
    boolean last = true;

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

        User.load(this); //загрузка данных о пользователе
    }

    public void setLast(boolean last) {
        this.last = last;
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
        if (last)
            super.onBackPressed();
        else{
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.container_first, PlaylistFragment.newInstance()).commit();
            last = true;
        }
    }
}