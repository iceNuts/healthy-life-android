package com.blue_stingray.healthy_life_app.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.blue_stingray.healthy_life_app.model.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Helper to read from our SQLite database
 */
@Singleton
public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context context;

    public static final String GOAL_TABLE = "goal_table";
    public static final String USER_ID = "user_id";
    public static final String PACKAGE_NAME = "package_name";
    public static final String TIME_LIMIT = "time_limit";
    public static final String LIMIT_DAY = "limit_day";
    private static final String GOAL_CREATE = tableCreateString(
            GOAL_TABLE,
            USER_ID + " text not null",
            PACKAGE_NAME + " text not null",
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
    public static final String USER_SESSION = "user_session";
    private static final String APPLICATION_USAGE_CREATE = tableCreateString(
            APPLICATION_USAGE_TABLE,
            USER_ID + " text not null",
            USAGE_YEAR + " integer not null",
            USAGE_MONTH + " integer not null",
            USAGE_DAY + " integer not null",
            USAGE_DAY_OF_WEEK + " integer not null",
            START_TIME + " integer not null",
            END_TIME + " integer not null",
            PACKAGE_NAME + " text not null",
            USER_SESSION + " text not null"
    ) + indexCreateString(END_TIME, APPLICATION_USAGE_TABLE);

    public static final String ALERT_RECORD_TABLE = "alert_record";
    public static final String APPLICATION_NAME = "application_name";
    public static final String USER_NAME = "user_name";
    public static final String ALERT_SUBJECT = "alert_subject";
    private static final String ALERT_RECORD_TABLE_CREATE = tableCreateString(
            ALERT_RECORD_TABLE,
            USER_ID + " text not null",
            APPLICATION_NAME + " text not null",
            USER_NAME + " text not null",
            ALERT_SUBJECT + " text not null"
    );

    public static final String WAKE_UP_RECORD_TABLE = "wake_up_record";
    private static final String WAKE_UP_RECORD_TABLE_CREATE = tableCreateString(
            WAKE_UP_RECORD_TABLE,
            USER_ID + " text not null",
            USAGE_YEAR + " integer not null",
            USAGE_MONTH + " integer not null",
            USAGE_DAY + " integer not null",
            USAGE_DAY_OF_WEEK + " integer not null",
            START_TIME + " integer not null",
            END_TIME + " integer not null",
            USER_SESSION + " text not null"
    );

    private static final String DB_NAME = "app.db";
    private static final int SCHEMA_VERSION = 8;


    @Inject
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String tableCreateString : new String[] {
                APPLICATION_USAGE_CREATE,
                GOAL_CREATE,
                ALERT_RECORD_TABLE_CREATE,
                WAKE_UP_RECORD_TABLE_CREATE
        }) {
            db.execSQL(tableCreateString);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String tableName : new String[] {
                APPLICATION_USAGE_TABLE,
                GOAL_TABLE,
                ALERT_RECORD_TABLE,
                WAKE_UP_RECORD_TABLE
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
