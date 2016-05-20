package cn.com.easytaxi.phone.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.com.easytaxi.common.Config;
import cn.com.easytaxi.common.Pair;

public class DaoAdapter {

	private Context mContext;
	protected SQLiteDatabase mSQLiteDatabase;
	private DaoHelper dh;

	private final String DB_NAME ="abc1.so";

	public DaoAdapter(Context context) {
		mContext = context;
		dh = new DaoHelper(mContext, DB_NAME, null, 7);
		mSQLiteDatabase = dh.getWritableDatabase();
	}

	public void close() {
		if (dh != null)
			dh.close();
	}

	// 司机
	public void deleteDrivers(List<String> phones) {
		for (String phone : phones) {
			mSQLiteDatabase.execSQL("delete from t_driver where _PHONE=?", new String[] { phone });
		}
	}

	private SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm");

	public void saveHistory(String cityId, String name, String phone) {

		try {
			Calendar c = Calendar.getInstance();
			String d = f.format(c.getTime());
			mSQLiteDatabase.execSQL("update t_driver set _CALL_NUM=_CALL_NUM+1,_LAST_TIME='" + d + "' where _PHONE='" + phone + "'");
			mSQLiteDatabase.execSQL("update t_tel set _CALL_NUM=_CALL_NUM+1 where _PHONE='" + phone + "'");

			long stamp = c.getTimeInMillis();

			ContentValues cv = new ContentValues();
			cv.put("_CITY_ID", cityId);
			cv.put("_NAME", name);
			cv.put("_CALL_TIME", stamp);
			cv.put("_PHONE", phone);
			mSQLiteDatabase.insert("t_history", null, cv);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void clearHistory(String cityId) {
		mSQLiteDatabase.execSQL("delete from t_history where _CITY_ID=" + cityId);
	}

	public JSONArray getHistoryList(String cityId) {

		Cursor cursor = null;
		try {
			JSONArray rows = new JSONArray();
			String sql = "SELECT _CITY_ID,_CALL_TIME,_NAME,_PHONE FROM t_history WHERE _CITY_ID=? ORDER BY _CALL_TIME DESC";
			cursor = mSQLiteDatabase.rawQuery(sql, new String[] { cityId });
			if (cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					try {
						JSONObject row = new JSONObject();
						row.put("_CITY_ID", cursor.getString(0));
						row.put("_CALL_TIME", cursor.getString(1));
						row.put("_NAME", cursor.getString(2));
						row.put("_PHONE", cursor.getString(3));
						rows.put(row);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return rows;
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	public List<Pair<String, String>> getDrivers(String cityID) {
		Cursor cursor = null;
		try {
			List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
			String sql = "SELECT _NAME,_PHONE,_TAXI_NUM,_SEX,_CALL_NUM,_LAST_TIME FROM t_driver WHERE _CITY_ID=? order by _CALL_NUM desc";
			cursor = mSQLiteDatabase.rawQuery(sql, new String[] { cityID });
			if (cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					list.add(new Pair<String, String>(cursor.getString(0), cursor.getString(1), new Object[] { cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getString(5) }));
				}
			}
			return list;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public int getPhoneSize() {
		Cursor mCount = mSQLiteDatabase.rawQuery("SELECT count(*) FROM t_tel", null);
		mCount.moveToFirst();
		return mCount.getInt(0);
	}

	public boolean addDriver(String name, String phone, String taxinum, String cityId, int sex) {
		Cursor mCount = null;
		try {
			mCount = mSQLiteDatabase.rawQuery("SELECT count(*) FROM t_driver where _PHONE=?", new String[] { phone });
			mCount.moveToFirst();
			int count = mCount.getInt(0);
			if (count > 0) {
				return false;
			} else {
				ContentValues cv = new ContentValues();
				cv.put("_CITY_ID", cityId);
				cv.put("_PHONE", phone);
				cv.put("_NAME", name);
				cv.put("_TAXI_NUM", taxinum);
				cv.put("_SEX", sex);
				cv.put("_CALL_NUM", 0);
				mSQLiteDatabase.insert("t_driver", null, cv);
				return true;
			}
		} finally {
			if (mCount != null)
				mCount.close();
		}
	}
	
	//判断电话是否已被收藏,true已收藏，false未被收藏
	public boolean isFav(String phone) {
		Cursor mCount = null;
		try {
			mCount = mSQLiteDatabase.rawQuery("SELECT count(*) FROM t_driver where _PHONE=?", new String[] { phone });
			mCount.moveToFirst();
			int count = mCount.getInt(0);
			return count > 0;
		}finally {
			if (mCount != null)
				mCount.close();
		}
	}

	// 电话列表操作
	public void savePhoneList(JSONArray datas) {
		try {
			mSQLiteDatabase.beginTransaction();
			mSQLiteDatabase.execSQL("delete from t_tel");
			for (int i = 0; i < datas.length(); i++) {
				JSONObject data = datas.getJSONObject(i);
				ContentValues cv = new ContentValues();
				cv.put("_CITY_ID", data.getInt("_CITY_ID"));
				cv.put("_COMPANY", data.getString("_COMPANY"));
				cv.put("_PHONE", data.getString("_PHONE"));
				mSQLiteDatabase.insert("t_tel", null, cv);
			}
			mSQLiteDatabase.setTransactionSuccessful();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			mSQLiteDatabase.endTransaction();
		}
	}

	public List<Pair<String, String>> getPhoneList(String cityID) {
		Cursor cursor = null;
		try {
			List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
			String sql = "SELECT _COMPANY,_PHONE,_CALL_NUM FROM t_tel WHERE _CITY_ID=? ORDER BY _CALL_NUM DESC";
			cursor = mSQLiteDatabase.rawQuery(sql, new String[] { cityID });
			if (cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					list.add(new Pair<String, String>(cursor.getString(0), cursor.getString(1), cursor.getInt(2)));
				}
			}
			return list;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

}
