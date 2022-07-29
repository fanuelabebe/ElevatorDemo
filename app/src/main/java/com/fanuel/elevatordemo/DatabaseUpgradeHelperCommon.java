package com.fanuel.elevatordemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fanuel.elevatordemo.dbmodels.DaoMaster;

public class DatabaseUpgradeHelperCommon extends DaoMaster.OpenHelper {
    public DatabaseUpgradeHelperCommon(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
