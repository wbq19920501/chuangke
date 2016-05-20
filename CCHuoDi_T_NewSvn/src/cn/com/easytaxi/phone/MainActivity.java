package cn.com.easytaxi.phone;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.HttpUtil;
import cn.com.easytaxi.common.Pair;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.phone.adapter.PhoneListAdapter;
import cn.com.easytaxi.phone.common.Const;
import cn.com.easytaxi.phone.common.DaoAdapter;
import cn.com.easytaxi.ui.view.CommonDialog;

/**
 * 电话召车
 * 
 * @author magi
 */
public class MainActivity extends Activity {

	private MainActivity self = this;

	private ListView listView;

	private SessionAdapter session;
	private DaoAdapter dao;

	private String cityId;
	private String cityName;

	private CitySwitchReceiver citySwitchReceiver;
	private ReloadReceiver reloadReceiver;

	private TitleBar bar;
	private boolean isFisrt = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone);
		session = new SessionAdapter(this);
		dao = new DaoAdapter(this);

		cityId = session.get("_CITY_ID");
		cityName = session.get("_CITY_NAME");

		bar = new TitleBar(MainActivity.this);
		bar.setTitleName("电话召车");
		bar.setRightButtonVisible(View.VISIBLE);
		bar.switchToCityButton();
		if(StringUtils.isEmpty(cityName)){
			cityName = "北京";
		}else if(cityName.equals("北京市")){
			
			cityName = StringUtils.replace(cityName, "市", "");
		}else{
			
		}
		
		if(StringUtils.isEmpty(cityId)){
			cityId = "1";
		}else{
			
		}
		
		bar.setRightButtonText(cityName);
 
		listView = (ListView) findViewById(R.id.phone_list);
		View view = new View(MainActivity.this);
		listView.addFooterView(view);
		findViewById(R.id.phone_item_add_button).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!ETApp.getInstance().isLogin()) {
					showRegistDialog(getBaseContext(), "提示", "请先注册以后再使用此功能", "立刻注册", "稍后再说", "cn.com.easytaxi.platform.RegisterActivity");
				}else{
					Intent intent = new Intent(self, AddActivity.class);

					intent.putExtra("_CITY_ID", cityId);
					intent.putExtra("_CITY_NAME", cityName);
					startActivity(intent);
					//写入日志
					ActionLogUtil.writeActionLog(self, R.array.phone_add_driver,"");
				}
			}
		});

		if (session.get("_PHONE_LIST_VERSION") == null || dao.getPhoneSize() == 0) {
			final ProgressDialog progressDialog = ProgressDialog.show(self, "稍等...", "正在加载全国电召列表", true, true);

			HttpUtil.sendMsg(Const.REQUEST_URL + "getPhoneList", null, new Callback<Object>() {
				@Override
				public void handle(Object param) {
					if (param != null) {
						try {
							JSONObject json = (JSONObject) param;
							
							AppLog.LogD(json.toString());
							
							dao.savePhoneList(json.getJSONArray("datas"));
							session.set("_PHONE_LIST_VERSION", json.getString("version"));
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public void complete() {
					progressDialog.dismiss();
					loadData();
				}
			});
		} else {
			loadData();
		}

		{
			citySwitchReceiver = new CitySwitchReceiver();
			IntentFilter intentFilter = new IntentFilter("cn.com.easytaxi.cityswitch.user");
			registerReceiver(citySwitchReceiver, intentFilter);
		}

		{
			reloadReceiver = new ReloadReceiver();
			IntentFilter intentFilter = new IntentFilter("cn.com.easytaxi.phone.add");
			registerReceiver(reloadReceiver, intentFilter);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isFisrt) {
			isFisrt = !isFisrt;
		} else {
			loadData();
		}
	}

	@Override
	protected void onDestroy() {
		bar.close();
		if (session != null) {
			session.close();
			session = null;
		}
		if (dao != null) {
			dao.close();
			dao = null;
		}
		if (citySwitchReceiver != null) {
			unregisterReceiver(citySwitchReceiver);
			citySwitchReceiver = null;
		}
		if (reloadReceiver != null) {
			unregisterReceiver(reloadReceiver);
			reloadReceiver = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "全选");
		menu.add(0, 1, 1, "反选");
		menu.add(0, 2, 2, "删除所选司机");
		menu.add(1, 3, 3, "添加司机");
		//menu.add(1, 4, 4, "本城市召车历史");

		if (dao.getDrivers(cityId).isEmpty()) {
			menu.setGroupEnabled(0, false);
		} else {
			menu.setGroupEnabled(0, true);
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (!ETApp.getInstance().isLogin()) {
			showRegistDialog(self.getBaseContext(), "提示", "请先注册以后再使用此功能", "立刻注册", "稍后再说", "cn.com.easytaxi.platform.RegisterActivity");
		}else{
			HeaderViewListAdapter hadapter = (HeaderViewListAdapter) listView.getAdapter();
			PhoneListAdapter adapter = (PhoneListAdapter) hadapter.getWrappedAdapter();
			// PhoneListAdapter adapter = (PhoneListAdapter) listView.getAdapter();
			if (item.getItemId() == 0) {
				adapter.selectAll();
			} else if (item.getItemId() == 1) {
				adapter.unSelect();
			} else if (item.getItemId() == 2) {
				final List<String> list = adapter.getSelectedPhones();
				if (list.size() > 0) {
					Window.confirm(self, "您确定要删除所选司机信息吗？", new Callback<Object>() {
						@Override
						public void handle(Object param) {
							dao.deleteDrivers(list);
							loadData();
						}
					});
				}
			} else if (item.getItemId() == 3) {
					Intent intent = new Intent(self, AddActivity.class);
					 
					intent.putExtra("_CITY_ID", cityId);
					intent.putExtra("_CITY_NAME", cityName);
					startActivity(intent);
			}  
			return super.onMenuItemSelected(featureId, item);
		}
		return false;
	}

	private void loadData() {
 
		List<Pair<String, String>> companys = dao.getPhoneList(cityId);
		if (cityId.equals("1")) {
			// 北京
			companys.clear();
			Pair<String, String> beijing = new Pair<String, String>("出租车调度中心", "01096106");
			beijing.data="0";
			companys.add(beijing);
		} 
		
		if(cityId.equals("211")){
			
			companys.clear();
			Pair<String, String> cd1 = new Pair<String, String>("出租车调度中心", "028962999");
			cd1.data="0";
			companys.add(cd1);
			
			Pair<String, String> cd2 = new Pair<String, String>("出租车调度中心", "4008000365");
			cd2.data="0";
			companys.add(cd2);
			 
			
			
//			for(int i = 0 ; i < companys.size(); i++){
//				Pair<String, String>	c =  companys.get(i);
//				if(c.value.equals("962999")){
//					c.value = "028962999";
//					break;
//				}
//			}
		}
		
		List<Pair<String, String>> dirvers = dao.getDrivers(cityId);
		listView.setAdapter(new PhoneListAdapter(this, dao, cityId, companys, dirvers));
	}

	
	
	private class CitySwitchReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context action, Intent intent) {
			cityName = intent.getStringExtra("_CITY_NAME");
			cityId = intent.getStringExtra("_CITY_ID");
			bar.setRightButtonText(cityName);
 	
			loadData();
		}
	}

	private class ReloadReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context action, Intent intent) {
			loadData();
		}
	}

	/**
	 * 显示注册dialog
	 */
	public  void showRegistDialog(final Context context,String titile,String content,String btn1,String btn2,final String gotoClassName){
			 Callback<Object> okBtnCallback = new Callback<Object>() {
					@Override
					public void handle(Object param) {
						try {
							Intent intent;
							intent = new Intent(context,Class.forName(gotoClassName));
							startActivity(intent);
							
							CommonDialog dialog= (CommonDialog)param;
							dialog.dismiss();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				};
				
				Callback<Object> cancelBtnCallback = new Callback<Object>() {
					@Override
					public void handle(Object param) {
						CommonDialog dialog= (CommonDialog)param;
						dialog.dismiss();
					}
				};
				
				Dialog dialog = new CommonDialog(MainActivity.this,titile, content, btn1, btn2, R.layout.dlg_close,okBtnCallback,cancelBtnCallback);
				dialog.show();
		}
}
