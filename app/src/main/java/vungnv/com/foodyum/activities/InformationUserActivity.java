package vungnv.com.foodyum.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.MainActivity;
import vungnv.com.foodyum.R;

public class InformationUserActivity extends AppCompatActivity implements Constant {
    private Toolbar toolbar;
    private ImageView imgUser;
    private EditText edName, edPhone, edEmail, edGender;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);

        init();
        setImg("1000002751");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String email = currentUser.getEmail();
        String name = currentUser.getDisplayName();
        setData(name, email);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationUserActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("screen", "account");
                intent.putExtra("idScreen", bundle);
                startActivity(intent);
                finishAffinity();

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String name = edName.getText().toString().trim();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                assert user != null;
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(InformationUserActivity.this, "Cập nhật tên thành công !", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolBarAccountDetail);
        imgUser = findViewById(R.id.imgUser);
        edName = findViewById(R.id.edNameUser);
        edPhone = findViewById(R.id.edPhone);
        edEmail = findViewById(R.id.edEmail);
        edGender = findViewById(R.id.edGender);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancelled);
    }

    private void setData(String name, String email) {
        edName.setText(name);
        edEmail.setText(email);
    }

    private void setImg(String idImage) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("images_users/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                // Load the image using Glide
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imgUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, "get image from firebase: " + exception.getMessage());
            }
        });
    }
}