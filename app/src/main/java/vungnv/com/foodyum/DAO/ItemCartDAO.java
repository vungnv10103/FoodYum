package vungnv.com.foodyum.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodyum.database.DbCart;
import vungnv.com.foodyum.model.ItemCart;
import vungnv.com.foodyum.model.User;


public class ItemCartDAO {
    private final SQLiteDatabase db;

    public ItemCartDAO(Context context) {
        DbCart dbHelper = new DbCart(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(@NonNull ItemCart obj) {
        ContentValues values = new ContentValues();
        values.put("id", obj.id);
        values.put("name", obj.name);
        values.put("idUser", obj.idUser);
        values.put("idMerchant", obj.idMerchant);
        values.put("dateTime", obj.dateTime);
        values.put("quantity", obj.quantity);
        values.put("status", obj.status);
        values.put("price", obj.price);
        values.put("notes", obj.notes);

        return db.insert("Cart", null, values);
    }

    public int updateStatus(@NonNull ItemCart obj) {
        ContentValues values = new ContentValues();
        values.put("status", obj.status);

        return db.update("Cart", values, "id=?", new String[]{obj.id});
    }

    public int updateQuantity(@NonNull ItemCart obj) {
        ContentValues values = new ContentValues();
        values.put("quantity", obj.quantity);

        return db.update("Cart", values, "id=?", new String[]{obj.id});
    }

    public int updatePrice(@NonNull ItemCart obj) {
        ContentValues values = new ContentValues();
        values.put("price", obj.price);

        return db.update("Cart", values, "id=?", new String[]{obj.id});
    }


    public List<ItemCart> getALL(String idUser, int status) {
        String sql = "SELECT * FROM Cart WHERE idUser=? AND status=?";
        return getData(sql, idUser, String.valueOf(status));
    }


    public List<ItemCart> getALL(String idUser) {
        String sql = "SELECT * FROM Cart WHERE idUser=?";
        return getData(sql, idUser);
    }

    public List<ItemCart> getALLBestSellingByType(String type) {
        String sql = "SELECT * FROM Cart WHERE type=? ORDER BY quantity_sold DESC";
        return getData(sql, type);
    }

    @NonNull
    @SuppressLint("Range")
    private List<ItemCart> getData(String sql, String... selectionArgs) {
        List<ItemCart> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            ItemCart obj = new ItemCart();
            obj.stt = Integer.parseInt(cursor.getString(cursor.getColumnIndex("stt")));
            obj.id = cursor.getString(cursor.getColumnIndex("id"));
            obj.name = cursor.getString(cursor.getColumnIndex("name"));
            obj.idUser = cursor.getString(cursor.getColumnIndex("idUser"));
            obj.idMerchant = cursor.getString(cursor.getColumnIndex("idMerchant"));
            obj.dateTime = cursor.getString(cursor.getColumnIndex("dateTime"));
            obj.quantity = Integer.parseInt(cursor.getString(cursor.getColumnIndex("quantity")));
            obj.status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("status")));
            obj.price = Double.valueOf(cursor.getString(cursor.getColumnIndex("price")));
            obj.notes = cursor.getString(cursor.getColumnIndex("notes"));

            list.add(obj);

        }
        return list;

    }
}
