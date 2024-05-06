package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText playlistNameEditText;
    private ImageView playlistImageView;
    private ImageButton selectImageButton, addPlaylistButton;
    private RecyclerView playlistsRecyclerView;
    private List<Playlist> playlistsList;
    private PlaylistsAdapter playlistsAdapter;
    private ImageButton homeNav, searchNav;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference playlistImagesRef;
    private FirebaseUser currentUser;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        playlistImagesRef = FirebaseStorage.getInstance().getReference().child("playlist_images");

        playlistNameEditText = findViewById(R.id.playlistNameEditText);
        playlistImageView = findViewById(R.id.playlistImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        addPlaylistButton = findViewById(R.id.addPlaylistButton);
        playlistsRecyclerView = findViewById(R.id.playlistsRecyclerView);
        homeNav = findViewById(R.id.homeNav);
        searchNav = findViewById(R.id.searchNav);

        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistsActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        searchNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistsActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
        currentUser = mAuth.getCurrentUser();

        // Setup RecyclerView
        playlistsList = new ArrayList<>();
        playlistsAdapter = new PlaylistsAdapter(this, playlistsList);
        playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlistsRecyclerView.setAdapter(playlistsAdapter);

        // Load user's playlists
        loadPlaylists();

        selectImageButton.setOnClickListener(v -> openFileChooser());
        addPlaylistButton.setOnClickListener(v -> addPlaylist());

        // Set item click listener for playlistsRecyclerView
        playlistsAdapter.setOnItemClickListener(position -> {
            Playlist selectedPlaylist = playlistsList.get(position);
            Intent intent = new Intent(PlaylistsActivity.this, PlaylistSongsActivity.class);
            intent.putExtra("playlistId", selectedPlaylist.getId());
            startActivity(intent);
        });
    }

    private void loadPlaylists() {
        usersRef.child(currentUser.getUid()).child("playlists")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        playlistsList.clear();
                        for (DataSnapshot playlistSnapshot : dataSnapshot.getChildren()) {
                            Playlist playlist = playlistSnapshot.getValue(Playlist.class);
                            playlistsList.add(playlist);
                        }
                        playlistsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                    .into(playlistImageView);
        }
    }

    private void addPlaylist() {
        String playlistName = playlistNameEditText.getText().toString().trim();
        if (playlistName.isEmpty()) {
            Toast.makeText(this, "Please enter a playlist name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image for the playlist", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileReference = playlistImagesRef.child(currentUser.getUid() + "_" + System.currentTimeMillis() + ".jpg");
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        String playlistId = usersRef.child(currentUser.getUid()).child("playlists").push().getKey();
                        Playlist playlist = new Playlist(playlistId, playlistName, imageUrl);
                        usersRef.child(currentUser.getUid()).child("playlists").child(playlistId).setValue(playlist);
                        Toast.makeText(this, "Playlist added successfully", Toast.LENGTH_SHORT).show();
                        playlistNameEditText.setText("");
                        playlistImageView.setImageResource(R.drawable.defaultplaylist);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful upload
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    public static class Playlist {
        private String id;
        private String name;
        private String imageUrl;

        public Playlist() {
            // Default constructor required for Firebase
        }

        public Playlist(String id, String name, String imageUrl) {
            this.id = id;
            this.name = name;
            this.imageUrl = imageUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder> {

        private Context context;
        private List<Playlist> playlistsList;
        private OnItemClickListener clickListener;

        public PlaylistsAdapter(Context context, List<Playlist> playlistsList) {
            this.context = context;
            this.playlistsList = playlistsList;
        }

        public void setOnItemClickListener(OnItemClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
            return new PlaylistViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
            Playlist playlist = playlistsList.get(position);
            holder.playlistNameTextView.setText(playlist.getName());
            Glide.with(context)
                    .load(playlist.getImageUrl())
                    .into(holder.playlistItemImageView);

            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return playlistsList.size();
        }

        public class PlaylistViewHolder extends RecyclerView.ViewHolder {
            ImageView playlistItemImageView;
            TextView playlistNameTextView;

            public PlaylistViewHolder(@NonNull View itemView) {
                super(itemView);
                playlistItemImageView = itemView.findViewById(R.id.playlistItemImageView);
                playlistNameTextView = itemView.findViewById(R.id.playlistItemNameTextView);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
