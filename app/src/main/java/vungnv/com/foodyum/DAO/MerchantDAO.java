package vungnv.com.foodyum.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodyum.database.DbMerchant;
import vungnv.com.foodyum.database.DbUser;
import vungnv.com.foodyum.model.Merchant;
import vungnv.com.foodyum.model.User;


public class MerchantDAO {
    private final SQLiteDatabase db;

    public MerchantDAO(Context context) {
        DbMerchant dbHelper = new DbMerchant(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(Merchant obj) {
        ContentValues values = new ContentValues();
        values.put("id", obj.id);
        values.put("status", obj.status);

        return db.insert("Merchant", null, values);
    }



    public void deleteTable() {
        db.execSQL("delete from Merchant");
    }


    public List<Merchant> getALL() {
        String sql = "SELECT * FROM Merchant";
        return getData(sql);
    }

    @SuppressLint("Range")
    private List<Merchant> getData(String sql, String... selectionArgs) {
        List<Merchant> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            Merchant obj = new Merchant();
            obj.status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("status")));
            obj.id = cursor.getString(cursor.getColumnIndex("id"));

            list.add(obj);

        }
        return list;

    }
}
