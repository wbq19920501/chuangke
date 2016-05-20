package cn.com.easytaxi.airport.store;

import cn.com.easytaxi.common.DatabaseContext;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AirportSessionHelper extends SQLiteOpenHelper {

	public AirportSessionHelper(Context context, String name, CursorFactory factory, int version) {
		super(new DatabaseContext(context), name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//无论什么版本数据库 只有 t_state 被保留 不删除
		db.execSQL("drop table if exists t_airport");
		create(db);
	}

	/**
	 * 创建机场表:_LAT纬度 ；  _LNG经度
	 * @param db
	 */
	private void create(SQLiteDatabase db) {
			db.execSQL("create table t_airport(_ID integer,_NAME varchar(32),_LAT integer,_LNG integer)");
	}

}
