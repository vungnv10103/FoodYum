package vungnv.com.foodyum.activities;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.ProductsAdapter;
import vungnv.com.foodyum.model.Product;
import vungnv.com.foodyum.utils.NetworkChangeListener;


public class SearchDetailActivity extends AppCompatActivity implements Constant {
    private Toolbar toolbar;
    private SearchView searchView;
    private EditText edSearch;
    private ImageView imgDeleteSearch;
    private RecyclerView rcvListAllProduct;
    private ProductsAdapter productsAdapter;
    private SpotsDialog progressDialog;
    private final ArrayList<Product> aListProducts = new ArrayList<>();

    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);

        init();


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                onBackPressed();
            }
        });
        listProduct();
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
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
                if (aListProducts.size() == 0) {
                    return;
                }
                productsAdapter.getFilter().filter(editable.toString());

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
        rcvListAllProduct = findViewById(R.id.rcvListAllProduct);
        progressDialog = new SpotsDialog(SearchDetailActivity.this, R.style.Custom);
    }

    private void listProduct() {
        progressDialog.show();
        String path = "list_product_all";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                aListProducts.clear();
                for (DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {
                    Product value = childSnapshot1.getValue(Product.class);
                    assert value != null;
                    if (value.status != 0) {
                        aListProducts.add(value);
                    }
                }

                productsAdapter = new ProductsAdapter(SearchDetailActivity.this, aListProducts);
                rcvListAllProduct.setAdapter(productsAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchDetailActivity.this, RecyclerView.VERTICAL, false);
                linearLayoutManager.setSmoothScrollbarEnabled(true);
                rcvListAllProduct.setLayoutManager(linearLayoutManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListAllProduct.getContext(),
                        linearLayoutManager.getOrientation());
                rcvListAllProduct.addItemDecoration(dividerItemDecoration);
//                rcvListAllProduct.setHasFixedSize(true);
//                rcvListAllProduct.setNestedScrollingEnabled(false);
//                rcvListAllProduct.setScrollBarSize(10);
//                rcvListAllProduct.setHovered(true);
//                rcvListAllProduct.setOverScrollMode();


                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
                progressDialog.dismiss();
            }
        });

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
//        edSearch.setSelection(edSearch.getText().length());

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}