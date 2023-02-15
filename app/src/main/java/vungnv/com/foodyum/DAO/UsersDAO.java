package vungnv.com.foodyum.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodyum.database.DbUser;
import vungnv.com.foodyum.model.User;


public class UsersDAO {
    private final SQLiteDatabase db;

    public UsersDAO(Context context) {
        DbUser dbHelper = new DbUser(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(User obj) {
        ContentValues values = new ContentValues();
        values.put("id", obj.id);
        values.put("img", obj.img);
        values.put("name", obj.name);
        values.put("email", obj.email);
        values.put("pass", obj.pass);
        values.put("phoneNumber", obj.phoneNumber);
        values.put("searchHistory", obj.searchHistory);
        values.put("favouriteRestaurant", obj.favouriteRestaurant);
        values.put("address", obj.address);
        values.put("coordinates", obj.coordinates);
        values.put("feedback", obj.feedback);

        return db.insert("User", null, values);
    }


    public int updateProfile(User obj) {
        ContentValues values = new ContentValues();
        values.put("name", obj.name);
        values.put("phoneNumber", obj.phoneNumber);
        values.put("address", obj.address);

        return db.update("User", values, "email=?", new String[]{obj.email});
    }

    public int updateAll(User obj) {
        ContentValues values = new ContentValues();
        values.put("img", obj.img);
        values.put("name", obj.name);
        values.put("pass", obj.pass);
        values.put("phoneNumber", obj.phoneNumber);
        values.put("coordinates", obj.coordinates);
        values.put("address", obj.address);

        return db.update("User", values, "email=?", new String[]{obj.email});
    }

    public int updateImg(User obj) {
        ContentValues values = new ContentValues();
        values.put("img", obj.img);
        return db.update("User", values, "email=?", new String[]{obj.email});
    }

    public int updateFeedBack(User obj) {
        ContentValues values = new ContentValues();
        values.put("feedback", obj.feedback);
        return db.update("User", values, "email=?", new String[]{obj.email});
    }

    public int updatePass(User obj) {
        ContentValues values = new ContentValues();
        values.put("pass", obj.pass);
        return db.update("User", values, "email=?", new String[]{obj.email});

    }

    public int updateSearchHistory(User obj) {
        ContentValues values = new ContentValues();
        values.put("searchHistory", obj.searchHistory);
        return db.update("User", values, "email=?", new String[]{obj.email});
    }

    public String getSearchHistory(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<User> list = getData(sql, email);
        return list.get(0).searchHistory;
    }

    public String getName(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<User> list = getData(sql, email);
        return list.get(0).name;
    }

    public String getPhone(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<User> list = getData(sql, email);
        return list.get(0).phoneNumber;
    }

    public String getAddress(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<User> list = getData(sql, email);
        return list.get(0).address;
    }

    public String getFeedback(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<User> list = getData(sql, email);
        return list.get(0).feedback;
    }

    public String getListRestaurantFavourite(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<User> list = getData(sql, email);
        return list.get(0).favouriteRestaurant;
    }

    public int updateListRestaurantFavourite(User obj) {
        ContentValues values = new ContentValues();
        values.put("favouriteRestaurant", obj.favouriteRestaurant);
        return db.update("User", values, "email=?", new String[]{obj.email});
    }

    public long updateListRestaurantFavouritePro(User obj) {
        ContentValues values = new ContentValues();
        values.put("favouriteRestaurant", obj.favouriteRestaurant);
        return db.replace("User", null, values);
    }


    public String autoFillPassWord(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<User> list = getData(sql, email);
        return list.get(0).pass;
    }

    public String getUriImg(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<User> list = getData(sql, email);
        if (list.size() != 0) {
            return list.get(0).img;
        }
        return "";
    }

    public String getIDUser(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<User> list = getData(sql, email);
        return list.get(0).id;
    }

    public String getCurrentPass(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<User> list = getData(sql, email);
        return list.get(0).pass;
    }

    public int delete(int id) {
        return db.delete("User", "id=?", new String[]{String.valueOf(id)});
    }


    public List<User> getALL() {
        String sql = "SELECT * FROM User";
        return getData(sql);
    }

    public List<User> getALLByEmail(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        return getData(sql, email);
    }


    @SuppressLint("Range")
    private List<User> getData(String sql, String... selectionArgs) {
        List<User> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            User obj = new User();
            obj.stt = Integer.parseInt(cursor.getString(cursor.getColumnIndex("stt")));
            obj.id = cursor.getString(cursor.getColumnIndex("id"));
            obj.img = cursor.getString(cursor.getColumnIndex("img"));
            obj.name = cursor.getString(cursor.getColumnIndex("name"));
            obj.email = cursor.getString(cursor.getColumnIndex("email"));
            obj.pass = cursor.getString(cursor.getColumnIndex("pass"));
            obj.phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
            obj.address = cursor.getString(cursor.getColumnIndex("address"));
            obj.searchHistory = cursor.getString(cursor.getColumnIndex("searchHistory"));
            obj.favouriteRestaurant = cursor.getString(cursor.getColumnIndex("favouriteRestaurant"));
            obj.coordinates = cursor.getString(cursor.getColumnIndex("coordinates"));
            obj.feedback = cursor.getString(cursor.getColumnIndex("feedback"));

            list.add(obj);

        }
        return list;

    }

    // check login in sqlite
    public boolean checkAccountExist(String email, String pass) {
        String sql = "SELECT * FROM User WHERE email=? AND pass=?";
        List<User> list = getData(sql, email, pass);
        return list.size() != 0;

    }
}
