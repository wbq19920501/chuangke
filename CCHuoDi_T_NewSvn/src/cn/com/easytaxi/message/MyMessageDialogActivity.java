package cn.com.easytaxi.message;

import org.apache.commons.lang3.StringUtils;

import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.ui.MoreWebviewActivity;
import cn.com.easytaxi.ui.bean.MsgBean;
import cn.com.easytaxi.ui.bean.MsgData;
import cn.com.easytaxi.ui.view.CommonDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;

public class MyMessageDialogActivity extends BaseDialogActivity {
	
	private Callback<Object> okBtnCallBack;
	private Callback<Object> cancelBtnCallBack;
	private MsgBean msgBean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}
	
	public void initData(){
		initData(getIntent());
		msgBean = (MsgBean) getIntent().getExtras().getSerializable("msgBean");
		
		okBtnCallBack = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				try {
				if(!StringUtils.isEmpty(gotoClassName)){
					Intent intent = new Intent(MyMessageDialogActivity.this,Class.forName(gotoClassName));
					intent.putExtra("title", "消息详情");
					intent.putExtra("uri", url);
					MyMessageDialogActivity.this.startActivity(intent);
				}
				//消息入库：设置为已读
				if(msgBean != null){
					msgBean.setRead(true);
					MsgData msgData = new MsgData();
					msgData.insert(msgBean);
				}
				CommonDialog dialog= (CommonDialog)param;
				dialog.dismiss();
				
				MyMessageDialogActivity.this.finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
		};
		
		cancelBtnCallBack =  new Callback<Object>() {
			@Override
			public void handle(Object param) {
				//消息入库：设置为未读
				if(msgBean != null){
					msgBean.setRead(false);
					MsgData msgData = new MsgData();
					msgData.insert(msgBean);
				}
				
				CommonDialog dialog= (CommonDialog)param;
				dialog.dismiss();
				MyMessageDialogActivity.this.finish();
			}
		};
		
		Dialog dialog = createDialog(MyMessageDialogActivity.this, title, content, btnOkText, btnCancleText, gotoClassName, okBtnCallBack, cancelBtnCallBack);
		//设置dialog点击返回键不消失
		dialog.setOnKeyListener(((CommonDialog)dialog).keylistener);
		setDialog(dialog);
		showDialog();
		vibrateInfo();
	}
	
	
	/**
	 * 创建dialog
	 */
	public  Dialog createDialog(final Context context,String titile,String content,String btn1,String btn2,final String gotoClassName,Callback<Object> okBtnCallback,Callback<Object> cancelBtnCallback){
		Dialog dialog = new CommonDialog(MyMessageDialogActivity.this, titile, content, btn1, btn2, R.layout.dlg_close,okBtnCallback,cancelBtnCallback);
	    return dialog;
	}

	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		MyMessageDialogActivity.this.finish();
	}
	

}