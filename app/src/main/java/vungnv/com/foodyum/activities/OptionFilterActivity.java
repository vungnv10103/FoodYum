package vungnv.com.foodyum.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;


import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.utils.NetworkChangeListener;

public class OptionFilterActivity extends AppCompatActivity implements Constant {
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_filter);

        ImageView imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                Intent intent1 = getIntent();
                Bundle bd = intent1.getBundleExtra("data-temp");
                if (bd != null){
                    String title = bd.getString("title");
                    String value = radioButton.getText().toString();
                    Intent intent = new Intent(getApplicationContext(), ShowListProductDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", title);
                    bundle.putString("value", value);
                    intent.putExtra("option-filter", bundle);
                    startActivity(intent);
                    finishAffinity();
                }

            }
        });


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