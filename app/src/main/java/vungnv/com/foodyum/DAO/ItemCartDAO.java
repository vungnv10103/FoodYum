package vungnv.com.foodyum.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public long insert(ItemCart obj) {
        ContentValues values = new ContentValues();
        values.put("id", obj.id);
        values.put("name", obj.name);
        values.put("idUser", obj.idUser);
        values.put("dateTime", obj.dateTime);
        values.put("quantity", obj.quantity);
        values.put("status", obj.status);
        values.put("price", obj.price);

        return db.insert("Cart", null, values);
    }


    public List<ItemCart> getALL(String type) {
        String sql = "SELECT * FROM Cart";
        return getData(sql, type);
    }


    public List<ItemCart> getALLDefault() {
        String sql = "SELECT * FROM Cart";
        return getData(sql);
    }

    public List<ItemCart> getALLBestSellingByType(String type) {
        String sql = "SELECT * FROM Cart WHERE type=? ORDER BY quantity_sold DESC";
        return getData(sql, type);
    }

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
            obj.dateTime = cursor.getString(cursor.getColumnIndex("dateTime"));
            obj.quantity = Integer.parseInt(cursor.getString(cursor.getColumnIndex("quantity")));
            obj.status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("status")));
            obj.price = Double.valueOf(cursor.getString(cursor.getColumnIndex("price")));

            list.add(obj);

        }
        return list;

    }
}
