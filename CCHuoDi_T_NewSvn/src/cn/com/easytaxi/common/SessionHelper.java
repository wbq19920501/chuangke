package cn.com.easytaxi.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SessionHelper extends SQLiteOpenHelper {
	private int newVersion;
	private int oldVersion;

	public SessionHelper(Context context, String name, CursorFactory factory, int version) {
		super(new DatabaseContext(context), name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		this.newVersion = newVersion;
		this.oldVersion = oldVersion;
		// oldVersion is 0 ,1, 2, 3
		/* db.execSQL("drop table if exists t_state"); */
		
		//无论什么版本数据库 只有 t_state 被保留 不删除
		if (newVersion > oldVersion && newVersion>=4) {
			db.execSQL("drop table if exists t_function");
			db.execSQL("drop table if exists t_tel");
			db.execSQL("drop table if exists t_city");
			db.execSQL("drop table if exists t_pois");
			create(db);
		}

		
		
	}

	private void create(SQLiteDatabase db) {
		 
		// 没装过的
		if (oldVersion == 0) {
			db.execSQL("create table t_pois(_ID INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1,type integer,_CITY_NAME varchar(32), _MOBILE varchar(32) ,POI varchar(32) , LAT integer, LNG integer )");
			db.execSQL("create table t_state(key varchar(16),value varchar(32))");
			db.execSQL("create table t_function(ID integer,SYS integer,NAME varchar(32),ICON varchar(32),SEQ integer,PACKAGE varchar(128),CLASSNAME varchar(32),FILE varchar(128),DESCP varchar(128),MODE integer,VERSION integer,ISCITY integer,ISLOGIN integer)");
			db.execSQL("create table t_tel(_CITY_ID integer,_COMPANY varchar(32),_PHONE varchar(32))");
			db.execSQL("create table t_city(_ID integer,_PROVINCE varchar(32),_NAME varchar(32),_LAT integer,_LNG integer,_SIMPLE varchar(32),_TYPE integer,_OFFLINE )");
			return;
		}

		if (oldVersion == 2 || oldVersion == 3 || oldVersion == 1) {
			db.execSQL("create table t_pois(_ID INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1,type integer,_CITY_NAME varchar(32), _MOBILE varchar(32) ,POI varchar(32) , LAT integer, LNG integer )");
			db.execSQL("create table t_function(ID integer,SYS integer,NAME varchar(32),ICON varchar(32),SEQ integer,PACKAGE varchar(128),CLASSNAME varchar(32),FILE varchar(128),DESCP varchar(128),MODE integer,VERSION integer,ISCITY integer,ISLOGIN integer)");
			db.execSQL("create table t_tel(_CITY_ID integer,_COMPANY varchar(32),_PHONE varchar(32))");
			db.execSQL("create table t_city(_ID integer,_PROVINCE varchar(32),_NAME varchar(32),_LAT integer,_LNG integer,_SIMPLE varchar(32),_TYPE integer,_OFFLINE )");
			return ;
		}

		if (oldVersion >= 4) {
			db.execSQL("create table t_pois(_ID INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1,type integer,_CITY_NAME varchar(32), _MOBILE varchar(32) ,POI varchar(32) , LAT integer, LNG integer )");
			db.execSQL("create table t_function(ID integer,SYS integer,NAME varchar(32),ICON varchar(32),SEQ integer,PACKAGE varchar(128),CLASSNAME varchar(32),FILE varchar(128),DESCP varchar(128),MODE integer,VERSION integer,ISCITY integer,ISLOGIN integer)");
			db.execSQL("create table t_tel(_CITY_ID integer,_COMPANY varchar(32),_PHONE varchar(32))");
			db.execSQL("create table t_city(_ID integer,_PROVINCE varchar(32),_NAME varchar(32),_LAT integer,_LNG integer,_SIMPLE varchar(32),_TYPE integer,_OFFLINE )");
		} 
	}

}
