package vungnv.com.foodyum;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

import vungnv.com.foodyum.databinding.ActivityMainBinding;
import vungnv.com.foodyum.utils.NetworkChangeListener;
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
        //mCreateNotification(MainActivity.this, "Tiêu đề", "Nội dung");

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Bundle data = getIntent().getBundleExtra("idScreen");
        if (data != null) {
            String idScreen = data.getString("screen");
            if (idScreen.equals("cart")) {
                navController.navigate(R.id.navigation_cart);
            }
        }


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