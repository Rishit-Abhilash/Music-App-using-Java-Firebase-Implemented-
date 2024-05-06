package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapp.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.List;

import adapter.CategoryAdapter;
import models.CategoryModel;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityHomeBinding binding;
    private CategoryAdapter CategoryAdapter;
    private FirebaseAuth mAuth;
    private Button SearchNav;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private Button PlaylistNav;
    private ImageView Logo;
    private Button Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//      Navbar
        binding.navView.setNavigationItemSelectedListener(this);

        setSupportActionBar(toolbar);
        binding.navView.bringToFront();
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//      BottomBar
        binding.homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,HomeActivity.class);

            }
        });

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
                Intent intent = new Intent(HomeActivity.this,PlaylistsActivity.class);
                startActivity(intent);
            }
        });

        getUserData();
        getCategories();
    }


//  DrawerItems
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here
        int id = item.getItemId();

        if (id == R.id.Profile) {
            // Start ProfileActivity
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        } else if (id == R.id.settings) {
            // Start SettingsActivity
            startActivity(new Intent(HomeActivity.this, PlaylistActivity.class));
        }

        // Close the navigation drawer
//        binding.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void getUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Get the user ID (UID)
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Construct a reference to the user's document in Firestore
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.set(new HashMap<>()) // You can set initial data if needed
                    .addOnSuccessListener(aVoid -> {
                        // Document creation successful
//                        Toast.makeText(HomeActivity.this, "User document created", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle document creation failure
                        Toast.makeText(HomeActivity.this, "Failed to create user document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            // Now you can perform operations specific to this user
            // For example, fetch user data and update UI elements accordingly
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // User document exists, you can retrieve and use user data here
                    // Example: String username = documentSnapshot.getString("username");
                } else {
                    // User document does not exist
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}