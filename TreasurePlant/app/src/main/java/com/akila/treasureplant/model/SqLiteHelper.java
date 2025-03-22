package com.akila.treasureplant.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqLiteHelper extends SQLiteOpenHelper {
    public SqLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE address (\n" +
                "    id          INTEGER     PRIMARY KEY AUTOINCREMENT,\n" +
                "    line1       TEXT        NOT NULL,\n" +
                "    line2       TEXT        NOT NULL,\n" +
                "    postal_code INTEGER (7) NOT NULL,\n" +
                "    name        TEXT        NOT NULL,\n" +
                "    mobile      TEXT        NOT NULL\n" +
                ");\n");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
