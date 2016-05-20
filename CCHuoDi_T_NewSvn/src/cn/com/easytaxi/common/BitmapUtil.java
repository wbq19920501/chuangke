package cn.com.easytaxi.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtil {

	/**
	 * ���������ֵ�bitmap
	 * 
	 * @param context
	 *            ������
	 * @param sourceId
	 *            Ҫ���Ƶı���ͼƬ
	 * @param str
	 *            Ҫ���Ƶ�����
	 * @param textSize
	 *            ���ִ�С
	 * @param textColor
	 *            ������ɫ
	 * @param paddingLeft
	 *            ���������������ߵļ��
	 * @param paddingRight
	 *            ��������������ұߵļ��
	 * @param paddingTop
	 *            ��������������ϱߵļ��
	 * @param paddingButtom
	 *            ��������������±ߵļ��
	 * @param nullstrCount
	 *            �ո������
	 * @return
	 */
	public static Bitmap createBitmap(Context context, int sourceId, String str, int textSize, int textColor, int paddingLeft, int paddingRight, int paddingTop, int paddingButtom, int nullstrCount) {
		// �ж��ִ��ĳ���
		int len = str.length() - nullstrCount;
		int widthBg = len * textSize + paddingLeft + paddingRight;
		int heightBg = textSize + paddingTop + paddingButtom;
		// ����Ҫ����ͼƬ
		Bitmap content = Bitmap.createBitmap(widthBg, heightBg, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(content);

		Paint paint = new Paint();
		paint.setTextSize(textSize);
		paint.setColor(textColor);
		// ���ű���
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), sourceId);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale((float) widthBg / w, (float) heightBg / h);
		Bitmap background = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);
		// ������
		c.drawBitmap(background, 0, 0, paint);
		// ������
		paint.setColor(textColor);
		paint.setAntiAlias(true);// �����Ч��
		// drawText(String text, float x, float y, Paint paint)������yָ�������ֵ����½�λ��
		c.drawText(str, paddingLeft, paddingTop + textSize, paint);

		return content;
	}

	public static Bitmap createBitmapWithArrow(Bitmap content, Bitmap arrow,int movePix) {
		// ����Ҫ����ͼƬ
		Bitmap bitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight() + arrow.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();

		canvas.drawBitmap(content, 0, 0, paint);
		canvas.drawBitmap(arrow, ((float) (content.getWidth() - arrow.getWidth())) / 2, content.getHeight()-movePix, paint);

		return bitmap;
	}

	/**
	 * ������Բ�ǵ�ͼƬ
	 * 
	 * @param context
	 * @param res
	 *            ��Դid
	 * @param roundPx
	 *            Բ�Ƿ��� �� 12
	 * @return
	 */
	public static Drawable getRoundedCornerBitmap(Context context, int res, int roundPx) {
		Drawable drawable = context.getResources().getDrawable(res);
		BitmapDrawable bt = (BitmapDrawable) drawable;
		Bitmap bitmap = bt.getBitmap();
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;// ��ɫֵ��0xff---alpha��
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);// Rect��ʹ��int������Ϊ��ֵ��RectF��ʹ��float������Ϊ��ֵ

		// --------�����-------//
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		Drawable yuanjiao = new BitmapDrawable(context.getResources(), output);
		return yuanjiao;
	}

	/**
	 * ������Բ�ǵ�ͼƬ
	 * 
	 * @param context
	 * @param bitmap
	 *            ��ԴͼƬ
	 * @param roundPx
	 *            Բ�Ƿ��� �� 12
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, int roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		setBitmapPremultiplied(output);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;// ��ɫֵ��0xff---alpha��
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);// Rect��ʹ��int������Ϊ��ֵ��RectF��ʹ��float������Ϊ��ֵ

		// --------�����-------//
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		// Drawable yuanjiao=new BitmapDrawable(context.getResources(),output);
		return output;
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param context
	 *            ������
	 * @param sourceId
	 *            ͼƬ��Դid
	 * @param newWidthBg
	 *            ������Ŀ��
	 * @param newHeightBg
	 *            ������ĸ߶�
	 * @return
	 */
	public static Bitmap scaleBitmap(Context context, int sourceId, int newWidthBg, int newHeightBg) {
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), sourceId);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale((float) newWidthBg / w, (float) newHeightBg / h);
		Bitmap background = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);
		return background;
	}

	public static Bitmap writeWordsOnBitmap(Context context, int sourceId, String str, int textSize, int textColor, int paddingLeft, int paddingRight, int paddingTop, int paddingButtom, int nullstrCount) {
		// �ж��ִ��ĳ���
		int len = str.length() - nullstrCount;
		int newWidthBg = len * textSize + paddingLeft + paddingRight;
		int newHeightBg = textSize + paddingTop + paddingButtom;
		// ����Ҫ����ͼƬ(widthBg,heightBg,Bitmap.Config.ARGB_8888);
		Bitmap scaleBit = BitmapUtil.scaleBitmap(context, sourceId, newWidthBg, newHeightBg);
//		setBitmapPremultiplied(scaleBit);
		Bitmap content = BitmapUtil.getRoundedCornerBitmap(context, scaleBit, 15);
		setBitmapPremultiplied(content);
		Canvas c = new Canvas(content);
		// ����
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		paint.setColor(textColor);
		// ������
		paint.setAntiAlias(true);// �����Ч��
		// drawText(String text, float x, float y, Paint paint)������yָ�������ֵ����½�λ��
		c.drawText(str, paddingLeft,textSize + paddingTop, paint);
		return content;
	}
	
	public static void setBitmapPremultiplied(Bitmap b) {
		// TODO Auto-generated method stub
		if ( android.os.Build.VERSION.SDK_INT >= 19 && !b.isPremultiplied() )
		{
			b.setPremultiplied( true );
		}
	}
	
	
	
	
	/**
	* ��ͼƬ������Ӱ 
	*
	* @param originalBitmap
	* @return
	*/
	public static Bitmap createBitmapShadow(int color, Bitmap originalBitmap) {
		//�����2��ʾx��y��ƫ����
		BlurMaskFilter blurFilter = new BlurMaskFilter(2, BlurMaskFilter.Blur.NORMAL);
		Paint shadowPaint = new Paint();
		shadowPaint.setAlpha(30);
		shadowPaint.setColor(color);
		shadowPaint.setMaskFilter(blurFilter);
	     
		int[] offsetXY = new int[2];
		Bitmap shadowBitmap = originalBitmap.extractAlpha(shadowPaint, offsetXY);
		Bitmap shadowImage32 = shadowBitmap.copy(Bitmap.Config.ARGB_8888, true);
		setBitmapPremultiplied(shadowImage32);
		Canvas c = new Canvas(shadowImage32);
		c.drawBitmap(originalBitmap, offsetXY[0], offsetXY[1], null);
		return shadowImage32;
	}
}
