package com.atharvredij.instagramclone;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddPostActivity extends AppCompatActivity {

    // Layout views
    private RelativeLayout addImageLayout;
    private ImageView addImageView;
    private EditText addTitleEditText, addDescriptionEditText;
    private TextView clickHereTextView;
    private Button submitButton;
    private ProgressBar UploadingProgressbar;

    private Uri selectedImageUri;

    private static final int RESULT_LOAD_IMAGE = 100;

    // Firebase instances
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference mPhotosStorageReference = mStorage.getReference();

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mPostsDatabaseReference = mDatabase.getReference("posts");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        addImageLayout = findViewById(R.id.AddImageLayout);
        addImageView = findViewById(R.id.AddImageView);
        addTitleEditText = findViewById(R.id.AddTitleET);
        addDescriptionEditText = findViewById(R.id.AddDescriptionET);
        clickHereTextView = findViewById(R.id.clickHereTV);
        submitButton = findViewById(R.id.submitButton);
        UploadingProgressbar = findViewById(R.id.UploadingProgressbar);

        addImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickHereTextView.setVisibility(View.GONE);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addTitleEditText.getText().toString().trim().equals("") ||
                        addDescriptionEditText.getText().toString().trim().equals("") ||
                        selectedImageUri == null) {

                    Toast.makeText(AddPostActivity.this,
                            "Fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    UploadingProgressbar.setVisibility(View.VISIBLE);
                    uploadPhoto();
                }

            }
        });
    }

    private void uploadPhoto() {
        final StorageReference currentImageReference = mPhotosStorageReference.child("InstagramImages/" +
                selectedImageUri.getLastPathSegment());

        UploadTask uploadTask = currentImageReference.putFile(selectedImageUri);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(AddPostActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // SuccessListener is added on getDownloadUrl() to get proper url
                currentImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        DatabaseReference currentPostDatabaseReference = mPostsDatabaseReference.child(mPostsDatabaseReference.push().getKey());
                        Post post = new Post(addTitleEditText.getText().toString().trim(),
                                addDescriptionEditText.getText().toString().trim(),
                                uri.toString());
                        currentPostDatabaseReference.setValue(post);
                        UploadingProgressbar.setVisibility(View.GONE);
                        Toast.makeText(AddPostActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddPostActivity.this, MainActivity.class));
                        finish();
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE) {

                selectedImageUri = data.getData();
                //Get ImageURI and load with help of picasso
                Picasso.get().load(selectedImageUri).noPlaceholder().centerCrop().fit()
                        .into(addImageView);
            }

        }
    }
}
