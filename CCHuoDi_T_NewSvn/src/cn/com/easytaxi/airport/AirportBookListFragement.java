package cn.com.easytaxi.airport;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;

import com.easytaxi.etpassengersx.R;

import cn.com.easytaxi.book.BookListActivity;
import cn.com.easytaxi.book.BookPublish;
import cn.com.easytaxi.book.BookUtil;
import cn.com.easytaxi.book.adapter.BookListAdapter;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.SocketUtil;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.common.XListView;
import cn.com.easytaxi.common.XListView.IXListViewListener;
import cn.com.easytaxi.common.XListViewFooter;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.MainActivityNew;
import cn.com.easytaxi.ui.BookBaseFragement;

public class AirportBookListFragement extends AirportBookBaseFragement implements IXListViewListener, OnClickListener {

	// View countLayout;
	private TextView bookCount;
	// TextView cityBookCount;
	private XListView bookList;
	private BookListAdapter adapter;
	private View pubishBut;
//	private TitleBar bar;
	// 0 = init , 1 = refresh , 2 = loadmore
	private int action = 0;
	private String passengerId;
	private TextView unRegister;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.book_list, container, false);
		initViews(view);
		initDatas();
		initListner();
		// 注册刷新广播
		registReceiver();
//		startLoopTime();
		AppLog.LogD(" BookListFragement onCreateView --------------"); 
		return view;
	}
	 

	public void loadData(){
		
	}
	
	@Override
	public void onResume() {
		
		
		if (!StringUtils.isEmpty(getPassengerId())) {
			unRegister.setVisibility(View.GONE);
			// bookList.setVisibility(View.INVISIBLE);
		} else {
			unRegister.setVisibility(View.VISIBLE);
		}

		super.onResume();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}

	}

	public void callDriver(final String phones) {
		try {
			Window.callTaxi(bookParent, phones);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initViews(View view) {
		// countLayout = findViewById(R.id.book_count_layout);
		bookCount = (TextView) view.findViewById(R.id.book_count);
		String subString = "0";
		String txt = bookParent.getString(R.string.book_count, subString);
		bookCount.setText(BookUtil.getSpecialText(txt, subString,
				bookParent.getResources().getColor(R.color.yellow_state)));
		// cityBookCount = (TextView) findViewById(R.id.book_city_count);
		bookList = (XListView) view.findViewById(R.id.book_list);
		bookList.setPullLoadEnable(true);
		pubishBut = view.findViewById(R.id.book_list_publish);
		unRegister = (TextView) view.findViewById(R.id.un_reg);
	}

	private void initDatas() {
		adapter = new BookListAdapter(bookParent, datas);
		bookList.setAdapter(adapter);

		if (!StringUtils.isEmpty(getPassengerId())) {
			count = START_ID;
			getMyCount(0);
			// getCityCount(0);
			getAPage(0);
		} else {

		}
	}

	private void getMyCount(final long delay) {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(delay);
					JSONObject json = new JSONObject();
					json.put("action", "bookAction");
					json.put("method", "getBookSizeByPassenger");
					
					json.put("cityId", MainActivityNew.cityId);
					json.put("cityName",MainActivityNew.currentCityName);
					json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
					Long id = Long.valueOf(getPassengerId());
					SocketUtil.getJSONObject(id, json, new Callback<JSONObject>() {
						@Override
						public void handle(JSONObject param) {
							try {
								String subString = param.getString("size");
								String txt = bookParent.getString(R.string.book_count, subString);
								bookCount.setText(BookUtil.getSpecialText(txt, subString,
										bookParent.getResources().getColor(R.color.yellow_state)));
							} catch (Exception e) {
								e.printStackTrace();
								String subString = "0";
								String txt = bookParent.getString(R.string.book_count, subString);
								bookCount.setText(BookUtil.getSpecialText(txt, subString,
										bookParent.getResources().getColor(R.color.yellow_state)));
							}
						}

						@Override
						public void error(Throwable e) {
							// TODO Auto-generated method stub
							super.error(e);
							String subString = "0";
							String txt =bookParent.getString(R.string.book_count, subString);
							bookCount.setText(BookUtil.getSpecialText(txt, subString,
									bookParent.getResources().getColor(R.color.yellow_state)));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					String subString = "0";
					String txt = bookParent.getString(R.string.book_count, subString);
					bookCount.setText(BookUtil.getSpecialText(txt, subString,
							bookParent.getResources().getColor(R.color.yellow_state)));
					// LayoutParams params = (LayoutParams)
					// countLayout.getLayoutParams();
					// params.gravity=Gravity.CENTER;
					// countLayout.setLayoutParams(params);
				}
			};
		}.start();
	}

 

	private void initListner() {
		bookList.setXListViewListener(this);
		pubishBut.setOnClickListener(this);
	}

	Handler ui = new Handler();

	public void onRefresh() {

		if (!StringUtils.isEmpty(getPassengerId())) {
			bookList.setAllowUpdate(false);
			count = START_ID;
			getMyCount(0);
			// getCityCount(0);
			getAPage(1);
		} else {

		}

	}

	public void onLoadMore() {
		if (!enableLoadMore) {
			bookList.stopRefresh();
			bookList.stopLoadMore();
			bookList.setAllowUpdate(true);
			Toast.makeText(bookParent, "没有了下一页了", Toast.LENGTH_SHORT).show();
			return;
		}
		getAPage(2);
	}

	public void getAPage(final int action) {
		bookList.setAllowUpdate(false);

		if (action == 0) {
			bookList.setFooterState(XListViewFooter.STATE_LOADING);
		}

		loadPage(false, new Callback<Integer>() {
			@Override
			public void handle(Integer result) {
				adapter.notifyDataSetChanged();

				if (action != 2) {// 加载更多时，不回到首项
					try {
						bookList.setSelection(0);
					} catch (Exception e) {

					}
				}
			}

			@Override
			public void complete() {
				super.complete();
				bookList.stopRefresh();
				bookList.stopLoadMore();
				bookList.setAllowUpdate(true);
				if (action == 0)
					bookList.setFooterState(XListViewFooter.STATE_NORMAL);

			}

			@Override
			public void error(Throwable e) {
				super.error(e);
			}
		});
	}

	// 查询出租车
	// private class SelectCityButtonListener implements View.OnClickListener {
	// public void onClick(View v) {
	// Window.selectCity(self, session, new Callback<JSONObject>() {
	// @Override
	// public void handle(JSONObject json) {
	// try {
	// session.set("_CITY_NAME", json.getString("_NAME"));
	// session.set("_CITY_ID", json.getString("_ID"));
	// Intent intent = new Intent("cn.com.easytaxi.cityswitch");
	// self.sendBroadcast(intent);
	// } catch (Throwable e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }
	// }

	public void onClick(View v) {
//		String startAddress = this.getIntent().getStringExtra("startAddress");
//		Intent i = new Intent(bookParent, BookPublish.class);
//		i.putExtra("startAddress", startAddress);
//		startActivity(i);
	}

	private ReloadReceiver refreshReceiver;

	private void registReceiver() {
		refreshReceiver = new ReloadReceiver();
		IntentFilter filter = new IntentFilter("cn.com.easytaxi.book.refresh_list");
		filter.addAction(Intent.ACTION_SCREEN_ON);
		bookParent.registerReceiver(refreshReceiver, filter);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		stopLoopTime();
		AppLog.LogD(" BookListFragement onDestroy --------------"); 
		if (refreshReceiver != null) {
			bookParent.unregisterReceiver(refreshReceiver);
			refreshReceiver = null;
		}

		enableLoadMore = true;
		count = 0;
		datas.clear();
		super.onDestroy();
	}

	@Override
	public void onTimeChange() {
		adapter.notifyDataSetChanged();
	}

	private class ReloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			onRefresh();
		}

	}
}
