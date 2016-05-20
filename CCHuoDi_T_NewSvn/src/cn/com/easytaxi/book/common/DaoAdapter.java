package cn.com.easytaxi.book.common;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.com.easytaxi.common.Config;

public class DaoAdapter {

	private Context mContext;
	protected SQLiteDatabase mSQLiteDatabase;
	private DaoHelper dh;

	private final String DB_NAME ="abx3.so";

	public DaoAdapter(Context context) {
		mContext = context;
		dh = new DaoHelper(mContext, DB_NAME, null, 1);
		mSQLiteDatabase = dh.getWritableDatabase();
	}

	public void close() {
		if (dh != null)
			dh.close();
	}

	//
	public void saveHistory(String src, String dest) {
		Cursor cursor = null;
		try {
			mSQLiteDatabase.beginTransaction();

			ContentValues cv = new ContentValues();
			cv.put("_SRC_ADDR", src);
			cv.put("_DEST_ADDR", dest);
			mSQLiteDatabase.insert("t_addr", null, cv);
			mSQLiteDatabase.setTransactionSuccessful();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			mSQLiteDatabase.endTransaction();
			if (cursor != null)
				cursor.close();
		}
	}

	private List<String> getHistoryList(String field) {
		Cursor cursor = null;
		try {
			List<String> list = new ArrayList<String>();
			String sql = "SELECT distinct " + field + " FROM t_addr order by _ID desc limit 5";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					list.add(cursor.getString(0));
				}
			}
			return list;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public List<String> getDestHistoryList() {
		return getHistoryList("_DEST_ADDR");
	}

	public List<String> getSrcHistoryList() {
		return getHistoryList("_SRC_ADDR");
	}

}
