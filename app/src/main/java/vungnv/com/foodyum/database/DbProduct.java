package vungnv.com.foodyum.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodyum.Constant;


public class DbProduct extends SQLiteOpenHelper implements Constant {

    public DbProduct(@Nullable Context context) {
        super(context, DB_PRODUCT, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableProduct = "create table Product(" +
                "stt INTEGER PRIMARY KEY AUTOINCREMENT," +
                "idUser TEXT REFERENCES User(id)," +
                "type TEXT REFERENCES Category(name)," +
                "img TEXT not null," +
                "name TEXT not null," +
                "description TEXT not null," +
                "timeDelay TEXT not null," +
                "price REAL not null," +
                "rate REAL not null," +
                "favourite INTEGER not null," +
                "mCheck INTEGER not null," +
                "status INTEGER not null," +
                "address TEXT not null," +
                "feedBack TEXT not null," +
                "quantity_sold INTEGER not null," +
                "quantityTotal INTEGER not null)" ;

        db.execSQL(createTableProduct);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableProduct = "drop table if exists Product";
        db.execSQL(dropTableProduct);

        onCreate(db);

    }
}
