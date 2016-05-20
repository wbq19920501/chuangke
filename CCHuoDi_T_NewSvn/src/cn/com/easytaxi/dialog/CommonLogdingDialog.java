package cn.com.easytaxi.dialog;

import com.easytaxi.etpassengersx.R;

import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


public class CommonLogdingDialog extends CommonDialog {

	ImageView imageView;

	public CommonLogdingDialog(Context context) {
		super(context, R.layout.common_dlg_loading, R.style.MyDialog);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	public void showWithAnimation(){
		imageView = (ImageView) findViewById(R.id.img);
		Animation operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.wait);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		imageView.startAnimation(operatingAnim);
		this.show();
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub

	}
}
