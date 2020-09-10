package com.AHeaven.ui.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.AHeaven.MainActivity;
import com.AHeaven.R;
import com.AHeaven.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * класс фрагмента выбора плейлистов
 */
public class PlaylistFragment extends Fragment {

    public static PlaylistFragment newInstance() {
        PlaylistFragment fragment = new PlaylistFragment();
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

        View fragmentView = inflater.inflate(R.layout.playlists_fragment, container, false);

        FloatingActionButton fbShare = fragmentView.findViewById(R.id.fab);       //всплывающая кнопка добавления плейлиста или песни
        fbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Soon it will add something:)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TableLayout layout = fragmentView.findViewById(R.id.table_layout);
        for (int i=0;i< User.playlistCount;i+=2){                 //добавляю плейлисты
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            row.setBackgroundResource(R.drawable.shelf);
            row.setPadding(MainActivity.DPtoPX(7),MainActivity.DPtoPX(16),0,0);

            ImageButton left = new ImageButton(getContext());    //левый плейлист в строке
            left.setBackground(null);
            left.setImageResource(R.drawable.playlist);
            left.setScaleType(ImageView.ScaleType.CENTER);
            row.addView(left);           //добавляем плейлист

            final int finalI = i;
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_first,BoxFragment.newInstance(User.getPlaylist(finalI)),"box")
                            .addToBackStack(null)
                            .commit();
                    ((MainActivity)getActivity()).setLast(false);
                }
            });


            if (i+1<User.playlistCount){                        //правый плейлист в строке
                ImageButton right = new ImageButton(getContext());
                right.setBackground(null);
                right.setImageResource(R.drawable.playlist);
                right.setScaleType(ImageView.ScaleType.CENTER);
                row.addView(right); //добавляем плейлист
                
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container_first,BoxFragment.newInstance(User.getPlaylist(finalI+1)),"box")
                                .addToBackStack(null)
                                .commit();
                        ((MainActivity)getActivity()).setLast(false);
                    }
                });
            }

            layout.addView(row); //добавляем полку
        }
        for (int i=User.playlistCount;i<7;i+=2){ //добавляю пустые полки, чтобы не было пустого места внизу
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            row.setBackgroundResource(R.drawable.shelf);
            row.setPadding(MainActivity.DPtoPX(25),MainActivity.DPtoPX(16),0,0);
            layout.addView(row);
        }
        return fragmentView;
    }
}
