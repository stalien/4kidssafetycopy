package org.erlymon.core.model.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 1/7/17.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(@NonNull Context context) {
        super(context, "erlymondb", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
       // db.execSQL(ServersTable.getCreateTableQuery());
       // db.execSQL(UsersTable.getCreateTableQuery());
        db.execSQL(DevicesTable.getCreateTableQuery());
       // db.execSQL(PositionsTable.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // no impl
    }
}