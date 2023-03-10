package vungnv.com.foodyum.ui.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.ValueEventListener;

import vungnv.com.foodyum.MainActivity;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.activities.ShowAllProductsByMerchantActivity;
import vungnv.com.foodyum.adapter.TabMenuOrderAdapter;
import vungnv.com.foodyum.databinding.ActivityMainBinding;
import vungnv.com.foodyum.databinding.ActivityManagerOrderBinding;

public class ManagerOrderActivity extends AppCompatActivity {

    private ActivityManagerOrderBinding binding;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private BottomNavigationView bottomNavigationView;
    private TabMenuOrderAdapter tabMenuOrderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_order);

        init();
        tabMenuOrderAdapter = new TabMenuOrderAdapter(ManagerOrderActivity.this);

        viewPager2.setAdapter(tabMenuOrderAdapter);
        setTabDividers();
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Đang chờ");
                        //  tab.setIcon(R.drawable.order_waiting);
                        break;
                    case 1:
                        tab.setText("Đã nhận");
                        // tab.setIcon(R.drawable.order_done);
                        break;
                    case 2:
                        tab.setText("Đã huỷ");
                        //tab.setIcon(R.drawable.order_cancel);
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
        Bundle data = getIntent().getBundleExtra("idTabLayout");
        int indexTab = data.getInt("index");
        TabLayout.Tab tab = tabLayout.getTabAt(indexTab);
        assert tab != null;
        tab.select();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = new Intent(ManagerOrderActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                switch (item.getItemId()) {
                    case R.id.navigation_home:

                        bundle.putString("screen", "home");
                        intent.putExtra("idScreen", bundle);
                        startActivity(intent);
                        finishAffinity();
                        return true;
                    case R.id.navigation_cart:
                        bundle.putString("screen", "cart");
                        intent.putExtra("idScreen", bundle);
                        startActivity(intent);
                        finishAffinity();
                        return true;
                    case R.id.navigation_account:
                        bundle.putString("screen", "account");
                        intent.putExtra("idScreen", bundle);
                        startActivity(intent);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });

    }

    private void init() {
        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayoutOrder);
        bottomNavigationView = findViewById(R.id.nav_view);

    }

    private void setTabDividers() {
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.GRAY);
            gradientDrawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(5);
            ((LinearLayout) root).setDividerDrawable(gradientDrawable);
        }
    }
}