package vungnv.com.foodyum.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodyum.Constant;


public class DbUser extends SQLiteOpenHelper implements Constant {

    public DbUser(@Nullable Context context) {
        super(context, DB_USER, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableUser = "create table User(" +
                "stt INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id TEXT not null," +
                "img TEXT ," +
                "name TEXT ," +
                "email TEXT not null," +
                "pass TEXT not null," +
                "phoneNumber TEXT ," +
                "searchHistory TEXT ," +
                "feedback TEXT ," +
                "coordinates TEXT ," +
                "address TEXT not null)";
        db.execSQL(createTableUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableUser = "drop table if exists User";
        db.execSQL(dropTableUser);

        onCreate(db);

    }
}
