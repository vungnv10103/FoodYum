package vungnv.com.foodyum;

import static vungnv.com.foodyum.utils.createNotification.mCreateNotification;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;


import vungnv.com.foodyum.databinding.ActivityMainBinding;
import vungnv.com.foodyum.utils.NetworkChangeListener;
import vungnv.com.foodyum.utils.OnBackPressed;
import vungnv.com.foodyum.utils.createNotificationChannel;


public class MainActivity extends AppCompatActivity implements Constant {

    private ActivityMainBinding binding;

    private int backPressCount = 0;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private final createNotificationChannel notification = new createNotificationChannel();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        notification.createNotificationChannel1(MainActivity.this);
        //vungnv.com.foodyum.utils.createNotification.mCreateNotification(MainActivity.this, "Tiêu đề", "Nội dung");

        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        Bundle data = getIntent().getBundleExtra("idScreen");
        if (data != null) {
            String idScreen = data.getString("screen");
            if (idScreen.equals("cart")) {
                navController.navigate(R.id.navigation_cart);
            } else if (idScreen.equals("account")) {
                navController.navigate(R.id.navigation_account);
            }
            else if (idScreen.equals("home")) {
                navController.navigate(R.id.navigation_home);
            }
        }
        NavigationUI.setupWithNavController(binding.navView, navController);

        binding.navView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                navController.navigate(R.id.navigation_home);
                return true;
            }
            if (item.getItemId() == R.id.navigation_cart) {
                navController.navigate(R.id.navigation_cart);
                return true;
            }
            if (item.getItemId() == R.id.navigation_account) {
                navController.navigate(R.id.navigation_account);
                return true;
            }
            return false;
        });


    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            backPressCount = 0;
        }
    };

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (fragment instanceof OnBackPressed) {
            ((OnBackPressed) fragment).onBackPressed();
        } else {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            int currentDestinationId = Objects.requireNonNull(navController.getCurrentDestination()).getId();

            if (currentDestinationId == R.id.navigation_home) {
                if (backPressCount == 0) {
                    backPressCount++;
                    Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(runnable, 3000);
                } else {
                    finish();
                }
            } else {
                navController.navigate(R.id.navigation_home);
                backPressCount = 0;
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
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