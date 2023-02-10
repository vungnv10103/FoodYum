package vungnv.com.foodyum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.DAO.UsersDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.model.ItemCart;
import vungnv.com.foodyum.ui.cart.CartFragment;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> implements Constant {
    private final Context context;
    private static List<ItemCart> listItem;
    private UsersDAO usersDAO;
    private ItemCartDAO itemCartDAO;

    public CartAdapter(Context context, List<ItemCart> listItem) {
        this.context = context;
        CartAdapter.listItem = listItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        itemCartDAO = new ItemCartDAO(context);
        ItemCart item = listItem.get(position);

        int status = item.status;
        int quantity = item.quantity;
        animationDecrease(quantity, holder.imgDecrease);
        animationIncrease(quantity, holder.imgIncrease);
        holder.cbCheck.setChecked(status != 1);
        holder.tvName.setText(item.name);
        holder.tvPrice.setText(item.price + "đ");
        holder.tvQuantity.setText(item.quantity + "");
        holder.cbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "" + item.id, Toast.LENGTH_SHORT).show();
                boolean isSelected = holder.cbCheck.isChecked();
                //Toast.makeText(context, "" + isSelected, Toast.LENGTH_SHORT).show();
                ItemCart item1 = new ItemCart();
                item1.id = item.id;
                if (isSelected) {
                    item1.status = 2;
                    if (itemCartDAO.updateStatus(item1) > 0) {
                        Log.d(TAG, "update status 1 -> 2 success");
                    } else {
                        Log.d(TAG, "error update status");
                    }
                } else {
                    item1.status = 1;
                    if (itemCartDAO.updateStatus(item1) > 0) {
                        Log.d(TAG, "update status 2 -> 1 success");
                    } else {
                        Log.d(TAG, "error update status");
                    }
                }
            }
        });


        holder.imgIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString().trim());
                currentQuantity++;
                if (currentQuantity > 20) {
                    animationIncrease(currentQuantity, holder.imgIncrease);
                    return;
                }
                holder.tvQuantity.setText(String.valueOf(currentQuantity));
                ItemCart item1 = new ItemCart();
                item1.id = item.id;
                item1.quantity = currentQuantity;
                if (itemCartDAO.updateQuantity(item1) > 0) {
                    Log.d(TAG, "update quantity success");
                } else {
                    Log.d(TAG, "update quantity fail");
                }
                animationIncrease(currentQuantity, holder.imgIncrease);
                animationDecrease(currentQuantity, holder.imgDecrease);
                double price = item.price / item.quantity * currentQuantity;
                holder.tvPrice.setText(price + "đ");
                ItemCart item2 = new ItemCart();
                item2.id = item.id;
                item2.price = price;
                if (itemCartDAO.updatePrice(item2) > 0) {
                    Log.d(TAG, "update price success");
                } else {
                    Log.d(TAG, "update price fail");
                }

            }
        });
        holder.imgDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString().trim());
                currentQuantity--;
                if (currentQuantity < 1) {
                    return;
                }
                holder.tvQuantity.setText(String.valueOf(currentQuantity));
                ItemCart item1 = new ItemCart();
                item1.id = item.id;
                item1.quantity = currentQuantity;
                if (itemCartDAO.updateQuantity(item1) > 0) {
                    Log.d(TAG, "update quantity success");
                } else {
                    Log.d(TAG, "update quantity fail");
                }
                animationIncrease(currentQuantity, holder.imgIncrease);
                animationDecrease(currentQuantity, holder.imgDecrease);
                double price = item.price / item.quantity * currentQuantity;
                holder.tvPrice.setText(price + "đ");
                ItemCart item2 = new ItemCart();
                item2.id = item.id;
                item2.price = price;
                if (itemCartDAO.updatePrice(item2) > 0) {
                    Log.d(TAG, "update price success");
                } else {
                    Log.d(TAG, "update price fail");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        TextView tvQuantity;
        CheckBox cbCheck;
        ImageButton imgDecrease, imgIncrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            cbCheck = itemView.findViewById(R.id.cbSelected);
            imgDecrease = itemView.findViewById(R.id.imgDecrease);
            imgIncrease = itemView.findViewById(R.id.imgIncrease);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "" + listItem.get(getAdapterPosition()).idMerchant, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void animationDecrease(int currentQuantity, ImageButton imgDecrease) {
        if (currentQuantity > 1) {
            imgDecrease.setImageResource(R.drawable.ic_decrease_teal);
            imgDecrease.setBackgroundResource(R.drawable.bg_corner_img_button_increase);
        } else {
            imgDecrease.setImageResource(R.drawable.ic_decrease_gray);
            imgDecrease.setBackgroundResource(R.drawable.bg_corner_img_button_decrease);
        }
    }

    private void animationIncrease(int currentQuantity, ImageButton imgIncrease) {
        if (currentQuantity >= 20) {
            imgIncrease.setImageResource(R.drawable.ic_increase_gray);
            imgIncrease.setBackgroundResource(R.drawable.bg_corner_img_button_decrease);
        } else {
            imgIncrease.setImageResource(R.drawable.ic_increase_teal);
            imgIncrease.setBackgroundResource(R.drawable.bg_corner_img_button_increase);
        }
    }
}
