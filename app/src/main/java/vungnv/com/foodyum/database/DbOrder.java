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
        String createTableOrder = "create table OrderHistory(" +
                "id TEXT PRIMARY KEY," +
                "idUser TEXT not null," +
                "dateTime TEXT not null," +
                "items TEXT not null," +
                "quantity INTEGER not null," +
                "status INTEGER not null," +
                "waitingTime INTEGER not null," +
                "notes TEXT ," +
                "price REAL not null)";
        db.execSQL(createTableOrder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableOrder = "drop table if exists OrderHistory";
        db.execSQL(dropTableOrder);

        onCreate(db);
    }
}
