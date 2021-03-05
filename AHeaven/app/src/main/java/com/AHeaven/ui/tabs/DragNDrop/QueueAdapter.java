package com.AHeaven.ui.tabs.DragNDrop;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AHeaven.R;
import com.AHeaven.playing.QueueController;
import com.AHeaven.playing.Song;
import com.AHeaven.ui.tabs.QueueFragment;

import java.util.ArrayList;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ItemViewHolder> implements MovingAdapter {

    private final List<LinearLayout> rows = new ArrayList<>();
    QueueFragment fragment;
    View finder;

    public QueueAdapter(QueueFragment fragment, View finder) {
        this.fragment = fragment;
        this.finder = finder;
    }

    public void update(){
        rows.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View line = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_line,parent,false);
        return new ItemViewHolder(line);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        LinearLayout line = holder.row;
        holder.nom = position;
        Song current = QueueController.getSongFromQueue(position);
        if (position==QueueController.getNomPlaying())
            line.setBackgroundColor(Color.rgb(77, 182, 255));
        else
            line.setBackgroundColor(0);

        TextView tvSongName = line.findViewById(R.id.song_name);          //надпись конкретной песни
        tvSongName.setText(current.name);
        TextView author = line.findViewById(R.id.song_author);          //надпись конкретной песни
        author.setText(current.author);

        ImageButton minus = line.findViewById(R.id.delete_button);  //кнопка удаления из плейлиста
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueueController.removeFromQueue(position);
                if (QueueController.getQueueLength()==0)
                    finder.findViewById(R.id.play).setBackgroundResource(R.drawable.play_button);
                fragment.updateUI();
            }
        });

        TextView tv_Length = line.findViewById(R.id.tv_length);
        tv_Length.setText(fragment.lengthToString(current.length));

        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //при нажатии проигрывать именно эту
                QueueController.moveTo(position);
                fragment.updateUI();
                finder.findViewById(R.id.play).setBackgroundResource(R.drawable.pause_button);
            }
        });
        rows.add(line);
    }

    @Override
    public int getItemCount() {
        return QueueController.getQueueLength();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        LinearLayout movingLL = (LinearLayout) rows.remove(fromPosition);
        rows.add(fromPosition < toPosition ? toPosition - 1 : toPosition, movingLL);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        rows.remove(position);
        notifyItemRemoved(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements MovingViewStateAdapter {

        public LinearLayout row;
        public int nom;

        public ItemViewHolder(View itemView) {
            super(itemView);
            row = (LinearLayout) itemView.findViewById(R.id.line);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemReleased() {
            itemView.setBackgroundColor(0);
            if (nom==QueueController.getNomPlaying())
                itemView.setBackgroundColor(Color.rgb(77, 182, 255));
        }
    }
}
