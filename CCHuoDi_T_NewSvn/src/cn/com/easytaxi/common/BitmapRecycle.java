package cn.com.easytaxi.common;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * ������ �ͷŲ���������Imageview���ռ�õ�ͼƬ���������Ƿ��ͷű���ͼ �����˳�ʱ�ͷ���Դ��������ɺ��벻Ҫˢ�½���
 * @ClassName: BitmapRecycle 
 * @Description: TODO
 * @author Yuanwei Xu
 * @date 2013-2-3 ����2:36:01 
 * @version V1.0
 */
public class BitmapRecycle {
	private static final String TAG = "RecycleBitmapInLayout";
	/* �Ƿ��ͷű���ͼ true:�ͷ�;false:���ͷ� */
	private boolean flagWithBackgroud = false;

	/**
	 * 
	 * @param flagWithBackgroud
	 *            �Ƿ�ͬʱ�ͷű���ͼ
	 */
	public BitmapRecycle(boolean flagWithBackgroud) {
		this.flagWithBackgroud = flagWithBackgroud;
	}

	/**
	 * �ͷ�Imageviewռ�õ�ͼƬ��Դ �����˳�ʱ�ͷ���Դ��������ɺ��벻Ҫˢ�½���
	 * 
	 * @param layout
	 *            ��Ҫ�ͷ�ͼƬ�Ĳ��� *
	 */
	public void recycle(ViewGroup layout) {

		for (int i = 0; i < layout.getChildCount(); i++) {
			// ��øò��ֵ������Ӳ���
			View subView = layout.getChildAt(i);
			// �ж��Ӳ������ԣ��������ViewGroup���ͣ��ݹ����
			if (subView instanceof ViewGroup) {
				// �ݹ����
				recycle((ViewGroup) subView);
			} else {
				// ��Imageview������
				if (subView instanceof ImageView) {
					// ����ռ�õ�Bitmap
					recycleImageViewBitMap((ImageView) subView);
					// ���flagWithBackgroudΪtrue,��ͬʱ���ձ���ͼ
					if (flagWithBackgroud) {

						recycleBackgroundBitMap((ImageView) subView);
					}
				}
			}
		}
	}

	/**
	 * ����imageView����ͼ
	 * @param view
	 */
	public static void recycleBackgroundBitMap(ImageView view) {
		if (view != null) {
			BitmapDrawable bd = (BitmapDrawable) view.getBackground();
			rceycleBitmapDrawable(bd);
		}
	}

	/**
	 * ����imageViewռ�õ�Bitmap
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