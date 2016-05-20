package cn.com.easytaxi.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class ImageUtil {
	private static int NUMBER_OF_RESIZE_ATTEMPTS = 4;

	/**
	 * drawable转换bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 缩放bitmap
	 * 
	 * @param bitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap getScaleBitmap(Bitmap bitmap, double newWidth, double newHeight) {
		int i = bitmap.getWidth();
		int j = bitmap.getHeight();
		float f1 = (float) newWidth / i;
		float f2 = (float) newHeight / j;
		Matrix matrix = new Matrix();
		matrix.postScale(f1, f2);
		return Bitmap.createBitmap(bitmap, 0, 0, i, j, matrix, true);
	}

	/**
	 * 等比缩放bitmap
	 * 
	 * @param bitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap getResizeScaleBitmap(Bitmap src, float mItemwidth, float mItemHerght) {
		float w = src.getWidth();
		float h = src.getHeight();
		float[] scaleValue = getResizeScaleValue(w, h, mItemwidth, mItemHerght);
		float scaleWidth = scaleValue[0];
		float scaleHeight = scaleValue[1];
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap dest = Bitmap.createBitmap(src, 0, 0, (int) w, (int) h, matrix, true);
		return dest;
	}

	public static float[] getResizeScaleValue(float w, float h, float mItemwidth, float mItemHerght) {
		float ft = w / h;
		float fs = mItemwidth / mItemHerght;

		double neww = ft >= fs ? mItemwidth : mItemHerght * ft;
		double newh = ft >= fs ? mItemwidth / ft : mItemHerght;

		float scaleWidth = ((float) neww) / w;
		float scaleHeight = ((float) newh) / h;
		return new float[] { scaleWidth, scaleHeight };
	}

	/**
	 * 图片圆角
	 * 
	 * @param bitmap
	 * @param radius
	 *            半径
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int radius) {
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(-12434878);
		canvas.drawRoundRect(rectF, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return newBitmap;
	}

	public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable, int radius) {
		return new BitmapDrawable(toRoundCorner(bitmapDrawable.getBitmap(), radius));
	}

	public static BitmapDrawable getBitmapDrawable(Resources res, int resId) {
		BitmapDrawable bitmapDrawable = new BitmapDrawable(BitmapFactory.decodeResource(res, resId));
		bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		bitmapDrawable.setDither(true);
		return bitmapDrawable;
	}

	/**
	 * 获得带倒影的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
		return bitmapWithReflection;
	}

	/**
	 * 获得带倒影的图片
	 * 
	 * @param resId
	 * @return
	 */
	public static Bitmap getReflectionImageWithOrigin(Resources res, int resId) {
		return getReflectionImageWithOrigin(BitmapFactory.decodeResource(res, resId));
	}

	/**
	 * 图片合成
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap synthesis(Bitmap bg, float bgScaleX, float bgScaleY, Bitmap fg, float x, float y, float rotate, float scaleX, float scaleY) {
		if (bg == null) {
			return null;
		}
		int w = bg.getWidth();
		int h = bg.getHeight();
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas canvas = new Canvas(newb);
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setFilterBitmap(true);

		canvas.drawBitmap(bg, 0, 0, p);
		canvas.save();
		if (fg != null && x != -1 && y != -1) {
			RectF rectF = new RectF(x / bgScaleX, y / bgScaleY, (x + fg.getWidth() * scaleX) / bgScaleX, (y + fg.getHeight() * scaleY) / bgScaleY);
			canvas.rotate(rotate, rectF.centerX(), rectF.centerY());
			canvas.scale(scaleX / bgScaleX, scaleY / bgScaleY, rectF.left, rectF.top);
			canvas.drawBitmap(fg, rectF.left, rectF.top, p);
			canvas.restore();
		}

		return newb;
	}

	public static Bitmap decodeBoundsInfo(Context gifChooseActivity, String path, int thumbWidth, int thumbHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options = new BitmapFactory.Options();
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		if (null == bitmap) {
			return null;
		}
		// 原始图片的尺寸
		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();
		bitmap.recycle();

		// 缩放图片的尺寸
		Options opts = new Options();
		if (bmpWidth > thumbWidth) {
			opts.outWidth = thumbWidth;
		} else {
			opts.outWidth = bmpWidth;
		}
		if (bmpHeight > thumbHeight) {
			opts.outHeight = thumbHeight;
		} else {
			opts.outHeight = bmpHeight;
		}
		Bitmap resizeBitmap = BitmapFactory.decodeFile(path, opts);
		return resizeBitmap;
	}

	/**
	 * Resize and recompress the image such that it fits the given limits. The
	 * resulting byte array contains an image in JPEG format, regardless of the
	 * original image's content type.
	 * 
	 * @param widthLimit
	 *            The width limit, in pixels
	 * @param heightLimit
	 *            The height limit, in pixels
	 * @param byteLimit
	 *            The binary size limit, in bytes
	 * @return A resized/recompressed version of this image, in JPEG format
	 */
	public static byte[] getResizedImageData(int widthLimit, int heightLimit, int byteLimit, int minQuality, Uri uri, Context context) {
		BitmapFactory.Options options = null;
		try {
			options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			InputStream in = context.getContentResolver().openInputStream(uri);
			BitmapFactory.decodeStream(in, null, options);
			in.close();
		} catch (Exception e) {
			return null;
		}
		int outHeight = options.outHeight;
		int outWidth = options.outWidth;

		float scaleFactor = 1.F;
		while ((outWidth * scaleFactor > widthLimit) || (outHeight * scaleFactor > heightLimit)) {
			scaleFactor *= .99F;
		}

		InputStream input = null;
		try {
			ByteArrayOutputStream os = null;
			int attempts = 1;
			int sampleSize = 1;
			options = new BitmapFactory.Options();
			int quality = 100;
			Bitmap b = null;

			// In this loop, attempt to decode the stream with the best possible
			// subsampling (we
			// start with 1, which means no subsampling - get the original
			// content) without running
			// out of memory.
			do {
				input = context.getContentResolver().openInputStream(uri);
				options.inSampleSize = sampleSize;
				try {
					b = BitmapFactory.decodeStream(input, null, options);
					if (b == null) {
						return null; // Couldn't decode and it wasn't because of
										// an exception,
										// bail.
					}
				} catch (OutOfMemoryError e) {
					sampleSize *= 2; // works best as a power of two
					attempts++;
					continue;
				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
						}
					}
				}
			} while (b == null && attempts < NUMBER_OF_RESIZE_ATTEMPTS);

			if (b == null) {
				return null;
			}

			boolean resultTooBig = true;
			attempts = 1; // reset count for second loop
			// In this loop, we attempt to compress/resize the content to fit
			// the given dimension
			// and file-size limits.
			do {
				try {
					if (options.outWidth > widthLimit || options.outHeight > heightLimit || (os != null && os.size() > byteLimit)) {
						// The decoder does not support the inSampleSize option.
						// Scale the bitmap using Bitmap library.
						int scaledWidth = (int) (outWidth * scaleFactor);
						int scaledHeight = (int) (outHeight * scaleFactor);

						b = Bitmap.createScaledBitmap(b, scaledWidth, scaledHeight, false);
						if (b == null) {
							return null;
						}
					}

					// Compress the image into a JPG. Start with
					// MessageUtils.IMAGE_COMPRESSION_QUALITY.
					// In case that the image byte size is still too large
					// reduce the quality in
					// proportion to the desired byte size.
					os = new ByteArrayOutputStream();
					b.compress(CompressFormat.JPEG, quality, os);
					int jpgFileSize = os.size();
					if (jpgFileSize > byteLimit) {
						quality = (quality * byteLimit) / jpgFileSize; // watch
																		// for
																		// int
																		// division!
						if (quality < minQuality) {
							quality = minQuality;
						}

						os = new ByteArrayOutputStream();
						b.compress(CompressFormat.JPEG, quality, os);
					}
				} catch (java.lang.OutOfMemoryError e) {
				}
				scaleFactor *= .99F;
				attempts++;
				resultTooBig = os == null || os.size() > byteLimit;
			} while (resultTooBig && attempts < NUMBER_OF_RESIZE_ATTEMPTS);
			b.recycle(); // done with the bitmap, release the memory

			return resultTooBig ? null : os.toByteArray();
		} catch (FileNotFoundException e) {
			return null;
		} catch (java.lang.OutOfMemoryError e) {
			return null;
		}
	}

	public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

}
