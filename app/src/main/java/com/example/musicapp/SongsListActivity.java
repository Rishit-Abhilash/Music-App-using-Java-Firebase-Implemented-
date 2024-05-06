package com.example.musicapp;
import androidx.appcompat.app.AppCompatActivity;

import adapter.SongsListAdapter;
import models.CategoryModel;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicapp.databinding.ActivitySongsListBinding;

public class SongsListActivity extends AppCompatActivity {
    private static CategoryModel Category;
    private ActivitySongsListBinding binding;
    private SongsListAdapter songsListAdapter;

    public static void setCategory(CategoryModel categoryModel) {
        Category = categoryModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);

        binding = ActivitySongsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.nameTextView.setText(Category.getName());
        Glide.with(binding.coverImageView)
                .load(Category.getCoverUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                .into(binding.coverImageView);

        setupSongsListRecyclerView();
    }

    private void setupSongsListRecyclerView() {
        songsListAdapter = new SongsListAdapter(Category.getSongs());
        binding.songsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.songsListRecyclerView.setAdapter(songsListAdapter);
    }
}