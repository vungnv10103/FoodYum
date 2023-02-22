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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.MainActivity;
import vungnv.com.foodyum.R;

public class InformationUserActivity extends AppCompatActivity implements Constant {
    private Toolbar toolbar;
    private ImageView imgUser;
    private EditText edName, edPhone, edEmail, edGender;
    private Button btnSave, btnCancel;

    private SpotsDialog processDialog;

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
        String phone = currentUser.getPhoneNumber();
        setData(name, phone, email);
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
        String currentName = edName.getText().toString().trim();
        String currentPhone = edPhone.getText().toString().trim();
        String currentEmail = edEmail.getText().toString().trim();
        String currentGender = edGender.getText().toString().trim();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String newName = edName.getText().toString().trim();
                String newEmail = edEmail.getText().toString().trim();
                String newPhone = edPhone.getText().toString().trim();
                // Toast.makeText(InformationUserActivity.this, "old: " + currentName + " new: " + newName , Toast.LENGTH_SHORT).show();
                if (!currentName.equals(newName)) {
                    updateName(currentUser, newName);
                } else if (!currentEmail.equals(newEmail)) {
                    updateEmail(currentUser, newEmail);
                } else if (!currentPhone.equals(newPhone)) {
                    updatePhoneNumber(auth, newPhone);
                }

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
        processDialog = new SpotsDialog(InformationUserActivity.this, R.style.Custom2);
    }

    private void setData(String name, String phone, String email) {
        edName.setText(name);
        edPhone.setText(phone);
        edEmail.setText(email);
    }

    private void updateName(FirebaseUser currentUser, String newName) {
        processDialog.show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(InformationUserActivity.this, "Cập nhật tên thành công !", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "User profile updated.");
                        }
                        processDialog.dismiss();
                    }
                });
    }

    private void updateEmail(FirebaseUser user, String newEmail) {
        processDialog.show();
        assert user != null;
        user.updateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // send verity email.....
                            Toast.makeText(InformationUserActivity.this, "Cập nhật email thành công", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "User email address updated.");
                        }
                        processDialog.dismiss();
                    }
                });
    }

    private void updatePhoneNumber(FirebaseAuth mAuth, String phoneNumber) {
        processDialog.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d(TAG, "onVerificationCompleted: " + "success");
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.d(TAG, "onVerificationFailed: " + e.getMessage());

                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        processDialog.dismiss();
    }

    private void setImg(String idImage) {
        processDialog.show();
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
        processDialog.dismiss();
    }
}
