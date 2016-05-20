package cn.com.easytaxi.mine;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;


public abstract class SystemMultiMediaIntent {

	public SystemMultiMediaIntent(Activity mActivity) {
		super();
		this.mActivity = mActivity;
	}

	private Activity mActivity;

	/**
	 * 选择图片文件
	 */
	public static final int INTENT_IMAGE_CODE = 1;
	/**
	 * 调用系统相机拍照
	 */
	public static final int INTENT_CAMERA_CODE = 2;
	/**
	 * 调用系统相机进行录像
	 */
	public static final int INTENT_VIDEO_CODE = 3;
	/**
	 * 选择视频文件
	 */
	public static final int INTENT_VCR_CODE = 4;
	/**
	 * 选择音频文件
	 */
	public static final int INTENT_AUDIO_CODE = 5;

	// 调用系统图库
	public void getPicFromSys() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		mActivity.startActivityForResult(intent, INTENT_IMAGE_CODE);
	}

	// 选择视频文件
	public void getVCRFromSys() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("video/*");
		mActivity.startActivityForResult(intent, INTENT_VCR_CODE);
	}

	// 选择音频文件
	public void getAudioFromSys() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		mActivity.startActivityForResult(intent, INTENT_AUDIO_CODE);
	}

	
	/**
	 * 调用系统相机拍照
	 * @deprecated
	 * @throws Exception
	 */
	public void takeCreamer() throws Exception {
		File mPictureFile = getOutputPictureFile();
		if (mPictureFile == null) {
			throw new Exception("mPictureFile is null");
		}

		// 必须确保文件夹路径存在，否则拍照后无法完成回调
		File dirFile = mPictureFile.getParentFile();
		if (!dirFile.exists()) {
			try {
				dirFile.mkdirs();// 创建存放图片的目录
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
		}

		Uri uri = Uri.fromFile(mPictureFile);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,10);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		mActivity.startActivityForResult(intent, INTENT_CAMERA_CODE);
	}
	
	// 调用系统相机拍照
	public void takeCreamerFromSys(){
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mActivity.startActivityForResult(intent, INTENT_CAMERA_CODE);
		}

	// 调用系统相机录像
	public void takeVideoFromSys() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, INTENT_VIDEO_CODE);
		mActivity.startActivityForResult(intent, INTENT_VIDEO_CODE);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Uri uri = data.getData();
			switch (requestCode) {
			case INTENT_IMAGE_CODE:
				// 返回图片
				onGetPicFromSys(uri);
				break;
			case INTENT_CAMERA_CODE:
				// 返回拍照后的图片
				onTakeCreamerFromSys(data);
				break;
			case INTENT_VIDEO_CODE:
				// 返回录像
				onTakeVideoFromSys(uri);
				break;
			case INTENT_VCR_CODE:
				// 返回视频文件
				onGetVCRFromSys(uri);
				break;
			case INTENT_AUDIO_CODE:
				// 返回音频文件
				onGetAudioFromSys(uri);
				break;
			default:
				break;
			}
		} else {
			AppLog.LogE("onActivityResult-->Intent data is null!");
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param type
	 *            要创建的文件类型 INTENT_CAMERA_CODE为图片，INTENT_VIDEO_CODE为视频
	 * @return 文件
	 */
	private File getOutputMediaFile(int type) {
		if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			AppLog.LogE("sdcard is not exist");
			return null;
		}

		File mediaStorageDir = new File(ETApp.getmSdcardAppDir() + "/tmp_media/");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				AppLog.LogE("failed to create mediaStorageDir!");
				return null;
			}
		}

		long timeStamp = System.currentTimeMillis();
		File mediaFile = null;
		if (type == INTENT_CAMERA_CODE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png");
		} else if (type == INTENT_VIDEO_CODE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			AppLog.LogE("file type is error");
			return null;
		}

		AppLog.LogD("mediaFile.getAbsolutePath = " + mediaFile.getAbsolutePath());

		return mediaFile;
	}

	public File getOutputPictureFile() {
		return getOutputMediaFile(INTENT_CAMERA_CODE);
	}

	public File getOutputVideoFile() {
		return getOutputMediaFile(INTENT_VIDEO_CODE);
	}
	
	public File getPicFileFromIntent(Intent data) throws Exception {
		if(data == null){
			throw new NullPointerException();
		}
		
		Bundle bundle = data.getExtras();
		if (bundle != null) {
			Bitmap photo = (Bitmap) bundle.get("data");
			return savePicInLocal(photo);
		}else{
			throw new NullPointerException();
		}
	}

	// 保存拍摄的照片到手机的sd卡
	public File savePicInLocal(Bitmap bitmap) throws Exception {
		File mPictureFile = getOutputPictureFile();
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null; // 字节数组输出流
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组
			if (!mPictureFile.exists()) {
				mPictureFile.createNewFile();// 创建文件
			}
			// 将字节数组写入到刚创建的图片文件中
			fos = new FileOutputStream(mPictureFile);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
			return mPictureFile;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			if (baos != null) {
				try {
					baos.flush();
					baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	public abstract void onGetPicFromSys(Uri uri);

	public abstract void onGetVCRFromSys(Uri uri);

	public abstract void onGetAudioFromSys(Uri uri);

	public abstract void onTakeCreamerFromSys(Intent Data);

	public abstract void onTakeVideoFromSys(Uri uri);
	
	
}

