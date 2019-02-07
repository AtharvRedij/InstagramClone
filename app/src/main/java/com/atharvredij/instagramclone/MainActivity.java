package com.atharvredij.instagramclone;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    // All the constants
    private static final int RC_SIGN_IN = 12345;
    public static final int StorageRequestPermissionCode = 1;

    // List that holds the posts
    private List<Post> postList = new ArrayList<>();

    private RecyclerView postsRecyclerView;
    private PostsAdapter postsAdapter;

    // Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mPostsDatabaseReference = mDatabase.getReference("posts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        setupRecyclerView();

        // Checks for storage permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{READ_EXTERNAL_STORAGE}, StorageRequestPermissionCode);
        }

        // setting up firebase
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if (mUser != null) {
                    Toast.makeText(MainActivity.this, "Logged In",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Create and launch sign-in intent
                    displayLoginScreen();
                }
            }
        };

        // Listener that listens to child node i.e. posts being added
        mPostsDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);
                postsAdapter.notifyDataSetChanged();
            }

            // These methods are not used
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    private void setupRecyclerView() {
        postsAdapter = new PostsAdapter(this, postList);
        postsRecyclerView.setAdapter(postsAdapter);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Displays login screen FirebaseUI
    private void displayLoginScreen() {
        // Choose authentication providers
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    // For storage permission check
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(MainActivity.this, "Login Success",
                        Toast.LENGTH_SHORT).show();

                // Launching app again because app won't load images after granting permission in dialog
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            } else {
                // Sign in failed.
                Toast.makeText(MainActivity.this, "Login Fail",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addPost:
                startActivity(new Intent(MainActivity.this, AddPostActivity.class));
                return true;

            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                if (mAuth != null) {
                    mAuth.removeAuthStateListener(mAuthListener);
                }
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // checks if permission is granted after the permissions dialog box
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case StorageRequestPermissionCode:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Storage Permission Granted",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Storage Permission Denied",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }
}

