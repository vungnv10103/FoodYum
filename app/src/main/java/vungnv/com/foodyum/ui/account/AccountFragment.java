package vungnv.com.foodyum.ui.account;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import vungnv.com.foodyum.R;
import vungnv.com.foodyum.activities.LoginActivity;
import vungnv.com.foodyum.databinding.FragmentAccountBinding;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private Button btnLogout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init(root);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}