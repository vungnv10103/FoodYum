package vungnv.com.foodyum.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.UsersDAO;
import vungnv.com.foodyum.MainActivity;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.model.User;
import vungnv.com.foodyum.utils.EncryptingPassword;
import vungnv.com.foodyum.utils.NetworkChangeListener;


public class RegisterActivity extends AppCompatActivity implements Constant {
    private SpotsDialog progressDialog;
    private EditText edEmail, edPass, edConfirmPass;
    private TextView tvSignIn;
    private Button btnRegister;

    FusedLocationProviderClient fusedLocationProviderClient;

    private UsersDAO usersDAO;
    private FirebaseAuth auth;
    private final EncryptingPassword encryptingPassword = new EncryptingPassword();
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setCancelable(false);
                String email = edEmail.getText().toString().trim();
                String pass = edPass.getText().toString().trim();
                String confirmPass = edConfirmPass.getText().toString().trim();
                auth = FirebaseAuth.getInstance();
                if (validate(email, pass, confirmPass)) {
                    if (checkEmail(email)) {
                        auth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            verifyEmail();

                                            if (checkAccountExist(auth.getUid())) {
                                                // add dialog
                                                saveDbUserInLocal(auth.getUid(), email, pass);
                                                addInformation(auth.getUid(), email, pass);

                                            }

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, REGISTER_FAIL + "\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        progressDialog.dismiss();
                    }

                } else {
                    progressDialog.dismiss();
                }

            }
        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });

    }

    private void init() {
        progressDialog = new SpotsDialog(RegisterActivity.this, R.style.Custom);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        edEmail = findViewById(R.id.edEmailRegister);
        edPass = findViewById(R.id.edPass);
        edConfirmPass = findViewById(R.id.edPassConfirm);
        tvSignIn = findViewById(R.id.tvSignIn);
        btnRegister = findViewById(R.id.btnRegister);
        usersDAO = new UsersDAO(getApplicationContext());
    }

    private boolean validate(String email, String pass, String confirmPass) {
        if (email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, REQUEST_FILL, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!pass.equals(confirmPass)) {
            Toast.makeText(this, PASS_NO_MATCH, Toast.LENGTH_SHORT).show();
            return false;
        } else if (pass.length() < 6 || confirmPass.length() < 6) {
            Toast.makeText(RegisterActivity.this, REQUEST_LENGTH, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            Toast.makeText(RegisterActivity.this, WRONG_EMAIL_FORMAT, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void verifyEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    private void saveDbUserInLocal(String id, String email, String pass) {
        getLastLocation();
        User itemUser = new User();

        itemUser.id = id;
        itemUser.email = email;
        itemUser.pass = pass;
        itemUser.img = "";
        itemUser.name = "";
        itemUser.phoneNumber = "";
        itemUser.searchHistory = "";
        itemUser.feedback = "";

        if (usersDAO.insert(itemUser) > 0) {
            Log.d(TAG, "save db user success: ");
        }
    }

    private boolean checkAccountExist(String idUser) {
        int temp = 0;
        ArrayList<User> listDataUser = (ArrayList<User>) usersDAO.getALL();
        for (User item : listDataUser) {
            if (idUser.toLowerCase(Locale.ROOT).equals(item.id.toLowerCase(Locale.ROOT))) {
                temp++;
            }
        }
        return temp <= 0;
    }

    private void addInformation(String id, String email, String pass) {

        String encryptPass = encryptingPassword.EncryptPassword(pass);
        showDialogAddInfo(id, email, pass, encryptPass);

    }

    private void rememberUser(String email, String pass) {
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // save data
        editor.putString("EMAIL", email);
        editor.putString("PASSWORD", pass);
        editor.putBoolean("REMEMBER", true);

        editor.apply();
    }

    private void showDialogAddInfo(String id, String email, String pass, String encryptPass) {
        Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_info);
        dialog.setCancelable(false);


        EditText edNameUser = dialog.findViewById(R.id.edNameUser);
        EditText edPhone = dialog.findViewById(R.id.edPhone);
        EditText edEmail = dialog.findViewById(R.id.edEmail);
        edEmail.setText(email);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = edPhone.getText().toString().trim();
                String name = edNameUser.getText().toString().trim();

                User user = new User(email, name, phone, "", encryptPass);
                Map<String, Object> mListUser = user.toMap();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                Map<String, Object> updates = new HashMap<>();
                updates.put("list_user_client/" + id, mListUser);
                reference.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Log.d(TAG, "upload user to firebase success: ");
                        rememberUser(email, pass);
                        updateName(Objects.requireNonNull(auth.getCurrentUser()), name);
                        updatePhone(email, phone);
                        dialog.dismiss();
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                });
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    private void updateName(FirebaseUser currentUser, String newName) {
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
                                Log.d(TAG, "User profile updated.");
                            }

                        }
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

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    String mLocation = addresses.get(0).getAddressLine(0);
                                    Log.d(TAG, "current Location: " + mLocation);
                                    double currentLongitude = addresses.get(0).getLongitude();
                                    double currentLatitude = addresses.get(0).getLatitude();
                                    //String coordinate = currentLongitude + "-" + currentLatitude;
                                    float[] results = new float[1];
                                    double coNhueLongitude = 105.77553463;
                                    double coNhueLatitude = 21.06693654;
                                    Log.d(TAG, "currentLongitude: " + currentLongitude + " currentLatitude: " + currentLatitude);
                                    Location.distanceBetween(currentLatitude, currentLongitude, coNhueLatitude, coNhueLongitude, results);
                                    float distanceInMeters = results[0];
                                    Log.d(TAG, "distance: " + String.format("%.1f", distanceInMeters / 1000) + "km");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finishAffinity();
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