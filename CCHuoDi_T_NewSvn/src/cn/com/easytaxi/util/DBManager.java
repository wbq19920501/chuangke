package cn.com.easytaxi.util;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import cn.com.easytaxi.taxi.bean.BookBeanDao;
import cn.com.easytaxi.taxi.bean.DaoMaster;
import cn.com.easytaxi.taxi.bean.DaoMaster.OpenHelper;
import cn.com.easytaxi.taxi.bean.DaoSession;
import cn.com.easytaxi.taxi.bean.MsgBeanDao;

/**
 * @author shiner
 */
public class DBManager {
	private final static String DB_NAME = "common.db";
	private static OpenHelper openHelper;
	private static SQLiteDatabase database;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	public static DaoSession getSession(Application app) {
		if (openHelper == null) {
			openHelper = new DaoMaster.DevOpenHelper(app, DB_NAME, null);
			database = openHelper.getWritableDatabase();
			daoMaster = new DaoMaster(database);
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

	public static MsgBeanDao getMsgDao(Application app) {
		if (daoSession == null) {
			getSession(app);
		}
		return daoSession.getMsgBeanDao();
	}

	public static BookBeanDao getBookDao(Application app) {
		if (daoSession == null) {
			getSession(app);
		}
		return daoSession.getBookBeanDao();
	}
}
