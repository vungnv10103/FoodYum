package vungnv.com.foodyum.activities;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.utils.NetworkChangeListener;


public class SearchDetailActivity extends AppCompatActivity implements Constant {
    private Toolbar toolbar;
    private SearchView searchView;
    private EditText edSearch;
    private ImageView imgDeleteSearch;
    private String email = "";

    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);

        init();

        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        if (pref != null) {
            email = pref.getString("EMAIL", "");

        }


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                onBackPressed();
            }
        });
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edSearch.getText().toString().trim().isEmpty()) {
                    imgDeleteSearch.setVisibility(View.VISIBLE);
                    imgDeleteSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            edSearch.setText("");
                        }
                    });
                } else {
                    imgDeleteSearch.setVisibility(View.INVISIBLE);
                }

            }
        });
//        View v = searchView.findViewById(R.id.search_view);
//        v.setBackgroundColor(Color.parseColor("white"));
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Log.d(TAG, "onQueryTextSubmit: " + query);
//                // filter
//                // Log.d(TAG, "email: " + email);
//
//                searchView.setQuery("", false);
//                Log.d(TAG, "query: " + query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // Update the search results as the user types
//                // filter
//
//                return false;
//            }
//        });
    }


    private void init() {
        toolbar = findViewById(R.id.toolBarSearch);
        //searchView = findViewById(R.id.search_view);
        edSearch = findViewById(R.id.edSearch);
        imgDeleteSearch = findViewById(R.id.imgDeleteSearch);
    }


    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
        //    setContentView(R.layout.activity_search_detail);
//        SearchView searchView = findViewById(R.id.search_view);
        //searchView.setIconified(false);
        edSearch.requestFocus();
        edSearch.setSelection(edSearch.getText().length());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}