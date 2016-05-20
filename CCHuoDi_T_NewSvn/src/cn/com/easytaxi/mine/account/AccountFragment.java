package cn.com.easytaxi.mine.account;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.XListView;
import cn.com.easytaxi.common.XListView.IXListViewListener;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;

/**
 * @ClassName: ImFragment
 * @Description: TODO
 * @author Brook xu
 * @date 2013-8-2 下午4:28:36
 * @version 1.0
 */
public class AccountFragment extends Fragment implements IXListViewListener, OnClickListener {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private int type;
	private List<Account> listDatas = new ArrayList<Account>();
	private MyAccountAdapter adapter;
	// 分页每页查询记录条数
	private static final int PAGE_SIZE = 10;
	// 分页默认起始页数
	private static final int PAGE_START_COUNT = 1;

	private TextView tvStartTime;
	private TextView tvEndTime;
	// 合计数
	private TextView tvAccount;
	private TextView tvAccountUnit;

	private TextView tvColumnTitleTime;
	private TextView tvColumnTitleAccount;
	private TextView tvColumnTitleRemark;

	private XListView mListView;

	private Calendar startTime;
	private Calendar endTime;
	private boolean isCreated = false;

	AccountTotle account;

	public AccountFragment(int type) {
		this.type = type;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyAccountActivity activity = (MyAccountActivity) this.getActivity();
		adapter = new MyAccountAdapter(activity, listDatas);
		startTime = Calendar.getInstance();
		endTime = Calendar.getInstance();
		// 默认时间段为月初到月底
		startTime.set(Calendar.DAY_OF_MONTH, 1);

		switch (type) {
		case Account.TYPE_ET_MONEY:
			account = new AccountTotle(Account.TYPE_ET_MONEY);
			break;
		case Account.TYPE_ET_COIN:
			account = new AccountTotle(Account.TYPE_ET_COIN);
			break;
		case Account.TYPE_ET_SCORE:
			account = new AccountTotle(Account.TYPE_ET_SCORE);
			break;
		default:
			break;
		}

		if (!isCreated) {
			AppLog.LogD("Fragment onCreate-->" + type);
			onRefresh();
		}

		isCreated = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.p_mine_account_pager_item, container, false);
		initViews(view);
		initDatas();
		return view;

	}

	private void initViews(View view) {
		tvStartTime = (TextView) view.findViewById(R.id.p_mine_account_starttime);
		tvEndTime = (TextView) view.findViewById(R.id.p_mine_account_endtime);
		tvAccount = (TextView) view.findViewById(R.id.p_mine_account_count);
		tvAccountUnit = (TextView) view.findViewById(R.id.p_mine_account_countunit);
		tvColumnTitleTime = (TextView) view.findViewById(R.id.p_mine_account_column_time);
		tvColumnTitleAccount = (TextView) view.findViewById(R.id.p_mine_account_column_count);
		tvColumnTitleRemark = (TextView) view.findViewById(R.id.p_mine_account_column_remark);

		mListView = (XListView) view.findViewById(R.id.p_mine_account_list);
		mListView.setXListViewListener(this);
		mListView.setPullLoadEnable(true);

		tvStartTime.setOnClickListener(this);
		tvEndTime.setOnClickListener(this);

	}

	private void initDatas() {
		switch (type) {
		case Account.TYPE_ET_MONEY:
			tvAccountUnit.setText("元");
			tvColumnTitleAccount.setText("金额（元）");
			break;
		case Account.TYPE_ET_COIN:
			tvAccountUnit.setText("枚");
			tvColumnTitleAccount.setText("易达币");
			break;
		case Account.TYPE_ET_SCORE:
			tvAccountUnit.setText("分");
			tvColumnTitleAccount.setText("积分");
			break;
		default:
			break;
		}

		mListView.setAdapter(adapter);
		tvStartTime.setText(getShowStringByCalendar(startTime));
		tvEndTime.setText(getShowStringByCalendar(endTime));

	}

	private int[] getNumberByCalendar(Calendar calendar) {
		int[] date = new int[3];
		date[0] = calendar.get(Calendar.YEAR);
		date[1] = calendar.get(Calendar.MONTH);
		date[2] = calendar.get(Calendar.DAY_OF_MONTH);
		return date;
	}

	private String getStringByCalendar(Calendar calendar) {
		if (calendar == null) {
			return "";
		}

		return dateFormat.format(calendar.getTime());
	}

	/**
	 * 界面上显示的日期-不显示年份
	 * 
	 * @param calendar
	 * @return
	 * @return String
	 */
	private String getShowStringByCalendar(Calendar calendar) {
		String result = getStringByCalendar(calendar);
		int index = result.indexOf("-");
		if (index == -1) {
			return "";
		} else {
			return result.substring(index + 1);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// 金钱
	private Callback<Object> requestEtMoneyCallback = new Callback<Object>() {
		public void complete() {
			mListView.stopRefresh();
			mListView.stopLoadMore();
			mListView.setAllowUpdate(true);
		}

		public void error(Throwable e) {
			showMsg("获取数据失败，请稍后重试！");
		};

		@Override
		public void handle(Object param) {
			// TODO Auto-generated method stub
			try {
				JSONObject object = (JSONObject) param;
				if (object.getInt("error") == 0) {
					boolean isRefresh = object.getBoolean("clientIsRefresh");
					account.setValues(object);
					tvAccount.setText(String.valueOf(account.getTotle()));

					synchronized (listDatas) {
						if (isRefresh) {// 刷新
							listDatas.clear();
							listDatas.addAll(account.getAccountList());

							adapter.setData(listDatas);
							adapter.notifyDataSetChanged();
						} else {// 更多
							listDatas.addAll(account.getAccountList());
							adapter.setData(listDatas);
							adapter.notifyDataSetChanged();
						}
					}
				} else {
					showMsg(object.getString("errormsg"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void showMsg(String msg) {
		Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	// 易达币
	private Callback<Object> requestEtCoinCallback = new Callback<Object>() {

		public void complete() {
			mListView.stopRefresh();
			mListView.stopLoadMore();
			mListView.setAllowUpdate(true);
		}

		public void error(Throwable e) {
			showMsg("获取数据失败，请稍后重试！");
		};

		@Override
		public void handle(Object param) {
			// TODO Auto-generated method stub
			try {
				JSONObject object = (JSONObject) param;
				if (object.getInt("error") == 0) {
					boolean isRefresh = object.getBoolean("clientIsRefresh");
					account.setValues(object);
					tvAccount.setText(String.valueOf(account.getTotle()));
					synchronized (listDatas) {
						if (isRefresh) {// 刷新
							listDatas.clear();
							listDatas.addAll(account.getAccountList());

							adapter.setData(listDatas);
							adapter.notifyDataSetChanged();
						} else {// 更多
							listDatas.addAll(account.getAccountList());
							adapter.setData(listDatas);
							adapter.notifyDataSetChanged();
						}
					}
				} else {
					showMsg(object.getString("errormsg"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	// 积分
	private Callback<Object> requestEtScoreCallback = new Callback<Object>() {

		public void complete() {
			mListView.stopRefresh();
			mListView.stopLoadMore();
			mListView.setAllowUpdate(true);
		}

		public void error(Throwable e) {
			showMsg("获取数据失败，请稍后重试！");
		}

		@Override
		public void handle(Object param) {
			// TODO Auto-generated method stub
			try {
				JSONObject object = (JSONObject) param;
				if (object.getInt("error") == 0) {
					boolean isRefresh = object.getBoolean("clientIsRefresh");
					account.setValues(object);
					tvAccount.setText(String.valueOf(account.getTotle()));
					synchronized (listDatas) {
						if (isRefresh) {// 刷新
							listDatas.clear();
							listDatas.addAll(account.getAccountList());

							adapter.setData(listDatas);
							adapter.notifyDataSetChanged();
						} else {// 更多
							listDatas.addAll(account.getAccountList());
							adapter.setData(listDatas);
							adapter.notifyDataSetChanged();
						}
					}
				} else {
					showMsg(object.getString("errormsg"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Callback<Object> generateRequesCallback(int type) {
		Callback<Object> requestCallback = null;
		switch (type) {
		case Account.TYPE_ET_MONEY:
			requestCallback = requestEtMoneyCallback;
			break;
		case Account.TYPE_ET_COIN:
			requestCallback = requestEtCoinCallback;
			break;
		case Account.TYPE_ET_SCORE:
			requestCallback = requestEtScoreCallback;
			break;
		default:
			break;
		}
		return requestCallback;
	}

	/**
	 * @param isRefresh
	 *            true为刷新，false为加载更多
	 * @param type
	 *            {@link Account}
	 * @param startTime
	 * @param endTime
	 * @return void
	 */
	private void requestDatas(boolean isRefresh, int type, String startTime, String endTime) {
		long uId = ETApp.getInstance().getCurrentUser().getPassengerId();
		try {
			NewNetworkRequest.httpRequest(isRefresh, generateRequesParams(isRefresh, type, uId, startTime, endTime), generateRequesCallback(type));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JSONObject generateRequesParams(boolean isRefresh, int type, long uId, String startTime, String endTime) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		int requestAccountType = 1;
		switch (type) {
		case Account.TYPE_ET_MONEY:
			requestAccountType = 1;
			break;
		case Account.TYPE_ET_COIN:
			requestAccountType = 2;
			break;
		case Account.TYPE_ET_SCORE:
			requestAccountType = 3;
			break;
		default:
			break;
		}

		jsonObject.put("action", "accountAction");
		jsonObject.put("method", "findAcctLog");
		jsonObject.put("uId", uId);
		// （1-司机、2-乘客）
		jsonObject.put("uType", 2);
		// （1-人民币账户、2-易达币账户）
		jsonObject.put("acctType", requestAccountType);
		// 分页当前页数
		if (isRefresh) {
			jsonObject.put("page", PAGE_START_COUNT);
		} else {
			jsonObject.put("page", getCurrentPageCount());
		}
		// 每页条数
		jsonObject.put("count", PAGE_SIZE);
		jsonObject.put("startDate", startTime);
		jsonObject.put("endDate", endTime);

		AppLog.LogD("账户查询params：" + jsonObject.toString());
		return jsonObject;
	}

	private int getCurrentPageCount() {
		return calculateCount(listDatas.size());
	}

	private int calculateCount(int size) {
		if (size % PAGE_SIZE == 0) {
			return size / PAGE_SIZE + PAGE_START_COUNT;
		} else {
			return size / PAGE_SIZE + 1 + PAGE_START_COUNT;
		}
	}

	// json数据处理
	private void dealData() {

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.p_mine_account_starttime:
			selectData(startTime, 1);
			break;
		case R.id.p_mine_account_endtime:
			selectData(endTime, 2);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @param calendar
	 * @param type
	 *            1为设置起始时间，2为设置结束时间
	 * @return void
	 */
	private void selectData(Calendar calendar, final int type) {
		int[] before = getNumberByCalendar(calendar);
		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(getActivity(), new OnDateSetListener() {

			public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
				if (type == 1) {
					if (getTimeByValues(year, month, day) > getTimeByCalendar(endTime)) {
						showMsg("起始时间不能大于结束时间，请重设!");
					} else {
						startTime = getCalendar(year, month, day);
						tvStartTime.setText(getShowStringByCalendar(startTime));
						onRefresh();
					}
				} else if (type == 2) {
					if (getTimeByValues(year, month, day) < getTimeByCalendar(startTime)) {
						showMsg("结束时间不能小于起始时间，请重设！");
					} else {
						endTime = getCalendar(year, month, day);
						tvEndTime.setText(getShowStringByCalendar(endTime));
						onRefresh();
					}

				}
			}
		}, before[0], before[1], before[2], false);
		datePickerDialog.show();
	}

	private Calendar getCalendar(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		return calendar;
	}

	private long getTimeByCalendar(Calendar calendar) {
		if (calendar == null) {
			return 0;
		}

		int[] date = getNumberByCalendar(calendar);
		return getTimeByValues(date[0], date[1], date[2]);
	}

	private long getTimeByValues(int year, int month, int day) {
		Date date = new Date(year, month, day);
		return date.getTime();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		requestDatas(true, type, getStringByCalendar(startTime), getStringByCalendar(endTime));
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		requestDatas(false, type, getStringByCalendar(startTime), getStringByCalendar(endTime));
	}
}
