package cn.com.easytaxi.ui;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.message.BadgeView;
import cn.com.easytaxi.message.MyMessage;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.AboutActivity;
import cn.com.easytaxi.platform.MainHelpActivity;
import cn.com.easytaxi.platform.RegisterActivity;
import cn.com.easytaxi.platform.YdBaseActivity;
import cn.com.easytaxi.platform.service.OneBookService;
import cn.com.easytaxi.ui.bean.YDMenus;
import cn.com.easytaxi.ui.bean.YDMenus.YDMenu;
import cn.com.easytaxi.ui.view.CommonDialog;

import com.easytaxi.etpassengersx.R;
import com.easytaxi.etpassengersx.wxapi.WXPayEntryActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MoreActivity extends YdBaseActivity {

	protected static final int LOGOUT_DLG = 10;
	private View mroe_help;
	private View more_help2;
	private View mroe_msg;
	private View mroe_suggest;
	private View more_history_books;
	private View software_info;

	private TitleBar bar;
	private Button more_btn_cancled;
	private Button more_btn_quit;
	private LinearLayout more_form_net;
	private YDMenus ydMenus;

	private Handler handler;
	public static int MENU_FROM_NET_OK = 1;
	private BadgeView badgeView;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case LOGOUT_DLG:
			return createLogoutDlg();

			// break;

		default:
			break;
		}

		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}

	public Dialog createLogoutDlg() {
		Callback<Object> okBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				CommonDialog dialog = (CommonDialog) param;
				dialog.dismiss();
				logOut();
			}
		};

		Callback<Object> cancelBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				CommonDialog dialog = (CommonDialog) param;
				dialog.dismiss();
			}
		};

		Dialog dialog = new CommonDialog(MoreActivity.this, "提示", "您确认要注销现在手机号码，重新注册新的手机吗？", "确定", "取消", R.layout.dlg_close, okBtnCallback, cancelBtnCallback);
		return dialog;
	}

	/*
	 * private Dialog createLogoutDlg() {
	 * 
	 * Dialog closeDlg = new Dialog(MoreActivity.this, R.style.Customdialog);
	 * 
	 * View view =
	 * LayoutInflater.from(MoreActivity.this).inflate(R.layout.dlg_close, null);
	 * closeDlg.setContentView(view);
	 * 
	 * TextView tv = (TextView) view.findViewById(R.id.dlg_info);
	 * tv.setText("您确认要注销现在手机号码，重新注册新的手机吗？"); Button ok = (Button)
	 * view.findViewById(R.id.button_ok); Button cancel = (Button)
	 * view.findViewById(R.id.button_cancel); ok.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View arg0) { // TODO Auto-generated method
	 * stub dismissDialog(LOGOUT_DLG);
	 * 
	 * logOut();
	 * 
	 * } }); cancel.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View arg0) { // TODO Auto-generated method
	 * stub dismissDialog(LOGOUT_DLG); } }); return closeDlg; }
	 */
	protected void logOut() {
		// TODO Auto-generated method stub

		ETApp.getInstance().logOut();
		Intent intent = new Intent(MoreActivity.this, RegisterActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.p_more);

		initViews();
		initListeners();
		checkUpdate();
		initUserData();
	}

	@Override
	public void initUserData() {

		/*String menuString = ETApp.getInstance().getCacheString("MORE_MENU");

		if (!TextUtils.isEmpty(menuString)) {
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			YDMenus menus = gson.fromJson(menuString, YDMenus.class);
			addItems(menus);
		}*/

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MENU_FROM_NET_OK) {
					String moreString = (String) msg.obj;
					if (!TextUtils.isEmpty(moreString)) {
						Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
						YDMenus menus = gson.fromJson(moreString, YDMenus.class);
						if (menus.error == 0) {
//							ETApp.getInstance().saveCacheString("MORE_MENU", moreString);
							addItems(menus);
							return;
						} else {

						}
					} else {

						// Toast.makeText(MoreActivity.this, "没有更多菜单",
						// 100).show();
					}
				}
				// super.handleMessage(msg);
			}
		};

		getMoreMenuFromNet();
	}

	public void getMoreMenuFromNet() {
		String cityId = getCityId();
		String passengerId = getPassengerId();
		NewNetworkRequest.getMoreMenus(handler, cityId, passengerId);
	}

	private synchronized void addItems(YDMenus menus) {
		more_form_net.removeAllViews();
		ydMenus = menus;
		ArrayList<YDMenu> menuList = ydMenus.menus;
		final int size = menuList.size();

		LayoutInflater li = getLayoutInflater();
		for (int i = 0; i < size; i++) {
			YDMenu menu = menuList.get(i);

			ViewGroup v = (ViewGroup) li.inflate(R.layout.p_more_menu_item, null);
			TextView tv = (TextView) v.findViewById(R.id.menu_tv_name);
			tv.setText(menu.title);

			more_form_net.addView(v);

			v.findViewById(R.id.menu_item).setTag(menu);
			v.findViewById(R.id.menu_item).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					YDMenu menu = (YDMenu) arg0.getTag();
					switch (menu.actionType) {
					case 1:
						String uri = menu.action;
						Intent choiceIntent = new Intent(MoreActivity.this, MoreWebviewActivity.class);
						choiceIntent.putExtra(MoreWebviewActivity.TITLE, menu.title);
						choiceIntent.putExtra(MoreWebviewActivity.URI, uri);
						AppLog.LogD(menu.toString());
						startActivity(choiceIntent);
						break;
					case 2:

						break;

					default:
						break;
					}

				}
			});

		}
	}

	@Override
	protected void onDestroy() {

		if (bar != null) {

			bar.close();
			bar = null;
		}
		super.onDestroy();

	}

	@Override
	public void initListeners() {

		// 反馈建议
		mroe_suggest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (ETApp.getInstance().isLogin()) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(MoreActivity.this, SuggestionActivity.class);
					startActivity(intent);
					// 写入日志
					ActionLogUtil.writeActionLog(MoreActivity.this, R.array.more_fankui_jianyi, "");
				} else {
					Intent intent = new Intent(MoreActivity.this, RegisterActivity.class);
					startActivity(intent);
				}
			}
		});

		// 帮助
		mroe_help.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MoreActivity.this, MainHelpActivity.class);
				startActivity(intent);
			}
		});

		// 打车秘籍
		more_help2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MoreActivity.this, MoreWebviewActivity.class);
				intent.putExtra("title", "使用指南");
				intent.putExtra("type", 0);
				String uri = "file:///android_asset/helpuser_shanxi.html";
				intent.putExtra("uri", uri);
				startActivity(intent);
			}
		});

		software_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MoreActivity.this, MoreWebviewActivity.class);
				intent.putExtra("title", "服务条款");
				String uri = "file:///android_asset/tiaokuan_shanxi.html";
				intent.putExtra("uri", uri);
				intent.putExtra("type", 0);
				startActivity(intent);
			}
		});

		// 我的消息
		mroe_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(MoreActivity.this,
				// MainMsgManageActivity.class);

				if (ETApp.getInstance().isLogin()) {
					// Intent intent = new Intent(MoreActivity.this,
					// cn.com.easytaxi.ui.Message.class);
					Intent intent = new Intent(MoreActivity.this, MyMessage.class);
					startActivity(intent);
					if (badgeView.isShown()) {
						badgeView.hide();
					}

				} else {
					Intent intent = new Intent(MoreActivity.this, RegisterActivity.class);
					startActivity(intent);
				}

			}
		});

		bar = new TitleBar(this);
		bar.setTitleName("更多");

		// 退出
		more_btn_quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent service = new Intent(MoreActivity.this, OneBookService.class);
				stopService(service);
				// Intent name = new Intent(MoreActivity.this,
				// MainService.class);
				// stopService(name);

				int currentVersion = android.os.Build.VERSION.SDK_INT;
				if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
					Intent startMain = new Intent(Intent.ACTION_MAIN);
					startMain.addCategory(Intent.CATEGORY_HOME);
					startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(startMain);
					System.exit(0);
				} else {// android2.1
					ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					am.restartPackage(getPackageName());
				}

				// finish();
				// System.exit(1);
			}
		});

		// 注销
		more_btn_cancled.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showDialog(LOGOUT_DLG);

			}
		});

		// 历史订单
		more_history_books.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (ETApp.getInstance().isLogin()) {
					Intent intent = new Intent(MoreActivity.this, OrderHistory.class);
					startActivity(intent);
					// 写入日志
					ActionLogUtil.writeActionLog(MoreActivity.this, R.array.more_history, "");
				} else {
					Intent intent = new Intent(MoreActivity.this, RegisterActivity.class);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		more_history_books = findViewById(R.id.more_history_books);
		mroe_help = findViewById(R.id.mroe_help);
		more_help2 = findViewById(R.id.mroe_help2);
		software_info = findViewById(R.id.software_info);
		mroe_msg = findViewById(R.id.mroe_msg);
		mroe_suggest = findViewById(R.id.mroe_suggest);
		more_btn_quit = (Button) findViewById(R.id.more_btn_quit);
		more_btn_cancled = (Button) findViewById(R.id.more_btn_cancled);
		more_form_net = (LinearLayout) findViewById(R.id.more_form_net);

		badgeView = new BadgeView(this, mroe_msg);
		badgeView.setText("New");
		badgeView.setTextColor(Color.RED);
		badgeView.setBadgeBackgroundColor(Color.YELLOW);
		badgeView.setBadgeMargin(30, 10);
		badgeView.setTextSize(14);
		if (ETApp.getInstance().getCacheBoolean("new_message")) {
			badgeView.show();
		}

		findViewById(R.id.mroe_about_us).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MoreActivity.this, AboutActivity.class);
				MoreActivity.this.startActivity(intent);
			}
		});
		
		//正式环境不能注销，测试环境可以注销
		if(WXPayEntryActivity.url.equals("http://taxibill.easytaxi.com.cn:8080/YdAccount/do")){
			more_btn_cancled.setVisibility(View.GONE);
		} 
	}

	@Override
	protected void regReceiver() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unRegReceiver() {
		// TODO Auto-generated method stub

	}

	/**
	 * 检查版本更新
	 * 
	 * @return void
	 */
	private void checkUpdate() {
		try {
			PackageManager pm = MoreActivity.this.getPackageManager();
			final PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);

			TextView versionView = (TextView) findViewById(R.id.setting_version);
			versionView.setText("当前版本：" + pi.versionName);
			findViewById(R.id.setting_update).setVisibility(View.GONE);
//			findViewById(R.id.setting_update).setOnClickListener(new View.OnClickListener() {
//
//				public void onClick(View v) {
//					String cityId = getCityId();
//					if (TextUtils.isEmpty(cityId)) {
//						cityId = "1";
//					}
//					String passengerId = getPassengerId();
//					NewNetworkRequest.checkUpdate(MoreActivity.this, cityId, pi.versionCode, true,passengerId, newVersion);
//				}
//			});

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	Callback<String> newVersion = new Callback<String>() {
		@Override
		public void handle(String param) {
			// TODO Auto-generated method stub
			if (param.equals("current")) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MoreActivity.this, "当前版本已经最新", 300).show();
					}
				});
			}
		}
	};
}
