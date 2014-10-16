package com.blue_stingray.healthy_life_app.db;

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
    public static final String EMAIL = "email";
    private static final String USER_CREATE = tableCreateString(
            USER_TABLE,
            NAME + " text not null",
            EMAIL + " text not null"
    );

    public static final String CURRENT_USER_TABLE = "current_user";
    public static final String USER_ID = "user_id";
    private static final String CURRENT_USER_CREATE = tableCreateString(
        CURRENT_USER_TABLE,
            USER_ID + "integer not null",
            foreignKey(USER_ID, USER_TABLE)
    );

    public static final String DEVICE_TABLE = "device";
    public static final String DEVICE_NAME = "name";
    private static final String DEVICE_CREATE = tableCreateString(
            DEVICE_TABLE,
            DEVICE_NAME + " string not null",
            USER_ID + " integer not null",
            foreignKey(USER_ID, USER_TABLE)
    );

    public static final String CURRENT_DEVICE_TABLE = "current_device";
    public static final String DEVICE_ID = "device_id";
    private static final String CURRENT_DEVICE_CREATE = tableCreateString(
            CURRENT_DEVICE_TABLE,
            DEVICE_ID + "integer not null",
            foreignKey(DEVICE_ID, DEVICE_TABLE)
    );

    public static final String APPLICATION_TABLE = "application";
    public static final String PACKAGE_NAME = "package";
    public static final String APPLICATION_NAME = "name";
    public static final String VERSION_CODE = "version";
    private static final String APPLICATION_CREATE = tableCreateString(
            APPLICATION_TABLE,
            PACKAGE_NAME + " text not null",
            APPLICATION_NAME + " text not null",
            VERSION_CODE + " integer not null",
            DEVICE_ID + " integer not null",
            foreignKey(DEVICE_ID, DEVICE_TABLE)
    );

    public static final String APPLICATION_USAGE_TABLE = "application_usage";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String APPLICATION_ID = "application_id";
    private static final String APPLICATION_USAGE_CREATE = tableCreateString(
            APPLICATION_USAGE_TABLE,
            START_TIME + " integer not null",
            END_TIME + " integer not null",
            APPLICATION_ID + " integer not null",
            foreignKey(APPLICATION_ID, APPLICATION_TABLE)    
    ) +
            indexCreateString(END_TIME, APPLICATION_USAGE_TABLE);


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
                CURRENT_USER_CREATE, 
                DEVICE_CREATE, 
                APPLICATION_CREATE,
                APPLICATION_USAGE_CREATE,
                CURRENT_DEVICE_CREATE
        }) {
            db.execSQL(tableCreateString);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String tableName : new String[] {
                USER_TABLE,
                CURRENT_USER_TABLE,
                DEVICE_TABLE,
                APPLICATION_TABLE,
                APPLICATION_USAGE_TABLE,
                CURRENT_DEVICE_TABLE
        }) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
    }

    public void populateInitialDb(SQLiteDatabase db, User user) {

    }

    public void tearDownDb(SQLiteDatabase db) {

    }

    private void populateApplicationsTable(SQLiteDatabase db) {
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        db.beginTransaction();
        for (ResolveInfo info : pm.queryIntentActivities(mainIntent, 0) ) {
            ApplicationInfo appInfo = info.activityInfo.applicationInfo;
            ContentValues values = new ContentValues();
            values.put(PACKAGE_NAME, appInfo.packageName);
            values.put(APPLICATION_NAME, pm.getApplicationLabel(appInfo).toString());
            try {
                values.put(VERSION_CODE, pm.getPackageInfo(appInfo.packageName, 0).versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                Log.wtf(getClass().getSimpleName(), e);
            }
        }
        db.endTransaction();
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
