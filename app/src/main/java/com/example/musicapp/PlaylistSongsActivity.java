package com.example.musicapp;

import static android.view.LayoutInflater.*;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import models.SongModel;

public class PlaylistSongsActivity extends AppCompatActivity {

    private RecyclerView songsRecyclerView;
    private List<SongModel> songsList;
    private SongsAdapter songsAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference songsRef;
    private DatabaseReference playlistRef;
    private ImageButton homeNav,searchNav;
    private String playlistId;

    private TextView playlistNameTextView;
    private ImageView playlistImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        playlistId = getIntent().getStringExtra("playlistId");
        songsRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(currentUser.getUid()).child("playlists").child(playlistId).child("songs");
        playlistRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(currentUser.getUid()).child("playlists").child(playlistId);

        songsRecyclerView = findViewById(R.id.songsRecyclerView);
        songsList = new ArrayList<>();
        songsAdapter = new SongsAdapter(this, songsList);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songsRecyclerView.setAdapter(songsAdapter);

        playlistNameTextView = findViewById(R.id.playlistNameTextView);
        playlistImageView = findViewById(R.id.playlistImageView);



        loadPlaylistInfo();
        loadSongs();
    }

    private void loadPlaylistInfo() {
        playlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String playlistName = dataSnapshot.child("name").getValue(String.class);
                String playlistImageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                playlistNameTextView.setText(playlistName);
                Glide.with(PlaylistSongsActivity.this)
                        .load(playlistImageUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                        .into(playlistImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void loadSongs() {
        songsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                songsList.clear();
                for (DataSnapshot songSnapshot : dataSnapshot.getChildren()) {
                    SongModel song = songSnapshot.getValue(SongModel.class);
                    songsList.add(song);
                }
                songsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    public static class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {
        private List<SongModel> songsList;
        private Context context;

        public SongsAdapter(Context context, List<SongModel> songsList) {
            this.context = context;
            this.songsList = songsList;
        }

        @NonNull
        @Override
        public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(context).inflate(R.layout.playlist_song_item, parent, false);
            return new SongViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
            SongModel song = songsList.get(position);

            // Load cover image
            Glide.with(context)
                    .load(song.getCoverUrl())
                    .into(holder.coverImageView);

            // Set title and subtitle
            holder.titleTextView.setText(song.getTitle());
            holder.subtitleTextView.setText(song.getSubtitle());

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
            return songsList.size();
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
}
