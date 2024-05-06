package adapter;

import adapter.SongsListAdapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicapp.MyExoplayer;
import com.example.musicapp.PlayerActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import models.SongModel;
import com.example.musicapp.databinding.SongListItemRecyclerRowBinding;


public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.MyViewHolder> {
    private final List<String> songIdList;

    public SongsListAdapter(List<String> songIdList) {
        this.songIdList = songIdList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final SongListItemRecyclerRowBinding binding;

        public MyViewHolder(SongListItemRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(String songId) {
            FirebaseFirestore.getInstance().collection("songs")
                    .document(songId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            SongModel song = documentSnapshot.toObject(SongModel.class);
                            if (song != null) {
                                binding.songTitleTextView.setText(song.getTitle());
                                binding.songSubtitleTextView.setText(song.getSubtitle());
                                Glide.with(binding.songCoverImageView)
                                        .load(song.getCoverUrl())
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                                        .into(binding.songCoverImageView);
                                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MyExoplayer.startPlaying(binding.getRoot().getContext(), song);
                                        v.getContext().startActivity(new Intent(v.getContext(), PlayerActivity.class));
                                    }

                                });
                            }
                        }
                    });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SongListItemRecyclerRowBinding binding = SongListItemRecyclerRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(songIdList.get(position));
    }

    @Override
    public int getItemCount() {
        return songIdList.size();
    }
}