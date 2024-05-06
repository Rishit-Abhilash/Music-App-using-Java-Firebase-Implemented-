package com.example.musicapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicapp.databinding.ActivityPlayerBinding;

import models.SongModel;

public class PlayerActivity extends AppCompatActivity {

    private ActivityPlayerBinding binding;
    private ExoPlayer exoPlayer;

//    private Player.Listener playerListener = new Player.Listener() {
//        @Override
//        public void onIsPlayingChanged(boolean isPlaying) {
//            Player.Listener.super.onIsPlayingChanged(isPlaying);
//            showGif(isPlaying);
//        }
//    };

    @UnstableApi @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SongModel song = MyExoplayer.getCurrentSong();
        if (song != null) {
            binding.songTitleTextView.setText(song.getTitle());
            binding.songSubtitleTextView.setText(song.getSubtitle());
            Glide.with(binding.songCoverImageView)
                    .load(song.getCoverUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                    .into(binding.songCoverImageView);
//            Glide.with(binding.songGifImageView).load(R.drawable.media_playing)
//                    .circleCrop()
//                    .into(binding.songGifImageView);
            exoPlayer = MyExoplayer.getInstance();
            binding.playerView.setPlayer(exoPlayer);
            binding.playerView.showController();
//            exoPlayer.addListener(playerListener);
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (exoPlayer != null) {
//            exoPlayer.removeListener(playerListener);
//        }
//    }
//
    private void showGif(boolean show) {
        if (show) {
            binding.songGifImageView.setVisibility(View.VISIBLE);
        } else {
            binding.songGifImageView.setVisibility(View.INVISIBLE);
        }
    }
}

