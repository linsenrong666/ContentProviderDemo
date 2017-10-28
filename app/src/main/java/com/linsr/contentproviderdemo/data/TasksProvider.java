package com.linsr.contentproviderdemo.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.linsr.contentproviderdemo.utils.JLog;

import java.util.HashMap;

/**
 * Description
 *
 * @author linsenrong on 2017/10/20 09:58
 */
public class TasksProvider extends ContentProvider {

    private static final String DATABASE_NAME = "task.db";
    private static final int DATABASE_VERSION = 1;

    private static class DbHelper extends SQLiteOpenHelper {

        private static volatile DbHelper mInstance;

        private DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mInstance = this;
        }

        static DbHelper getInstance(Context context) {
            if (mInstance == null) {
                synchronized (DbHelper.class) {
                    if (mInstance == null) {
                        mInstance = new DbHelper(context);
                    }
                }
            }
            return mInstance;
        }

        private static final String TEXT_TYPE = " TEXT";

        private static final String BOOLEAN_TYPE = " INTEGER";

        private static final String COMMA_SEP = ",";

        private static final String SQL_CREATE_TASK_ENTRIES =
                "CREATE TABLE " + Tasks.Task.TABLE_NAME + " (" +
                        Tasks.Task.TASK_ID + TEXT_TYPE + " PRIMARY KEY," +
                        Tasks.Task.TITLE + TEXT_TYPE + COMMA_SEP +
                        Tasks.Task.DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                        Tasks.Task.IS_COMPLETED + BOOLEAN_TYPE +
                        " )";

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TASK_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private DbHelper mDbHelper;
    private static final UriMatcher uriMatcher;

    private static final int TASK = 1;
    private static final int TASK_ID = 2;
    private static HashMap<String, String> taskMaps;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Tasks.AUTHORITY, "Task", TASK);
        uriMatcher.addURI(Tasks.AUTHORITY, "Task/#", TASK_ID);

        taskMaps = new HashMap<>();
        taskMaps.put(Tasks.Task._ID, Tasks.Task._ID);
        taskMaps.put(Tasks.Task._COUNT, Tasks.Task._COUNT);
        taskMaps.put(Tasks.Task.TASK_ID, Tasks.Task.TASK_ID);
        taskMaps.put(Tasks.Task.TITLE, Tasks.Task.TITLE);
        taskMaps.put(Tasks.Task.DESCRIPTION, Tasks.Task.DESCRIPTION);
        taskMaps.put(Tasks.Task.IS_COMPLETED, Tasks.Task.IS_COMPLETED);

    }

    @Override
    public boolean onCreate() {
        mDbHelper = DbHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        long rowId;
        Uri _uri;
        switch (uriMatcher.match(uri)) {
            //把数据库打开放到里面是想证明uri匹配完成
            case TASK:
                rowId = db.insert(Tasks.Task.TABLE_NAME, null, values);
                if (rowId > 0) {
                    //在前面已有的Uri后面追加ID
                    _uri = ContentUris.withAppendedId(uri, rowId);
                    break;
                }
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (_uri != null) {
            //通知数据已经发生改变
            notifyChange(_uri);
        }
        return _uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case TASK:
                count = db.delete(Tasks.Task.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                String taskId = uri.getPathSegments().get(1);
                count = db.delete(Tasks.Task.TABLE_NAME, Tasks.Task._ID + "=" + taskId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        notifyChange(uri);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case TASK:
                count = db.update(Tasks.Task.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TASK_ID:
                String taskId = uri.getPathSegments().get(1);
                count = db.update(Tasks.Task.TABLE_NAME, values, Tasks.Task._ID + "=" + taskId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (count != 0) {
            notifyChange(uri);
        }
        return count;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case TASK:
                sqb.setTables(Tasks.Task.TABLE_NAME);
                sqb.setProjectionMap(taskMaps);
                return queryDb(sqb, mDbHelper, uri, projection, selection, selectionArgs, sortOrder);
            case TASK_ID:
                sqb.setTables(Tasks.Task.TABLE_NAME);
                sqb.setProjectionMap(taskMaps);
                sqb.appendWhere(Tasks.Task._ID + "=" + uri.getPathSegments().get(1));
                return queryDb(sqb, mDbHelper, uri, projection, selection, selectionArgs, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private Cursor queryDb(SQLiteQueryBuilder sqb, SQLiteOpenHelper helper, Uri uri, String[] projection,
                           String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = sqb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private void notifyChange(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        if (contentResolver != null) {
            contentResolver.notifyChange(uri, null);
        } else {
            JLog.e("contentResolver is null");
        }

    }
}
