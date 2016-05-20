package cn.com.easytaxi.common;

public abstract class Callback<T> {

	public boolean isCache;

	abstract public void handle(T param);

	public void error(Throwable e, int errorcode) {
		e.printStackTrace();
	}

	public void error(Throwable e) {
		error(e, 0);
	}

	public void complete() {

	}
}
