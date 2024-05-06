package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import adapter.SongAdapter;
import models.SongModel;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private List<SongModel> songList;
    private List<SongModel> filteredList;
    private FirebaseFirestore firestore;
    private EditText searchEditText;
    private Button homeNav;
    private Button playlistNav;

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

        firestore = FirebaseFirestore.getInstance();

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
                Intent intent = new Intent(SearchActivity.this, PlaylistActivity.class);
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
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
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
}
