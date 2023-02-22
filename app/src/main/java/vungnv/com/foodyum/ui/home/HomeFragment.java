package vungnv.com.foodyum.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import me.relex.circleindicator.CircleIndicator3;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ProductDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.activities.SearchDetailActivity;
import vungnv.com.foodyum.activities.ShowListProductDetailActivity;
import vungnv.com.foodyum.adapter.CategoriesAdapter;
import vungnv.com.foodyum.adapter.SlideShowAdapter;
import vungnv.com.foodyum.databinding.FragmentHomeBinding;
import vungnv.com.foodyum.model.Category;
import vungnv.com.foodyum.model.Product;
import vungnv.com.foodyum.model.ProductSlideShow;
import vungnv.com.foodyum.utils.OnBackPressed;

public class HomeFragment extends Fragment implements OnBackPressed, Constant, SwipeRefreshLayout.OnRefreshListener {

    private FragmentHomeBinding binding;

    private SwipeRefreshLayout swipeRefreshLayout;
    private SpotsDialog progressDialog;
    private GridView gridViewCategories;

    private final ArrayList<Category> aListCate = new ArrayList<>();
    private ArrayList<ProductSlideShow> aListSlideShow;

    private boolean isReady = false;

    EditText edSearch;
    TextView tvSeeMore;

    private ViewPager2 viewPager2;
    private CircleIndicator3 circleIndicator;
    private final Handler handler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init(root);

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));
        listSlideShow();
        listCate();


        tvSeeMore.setOnClickListener(view1 -> {
            // startActivity(new Intent(getContext(), SearchDetailActivity.class));
        });


        edSearch.setOnClickListener(view12 -> startActivity(new Intent(getContext(), SearchDetailActivity.class)));

        return root;
    }

    private void init(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        edSearch = view.findViewById(R.id.edSearchHome);
        viewPager2 = view.findViewById(R.id.viewpager_slideshow);
        circleIndicator = view.findViewById(R.id.indicator);
        tvSeeMore = view.findViewById(R.id.tvSeeMore);
        gridViewCategories = view.findViewById(R.id.gridViewCategories);
        progressDialog = new SpotsDialog(getContext(), R.style.Custom2);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager2.getCurrentItem() == aListSlideShow.size() - 1) {
                viewPager2.setCurrentItem(0);
            } else {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            }
        }
    };


    private void listSlideShow() {
        progressDialog.show();
        aListSlideShow = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_slideshow");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aListSlideShow.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductSlideShow model = snapshot1.getValue(ProductSlideShow.class);
                    aListSlideShow.add(model);
                }
                if (aListSlideShow.size() == 0) {
                    Toast.makeText(getContext(), ERROR_FETCHING_DATE + "danh sách slide show", Toast.LENGTH_SHORT).show();
                    return;
                }
                SlideShowAdapter adapterSlideShow = new SlideShowAdapter(aListSlideShow, getContext());
                viewPager2.setAdapter(adapterSlideShow);
                progressDialog.dismiss();
                viewPager2.setClipToPadding(false);
                viewPager2.setClipChildren(false);
                viewPager2.setOffscreenPageLimit(3);
                viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_IF_CONTENT_SCROLLS);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                compositePageTransformer.addTransformer((page, position) -> {
                    float r = 1 - Math.abs(position);
                    page.setScaleY(0.85f + r * 0.15f);
                });

                viewPager2.setPageTransformer(compositePageTransformer);
                circleIndicator.setViewPager(viewPager2);


                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, 5000);
                    }
                });
                isReady = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isReady = true;
                progressDialog.dismiss();

            }
        });

    }

    private void listCate() {
        progressDialog.show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aListCate.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category data = dataSnapshot.getValue(Category.class);
                    aListCate.add(data);
                }

                if (aListCate.size() == 0) {
                    Toast.makeText(getContext(), ERROR_FETCHING_DATE + CATEGORY_LIST, Toast.LENGTH_SHORT).show();
                    return;
                }
                CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getContext(), R.layout.item_category, aListCate);
                gridViewCategories.setAdapter(categoriesAdapter);
                isReady = true;
                progressDialog.dismiss();

                gridViewCategories.setOnItemClickListener((adapterView, view, i, l) -> {
                    Intent intent = new Intent(getContext(), ShowListProductDetailActivity.class);
                    Bundle bundle = new Bundle();
                        bundle.putInt("id", aListCate.get(i).id);
                    bundle.putString("img", aListCate.get(i).img);
                    bundle.putString("name", aListCate.get(i).name);
                    intent.putExtra("data-category", bundle);
                    startActivity(intent);
                    isReady = true;
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isReady = true;
                progressDialog.dismiss();
            }
        });
    }


    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            listSlideShow();
            listCate();
        }, 1500);
    }

    @Override
    public void onBackPressed() {
        if (isReady) {
            requireActivity().onBackPressed();
        }

    }
}