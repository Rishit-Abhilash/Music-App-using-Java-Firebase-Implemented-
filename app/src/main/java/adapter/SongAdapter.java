package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.MyExoplayer;
import com.example.musicapp.PlayerActivity;
import com.example.musicapp.R;

import java.util.List;

import models.SongModel;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<SongModel> songList;
    private Context context;


    public SongAdapter(Context context, List<SongModel> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item_recycler_row, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongModel song = songList.get(position);

        // Load cover image
        Glide.with(context)
                .load(song.getCoverUrl())
                .into(holder.coverImageView);

        // Set title and subtitle
        holder.titleTextView.setText(song.getTitle());
        holder.subtitleTextView.setText(song.getSubtitle());

        // Set OnClickListener on the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyExoplayer.startPlaying(context, song);
                // Handle click event, for example, start PlayerActivity
                Intent intent = new Intent(context, PlayerActivity.class);
                // Pass data to PlayerActivity if needed

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView subtitleTextView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.song_cover_image_view);
            titleTextView = itemView.findViewById(R.id.song_title_text_view);
            subtitleTextView = itemView.findViewById(R.id.song_subtitle_text_view);
        }
    }
}