package vungnv.com.foodyum.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodyum.Constant;


public class DbCart extends SQLiteOpenHelper implements Constant {
    public DbCart(@Nullable Context context) {
        super(context, DB_CART, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableCart = "create table Cart(" +
                "stt INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id TEXT not null," +
                "name TEXT not null," +
                "idUser TEXT not null," +
                "dateTime TEXT not null," +
                "quantity INTEGER not null," +
                "status INTEGER not null," +
                "price REAL not null)";
        db.execSQL(createTableCart);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableCart = "drop table if exists Cart";
        db.execSQL(dropTableCart);

        onCreate(db);
    }
}
