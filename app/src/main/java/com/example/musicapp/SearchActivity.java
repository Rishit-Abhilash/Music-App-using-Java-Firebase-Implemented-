package com.example.musicapp;
//
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import adapter.SongAdapter;
import models.SongModel;
//
//public class SearchActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private SongAdapter adapter;
//    private List<SongModel> songList;
//    private List<SongModel> filteredList;
//    private FirebaseFirestore firestore;
//    private EditText searchEditText;
//    private TextView text;
//    private ImageButton homeNav;
//    private ImageButton playlistNav;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//
//        recyclerView = findViewById(R.id.search_list_recycler_view);
//        searchEditText = findViewById(R.id.search_bar1);
//        text = findViewById(R.id.Hint);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        songList = new ArrayList<>();
//        filteredList = new ArrayList<>();
//        adapter = new SongAdapter(this, filteredList);
//        recyclerView.setAdapter(adapter);
//
//        firestore = FirebaseFirestore.getInstance();
//
//        homeNav = findViewById(R.id.homeNav);
//        homeNav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        playlistNav = findViewById(R.id.playlistNav);
//        playlistNav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SearchActivity.this, PlaylistActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        loadSongsFromFirestore();
//        setupSearchBar();
//    }
//
//    private void setupSearchBar() {
//        searchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                filterSongs(charSequence.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (editable.toString().isEmpty()) {
//                    recyclerView.setVisibility(View.INVISIBLE);
//                    text.setVisibility(View.VISIBLE);
//                } else {
//                    recyclerView.setVisibility(View.VISIBLE);
//                    text.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//    }
//
//    private void filterSongs(String searchText) {
//        filteredList.clear();
//
//        for (SongModel song : songList) {
//            if (song.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
//                filteredList.add(song);
//            }
//        }
//        adapter.notifyDataSetChanged();
//    }
//
//    private void loadSongsFromFirestore() {
//        firestore.collection("songs").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    SongModel song = document.toObject(SongModel.class);
//                    songList.add(song);
//                }
//                filteredList.addAll(songList);
//                adapter.notifyDataSetChanged();
//            } else {
//                Log.e("SearchActivity", "Error getting documents: ", task.getException());
//            }
//        });
//    }


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

import models.SongModel;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private List<SongModel> songList;
    private List<SongModel> filteredList;
    private FirebaseFirestore firestore;
    private EditText searchEditText;
    private ImageButton homeNav;
    private ImageButton playlistNav;
    private TextView text;
    private ArrayAdapter<String> arrayAdapter;

    private ArrayList<String> playlistNames = new ArrayList<>();

    private ArrayList<String> playlistIds = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.search_list_recycler_view);
        searchEditText = findViewById(R.id.search_bar1);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new SongAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);
        text = findViewById(R.id.Hint);

        firestore = FirebaseFirestore.getInstance();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playlistNames);

        homeNav = findViewById(R.id.homeNav);
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        playlistNav = findViewById(R.id.playlistNav);
        playlistNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, PlaylistsActivity.class);
                startActivity(intent);
            }
        });

        loadSongsFromFirestore();
        setupSearchBar();
    }

    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterSongs(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    text.setVisibility(View.VISIBLE);

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    text.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void filterSongs(String searchText) {
        filteredList.clear();

        for (SongModel song : songList) {
            if (song.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(song);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadSongsFromFirestore() {
        firestore.collection("songs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    SongModel song = document.toObject(SongModel.class);
                    songList.add(song);
                }
                filteredList.addAll(songList);
                adapter.notifyDataSetChanged();
            } else {
                Log.e("SearchActivity", "Error getting documents: ", task.getException());
            }
        });
    }


    //    Adapter
    public static class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
        private List<SongModel> songList;
        private Context context;


        public SongAdapter(Context context, List<SongModel> songList) {
            this.context = context;
            this.songList = songList;
        }

        @NonNull
        @Override
        public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.song_list_item_recycler_row, parent, false);
            return new SongViewHolder(view);
        }

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

            // Set OnLongClickListener on the item view
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // Create an instance of SearchActivity to call the method
                    SearchActivity searchActivity = (SearchActivity) context;
                    searchActivity.showPlaylistDialog(song);
                    return true;
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

    private void showPlaylistDialog(SongModel song) {
        // Load playlist names before showing the dialog
        loadPlaylistNames();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add to Playlist");

        ListView listView = new ListView(this);
        listView.setAdapter(arrayAdapter);
        builder.setView(listView);

        final AlertDialog dialog = builder.create();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedPlaylistId = playlistIds.get(i); // Get the ID of the selected playlist
                String selectedPlaylistName = playlistNames.get(i); // Get the name of the selected playlist
                // Add the song to the selected playlist
                addSongToPlaylist(song, selectedPlaylistId, selectedPlaylistName);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadPlaylistNames() {
        // Get the reference to the playlists for the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference playlistsRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(currentUser.getUid())
                    .child("playlists");

            // Listen for changes in the playlists node
            playlistsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Clear the playlist names and IDs lists
                    playlistNames.clear();
                    playlistIds.clear();

                    // Add each playlist name and ID to the lists
                    for (DataSnapshot playlistSnapshot : dataSnapshot.getChildren()) {
                        String playlistName = playlistSnapshot.child("name").getValue(String.class);
                        String playlistId = playlistSnapshot.getKey();
                        if (playlistName != null && playlistId != null) {
                            playlistNames.add(playlistName);
                            playlistIds.add(playlistId);
                        }
                    }

                    // Update the ArrayAdapter for the playlist dialog
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Log.e("SearchActivity", "Error loading playlist names", databaseError.toException());
                }
            });
        }
    }

    private void addSongToPlaylist(SongModel song, String playlistId, String playlistName) {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the current user is authenticated
        if (currentUser != null) {
            // Get the reference to the playlist for the current user
            DatabaseReference playlistRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(currentUser.getUid())
                    .child("playlists")
                    .child(playlistId)
                    .child("songs")
                    .child(song.getId()); // Use the song ID as the key

            // Push the song details to the playlist
            playlistRef.setValue(song)
                    .addOnSuccessListener(aVoid -> {
                        // Song added successfully
                        Toast.makeText(SearchActivity.this, "Song added to playlist: " + playlistName, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to add song to playlist
                        Toast.makeText(SearchActivity.this, "Failed to add song to playlist", Toast.LENGTH_SHORT).show();
                        Log.e("SearchActivity", "Error adding song to playlist", e);
                    });
        } else {
            // User is not authenticated
            Toast.makeText(SearchActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.e("SearchActivity", "User not authenticated");
        }
    }



}
