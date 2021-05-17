package com.AHeaven.ui.tabs.DragNDrop;

import android.app.Dialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AHeaven.R;
import com.AHeaven.playing.Playlist;
import com.AHeaven.playing.QueueController;
import com.AHeaven.playing.Song;
import com.AHeaven.playing.User;
import com.AHeaven.ui.tabs.BoxFragment;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemViewHolder> implements MovingAdapter {

    BoxFragment fragment;
    View finder;

    public PlaylistAdapter(BoxFragment fragment, View finder) {
        this.fragment = fragment;
        this.finder = finder;
    }

    public void update(){
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View line = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_song_line,parent,false);
        return new PlaylistAdapter.ItemViewHolder(line);
    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        LinearLayout line = holder.row;
        holder.nom = position;
        final Song current = fragment.getPlaylist().getSong(position);

        TextView TVsongName = line.findViewById(R.id.song_name);        //надпись конкретной песни
        TVsongName.setText(current.name);
        TextView author = line.findViewById(R.id.song_author);
        author.setText(current.author);

        final ImageButton plus = line.findViewById(R.id.plus_button); //кнопка добавить песню в конец очереди
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueueController.addToQueue(current);
            }
        });

        String text;
        TextView tv_Length = line.findViewById(R.id.tv_length);
        if (current.length%60<10)
            text = current.length/60+":0"+current.length%60;
        else
            text = current.length/60+":"+current.length%60;
        tv_Length.setText(text);

        final ImageButton dots = line.findViewById(R.id.dots_button);
        dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(fragment.getContext(),dots);
                popupMenu.inflate(R.menu.song_popupmenu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.remove_song:
                                fragment.getPlaylist().removeSong(position);
                                fragment.updateUI();
                                return true;
                            case R.id.add_song_to_another:
                                PopupMenu playlistsMenu = new PopupMenu(fragment.getContext(),dots);
                                playlistsMenu.inflate(R.menu.playlists_popupmenu);
                                for (int i = 0; i< User.playlistCount; i++)
                                    playlistsMenu.getMenu().add(User.getPlaylist(i).name);
                                playlistsMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        for (int i=0;i<User.playlistCount;i++) {
                                            Playlist list = User.getPlaylist(i);
                                            if (list.name == item.getTitle()) {
                                                list.addSong(current);
                                                break;
                                            }
                                        }
                                        return true;
                                    }
                                });
                                playlistsMenu.show();
                                return true;
                            case R.id.change_song:
                                final Dialog addSong = new Dialog(fragment.getContext());         //диалог получения данных о песне
                                addSong.setContentView(R.layout.sond_additing_dialog);
                                final EditText et_name = addSong.findViewById(R.id.et_name);
                                final EditText et_author = addSong.findViewById(R.id.et_author);

                                et_name.setText(current.name);
                                et_author.setText(current.author);

                                addSong.show();

                                Button add = addSong.findViewById(R.id.b_add);
                                add.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        current.name = et_name.getText().toString().trim();
                                        current.author = et_author.getText().toString().trim();

                                        fragment.updateUI();
                                        addSong.dismiss();
                                    }
                                });
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fragment.getPlaylist().getSize();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Song moving = fragment.getPlaylist().getSong(fromPosition);
        fragment.getPlaylist().removeSong(fromPosition);
        fragment.getPlaylist().addSong(fromPosition < toPosition ? toPosition - 1 : toPosition,moving);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {}

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements MovingViewStateAdapter {

        public LinearLayout row;
        public int nom;

        public ItemViewHolder(View itemView) {
            super(itemView);
            row = itemView.findViewById(R.id.line);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemReleased() {

        }
    }
}