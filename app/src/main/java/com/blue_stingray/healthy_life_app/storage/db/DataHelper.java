package com.blue_stingray.healthy_life_app.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.inject.Inject;

import java.util.Dictionary;

/**
 * Created by BillZeng on 11/24/14.
 */
public class DataHelper {

    @Inject
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private static DataHelper instance = null;

    public static synchronized DataHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataHelper();
            instance.dbHelper = new DatabaseHelper(context);
            instance.db = instance.dbHelper.getWritableDatabase();
        }
        return instance;
    }

    public void createNewGoal(Dictionary newGoal) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                instance.db.beginTransaction();
                ContentValues newStat = new ContentValues();
//                newStat.put();

                instance.db.setTransactionSuccessful();
                instance.db.endTransaction();
            }
        }).start();
    }

}
