package com.blue_stingray.healthy_life_app.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.blue_stingray.healthy_life_app.model.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Helper to read from our SQLite database
 */
@Singleton
public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context context;

    public static final String USER_TABLE = "user";
    public static final String NAME = "name";
    public static final String USER_ID = "user_id";
    public static final String EMAIL = "email";
    private static final String USER_CREATE = tableCreateString(
            USER_TABLE,
            USER_ID + " integer not null",
            NAME + " text not null",
            EMAIL + " text not null"
    );

    public static final String GOAL_TABLE = "goal_table";
    public static final String PACKAGE_NAME = "package_name";
    public static final String LIMIT_TYPE = "limit_type";
    public static final String TIME_LIMIT = "time_limit";
    public static final String LIMIT_DAY = "limit_day";
    private static final String GOAL_CREATE = tableCreateString(
            GOAL_TABLE,
            PACKAGE_NAME + " text not null",
            LIMIT_TYPE + " integer not null",
            TIME_LIMIT + " integer not null",
            LIMIT_DAY + " integer not null"
    );

    public static final String APPLICATION_USAGE_TABLE = "application_usage";
    public static final String USAGE_YEAR = "usage_year";
    public static final String USAGE_MONTH = "usage_month";
    public static final String USAGE_DAY = "usage_day";
    public static final String USAGE_DAY_OF_WEEK = "usage_day_of_week";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    private static final String APPLICATION_USAGE_CREATE = tableCreateString(
            APPLICATION_USAGE_TABLE,
            USAGE_YEAR + " integer not null",
            USAGE_MONTH + " integer not null",
            USAGE_DAY + " integer not null",
            USAGE_DAY_OF_WEEK + " integer not null",
            START_TIME + " integer not null",
            END_TIME + " integer not null",
            PACKAGE_NAME + " text not null",
            USER_ID + " integer not null",
            foreignKey(USER_ID, USER_TABLE),
            foreignKey(PACKAGE_NAME, GOAL_TABLE)
    ) + indexCreateString(END_TIME, APPLICATION_USAGE_TABLE);

    private static final String DB_NAME = "app.db";
    private static final int SCHEMA_VERSION = 1;


    @Inject
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String tableCreateString : new String[] {
                USER_CREATE, 
                APPLICATION_USAGE_CREATE,
                GOAL_CREATE
        }) {
            db.execSQL(tableCreateString);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String tableName : new String[] {
                USER_TABLE,
                APPLICATION_USAGE_TABLE,
                GOAL_TABLE
        }) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
        onCreate(db);
    }

    public void populateInitialDb(SQLiteDatabase db, User user) {

    }

    public void tearDownDb(SQLiteDatabase db) {

    }

    private static String tableCreateString(String tableName, String... columns) {
        StringBuilder sb = new StringBuilder()
                .append("create table ")
                .append(tableName)
                .append("(id integer primary key autoincrement");
        for (String column : columns) {
            sb.append(", ");
            sb.append(column);
        }
        sb.append(");");
        return sb.toString();
    }

    private static String indexCreateString(String column, String table) {
        return "create index " + table + '_' + column + " on " + table + " (" + column + ");";
    }
    
    private static String foreignKey(String column, String table) {
        return "foreign key(" + column + ") references " + table + "(id)";
    }
}
