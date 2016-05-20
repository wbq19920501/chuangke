package cn.com.easytaxi.common;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 工具类 释放布局中所有Imageview组件占用的图片，可设置是否释放背景图 用于退出时释放资源，调用完成后，请不要刷新界面
 * @ClassName: BitmapRecycle 
 * @Description: TODO
 * @author Yuanwei Xu
 * @date 2013-2-3 下午2:36:01 
 * @version V1.0
 */
public class BitmapRecycle {
	private static final String TAG = "RecycleBitmapInLayout";
	/* 是否释放背景图 true:释放;false:不释放 */
	private boolean flagWithBackgroud = false;

	/**
	 * 
	 * @param flagWithBackgroud
	 *            是否同时释放背景图
	 */
	public BitmapRecycle(boolean flagWithBackgroud) {
		this.flagWithBackgroud = flagWithBackgroud;
	}

	/**
	 * 释放Imageview占用的图片资源 用于退出时释放资源，调用完成后，请不要刷新界面
	 * 
	 * @param layout
	 *            需要释放图片的布局 *
	 */
	public void recycle(ViewGroup layout) {

		for (int i = 0; i < layout.getChildCount(); i++) {
			// 获得该布局的所有子布局
			View subView = layout.getChildAt(i);
			// 判断子布局属性，如果还是ViewGroup类型，递归回收
			if (subView instanceof ViewGroup) {
				// 递归回收
				recycle((ViewGroup) subView);
			} else {
				// 是Imageview的子例
				if (subView instanceof ImageView) {
					// 回收占用的Bitmap
					recycleImageViewBitMap((ImageView) subView);
					// 如果flagWithBackgroud为true,则同时回收背景图
					if (flagWithBackgroud) {

						recycleBackgroundBitMap((ImageView) subView);
					}
				}
			}
		}
	}

	/**
	 * 回收imageView背景图
	 * @param view
	 */
	public static void recycleBackgroundBitMap(ImageView view) {
		if (view != null) {
			BitmapDrawable bd = (BitmapDrawable) view.getBackground();
			rceycleBitmapDrawable(bd);
		}
	}

	/**
	 * 回收imageView占用的Bitmap
	 * @param imageView
	 */
	public static void recycleImageViewBitMap(ImageView imageView) {
		if (imageView != null) {
			BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
			rceycleBitmapDrawable(bd);
		}
	}

	public static void rceycleBitmapDrawable(BitmapDrawable bd) {
		if (bd != null) {
			Bitmap bitmap = bd.getBitmap();
			rceycleBitmap(bitmap);
		}
		bd = null;
	}

	public static void rceycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

}