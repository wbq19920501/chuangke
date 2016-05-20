package cn.com.easytaxi.book;

import java.lang.reflect.Type;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.NewNetworkRequest;

import com.easytaxi.etpassengersx.R;

import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.SocketUtil;
import cn.com.easytaxi.common.ToolUtil;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.MainActivityNew;
import cn.com.easytaxi.ui.view.PingJiaDlg;
import cn.com.easytaxi.ui.view.PingJiaDlg.MyDialogListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BookShow extends BaseBookLoader {

	View taxiNameLayout;
	View taxiNumberLayout;

	View opt;
	TextView bookNumber;
	TextView taxiName;
	TextView yundan;
	TextView bookTime;
	TextView earnest;
	TextView startAddr;
	TextView endAddr;
	TextView distance;
	TextView bookState;
	ImageButton callTaxiPhone;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		flag = false;
		bar.close();
		super.onDestroy();
	}

	BookBean currentBook;
	String phones;
	int currentIndex;
	TitleBar bar;

	private boolean flag = true;
	Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_show);
		
		catchCrash();
		
		initViews();
		initDatas();
		initListner();
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (flag) {
				try {
					setState(bookState, currentBook);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHandler.postDelayed(this, 1000);
			}
		}
	};

	private void initViews() {
		opt = findViewById(R.id.book_show_opt);
		bookNumber = (TextView) findViewById(R.id.book_number);
		taxiName = (TextView) findViewById(R.id.book_taxi_name);
		yundan = (TextView) findViewById(R.id.book_yundan_val);
		bookTime = (TextView) findViewById(R.id.book_time_val);
		earnest = (TextView) findViewById(R.id.book_earnest_val);
		startAddr = (TextView) findViewById(R.id.book_start_addr);
		endAddr = (TextView) findViewById(R.id.book_end_addr);
		distance = (TextView) findViewById(R.id.tv_book_distance);
		bookState = (TextView) findViewById(R.id.tv_book_state);
		callTaxiPhone = (ImageButton) findViewById(R.id.book_call_taxi_phone);
		taxiNameLayout = findViewById(R.id.book_taxi_name_layout);
		taxiNumberLayout = findViewById(R.id.book_number_layout);
		bar = new TitleBar(BookShow.this);
		bar.setTitleName("ԤԼ������Ϣ");
	}

	private void initDatas() {
		currentIndex = getIntent().getIntExtra("index", -1);
		if (currentIndex == -1) {
			return;
		}
		currentBook = (BookBean) getIntent().getSerializableExtra("book");

		phones = currentBook.getReplyerPhone();
		callTaxiPhone.setVisibility(View.VISIBLE);
		if (phones != null && !phones.equals("")) {
			callTaxiPhone.setEnabled(true);
		} else {
			// callTaxiPhone.setVisibility(View.GONE);
			callTaxiPhone.setEnabled(false);
		}

		String taxiNumber = currentBook.getReplyerNumber();
		
		if (taxiNumber != null && !taxiNumber.equals("")) {
			taxiNumberLayout.setVisibility(View.VISIBLE);
			bookNumber.setText(taxiNumber);
		} else {
			taxiNumberLayout.setVisibility(View.GONE);
		}
		

		String driverName = currentBook.getReplyerName();
		if (driverName != null && !driverName.equals("")) {
			taxiNameLayout.setVisibility(View.VISIBLE);
			taxiName.setText(driverName);
		} else {
			taxiNameLayout.setVisibility(View.GONE);
		}

		setData();
	}

	private void setData() {

		try {
			yundan.setText(ToolUtil.bookNumber(currentBook.getCacheId() + ""));
			String showTime = null;
			showTime = ToolUtil.showTime(f.parse(currentBook.getUseTime()));
			bookTime.setText(showTime);

			int price = currentBook.getPrice();
			if (price == -1) {
				earnest.setText(R.string.rightnow);
			} else if (price >= 0) {
				earnest.setText("+" + currentBook.getPrice() + "Ԫ");
			}

			if (BookUtil.isHistory(currentBook)) {
				changeOptState(0);
			} else {
				changeOptState(1);
			}
			startAddr.setText(currentBook.getStartAddress());
			endAddr.setText(currentBook.getEndAddress());
			if (currentBook.getForecastDistance() != 0 && currentBook.getForecastPrice() != 0) {
				String subDistance = BookUtil.getDecimalNumber(currentBook.getForecastDistance());
				String subPrice = String.valueOf(currentBook.getForecastPrice());
				String txt = getString(R.string.book_distance, subDistance, subPrice);

				SpannableStringBuilder ssb = BookUtil.getSpecialText(txt, subDistance, getResources().getColor(R.color.yellow_state));
				distance.setText(BookUtil.getSpecialText(ssb, txt, subPrice, getResources().getColor(R.color.yellow_state)));
				findViewById(R.id.distance_layout).setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.distance_layout).setVisibility(View.GONE);
				findViewById(R.id.distance_bottom_line).setVisibility(View.GONE);
			}

			setState(bookState, currentBook);
		} catch (Exception e) {
			e.printStackTrace();
		}

		mHandler.postDelayed(runnable, 1000);
	}

	// ����״̬,0.δ����,1.��ʾ�Ѵ���,2��ʾ�˿�ȡ����3��ʾ���⳵ȡ��
	private void setState(TextView tv, BookBean b) throws Exception {
		if (tv == null || b == null) {
			return;
		}

		boolean dateCpr = ToolUtil.compareTime(System.currentTimeMillis(), f.parse(b.getUseTime()).getTime());
		if (!BookUtil.isEffective(b.getReplyerId()) && dateCpr && b.getState() == 0) {// δ�ӵ���ʱ
			tv.setText("�����ѳ�ʱ");
		} else if (!BookUtil.isEffective(b.getReplyerId()) && b.getState() == 2) {// δ�ӵ��˿�ȡ��
			tv.setText("����ȡ��");
		} else if (BookUtil.isEffective(b.getReplyerId()) && b.getState() == 2) {// �ӵ���˿�ȡ��
			tv.setText("����ȡ��");
		} else if (BookUtil.isEffective(b.getReplyerId()) && b.getState() == 3) {// �ӵ���˾��ȡ��
			tv.setText("˾����ȡ��");
		} else if (BookUtil.isEffective(b.getReplyerId()) && b.getState() == 1 && dateCpr) {// ���������
			tv.setText("���������");
			tv.setTextColor(this.getResources().getColor(R.color.green_state));
		} else if (BookUtil.isNew(b)) {// ���ڵȴ��ӵ�
			// tv.setTextColor(this.getResources().getColor(R.color.yellow_state));

			if (b.getDyTime() == 0) {
				long t = f.parse(b.getUseTime()).getTime() - System.currentTimeMillis();
				b.setDyTime(t);
			} else {
				b.setDyTime(b.getDyTime() - 1000);
			}

			if (b.getDyTime() <= 0) {
				tv.setText("�����ѳ�ʱ");
			} else {
				tv.setText("���ӵ���" + ToolUtil.getTimeStr(b.getDyTime()));
			}

		} else if (BookUtil.isActive(b)) {// ��ǰ�����,������˾���ӵ�

			tv.setTextColor(this.getResources().getColor(R.color.green_state));
			if (b.getDyTime() == 0) {
				long t = f.parse(b.getUseTime()).getTime() - System.currentTimeMillis();
				b.setDyTime(t);
			} else {
				b.setDyTime(b.getDyTime() - 1000);
			}

			if (b.getDyTime() <= 0) {
				tv.setText("���������");
			} else {
				tv.setText("�ѽӵ���" + ToolUtil.getTimeStr(b.getDyTime()));
			}
		} else {
			try {
				Gson gson = new Gson();
				Type type = new TypeToken<BookBean>() {
				}.getType();
				String j = gson.toJson(b, type);
				AppLog.LogD("BookListAdapter2", "unknow_state:" + j);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void changeOptState(final int i) {
		opt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (i == 0) {// ��ʷ����
					reorder();
				} else if (i == 1) {// �����
					String[] choiceInfo = new String[] { "���Ѵ򵽳�", "�г��б�,��ʱ����", "����" };
					
					final PingJiaDlg pj = new PingJiaDlg(BookShow.this, R.style.Customdialog, "ȡ������", choiceInfo);
					
					pj.setListener(new MyDialogListener() {
						
						@Override
						public void onClick(View view) {
							switch (view.getId()) {
							case R.id.button_ok:
								int value = pj.getCurrenPingji();
								String contnet = "�г��б�,��ʱ����";
								if(value == 100){
									contnet = "���Ѵ򵽳�";
								}else if(value == 60){
									contnet = "�г��б�,��ʱ����";
								}else if(value == 10){
									contnet = pj.getPingjiaInfo();
									if(TextUtils.isEmpty(contnet)){
										contnet="����";
									}
								}
//								NewNetworkRequest.suggest(null, cityId, passengerId, taxiId, info, value, type);
//								NewNetworkRequest.suggest(null, "211", "13551387523", 15680068763L, "11111111",100 ,3);
//								NewNetworkRequest.cancelBook(currentBook.getCacheId(), 15680068763L, 100, "11111111", null);
								cancel(currentBook.getCacheId(), contnet);
								pj.dismiss();
								break;
							case R.id.button_cancel:
								pj.dismiss();
								break;

							default:
								break;
							}
							
						}
					});
//					
					pj.show();
//					Window.confirm(self, "ȷ��ȡ��", "ȷ��ȡ������?", new Callback<Object>() {
//
//						@Override
//						public void handle(Object param) {
//							cancel(currentBook.getCacheId());
//						}
//					});

				}
			}
		});
		if (i == 0) {// ��ʷ����
			((ImageView) findViewById(R.id.book_show_opt_left)).setImageResource(R.drawable.btn_drawable_commit);
			((TextView) findViewById(R.id.book_show_opt_text)).setText("�ٴ�Ԥ��");
		} else if (i == 1) {// �����
			((ImageView) findViewById(R.id.book_show_opt_left)).setImageResource(R.drawable.btn_drawable_commit2);
			((TextView) findViewById(R.id.book_show_opt_text)).setText("ȡ������");
		}
	}

	protected void reorder() {
		Intent intent = new Intent(self, BookPublish.class);
		intent.setAction("cn.com.easytaxi.book.resubmit");
		intent.putExtra("earnst", (int) currentBook.getPrice());
		intent.putExtra("startAddr", currentBook.getStartAddress());
		intent.putExtra("startLat", currentBook.getStartLatitude());
		intent.putExtra("startLng", currentBook.getStartLongitude());
		intent.putExtra("endAddr", currentBook.getEndAddress());
		if (currentBook.getEndLatitude() != null && currentBook.getEndLongitude() != null) {
			intent.putExtra("endLat", currentBook.getEndLatitude());
			intent.putExtra("endLng", currentBook.getEndLongitude());
		}

		intent.putExtra("distance", currentBook.getForecastDistance());
		intent.putExtra("money", currentBook.getForecastPrice());
		intent.putExtra("sex", 1);
		intent.putExtra("mobile", currentBook.getPassengerPhone());
		intent.putExtra("personCount", 1);
		intent.putExtra("descp", "");
		startActivity(intent);
		finish();
	}

	private void cancel(final long id ,final String content) {
		try {
			showDialog(0);
			JSONObject json = new JSONObject();
			json.put("action", "bookAction");
			json.put("method", "cancelBookByPasssenger");
			json.put("id", id);
			json.put("content", content);
			
			json.put("cityId", MainActivityNew.cityId);
			json.put("cityName",MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			SocketUtil.getJSONObject(Long.valueOf(getPassengerId()), json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {
					try {
						if (param != null) {
							JSONObject result = (JSONObject) param;
							if (result.getInt("id") != id) {
								Toast.makeText(self, "ȡ��ʧ��", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(self, "ȡ���ɹ�", Toast.LENGTH_SHORT).show();
								changeOptState(0);
								Intent intent = new Intent("cn.com.easytaxi.book.refresh_list");
								intent.putExtra("fromAdapter", true);
								self.sendBroadcast(intent);
								self.finish();
								// startActivity(new Intent(BookShow.this,
								// BookListActivity.class));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(self, "ȡ������ʧ��", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void error(Throwable e) {
					// TODO Auto-generated method stub
					super.error(e);
					e.printStackTrace();
					Toast.makeText(self, "ȡ������ʧ��", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
					try {
						dismissDialog(0);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initListner() {
		callTaxiPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				callDriver(phones);
			}
		});
	}

	protected void callDriver(final String phones) {
		// TODO Auto-generated method stub
		try {
			Window.callTaxi(BookShow.this, phones);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "˾��������������ϵ�ͷ���", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onTimeChange() {
		// TODO Auto-generated method stub

	}
}
