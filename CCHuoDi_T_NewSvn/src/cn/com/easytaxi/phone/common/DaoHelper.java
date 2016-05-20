package cn.com.easytaxi.phone.common;

import cn.com.easytaxi.common.DatabaseContext;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DaoHelper extends SQLiteOpenHelper {

	public DaoHelper(Context context, String name, CursorFactory factory, int version) {
		super(new DatabaseContext(context), name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table  if exists t_driver");
		db.execSQL("drop table  if exists t_tel");
		db.execSQL("drop table  if exists t_history");
		create(db);
	}
	
	private void create(SQLiteDatabase db){
		db.execSQL("create table t_driver(_CITY_ID integer,_SEX integer,_LAST_TIME varchar(32),_NAME varchar(32),_PHONE varchar(32),_TAXI_NUM integer default 0,_CALL_NUM integer)");
		db.execSQL("create table t_tel(_CITY_ID integer,_COMPANY varchar(32),_PHONE varchar(32),_CALL_NUM integer default 0)");
		db.execSQL("create table t_history(_CITY_ID integer,_CALL_TIME integer,_NAME varchar(32),_PHONE varchar(32))");
	}
	
}
