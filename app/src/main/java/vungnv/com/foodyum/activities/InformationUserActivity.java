package vungnv.com.foodyum.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
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
import vungnv.com.foodyum.DAO.UsersDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.model.User;
import vungnv.com.foodyum.utils.NetworkChangeListener;

public class InformationUserActivity extends AppCompatActivity implements Constant {
    private Toolbar toolbar;
    private ImageView imgUser;
    private EditText edName, edPhone, edEmail, edOTP;
    private Button btnSave, btnCancel, btnSendOTP;

    private String verificationId;
    private FirebaseAuth auth;
    private UsersDAO usersDAO;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private SpotsDialog processDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);

        init();
        setImg("1000002751");
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        assert currentUser != null;
        String email = currentUser.getEmail();
        String name = currentUser.getDisplayName();
        String phone = currentUser.getPhoneNumber();
        if (phone == null) {
            phone = usersDAO.getPhone(email);
            setData(name, phone, email);
        } else {
            setData(name, phone, email);
        }

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(InformationUserActivity.this, MainActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("screen", "account");
//                intent.putExtra("idScreen", bundle);
//                startActivity(intent);
//                finishAffinity();

                onBackPressed();

            }
        });
        String currentName = edName.getText().toString().trim();
        String currentPhone = edPhone.getText().toString().trim();
        String currentEmail = edEmail.getText().toString().trim();
        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPhone = edPhone.getText().toString().trim();
                sendOTP(newPhone);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newName = edName.getText().toString().trim();
                String newEmail = edEmail.getText().toString().trim();
                String newPhone = edPhone.getText().toString().trim();
                // Toast.makeText(InformationUserActivity.this, "old: " + currentName + " new: " + newName , Toast.LENGTH_SHORT).show();
                if (!currentName.equals(newName)) {
                    updateName(currentUser, newName);
                }
//               if (!currentEmail.equals(newEmail)) {
//                    updateEmail(currentUser, newEmail);
//                }
                if (!currentPhone.equals(newPhone)) {
//                    verifyCode(edOTP.getText().toString());
                    updatePhone(newEmail, newPhone);
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
        edOTP = findViewById(R.id.edOTP);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancelled);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        processDialog = new SpotsDialog(InformationUserActivity.this, R.style.Custom2);
        usersDAO = new UsersDAO(getApplicationContext());
    }

    private void setData(String name, String phone, String email) {
        edName.setText(name);
        edPhone.setText(phone);
        edEmail.setText(email);
        edEmail.setEnabled(false);
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
                            User user = new User();
                            user.email = currentUser.getEmail();
                            user.name = newName;

                            if (usersDAO.updateName(user) > 0) {
                                Toast.makeText(InformationUserActivity.this, "Cập nhật tên thành công !", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "User profile updated.");
                                onBackPressed();
                            }

                        }
                        processDialog.dismiss();
                    }
                });
    }


    private void updatePhone(String email, String phone) {

        User user = new User();
        user.email = email;
        user.phoneNumber = phone;

        if (usersDAO.updatePhone(user) > 0) {
            Toast.makeText(this, "Cập nhật SĐT thành công", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }

    private void updateEmail(String newEmail) {

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

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.

                            Toast.makeText(InformationUserActivity.this, "Thêm SĐT thành công", Toast.LENGTH_SHORT).show();

                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(InformationUserActivity.this, "Thêm SĐT thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendOTP(String phoneNumber) {
        processDialog.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)            // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        processDialog.dismiss();
    }

    // callback method is called on phone auth provider
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                edOTP.setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.d(TAG, "onVerificationFailed: " + e.getMessage());
        }
    };

    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Log.d(TAG, "onComplete: " + user);
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
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

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}
