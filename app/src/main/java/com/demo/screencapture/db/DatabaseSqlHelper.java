package com.demo.screencapture.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/18
 */

public class DatabaseSqlHelper extends SQLiteOpenHelper {
    private static final String TAG = "ComplexDatabaseSqlHelper";
    //数据库的版本号
    private static final int DB_VERSION = 1;

    public SQLiteDatabase getDb() {
        return db;
    }

    private SQLiteDatabase db;
    //数据库db文件的路径,由调用者传入
    private static String mDBPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "giscoll.db";

    private static DatabaseSqlHelper mDBSqlHelper;

    public DatabaseSqlHelper(Context context, String DBpath) {
        super(context, DBpath, null, DB_VERSION);
        mDBPath = DBpath;
        if (db == null) {
            db = SQLiteDatabase.openOrCreateDatabase(DBpath, null);
        }
        onCreate(db);
    }

    public DatabaseSqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseSqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POINT = "create table tab_picture ("
                + "id integer primary key autoincrement,"
                + "timeTemp long, "
                + "isChange integer, "
                + "picStop integer, "
                + "cleanAllPic integer, "
                + "picBegin integer) ";
        //如果不存在该表，则创建该表
        if (!tableIsExist("tab_picture")) {
            db.execSQL(CREATE_POINT);
        }

    }
    /**
     * 获取数据库的帮助类
     *
     * @return
     */
    public static DatabaseSqlHelper getDBSqlHelper(Context context) {
        if (mDBSqlHelper == null) {
            synchronized (DatabaseSqlHelper.class) {
                if (mDBSqlHelper == null) {
                    mDBSqlHelper = new DatabaseSqlHelper(context, mDBPath);
                }
            }
        }
        return mDBSqlHelper;
    }

    /**
     * 根据sql查询数据库的方法
     *
     * @param sql
     * @return
     */
    public Cursor query(String sql) {
        return db.rawQuery(sql, null);
    }

    /**
     * 执行Sql语句
     *
     * @param sql
     */
    public void execSQL(String sql) {
        db.execSQL(sql);
    }

    /**
     * 判断表格是否存在
     *
     * @param tableName
     * @return
     */
    public boolean tableIsExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            //db = SQLiteDatabase.openDatabase(this.mDBPath,null,SQLiteDatabase.OPEN_READONLY);
            String sql = "select count(*) as c from Sqlite_master where type ='table' and name ='" + tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
            result = false;
        }
        return result;
    }

    /**
     * 判断表中是否包含某个字段
     *
     * @param tableName
     * @param columnName
     * @return
     */
    public boolean columnIsExistsInTable(String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            //  db = SQLiteDatabase.openDatabase(this.mDBPath, null, SQLiteDatabase.OPEN_READONLY);
            cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{tableName, "%" + columnName + "%"});
            result = null != cursor && cursor.moveToFirst();
        } catch (Exception ignored) {

        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    private void open() {
        if (db != null && !db.isOpen())
            db = SQLiteDatabase.openOrCreateDatabase(mDBPath, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
