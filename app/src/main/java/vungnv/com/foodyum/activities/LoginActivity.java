package vungnv.com.foodyum.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.UsersDAO;
import vungnv.com.foodyum.MainActivity;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.model.User;
import vungnv.com.foodyum.utils.ImagePicker;
import vungnv.com.foodyum.utils.LocationProvider;
import vungnv.com.foodyum.utils.NetworkChangeListener;


public class LoginActivity extends AppCompatActivity implements Constant {
    private Button btnLogin, btnLoginWithFacebook;
    private TextView tvTitleApp, textView4, tvForgotPass;
    private ImageView imgLoginWithFacebook, logWithGoogle;
    private EditText edPass;
    private Button btnSubmit, btnCancel;
    private ToggleButton chkLanguage;
    private ProgressBar progress_bar;
    private AutoCompleteTextView edEmail;
    private CheckBox cbRemember;
    private TextView tvRegister;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private User itemUser;
    private UsersDAO usersDAO;
    private ArrayList<User> listDataUser;
    private FusedLocationProviderClient fusedLocationProviderClient;

    ArrayAdapter<String> adapterItems;
    private SpotsDialog progressDialog;
    GoogleSignInClient mGoogleSignInClient;

    private ImageView imageView9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        getToken();
        //createNotification.mCreateNotification(LoginActivity.this, "Tiêu đề", "Nội dung");
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        chkLanguage.setChecked(currentLanguage.equals("en"));
//        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/loadimg-8b067.appspot.com/o/images%2F2022_12_04_07_45_07?alt=media&token=0525ea03-1702-4542-9290-a60cac8a8d67").into(img);
        askPermission();
        getLastLocation();

        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        if (pref != null) {
            edEmail.setText(pref.getString("EMAIL", ""));
            edPass.setText(pref.getString("PASSWORD", ""));
            cbRemember.setChecked(pref.getBoolean("REMEMBER", false));
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        account.getEmail();
//        updateUI(account);


//        SignInButton signInButton = findViewById(R.id.sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
        logWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.logWithGoogle) {
                    signIn();
                }
            }
        });

        chkLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // false = vi , true = en
                String currentLanguage = getResources().getConfiguration().locale.getLanguage();
//                Toast.makeText(LoginActivity.this, "current language: " + currentLanguage, Toast.LENGTH_SHORT).show();
                String selectLanguage;
                if (isChecked) {
                    // change to en
                    selectLanguage = "en";
                } else {
                    // change to vi
                    selectLanguage = "vi";
                }
                if (!currentLanguage.equals(selectLanguage)) {
                    changeLanguage(selectLanguage);
                }


            }
        });
        listDataUser = (ArrayList<User>) usersDAO.getALL();
        String[] listUser = new String[listDataUser.size()];

        for (int i = 0; i < listDataUser.size(); i++) {
            String temp = listDataUser.get(i).email;
            listUser[i] = temp;
        }
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item_acc, listUser);
        edEmail.setAdapter(adapterItems);
        edEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pass = usersDAO.autoFillPassWord(listUser[position]);
                edPass.setText(pass);
            }
        });

        imageView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.pickImage(LoginActivity.this, new ImagePicker.OnImagePickedListener() {
                    @Override
                    public void onImagePicked(Uri uri) {
                        imageView9.setImageURI(uri);
                        // Get a reference to the storage location
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        // Create a reference to the file to upload
                        StorageReference imageRef = storageRef.child("images_coupon/" + uri.getLastPathSegment().substring(6));
//                        // Upload the file to the reference
                        UploadTask uploadTask = imageRef.putFile(uri);
                    }
                });
            }
        });

        tvTitleApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getLastLocation1();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edEmail.getText().toString().trim();
                String pass = edPass.getText().toString().trim();
                login(email, pass);


            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finishAffinity();
            }
        });
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                resetPassWorld(edEmail.getText().toString().trim());
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                String newPassword = "foodyum321";
//
//                assert user != null;
//                user.updatePassword(newPassword)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(LoginActivity.this, "Mật khẩu của bạn đã mặc định về: " + newPassword, Toast.LENGTH_SHORT).show();
//                                    edPass.setText("");
//                                }
//                            }
//                        });


            }
        });
    }

    private void init() {
        textView4 = findViewById(R.id.textView4);
        imageView9 = findViewById(R.id.imageView9);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        btnLogin = findViewById(R.id.btnSignIn);
        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        cbRemember = findViewById(R.id.checkBoxRemember);
        tvRegister = findViewById(R.id.tvRegister);
        tvTitleApp = findViewById(R.id.tvTitleApp);
        usersDAO = new UsersDAO(getApplicationContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        chkLanguage = findViewById(R.id.chkLanguage);
        imgLoginWithFacebook = findViewById(R.id.logWithFacebook);
        logWithGoogle = findViewById(R.id.logWithGoogle);
        progressDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
    }

    private boolean validate(String email, String pass) {
        if (email.isEmpty() || pass.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, REQUEST_FILL, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private boolean checkAccountExistInLocal(String idUser) {
        int temp = 0;
        listDataUser = (ArrayList<User>) usersDAO.getALL();
        for (User item : listDataUser) {
            if (idUser.toLowerCase(Locale.ROOT).equals(item.id.toLowerCase(Locale.ROOT))) {
                temp++;
            }
        }
        return temp <= 0;
    }

    private boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), WRONG_EMAIL_FORMAT, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void login(String email, String pass) {
        progressDialog.show();
        progressDialog.setCancelable(false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (validate(email, pass)) {
            if (checkEmail(email)) {
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = auth.getCurrentUser();
                                    assert currentUser != null;
                                    //verifyEmail();
                                    Log.d(TAG, "current user: " + currentUser.getEmail());
                                    Log.d(TAG, "current id: " + currentUser.getUid());
                                    rememberUser(email, pass, cbRemember.isChecked());
                                    if (checkAccountExistInLocal(auth.getUid())) {
                                        saveDbUserInLocal(auth.getUid(), email, pass);
                                    }
                                    progressDialog.dismiss();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finishAffinity();
                                } else {
                                    progressDialog.dismiss();
                                    Exception exception = task.getException();
                                    assert exception != null;
                                    String errorCode = ((FirebaseAuthException) exception).getErrorCode();
                                    Log.d(TAG, "error code: " + errorCode);
                                    String errorMessage = "";
                                    switch (errorCode) {
                                        case "ERROR_INVALID_EMAIL":
                                            errorMessage = "Địa chỉ email không hợp lệ !";
                                            break;
                                        case "ERROR_USER_NOT_FOUND":
                                            errorMessage = "Tài khoản không tồn tại !";
                                            break;
                                        case "ERROR_WRONG_PASSWORD":
                                            errorMessage = "Mật khẩu sai !";
                                            break;
                                        case "ERROR_EMAIL_ALREADY_IN_USE":
                                            errorMessage = "Địa chỉ email đã được sử dụng !";
                                            break;
                                        // Add other error cases as needed
                                        default:
                                            errorMessage = exception.getLocalizedMessage();
                                            break;
                                    }
                                    Log.d(TAG, "Error: " + errorMessage);
                                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }

        }
    }

    private void saveDbUserInLocal(String id, String email, String pass) {
        User itemUser = new User();

        itemUser.id = id;
        itemUser.email = email;
        itemUser.pass = pass;
        itemUser.img = "";
        itemUser.name = "";
        itemUser.phoneNumber = "";
        itemUser.searchHistory = "";
        itemUser.favouriteRestaurant = "";
        itemUser.feedback = "";

        if (usersDAO.insert(itemUser) > 0) {
            Log.d(TAG, "save db user success: ");
        }
    }


    private void askPermission() {
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private void rememberUser(String email, String pass, boolean isRemember) {
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // save data
        editor.putString("EMAIL", email);
        editor.putString("PASSWORD", pass);
        editor.putBoolean("REMEMBER", true);
        // save
        editor.apply();
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(LoginActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                    edLocation.setText("" + addresses.get(0).getAddressLine(0));
                                    String mLocation = addresses.get(0).getAddressLine(0);
                                    float[] results = new float[1];
                                    Log.d(TAG, "current Location: " + mLocation);
                                    double currentLongitude = addresses.get(0).getLongitude();
                                    double currentLatitude = addresses.get(0).getLatitude();
                                   // String coordinate = currentLongitude + "-" + currentLatitude;
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


    private void changeLanguage(String language) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);

        recreate();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            edEmail.setText(account.getEmail());
            edPass.setText(account.getDisplayName());
            Log.d(TAG, "handleSignInResult: " + account.getEmail());
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePicker.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        // Log
                        Log.d(TAG, "token:" + token);
                        rememberToken(token);
                    }
                });
    }
    private void verifyEmail(){
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
    private void resetPassWorld(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    private void rememberToken(String token) {
        SharedPreferences pref = getSharedPreferences("TOKEN", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        // save
        editor.apply();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);

        // auto login

        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        if (pref != null) {
            boolean isSave = pref.getBoolean("REMEMBER", false);
            if (isSave) {

                String email = pref.getString("EMAIL", "");
                String pass = pref.getString("PASSWORD", "");
                login(email, pass);
            }
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}