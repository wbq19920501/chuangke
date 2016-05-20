package cn.com.easytaxi.airport.store;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.com.easytaxi.airport.bean.AirportBean;
import cn.com.easytaxi.platform.common.Cities.City;

public class AirportSessionAdapter {

	private Context mContext;
	protected SQLiteDatabase mSQLiteDatabase;
	private AirportSessionHelper dh;
	private final String DB_NAME = "airport.so";

	public AirportSessionAdapter(Context context) {
		mContext = context;
		dh = new AirportSessionHelper(mContext, DB_NAME, null, 1); 
		mSQLiteDatabase = dh.getWritableDatabase();
	}

	/**
	 * 初始化存储JSONArray数据到t_airport表
	 * @param datas
	 */
	public void saveAirportList(JSONArray datas){
		try {
			mSQLiteDatabase.beginTransaction();

			mSQLiteDatabase.execSQL("delete from t_airport");
			for (int i = 0; i < datas.length(); i++) {
				JSONObject data = datas.getJSONObject(i);
				ContentValues cv = new ContentValues();
				cv.put("_ID", data.getInt("_ID"));
				cv.put("_NAME", data.getString("_NAME"));
				cv.put("_LAT", data.getInt("_LAT"));
				cv.put("_LNG", data.getString("_LNG"));
				mSQLiteDatabase.insert("t_airport", null, cv);
			}
			mSQLiteDatabase.setTransactionSuccessful();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			mSQLiteDatabase.endTransaction();
			close();
		}
	}
	
	
	/**
	 * 初始化存储list数据到t_airport表
	 * @param datas
	 */
	public void saveAirportList(ArrayList<AirportBean> datas){
		try {
			mSQLiteDatabase.beginTransaction();

			mSQLiteDatabase.execSQL("delete from t_airport");
			for (int i = 0; i < datas.size(); i++) {
				AirportBean data = datas.get(i);
				ContentValues cv = new ContentValues();
				cv.put("_ID", data.id);
				cv.put("_NAME", data.name);
				cv.put("_LAT", data.latitude);
				cv.put("_LNG", data.longitude);
				mSQLiteDatabase.insert("t_airport", null, cv);
			}
			mSQLiteDatabase.setTransactionSuccessful();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			mSQLiteDatabase.endTransaction();
			close();
		}
	}
	
	/**
	 * 获取机场列表
	 * @param flag
	 * @return
	 */
	public ArrayList<AirportBean> getAirportList() {
		ArrayList<AirportBean> list = new ArrayList<AirportBean>();
		Cursor cursor = null;
		try {

			String sql = null;
			sql = "SELECT _ID,_NAME,_LAT,_LNG FROM t_airport order by _ID desc";

			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					AirportBean airport = new AirportBean();
					airport.id = cursor.getInt(0);
					airport.name = cursor.getString(1);
					airport.latitude = cursor.getInt(2);
					airport.longitude = cursor.getInt(3);
					list.add(airport);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (cursor != null){
				cursor.close();
			}
			close();
		}
		return list;
	}
	
	/**
	 * 从表中删除数据
	 * @param key
	 */
	public void delete(String key,String value) {
		mSQLiteDatabase.execSQL("delete from t_airport where "+key+"=?", new String[] { value });
	}

	/**
	 * 清空表
	 */
	public void clear() {
		mSQLiteDatabase.execSQL("delete from t_airport");
	}
	
	public void close() {
		if(mSQLiteDatabase !=null){
			mSQLiteDatabase.close();
		}
		if (dh != null)
			dh.close();
	}
}
