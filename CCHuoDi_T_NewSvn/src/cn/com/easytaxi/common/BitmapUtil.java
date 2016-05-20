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
	 * 创建带文字的bitmap
	 * 
	 * @param context
	 *            上下文
	 * @param sourceId
	 *            要绘制的背景图片
	 * @param str
	 *            要绘制的文字
	 * @param textSize
	 *            文字大小
	 * @param textColor
	 *            文字颜色
	 * @param paddingLeft
	 *            文字内容相对于左边的间距
	 * @param paddingRight
	 *            文字内容相对于右边的间距
	 * @param paddingTop
	 *            文字内容相对于上边的间距
	 * @param paddingButtom
	 *            文字内容相对于下边的间距
	 * @param nullstrCount
	 *            空格的数量
	 * @return
	 */
	public static Bitmap createBitmap(Context context, int sourceId, String str, int textSize, int textColor, int paddingLeft, int paddingRight, int paddingTop, int paddingButtom, int nullstrCount) {
		// 判断字串的长度
		int len = str.length() - nullstrCount;
		int widthBg = len * textSize + paddingLeft + paddingRight;
		int heightBg = textSize + paddingTop + paddingButtom;
		// 生成要画的图片
		Bitmap content = Bitmap.createBitmap(widthBg, heightBg, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(content);

		Paint paint = new Paint();
		paint.setTextSize(textSize);
		paint.setColor(textColor);
		// 缩放背景
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), sourceId);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale((float) widthBg / w, (float) heightBg / h);
		Bitmap background = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);
		// 画背景
		c.drawBitmap(background, 0, 0, paint);
		// 画文字
		paint.setColor(textColor);
		paint.setAntiAlias(true);// 抗锯齿效果
		// drawText(String text, float x, float y, Paint paint)，其中y指的是文字的左下角位置
		c.drawText(str, paddingLeft, paddingTop + textSize, paint);

		return content;
	}

	public static Bitmap createBitmapWithArrow(Bitmap content, Bitmap arrow,int movePix) {
		// 生成要画的图片
		Bitmap bitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight() + arrow.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();

		canvas.drawBitmap(content, 0, 0, paint);
		canvas.drawBitmap(arrow, ((float) (content.getWidth() - arrow.getWidth())) / 2, content.getHeight()-movePix, paint);

		return bitmap;
	}

	/**
	 * 创建带圆角的图片
	 * 
	 * @param context
	 * @param res
	 *            资源id
	 * @param roundPx
	 *            圆角幅度 如 12
	 * @return
	 */
	public static Drawable getRoundedCornerBitmap(Context context, int res, int roundPx) {
		Drawable drawable = context.getResources().getDrawable(res);
		BitmapDrawable bt = (BitmapDrawable) drawable;
		Bitmap bitmap = bt.getBitmap();
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;// 颜色值（0xff---alpha）
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);// Rect是使用int类型作为数值，RectF是使用float类型作为数值

		// --------抗锯齿-------//
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
	 * 创建带圆角的图片
	 * 
	 * @param context
	 * @param bitmap
	 *            资源图片
	 * @param roundPx
	 *            圆角幅度 如 12
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, int roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		setBitmapPremultiplied(output);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;// 颜色值（0xff---alpha）
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);// Rect是使用int类型作为数值，RectF是使用float类型作为数值

		// --------抗锯齿-------//
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
	 * 缩放图片
	 * 
	 * @param context
	 *            上下文
	 * @param sourceId
	 *            图片资源id
	 * @param newWidthBg
	 *            放缩后的宽度
	 * @param newHeightBg
	 *            放缩后的高度
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
		// 判断字串的长度
		int len = str.length() - nullstrCount;
		int newWidthBg = len * textSize + paddingLeft + paddingRight;
		int newHeightBg = textSize + paddingTop + paddingButtom;
		// 生成要画的图片(widthBg,heightBg,Bitmap.Config.ARGB_8888);
		Bitmap scaleBit = BitmapUtil.scaleBitmap(context, sourceId, newWidthBg, newHeightBg);
//		setBitmapPremultiplied(scaleBit);
		Bitmap content = BitmapUtil.getRoundedCornerBitmap(context, scaleBit, 15);
		setBitmapPremultiplied(content);
		Canvas c = new Canvas(content);
		// 画笔
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		paint.setColor(textColor);
		// 画文字
		paint.setAntiAlias(true);// 抗锯齿效果
		// drawText(String text, float x, float y, Paint paint)，其中y指的是文字的左下角位置
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
	* 给图片创建阴影 
	*
	* @param originalBitmap
	* @return
	*/
	public static Bitmap createBitmapShadow(int color, Bitmap originalBitmap) {
		//这里的2表示x和y的偏移量
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
