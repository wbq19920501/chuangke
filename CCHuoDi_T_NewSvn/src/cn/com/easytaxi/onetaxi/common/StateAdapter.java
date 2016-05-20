package cn.com.easytaxi.onetaxi.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.com.easytaxi.common.Config;

public class StateAdapter {

	private Context mContext;
	protected SQLiteDatabase mSQLiteDatabase;
	private StateHelper dh;

	private final String DB_NAME = "ttc3.so";

	public StateAdapter(Context context) {
		mContext = context;
		dh = new StateHelper(mContext, DB_NAME, null, 1);
		mSQLiteDatabase = dh.getWritableDatabase();
	}

	public void close() {
		if (dh != null)
			dh.close();
	}

	// ×´Ì¬ÁÐ±í²Ù×÷
	public void setState(String key, String value) {
		deleteState(key);
		ContentValues cv = new ContentValues();
		cv.put("key", key);
		cv.put("value", value);
		mSQLiteDatabase.insert("t_state", null, cv);
	}

	public void deleteState(String key) {
		try {
			mSQLiteDatabase.execSQL("delete from t_state where key=?", new String[] { key });
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void clearState() {
		try {
			mSQLiteDatabase.execSQL("delete from t_state");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public String getState(String key) {
		Cursor cursor = null;
		try {
			String sql = "SELECT value FROM t_state where key=? limit(1)";
			cursor = mSQLiteDatabase.rawQuery(sql, new String[] { key });
			if (cursor.moveToFirst()) {
				String r = cursor.getString(0);
				if (r != null && !r.equals("null")) {
					return r;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public int getIntState(String key) {
		String str = getState(key);
		return str == null ? 0 : Integer.valueOf(str);
	}
}
