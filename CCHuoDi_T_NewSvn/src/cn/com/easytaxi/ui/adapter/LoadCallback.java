package cn.com.easytaxi.ui.adapter;

import cn.com.easytaxi.common.Callback;

public abstract class LoadCallback<T> extends Callback<T> {
	public abstract void onStart();
}
