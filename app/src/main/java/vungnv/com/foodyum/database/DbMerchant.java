package vungnv.com.foodyum.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodyum.Constant;


public class DbMerchant extends SQLiteOpenHelper implements Constant {

    public DbMerchant(@Nullable Context context) {
        super(context, DB_MERCHANT, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableMerchant = "create table Merchant(" +
                "id TEXT PRIMARY KEY," +
                "status INTEGER not null)";
        db.execSQL(createTableMerchant);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableMerchant = "drop table if exists Merchant";
        db.execSQL(dropTableMerchant);

        onCreate(db);

    }
}
