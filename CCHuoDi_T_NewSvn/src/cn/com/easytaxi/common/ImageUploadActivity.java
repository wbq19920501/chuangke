package cn.com.easytaxi.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ImageUploadActivity extends Activity {

	private ImageUploadActivity self = this;
	protected ImageView imageView;
	protected OnClickListener listener;
	protected Bitmap bitmap;

	// 照相的标识
	private final int CAMERA_WITH_DATA = 0;
	// 选择图片的标识
	private final int PHOTO_PICKED_WITH_DATA = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listener = new OnClickListener() {
			public void onClick(View v) {
				final CharSequence[] items = { "拍照", "相册" };
				AlertDialog dialog = new AlertDialog.Builder(self).setTitle("选择图片").setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// 这里item是根据选择的方式， 在items数组里面定义了两种方式，拍照的下标为1所以就调用拍照方法
						if (item == CAMERA_WITH_DATA) {
							Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
							startActivityForResult(getImageByCamera, CAMERA_WITH_DATA);
						} else {
							Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
							getImage.addCategory(Intent.CATEGORY_OPENABLE);
							getImage.setType("image/*");
							startActivityForResult(getImage, PHOTO_PICKED_WITH_DATA);
						}
					}
				}).create();
				dialog.show();
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode != RESULT_OK)
				return;
			switch (requestCode) {
			case PHOTO_PICKED_WITH_DATA: // 从本地选择图片
				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
				}
				Uri selectedImageUri = data.getData();
				if (selectedImageUri != null) {
					try {
						bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
						imageView.setImageBitmap(bitmap);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				break;
			case CAMERA_WITH_DATA: // 拍照
				Bundle bundle = data.getExtras();
				bitmap = (Bitmap) bundle.get("data");
				if (bitmap != null)
					bitmap.recycle();
				bitmap = (Bitmap) data.getExtras().get("data");
				bitmap = ImageUtil.zoomBitmap(bitmap, imageView.getWidth(), imageView.getHeight());
				bitmap = ImageUtil.getRoundedCornerBitmap(bitmap, 10);
				imageView.setImageBitmap(bitmap);
				break;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}