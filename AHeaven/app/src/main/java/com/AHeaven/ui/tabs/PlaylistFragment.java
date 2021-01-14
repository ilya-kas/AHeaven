package com.AHeaven.ui.tabs;

import android.app.Dialog;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.AHeaven.MainActivity;
import com.AHeaven.playing.Playlist;
import com.AHeaven.R;
import com.AHeaven.playing.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * класс фрагмента выбора плейлистов
 */
public class PlaylistFragment extends Fragment {
    View fragment;
    ViewGroup _container;

    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
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
        final EditText name = addPlaylist.findViewById(R.id.et_name);
        name.setText("");
        Button create = addPlaylist.findViewById(R.id.b_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //кнопка в правом нижнем углу "добавить плейлист"
                String playlistName = name.getText().toString().trim();
                if (playlistName.equals(""))
                    return;
                User.addPlaylist(new Playlist(playlistName));
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
            int textSize = 15;
            left.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            left.setTextColor(getResources().getColor(R.color.white));

            switch (getLinesCount(left,(String) left.getText(),left.getTextSize())){     //отступы и размеры в зависимости от количества строк
                case 1:left.setPadding(MainActivity.DPtoPX(37), MainActivity.DPtoPX(99), 0, 0); break;
                case 2:left.setPadding(MainActivity.DPtoPX(33), MainActivity.DPtoPX(94), 0, 0); break;
                default: left.setPadding(MainActivity.DPtoPX(28), MainActivity.DPtoPX(95), 0, 0);
                        do {
                            left.setTextSize(TypedValue.COMPLEX_UNIT_SP,--textSize);
                        }while (getLinesCount(left, (String) left.getText(),left.getTextSize())>2);
            }
            left.setWidth(MainActivity.DPtoPX(150));
            left.setHeight(MainActivity.DPtoPX(150));
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
                    ((MainActivity) getActivity()).setWardrobeFragmentNow(false);
                }
            });

            if (i + 1 < User.playlistCount) {                        //правый плейлист в строке
                /*TextView right = new TextView(getContext());    //левый плейлист в строке
                right.setBackground(getResources().getDrawable(R.drawable.playlist));
                right.setText(User.getPlaylist(i+1).name);
                textSize = 15;
                right.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                right.setTextColor(getResources().getColor(R.color.white));

                switch (getLinesCount(right,(String) right.getText(),right.getTextSize())){     //отступы и размеры в зависимости от количества строк
                    case 1:right.setPadding(MainActivity.DPtoPX(37), MainActivity.DPtoPX(99), 0, 0); break;
                    case 2:right.setPadding(MainActivity.DPtoPX(33), MainActivity.DPtoPX(94), 0, 0); break;
                    default: right.setPadding(MainActivity.DPtoPX(28), MainActivity.DPtoPX(95), 0, 0);
                        do {
                            right.setTextSize(TypedValue.COMPLEX_UNIT_SP,--textSize);
                        }while (getLinesCount(right, (String) right.getText(),right.getTextSize())>2);
                }

                right.setLayoutParams(params);
                right.setWidth(MainActivity.DPtoPX(150));
                right.setHeight(MainActivity.DPtoPX(150));*/
                FrameLayout right = new FrameLayout(getContext());    //левый плейлист в строке
                TextView tVName = new TextView(getContext());
                right.addView(tVName);
                right.setBackground(getResources().getDrawable(R.drawable.playlist));

                tVName.setText(User.getPlaylist(i+1).name);
                textSize = 15;
                tVName.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                tVName.setTextColor(getResources().getColor(R.color.white));

                switch (getLinesCount(tVName,(String) tVName.getText(),tVName.getTextSize())){     //отступы и размеры в зависимости от количества строк
                    case 1:tVName.setPadding(MainActivity.DPtoPX(37), MainActivity.DPtoPX(99), 0, 0); break;
                    case 2:tVName.setPadding(MainActivity.DPtoPX(33), MainActivity.DPtoPX(94), 0, 0); break;
                    default: tVName.setPadding(MainActivity.DPtoPX(28), MainActivity.DPtoPX(95), 0, 0);
                        do {
                            tVName.setTextSize(TypedValue.COMPLEX_UNIT_SP,--textSize);
                        }while (getLinesCount(tVName, (String) tVName.getText(),tVName.getTextSize())>2);
                }

                //FrameLayout.LayoutParams fLSize = new FrameLayout.LayoutParams(450,450);
                //fLSize.setMargins(MainActivity.DPtoPX(20), 0, 0, 0);
                //right.setLayoutParams(fLSize);
                row.addView(right); //добавляем плейлист

                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container_first, BoxFragment.newInstance(User.getPlaylist(finalI + 1)), "box")
                                .addToBackStack(null)
                                .commit();
                        ((MainActivity) getActivity()).setWardrobeFragmentNow(false);
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

    int getLinesCount(TextView tv, String text, float size){  //считает количество строк в textview при размере шрифта size
        Paint paint = new Paint();
        paint.setTextSize(size);
        paint.setTypeface(Typeface.DEFAULT);

        float textViewWidthPx = MainActivity.DPtoPX(100);

        List<String> strings = splitWordsIntoStringsThatFit(text, textViewWidthPx, paint);
        tv.setText(TextUtils.join("\n", strings));

        int lineCount = strings.size();
        return lineCount;
    }


    /**
     * дальше идут методы, помогающие разбить текст на строки
     * чтобы посчитать длину
     */

    public List<String> splitWordsIntoStringsThatFit(String source, float maxWidthPx, Paint paint) {
        ArrayList<String> result = new ArrayList<>();

        ArrayList<String> currentLine = new ArrayList<>();

        String[] sources = source.split("\\s");
        for(String chunk : sources) {
            if(paint.measureText(chunk) < maxWidthPx) {
                processFitChunk(maxWidthPx, paint, result, currentLine, chunk);
            } else {
                //the chunk is too big, split it.
                List<String> splitChunk = splitIntoStringsThatFit(chunk, maxWidthPx, paint);
                for(String chunkChunk : splitChunk) {
                    processFitChunk(maxWidthPx, paint, result, currentLine, chunkChunk);
                }
            }
        }
        if(! currentLine.isEmpty()) {
            result.add(TextUtils.join(" ", currentLine));
        }
        return result;
    }

    /**
     * Splits a string to multiple strings each of which does not exceed the width
     * of maxWidthPx.
     */
    private List<String> splitIntoStringsThatFit(String source, float maxWidthPx, Paint paint) {
        if(TextUtils.isEmpty(source) || paint.measureText(source) <= maxWidthPx) {
            return Arrays.asList(source);
        }

        ArrayList<String> result = new ArrayList<>();
        int start = 0;
        for(int i = 1; i <= source.length(); i++) {
            String substr = source.substring(start, i);
            if(paint.measureText(substr) >= maxWidthPx) {
                //this one doesn't fit, take the previous one which fits
                String fits = source.substring(start, i - 1);
                result.add(fits);
                start = i - 1;
            }
            if (i == source.length()) {
                String fits = source.substring(start, i);
                result.add(fits);
            }
        }

        return result;
    }

    /**
     * Processes the chunk which does not exceed maxWidth.
     */
    private void processFitChunk(float maxWidth, Paint paint, ArrayList<String> result, ArrayList<String> currentLine, String chunk) {
        currentLine.add(chunk);
        String currentLineStr = TextUtils.join(" ", currentLine);
        if (paint.measureText(currentLineStr) >= maxWidth) {
            //remove chunk
            currentLine.remove(currentLine.size() - 1);
            result.add(TextUtils.join(" ", currentLine));
            currentLine.clear();
            //ok because chunk fits
            currentLine.add(chunk);
        }
    }
}
