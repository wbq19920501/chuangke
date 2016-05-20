package cn.com.easytaxi.onetaxi.common;

import cn.com.easytaxi.common.DatabaseContext;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class StateHelper extends SQLiteOpenHelper {

	public StateHelper(Context context, String name, CursorFactory factory, int version) {
		super(new DatabaseContext(context), name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists t_state");
		create(db);
	}

	private void create(SQLiteDatabase db) {
		db.execSQL("create table t_state(key varchar(16),value varchar(32))");
	}

}
