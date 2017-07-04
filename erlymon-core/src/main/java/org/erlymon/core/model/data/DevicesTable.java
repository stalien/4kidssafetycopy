package org.erlymon.core.model.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 1/7/17.
 */
public class DevicesTable {

    @NonNull
    public static final String TABLE = "device_settings";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_NAME = "name";

    @NonNull
    public static final String COLUMN_UNIQUE_ID = "unique_id";

    @NonNull
    public static final String COLUMN_STATUS = "status";

    @NonNull
    public static final String COLUMN_LAST_UPDATE = "last_update";

    @NonNull
    public static final String COLUMN_POSITION_ID = "position_id";


    // Yep, with StorIO you can safely store queries as objects and reuse them, they are immutable
    @NonNull
    public static final Query QUERY_ALL = Query.builder()
            .table(TABLE)
            .build();

    @NotNull
    public static final RawQuery QUERY_DROP = RawQuery.builder()
            .query("DELETE FROM " + TABLE + ";")
            .build();

    // This is just class with Meta Data, we don't need instances
    private DevicesTable() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_UNIQUE_ID + " TEXT NOT NULL, "
                + COLUMN_STATUS + " TEXT NOT NULL, "
                + COLUMN_LAST_UPDATE + " NUMERIC DEFAULT NULL, "
                + COLUMN_POSITION_ID + " INTEGER DEFAULT NULL"
                + ");";
    }

    public static class DeviceSQLiteTypeMapping extends SQLiteTypeMapping<DeviceSettings> {
        public DeviceSQLiteTypeMapping() {
            super(new DeviceStorIOSQLitePutResolver(),
                    new DeviceStorIOSQLiteGetResolver(),
                    new DeviceStorIOSQLiteDeleteResolver());
        }
    }

    private static class DeviceStorIOSQLitePutResolver extends DefaultPutResolver<DeviceSettings> {
        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public InsertQuery mapToInsertQuery(@NonNull DeviceSettings object) {
            return InsertQuery.builder()
                    .table("device_settings")
                    .build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public UpdateQuery mapToUpdateQuery(@NonNull DeviceSettings object) {
            return UpdateQuery.builder()
                    .table("device_settings")
                    .where("_id = ?")
                    .whereArgs(object.id)
                    .build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public ContentValues mapToContentValues(@NonNull DeviceSettings object) {
            ContentValues contentValues = new ContentValues(6);

            contentValues.put("_id", object.id);
            contentValues.put("name", object.name);
            contentValues.put("unique_id", object.uniqueId);
            contentValues.put("status", object.status);
            contentValues.put("last_update", object.lastUpdate == null ? null : object.lastUpdate.getTime());
            contentValues.put("position_id", object.positionId);

            return contentValues;
        }
    }

    private static  class DeviceStorIOSQLiteGetResolver extends DefaultGetResolver<DeviceSettings> {
        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public DeviceSettings mapFromCursor(@NonNull Cursor cursor) {
            DeviceSettings object = new DeviceSettings();

            if(!cursor.isNull(cursor.getColumnIndex("_id"))) {
                object.id = cursor.getLong(cursor.getColumnIndex("_id"));
            }
            object.name = cursor.getString(cursor.getColumnIndex("name"));
            object.uniqueId = cursor.getString(cursor.getColumnIndex("unique_id"));
            object.status = cursor.getString(cursor.getColumnIndex("status"));
            if(!cursor.isNull(cursor.getColumnIndex("last_update"))) {
                object.lastUpdate = new Date(cursor.getLong(cursor.getColumnIndex("last_update")));
            }
            if(!cursor.isNull(cursor.getColumnIndex("position_id"))) {
                object.positionId = cursor.getLong(cursor.getColumnIndex("position_id"));
            }

            return object;
        }
    }

    private static class DeviceStorIOSQLiteDeleteResolver extends DefaultDeleteResolver<DeviceSettings> {
        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public DeleteQuery mapToDeleteQuery(@NonNull DeviceSettings object) {
            return DeleteQuery.builder()
                    .table("device_settings")
                    .where("_id = ?")
                    .whereArgs(object.id)
                    .build();
        }
    }
}