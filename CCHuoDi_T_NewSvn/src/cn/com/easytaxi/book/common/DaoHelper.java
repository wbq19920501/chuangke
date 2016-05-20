package cn.com.easytaxi.book.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import cn.com.easytaxi.common.DatabaseContext;

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
		db.execSQL("drop table if exists t_addr");
		create(db);
	}

	private void create(SQLiteDatabase db) {
		db.execSQL("create table t_addr(_ID integer primary key autoincrement,_SRC_ADDR varchar(32),_DEST_ADDR varchar(32))");
	}

}
