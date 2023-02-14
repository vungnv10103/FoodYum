package vungnv.com.foodyum.ui.account;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.activities.LoginActivity;
import vungnv.com.foodyum.databinding.FragmentAccountBinding;
import vungnv.com.foodyum.utils.OnBackPressed;


public class AccountFragment extends Fragment implements Constant, OnBackPressed {

    private FragmentAccountBinding binding;
    private Button btnLogout;
    private EditText edCode;
    private Button btnRequest;
    private ItemCartDAO itemCartDAO;
    private boolean isReady = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        init(root);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edCode.getText().toString().trim();
                if (code.equals("cart")){
                    // delete table cart
                    itemCartDAO.deleteCart();
                    Toast.makeText(getContext(), "Delete Table Cart Success", Toast.LENGTH_SHORT).show();
                    isReady =true;
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
        btnLogout = view.findViewById(R.id.btnLogout);
        edCode = view.findViewById(R.id.edCode);
        btnRequest = view.findViewById(R.id.btnRequest);
        itemCartDAO = new ItemCartDAO(getContext());
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