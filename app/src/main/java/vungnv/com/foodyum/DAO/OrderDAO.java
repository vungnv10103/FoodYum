package vungnv.com.foodyum.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodyum.database.DbOrder;
import vungnv.com.foodyum.model.Order;


public class OrderDAO {
    private final SQLiteDatabase db;

    public OrderDAO(Context context) {
        DbOrder dbHelper = new DbOrder(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(Order obj) {
        ContentValues values = new ContentValues();
        values.put("id", obj.id);
        values.put("idUser", obj.idUser);
        values.put("idMerchant", obj.idMerchant);
        values.put("dateTime", obj.dateTime);
        values.put("items", obj.items);
        values.put("status", obj.status);
        values.put("price", obj.price);
        values.put("notes", obj.notes);

        return db.insert("Orders", null, values);
    }


    public List<Order> getALL(String type) {
        String sql = "SELECT * FROM Orders";
        return getData(sql, type);
    }


    public List<Order> getALLDefault() {
        String sql = "SELECT * FROM Orders";
        return getData(sql);
    }

    public List<Order> getALLBestSellingByType(String type) {
        String sql = "SELECT * FROM Orders WHERE type=? ORDER BY quantity_sold DESC";
        return getData(sql, type);
    }

    @SuppressLint("Range")
    private List<Order> getData(String sql, String... selectionArgs) {
        List<Order> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            Order obj = new Order();
            obj.stt = Integer.parseInt(cursor.getString(cursor.getColumnIndex("stt")));
            obj.id = cursor.getString(cursor.getColumnIndex("id"));
            obj.idUser = cursor.getString(cursor.getColumnIndex("idUser"));
            obj.idMerchant = cursor.getString(cursor.getColumnIndex("idMerchant"));
            obj.dateTime = cursor.getString(cursor.getColumnIndex("dateTime"));
            obj.items = cursor.getString(cursor.getColumnIndex("items"));
            obj.status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("status")));
            obj.price = Double.valueOf(cursor.getString(cursor.getColumnIndex("price")));
            obj.notes = cursor.getString(cursor.getColumnIndex("notes"));

            list.add(obj);

        }
        return list;

    }
}
