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

    public void deleteCart() {
        db.execSQL("delete from Cart");
    }

    public int updateStatus(@NonNull ItemCart obj) {
        ContentValues values = new ContentValues();
        values.put("status", obj.status);

        return db.update("Cart", values, "id=?", new String[]{obj.id});
    }
    public int updateStatusWithStt(@NonNull ItemCart obj) {
        ContentValues values = new ContentValues();
        values.put("status", obj.status);

        return db.update("Cart", values, "stt=?", new String[]{String.valueOf(obj.stt)});
    }
    public int getCurrentStatus(String id) {
        String sql = "SELECT * FROM Cart WHERE id=?";
        List<ItemCart> list = getData(sql, id);
        return list.get(0).status;
    }


    public int getCurrentQuantity(String id) {
        String sql = "SELECT * FROM Cart WHERE id=?";
        List<ItemCart> list = getData(sql, id);
        return list.get(0).quantity;
    }
    public double getCurrentPrice(String id) {
        String sql = "SELECT * FROM Cart WHERE id=?";
        List<ItemCart> list = getData(sql, id);
        return list.get(0).price;
    }

    public int updateQuantityAPrice(@NonNull ItemCart obj) {
        ContentValues values = new ContentValues();
        values.put("quantity", obj.quantity);
        values.put("price", obj.price);

        return db.update("Cart", values, "id=?", new String[]{obj.id});
    }


    public List<ItemCart> getALL(String idUser, int status) {
        String sql = "SELECT * FROM Cart WHERE idUser=? AND status=?";
        return getData(sql, idUser, String.valueOf(status));
    }
    public List<ItemCart> getALL(String idUser, int status1, int status2) {
        String sql = "SELECT * FROM Cart WHERE idUser=? AND status BETWEEN ? and ?";
        return getData(sql, idUser, String.valueOf(status1), String.valueOf(status2));
    }


    public List<ItemCart> getALL(String idUser) {
        String sql = "SELECT * FROM Cart WHERE idUser=?";
        return getData(sql, idUser);
    }

    public List<ItemCart> getALLByIdMerchant(String idMerchant, int status) {
        String sql = "SELECT * FROM Cart WHERE idMerchant=? AND status=?";
        return getData(sql, idMerchant, String.valueOf(status));
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
