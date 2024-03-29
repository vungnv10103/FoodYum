package vungnv.com.foodyum.ui.account;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.MainActivity;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.activities.InformationUserActivity;
import vungnv.com.foodyum.activities.LoginActivity;
import vungnv.com.foodyum.activities.ShowAllProductsByMerchantActivity;
import vungnv.com.foodyum.databinding.FragmentAccountBinding;
import vungnv.com.foodyum.model.Order;
import vungnv.com.foodyum.ui.order.ManagerOrderActivity;
import vungnv.com.foodyum.utils.OnBackPressed;


public class AccountFragment extends Fragment implements Constant, OnBackPressed {

    private Toolbar toolbar;
    private LinearLayout lnlInfoUser;
    private ImageView imgUser;
    private TextView tvNameUser;
    private TextView tvOrderWaiting, tvOrderDone, tvOrderCancel;
    private Button btnLogout;
    private EditText edCode;
    private Button btnRequest;
    private ItemCartDAO itemCartDAO;
    private boolean isReady = false;
    private List<Order> listOrder;
    private ArrayList<Order> aListOrder = new ArrayList<>();

    private SpotsDialog processDialog;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        vungnv.com.foodyum.databinding.FragmentAccountBinding binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        init(root);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String img = String.valueOf(currentUser.getPhotoUrl());
        String name = currentUser.getDisplayName();
        String phone = currentUser.getPhoneNumber();
        if (name != null) {
            tvNameUser.setText(name);
        }
        else {
            tvNameUser.setText("UserName");
        }
        Toast.makeText(getContext(), "phone: " + phone, Toast.LENGTH_SHORT).show();

        setImg("1000002751");


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isReady = true;
                onBackPressed();

            }
        });
        lnlInfoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), InformationUserActivity.class));
            }
        });
        tvOrderWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "waiting...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ManagerOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("index", 0);
                intent.putExtra("idTabLayout", bundle);
                startActivity(intent);
            }
        });
        tvOrderDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "done...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ManagerOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("index", 1);
                intent.putExtra("idTabLayout", bundle);
                startActivity(intent);
            }
        });
        tvOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ManagerOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("index", 2);
                intent.putExtra("idTabLayout", bundle);
                startActivity(intent);
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edCode.getText().toString().trim();
                if (code.equals("cart")) {
                    // delete table cart
                    itemCartDAO.deleteCart();
                    Toast.makeText(getContext(), "Delete Table Cart Success", Toast.LENGTH_SHORT).show();
                    isReady = true;
                    onBackPressed();

                }
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReady = true;

                SharedPreferences pref = requireContext().getSharedPreferences("USER_FILE", MODE_PRIVATE);
                if (pref != null) {
                    String email = pref.getString("EMAIL", "");
                    String pass = pref.getString("PASSWORD", "");
                    upDateRememberUser(email, pass);
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }


            }
        });
        return root;
    }

    private void init(View view) {
        toolbar = view.findViewById(R.id.toolBarAccount);
        btnLogout = view.findViewById(R.id.btnLogout);
        edCode = view.findViewById(R.id.edCode);
        btnRequest = view.findViewById(R.id.btnRequest);
        itemCartDAO = new ItemCartDAO(getContext());
        lnlInfoUser = view.findViewById(R.id.lnlInfoUser);
        imgUser = view.findViewById(R.id.imgUser);
        tvNameUser = view.findViewById(R.id.tvNameUser);
        tvOrderWaiting = view.findViewById(R.id.tvOrderWaiting);
        tvOrderDone = view.findViewById(R.id.tvOrderDone);
        tvOrderCancel = view.findViewById(R.id.tvOrderCancel);
        processDialog = new SpotsDialog(getContext(), R.style.Custom2);
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
                Glide.with(requireContext())
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

    private void upDateRememberUser(String email, String pass) {
        SharedPreferences pref = requireContext().getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // save data
        editor.putString("EMAIL", email);
        editor.putString("PASSWORD", pass);
        editor.putBoolean("REMEMBER", false);

        // save
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (isReady) {
            requireActivity().onBackPressed();
        }
    }
}