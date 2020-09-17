package com.AHeaven.ui.tabs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.AHeaven.MainActivity;
import com.AHeaven.Playlist;
import com.AHeaven.R;
import com.AHeaven.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * класс фрагмента выбора плейлистов
 */
public class PlaylistFragment extends Fragment {
    View fragment;
    ViewGroup _container;

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

        fragment = inflater.inflate(R.layout.playlists_fragment, container, false);
        _container = container;

        final Dialog addPlaylist = new Dialog(getContext());
        addPlaylist.setContentView(R.layout.playlist_creation_dialog);
        Button create = addPlaylist.findViewById(R.id.b_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.addPlaylist(new Playlist(
                                            ((EditText) addPlaylist.findViewById(R.id.et_name)) .getText().toString()));
                updateUI();
                addPlaylist.dismiss();
            }
        });

        FloatingActionButton fbAdd = fragment.findViewById(R.id.fab);       //всплывающая кнопка добавления плейлиста или песни
        fbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlaylist.show();
            }
        });

        updateUI();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    void updateUI(){
        TableLayout layout = fragment.findViewById(R.id.table_layout);
        layout.removeAllViews();
        TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(MainActivity.DPtoPX(20), 0, 0, 0);

        for (int i = 0; i < User.playlistCount; i += 2) {                 //добавляю плейлисты
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            row.setBackgroundResource(R.drawable.shelf);
            row.setPadding(MainActivity.DPtoPX(20), MainActivity.DPtoPX(16), 0, 0);

            TextView left = new TextView(getContext());    //левый плейлист в строке
            left.setBackground(getResources().getDrawable(R.drawable.playlist));
            left.setText(User.getPlaylist(i).name);
            left.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            left.setTextColor(getResources().getColor(R.color.white));
            left.setPadding(MainActivity.DPtoPX(37), MainActivity.DPtoPX(97), 0, 0);
            row.addView(left);//добавляем плейлист

            final int finalI = i;
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_first, BoxFragment.newInstance(User.getPlaylist(finalI)), "box")
                            .addToBackStack(null)
                            .commit();
                    ((MainActivity) getActivity()).setLast(false);
                }
            });

            if (i + 1 < User.playlistCount) {                        //правый плейлист в строке
                TextView right = new TextView(getContext());    //левый плейлист в строке
                right.setBackground(getResources().getDrawable(R.drawable.playlist));
                right.setText(User.getPlaylist(i+1).name);
                right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                right.setTextColor(getResources().getColor(R.color.white));
                right.setPadding(MainActivity.DPtoPX(37), MainActivity.DPtoPX(97), 0, 0);
                right.setLayoutParams(params);
                row.addView(right); //добавляем плейлист

                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container_first, BoxFragment.newInstance(User.getPlaylist(finalI + 1)), "box")
                                .addToBackStack(null)
                                .commit();
                        ((MainActivity) getActivity()).setLast(false);
                    }
                });
            }

            layout.addView(row); //добавляем полку
        }
        for (int i = User.playlistCount; i < 7; i += 2) { //добавляю пустые полки, чтобы не было пустого места внизу
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            row.setBackgroundResource(R.drawable.shelf);
            row.setPadding(MainActivity.DPtoPX(25), MainActivity.DPtoPX(16), 0, 0);
            layout.addView(row);
        }
    }
}
