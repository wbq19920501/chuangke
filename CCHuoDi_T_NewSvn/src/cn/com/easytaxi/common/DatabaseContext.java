package cn.com.easytaxi.common;

import android.content.Context;
import android.content.ContextWrapper;

public class DatabaseContext extends ContextWrapper {

	public DatabaseContext(Context base) {
		super(base);
	}
 
}
