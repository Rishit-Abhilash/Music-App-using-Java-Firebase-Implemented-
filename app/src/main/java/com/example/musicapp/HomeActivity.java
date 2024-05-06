package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapp.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import adapter.CategoryAdapter;
import models.CategoryModel;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.view.MenuItem;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapp.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.List;

import adapter.CategoryAdapter;
import models.CategoryModel;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private CategoryAdapter CategoryAdapter;
    private FirebaseAuth mAuth;
    private Button SearchNav;
    private Button PlaylistNav;
    private ImageView Logo;
    private Button Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        Button Logout = findViewById(R.id.searchNav);

//        binding.searchNav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        binding.searchNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        binding.playlistNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,PlaylistActivity.class);
                startActivity(intent);
            }
        });


        getCategories();
    }
    private void getCategories() {
        FirebaseFirestore.getInstance().collection("Category")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<CategoryModel> categoryList = queryDocumentSnapshots.toObjects(CategoryModel.class);
                        setupCategoryRecyclerView(categoryList);
                    }
                });
    }
    private void setupCategoryRecyclerView(List<CategoryModel> categoryList) {
        CategoryAdapter = new CategoryAdapter(categoryList);
        binding.categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.categoriesRecyclerView.setAdapter(CategoryAdapter);
    }

}