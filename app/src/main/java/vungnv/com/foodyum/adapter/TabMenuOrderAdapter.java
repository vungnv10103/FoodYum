package vungnv.com.foodyum.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vungnv.com.foodyum.ui.order.OrderCancelFragment;
import vungnv.com.foodyum.ui.order.OrderDoneFragment;
import vungnv.com.foodyum.ui.order.OrderWaitingFragment;

public class TabMenuOrderAdapter extends FragmentStateAdapter {
    public TabMenuOrderAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = OrderWaitingFragment.newInstance();
                break;
            case 1:
                fragment = OrderDoneFragment.newInstance();
                break;
            case 2:
                fragment = OrderCancelFragment.newInstance();
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
