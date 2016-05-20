package cn.com.easytaxi.util;

import java.util.concurrent.Callable;

import android.os.Handler;
import android.os.Looper;
import cn.com.easytaxi.ui.adapter.LoadCallback;

/**
 * @author shiner
 */
public class AsyncUtil {
	private static final String TAG = "AsyncUtil";

	private static final Handler handler = new Handler(Looper.getMainLooper());

	// /**
	// * 使方法异步执行
	// *
	// * @param obj
	// * 调用者
	// * @param method
	// * 方法名
	// * @param types
	// * 方法参数类型数组 new Class[]
	// * @param params
	// * 方法参数值 new Object[]
	// * @param callback
	// * 回调接口
	// */
	// public static void goAsync(final Object obj, final String method, final
	// Class[] types, final Object[] params, final LoadCallback callback) {
	// new Thread() {
	// @Override
	// public void run() {
	// onStart(callback);
	// try {
	// Class cls = obj.getClass();
	// Method m = cls.getDeclaredMethod(method, types);
	// m.setAccessible(true);
	// Object o = m.invoke(obj, params);
	// handle(callback, o);
	// } catch (Exception e) {
	// if (e instanceof InvocationTargetException) {
	// InvocationTargetException targetEx = (InvocationTargetException) e;
	// Throwable t = targetEx.getTargetException();
	// ETLog.e(TAG, "obj:" + obj + ",method:" + method);
	// t.printStackTrace();
	// error(callback, t);
	// } else {
	// e.printStackTrace();
	// }
	// } finally {
	// complete(callback);
	// }
	// };
	// }.start();
	//
	// }

	public static <T> void goAsync(final Callable<T> callable, final LoadCallback<T> callback) {
		new Thread() {
			public void run() {
				try {
					onStart(callback);
					// 直接调call了。。
					T result = callable.call();
					handle(callback, result);
				} catch (Throwable e) {
					e.printStackTrace();
					error(callback, e);
				} finally {
					complete(callback);
				}
			};
		}.start();

	}

	// /**
	// * 使静态方法异步执行
	// *
	// * @param cls
	// * 调用者类型
	// * @param method
	// * 方法名
	// * @param types
	// * 方法参数类型数组 new Class[]
	// * @param params
	// * 方法参数值 new Object[]
	// * @param callback
	// * 回调接口
	// */
	// public static void goAsyncStatic(final Class cls, final String method,
	// final Class[] types, final Object[] params, final LoadCallback callback)
	// {
	// new Thread() {
	// @Override
	// public void run() {
	// onStart(callback);
	// try {
	// Method m = cls.getDeclaredMethod(method, types);
	// m.setAccessible(true);
	// Object o = m.invoke(null, params);
	// handle(callback, o);
	// } catch (Exception e) {
	// InvocationTargetException targetEx = (InvocationTargetException) e;
	// Throwable t = targetEx.getTargetException();
	// ETLog.e(TAG, "cls:" + cls + ",method:" + method);
	// t.printStackTrace();
	// error(callback, t);
	// } finally {
	// complete(callback);
	// }
	// };
	// }.start();
	//
	// }

	public static <T> void complete(final LoadCallback<T> callback) {
		if (callback == null)
			return;
		if (Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId()) {
			callback.complete();
		} else {
			handler.post(new Runnable() {

				@Override
				public void run() {
					callback.complete();
				}
			});
		}

	}

	public static <T> void error(final LoadCallback<T> callback, final Throwable e) {
		if (callback == null)
			return;
		if (Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId()) {
			callback.error(e);
		} else {
			handler.post(new Runnable() {

				@Override
				public void run() {
					callback.error(e);
				}
			});
		}

	}

	public static <T> void handle(final LoadCallback<T> callback, final T data) {
		if (callback == null)
			return;
		if (Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId()) {
			callback.handle(data);
		} else {
			handler.post(new Runnable() {

				@Override
				public void run() {
					callback.handle(data);
				}
			});
		}

	}

	public static <T> void onStart(final LoadCallback<T> callback) {
		if (callback == null)
			return;
		if (Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId()) {
			callback.onStart();
		} else {
			handler.post(new Runnable() {

				@Override
				public void run() {
					callback.onStart();
				}
			});
		}
	}

}
