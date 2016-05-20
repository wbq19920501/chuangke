package cn.com.easytaxi.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.platform.common.Cities.City;
import cn.com.easytaxi.workpool.bean.GeoPointLable;

public class SessionAdapter {

	private Context mContext;
	protected SQLiteDatabase mSQLiteDatabase;
	private SessionHelper dh;

	private final String DB_NAME = "ddd.so";

	public SessionAdapter(Context context) {
		mContext = context;
		dh = new SessionHelper(mContext, DB_NAME, null, 7); // old is 3 ,now is
															// 4 20130321 add
															// pois history 5
															// add type 6
		mSQLiteDatabase = dh.getWritableDatabase();
	}

	public void close() {
		if (dh != null)
			dh.close();
	}

	// 状态列表操作
	public void set(String key, String value) {
		delete(key);
		ContentValues cv = new ContentValues();
		cv.put("key", key);
		cv.put("value", value);
		mSQLiteDatabase.insert("t_state", null, cv);
	}

	public void delete(String key) {
		mSQLiteDatabase.execSQL("delete from t_state where key=?", new String[] { key });
	}

	public void clear() {
		mSQLiteDatabase.execSQL("delete from t_state");
	}

	public String get(String key) {
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

	// 电话列表操作和城市列表操作
	public List<Pair<String, String>> getPhoneList(String cityID) {
		Cursor cursor = null;
		try {
			List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
			String sql = "SELECT _COMPANY,_PHONE FROM t_tel WHERE _CITY_ID=?";
			cursor = mSQLiteDatabase.rawQuery(sql, new String[] { cityID });
			if (cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					list.add(new Pair<String, String>(cursor.getString(0), cursor.getString(1)));
				}
			}
			return list;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	// 功能列表操作
	public JSONArray getFunctionList() {

		Cursor cursor = null;
		try {
			JSONArray rows = new JSONArray();
			String sql = "SELECT * FROM t_function";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					JSONObject json = new JSONObject();
					for (String columnName : cursor.getColumnNames()) {
						int index = cursor.getColumnIndex(columnName);
						json.put(columnName, cursor.getString(index));
					}
					rows.put(json);
				}
			}
			return rows;
		} catch (Throwable e) {
			e.printStackTrace();
			return new JSONArray();
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public void saveFunction(JSONObject json) {
		Cursor cursor = null;
		try {

			mSQLiteDatabase.beginTransaction();

			cursor = mSQLiteDatabase.rawQuery("SELECT count(*) FROM t_function where id=" + json.getInt("ID"), null);
			cursor.moveToFirst();
			if (cursor.getInt(0) == 0) {

				ContentValues cv = new ContentValues();
				cv.put("ID", json.getInt("ID"));
				cv.put("SYS", json.getInt("SYS"));
				cv.put("NAME", json.getString("NAME"));
				cv.put("ICON", json.getString("ICON"));
				cv.put("SEQ", json.getInt("SEQ"));
				cv.put("PACKAGE", json.getString("PACKAGE"));
				cv.put("CLASSNAME", json.getString("CLASSNAME"));
				cv.put("DESCP", json.getString("DESCP"));
				cv.put("MODE", json.getInt("MODE"));
				cv.put("VERSION", json.getInt("VERSION"));
				cv.put("ISLOGIN", json.getInt("ISLOGIN"));
				cv.put("ISCITY", json.getInt("ISCITY"));
				// Log.v("MY", mSQLiteDatabase.insert("t_function", null, cv) +
				// ".......");
			}

			mSQLiteDatabase.setTransactionSuccessful();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			mSQLiteDatabase.endTransaction();

			if (cursor != null)
				cursor.close();
		}
	}

	public void deleteFunction(Integer id) {
		try {
			mSQLiteDatabase.beginTransaction();
			mSQLiteDatabase.execSQL("delete from t_function where ID=" + id);
			mSQLiteDatabase.setTransactionSuccessful();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			mSQLiteDatabase.endTransaction();
		}
	}

	public void saveCityList(List<City> cityList) {
		try {
			mSQLiteDatabase.beginTransaction();
			/*
			 * File f = new File("sdcard/citi.txt"); if(!f.exists()){
			 * f.createNewFile(); }
			 * 
			 * FileWriter file = new FileWriter(f);
			 * 
			 * BufferedWriter out = null; out = new BufferedWriter (file);
			 */

			mSQLiteDatabase.execSQL("delete from t_city");
			for (City city : cityList) {

				ContentValues cv = new ContentValues();
				cv.put("_ID", city.id);

				cv.put("_PROVINCE", city.provice);
				cv.put("_NAME", city.name);
				cv.put("_LAT", city.lat);
				cv.put("_LNG", city.lng);
				cv.put("_SIMPLE", city.cityNameSimple);
				cv.put("_TYPE", city.type);
				// out.write(data.getInt("_ID")+","+data.getString("_PROVINCE")+","+data.getString("_NAME")+","+data.getString("_LAT")+","+data.getString("_LNG")+","+data.getString("_SIMPLE"));
				// out.newLine();
				mSQLiteDatabase.insert("t_city", null, cv);
			}
			// out.flush();
			// file.close();
			mSQLiteDatabase.setTransactionSuccessful();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			mSQLiteDatabase.endTransaction();
		}
	}

	// 城市列表操作
	public void saveCityList(JSONArray datas) {
		try {
			mSQLiteDatabase.beginTransaction();
			/*
			 * File f = new File("sdcard/citi.txt"); if(!f.exists()){
			 * f.createNewFile(); }
			 * 
			 * FileWriter file = new FileWriter(f);
			 * 
			 * BufferedWriter out = null; out = new BufferedWriter (file);
			 */

			mSQLiteDatabase.execSQL("delete from t_city");
			for (int i = 0; i < datas.length(); i++) {
				JSONObject data = datas.getJSONObject(i);
				ContentValues cv = new ContentValues();
				cv.put("_ID", data.getInt("_ID"));

				cv.put("_PROVINCE", data.getString("_PROVINCE"));
				cv.put("_NAME", data.getString("_NAME"));
				cv.put("_LAT", data.getString("_LAT"));
				cv.put("_LNG", data.getString("_LNG"));
				cv.put("_SIMPLE", data.getString("_SIMPLE"));
				// out.write(data.getInt("_ID")+","+data.getString("_PROVINCE")+","+data.getString("_NAME")+","+data.getString("_LAT")+","+data.getString("_LNG")+","+data.getString("_SIMPLE"));
				// out.newLine();
				mSQLiteDatabase.insert("t_city", null, cv);
			}
			// out.flush();
			// file.close();
			mSQLiteDatabase.setTransactionSuccessful();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			mSQLiteDatabase.endTransaction();
		}
	}

	public List<String> getProvinceList() {
		Cursor cursor = null;
		try {
			List<String> provinceList = new ArrayList<String>();
			String sql = "SELECT distinct _PROVINCE FROM t_city";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					provinceList.add(cursor.getString(0));
				}
			}
			return provinceList;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public JSONArray getCityByProvinceList(String province) {
		Cursor cursor = null;
		JSONArray rows = new JSONArray();
		try {
			String sql = "SELECT _ID,_NAME,_SIMPLE,_LAT,_LNG FROM t_city WHERE _PROVINCE='" + province + "'";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					JSONObject json = new JSONObject();
					json.put("_ID", cursor.getInt(0));
					json.put("_NAME", cursor.getString(1));
					json.put("_SIMPLE", cursor.getString(2));
					json.put("_LAT", cursor.getInt(3));
					json.put("_LNG", cursor.getInt(4));
					rows.put(json);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return rows;
	}

	public String getCityIdBySimple(String name) {
		Cursor cursor = null;
		try {
			String sql = "SELECT  _ID FROM t_city where _SIMPLE='" + name + "'";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					return cursor.getString(0);
				}
			}
			return null;
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}
	public String getCityIdByName(String name) {
		Cursor cursor = null;
		try {
			String sql = "SELECT  _ID FROM t_city where _SIMPLE like'" + name + "'";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					return cursor.getString(0);
				}
			}
			return null;
		} finally {
			if (cursor != null)
				cursor.close();
		}
		
	}

 
	
	public String getCityProvinceByName(String name) {
		Cursor cursor = null;
		try {
			String sql = "SELECT  _PROVINCE FROM t_city where _NAME='" + name + "'";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				return cursor.getString(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	public HashMap<String, String> getCityInfo(String cityId) {
		Cursor cursor = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			String sql = "SELECT * FROM t_city WHERE _ID=" + cityId;
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					map.put(cursor.getColumnName(i), cursor.getString(i));
				}
			}
			return map;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public void setCityOffline(String cityId) {
		mSQLiteDatabase.execSQL("UPDATE  t_city SET _OFFLINE=1 WHERE _ID=" + cityId);
	}

	public ArrayList<City> getCityList(int flag) {
		ArrayList<City> list = new ArrayList<City>(12);
		Cursor cursor = null;

		try {

			String sql = null;
			if (flag == 0) {
				sql = "SELECT _ID,_NAME,_SIMPLE,_LAT,_LNG FROM t_city order by _SIMPLE desc";
			} else {
				sql = "SELECT _ID,_NAME,_SIMPLE,_LAT,_LNG FROM t_city WHERE _TYPE>" + 0 + " order by _TYPE asc";
			}

			cursor = mSQLiteDatabase.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst()) {
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					City city = new City();
					city.id = cursor.getInt(0);
					city.name = cursor.getString(1);
					city.cityNameSimple = cursor.getString(1);
					city.lat = cursor.getInt(3);
					city.lng = cursor.getInt(4);
					list.add(city);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return list;
	}

	public void savePoi(String city, String mobile, int type, GeoPointLable selectedGeoPoint) {
		
		if (selectedGeoPoint == null) {
	//		AppLog.LogD("=========selectedGeoPoint null======== savePoi ");
			return;
		}else{
	//		AppLog.LogD("================= savePoi " + selectedGeoPoint.getName());
		}

		String name = selectedGeoPoint.getName();
		int lat = selectedGeoPoint.getLat();
		int lng = selectedGeoPoint.getLog();

		String sql = "SELECT _CITY_NAME FROM t_pois where _CITY_NAME='" + city + "' and _MOBILE='" + mobile + "' and POI='" + name
				+ "' and LAT=" + lat + " and LNG=" + lng + " and type="+type + ";";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);

		if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
			cursor.close();
			return;
		}

		ContentValues cv = new ContentValues();
		cv.put("_CITY_NAME", city);
		cv.put("_MOBILE", mobile);
		cv.put("POI", name);
		cv.put("LAT", lat);
		cv.put("LNG", lng);
		cv.put("type", type);

		long i = mSQLiteDatabase.insert("t_pois", null, cv);
		AppLog.LogD(" savePoi end : " + i);
	}

	public ArrayList<GeoPointLable> getPois(String city, int type, String mobile) {
		ArrayList<GeoPointLable> data = new ArrayList<GeoPointLable>(12);
		String sql = "SELECT * FROM t_pois where type=" + type + " order by _ID desc limit 50;";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);

		if (cursor != null && cursor.moveToFirst() && (cursor.getCount()) > 0) {

			for (; !cursor.isAfterLast(); cursor.moveToNext()) {

				int lat = cursor.getInt(cursor.getColumnIndex("LAT"));
				 
				int log = cursor.getInt(cursor.getColumnIndex("LNG"));
				 
				String name = cursor.getString(cursor.getColumnIndex("POI"));
//				AppLog.LogD("SESSION", " == name " + name);
				GeoPointLable gp = new GeoPointLable(lat, log, name,"");
				data.add(gp);
			}
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}

		return data;
	}

	public void deletePoi(String cityName, String mobile, int type, GeoPointLable selectedGeoPoint) {
		if (selectedGeoPoint == null) {
			AppLog.LogD("=========selectedGeoPoint null======== deletePoi ");
			return;
		}else{
			AppLog.LogD("================= deletePoi " + selectedGeoPoint.getName());
		}
		
		String name = selectedGeoPoint.getName();
		int lat = selectedGeoPoint.getLat();
		int lng = selectedGeoPoint.getLog();

		String sql = "DELETE  FROM t_pois where _CITY_NAME='" + cityName + "' and _MOBILE='" + mobile + "' and POI='" + name
				+ "' and LAT=" + lat + " and LNG=" + lng + " and type="+type + ";";
		AppLog.LogD(sql);
		mSQLiteDatabase.execSQL(sql);
//		mSQLiteDatabase.rawQuery(sql, null);
		return;
	}

}
