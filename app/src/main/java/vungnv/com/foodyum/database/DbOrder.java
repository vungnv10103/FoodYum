package vungnv.com.foodyum.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodyum.Constant;


public class DbOrder extends SQLiteOpenHelper implements Constant {
    public DbOrder(@Nullable Context context) {
        super(context, DB_ORDER, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableOrder = "create table Orders(" +
                "stt INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id TEXT not null," +
                "idUser TEXT not null," +
                "idMerchant TEXT not null," +
                "dateTime TEXT not null," +
                "items TEXT not null," +
                "status INTEGER not null," +
                "notes TEXT not null," +
                "price REAL not null)";
        db.execSQL(createTableOrder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableOrder = "drop table if exists Orders";
        db.execSQL(dropTableOrder);

        onCreate(db);
    }
}
