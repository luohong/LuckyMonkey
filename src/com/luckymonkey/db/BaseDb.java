package com.luckymonkey.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class BaseDb {
	
	public static final String CREATE_TABLE_PREFIX = "CREATE TABLE IF NOT EXISTS ";
	public static final String DROP_TABLE_PREFIX = "DROP TABLE IF EXISTS ";
	
	public static interface COLUMN_TYPE {
		public static final String INTEGER = " INTEGER ";
		public static final String LONG = " LONG ";
		public static final String TEXT = " TEXT ";
	}
	
	public static final String PRIMARY_KEY = " PRIMARY KEY ";
	public static final String PRIMARY_KEY_AUTOINCREMENT = " PRIMARY KEY AUTOINCREMENT ";
	
	public static final String BRACKET_LEFT = " ( ";
	public static final String BRACKET_RIGHT = " );";
	public static final String COMMA = ",";
	
	protected Cursor cursor = null;

	protected DbHelper helper = null;

	protected SQLiteDatabase db = null;

	public BaseDb(Context context) {
		helper = DbHelper.getInstance(context);
		db = helper.getWritableDatabase();
	}

	public void beginTransaction() {
		if (db == null || !db.isOpen()) {
			db = helper.getWritableDatabase();
		}
		db.beginTransaction();
	}

	public void endTransaction() {
		if (db == null || !db.isOpen()) {
			db = helper.getWritableDatabase();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	protected void checkDb() {
		if (db == null || !db.isOpen()) {
			db = helper.getWritableDatabase();
		}
	}

	public void closeDbAndCursor() {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		/*
		 * if (db != null) { db.close(); db = null; }
		 */
	}

	protected void clearAllData() {
		try {
			checkDb();
			String sql = "delete from " + getTableName() + ";";
			Log.e("SQL", sql);
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDbAndCursor();
		}
	}

	protected boolean isHasData() {
		checkDb();
		String sql = "select * from " + getTableName() + " limit 1";
		cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			int count = cursor.getCount();
			return count > 0 ? true : false;
		}
		return false;
	}

    protected abstract String getTableName();
    
    protected abstract String getCreateTableSQL();
    
    protected abstract String getDropTableSQL();
}
