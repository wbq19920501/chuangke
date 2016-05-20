package cn.com.easytaxi.pay;

public abstract class Callback<T> {
	
	abstract public void onSuccess(T param);
	
	public void onFailure(Throwable e, String errorMsg) {
		e.printStackTrace();
	}
	
	public void onStart() {

	}
	
	public void onProgress(Object progress) {

	}

	public void onFinish() {

	}
}
