package cn.com.easytaxi.platform;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.easytaxi.AppLog;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Config;
import cn.com.easytaxi.onetaxi.TitleBar;

import com.umeng.api.sns.UMSnsService;

public class RemdActivity extends Activity {

	TitleBar bar;
	private View send_sms;
	private View send_sina;
	private View send_qq;
	private View send_to_taxi;
	private View send_to_user;

	private final List<ResolveInfo> mActivites = new ArrayList<ResolveInfo>();

	private List<SharedItem> sharedItems = new ArrayList<SharedItem>(12);
	private LinearLayout shared_public;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_remd_setting);
		initViews();
		inintListeners();
		initUserData();
	}

	private void initUserData() {
		// TODO Auto-generated method stub
//		 Intent intent=new Intent(Intent.ACTION_SEND);
//		 intent.setType("text/plain");
//		 intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
//		 intent.putExtra(Intent.EXTRA_TEXT, "终于可以了!!!");
//		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		 startActivity(Intent.createChooser(intent, getTitle()));
//		loadShareItems();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		};
	};
	private Intent mIntent;

	private void loadShareItems() {

		// intent.addCategory(Intent.CATEGORY_DEFAULT);
		loadActivitys();

		addItems();

	}

	private void addItems() {
		LayoutInflater li = getLayoutInflater();
		for (int i = 0; i < mActivites.size(); i++) {
			ResolveInfo resolve = mActivites.get(i);
			SharedItem shareItem = new SharedItem();

			shareItem.appName = resolve.loadLabel(getPackageManager()).toString();

			ViewGroup v = (ViewGroup) li.inflate(R.layout.p_remdchild_item, null);
			v.setTag(shareItem);
			TextView tv = (TextView) v.findViewById(R.id.shared_tv_name);
			tv.setText(shareItem.appName);
			ImageView icon = (ImageView) v.findViewById(R.id.imageView_icon);

			shareItem.appIntent = new Intent(mIntent);
			ComponentName chosenName = new ComponentName(resolve.activityInfo.packageName, resolve.activityInfo.name);
			shareItem.appIntent.setComponent(chosenName);
			icon.setImageDrawable(resolve.loadIcon(getPackageManager()));
			if (i == mActivites.size() - 1) {
				v.removeView(v.findViewById(R.id.slide));
			}

			shared_public.addView(v);

			v.findViewById(R.id.send_sms).setTag(shareItem);
			v.findViewById(R.id.send_sms).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					SharedItem shi = (SharedItem) arg0.getTag();
					Intent choiceIntent = shi.appIntent;
					startActivity(choiceIntent);
				}
			});

		}
	}

	private void loadActivitys() {
		mIntent = new Intent(Intent.ACTION_SEND);
//		mIntent.setType("image/*");
		mIntent.setType("text/plain");
		mIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		//mIntent.putExtra(Intent.EXTRA_STREAM, value)
		 
//		String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/advice.png";
//		File file = new File(path);
		mIntent.putExtra(Intent.EXTRA_TEXT, Config.SHARE_CONTENT);
//		mIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		mActivites.clear();
		List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(mIntent, 0);

		final int resolveInfoCount = resolveInfos.size();

		for (int i = 0; i < resolveInfoCount; i++) {

			ResolveInfo resolveInfo = resolveInfos.get(i);
			if ("com.android.mms.ui.ComposeMessageActivity".equals(resolveInfo.activityInfo.name.toString())) {
				continue;
			}
			
			if("com.google.android.googlequicksearchbox.SearchActivity".equals(resolveInfo.activityInfo.name.toString())){
				continue;
			}
			AppLog.LogD(resolveInfo.toString());
			// if("com.android.mms.ui.ComposeMessageActivity".equals(resolveInfo.))
			mActivites.add(resolveInfo);
		}
	}

	private void launchActivity(int item) {
		if (item == 0) {
			// 短信
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
			intent.putExtra("sms_body", Config.SHARE_CONTENT);
			startActivity(intent);
			//写入日志
			ActionLogUtil.writeActionLog(RemdActivity.this, R.array.share_by_message, "");
		} else if (item == 1) {
			// 新浪微博
			if (UMSnsService.isAuthorized(this, UMSnsService.SHARE_TO.SINA)) {
				UMSnsService.shareToSina(this, Config.SHARE_CONTENT_WEIBOO, null);
			} else {
				UMSnsService.oauthSina(this, null);
			}
			//写入日志
			ActionLogUtil.writeActionLog(RemdActivity.this,  R.array.share_by_sina_weibo, "");
		} else if (item == 2) {
			// 腾讯微博
			if (UMSnsService.isAuthorized(this, UMSnsService.SHARE_TO.TENC)) {
				UMSnsService.shareToTenc(this, Config.SHARE_CONTENT_WEIBOO, null);
			} else {
				UMSnsService.oauthTenc(this, null);
			}
			//写入日志
			ActionLogUtil.writeActionLog(RemdActivity.this, R.array.share_by_tencent_weibo, "");
		} else if (item == 3) {
			Intent intent = new Intent(this, RemTaxiActiviy.class);
			this.startActivity(intent);
			//写入日志
			ActionLogUtil.writeActionLog(RemdActivity.this, R.array.share_to_driver, "");
		} else if (item == 4) {
			Intent intent = new Intent(this, RemPassengerActiviy.class);
			this.startActivity(intent);
			//写入日志
			ActionLogUtil.writeActionLog(RemdActivity.this, R.array.share_to_passenger, "");
		}
	}

	private void inintListeners() {
		// TODO Auto-generated method stub

		send_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				launchActivity(0);
			}
		});
		send_sina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				launchActivity(1);
			}
		});
		send_qq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				launchActivity(2);
			}
		});
		send_to_taxi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				launchActivity(3);
			}
		});
		send_to_user.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				launchActivity(4);
			}
		});
	}

	private void initViews() {
		// TODO Auto-generated method stub
		bar = new TitleBar(this);
		bar.setTitleName("分享");

		send_sms = findViewById(R.id.send_sms);
		send_sina = findViewById(R.id.send_sina);
		send_qq = findViewById(R.id.send_qq);
		send_to_taxi = findViewById(R.id.send_to_taxi);
		send_to_user = findViewById(R.id.send_to_user);

		shared_public = (LinearLayout) findViewById(R.id.shared_public);
	}

	@Override
	protected void onDestroy() {
		bar.close();
		super.onDestroy();
	}

	public static class SharedItem {
		public ComponentName chosenName;
		public String appName = "";
		public Intent appIntent;
		public int appId;
	}
}
