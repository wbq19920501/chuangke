package cn.com.easytaxi.book;

import java.util.ArrayList;
import java.util.List;

import mapapi.overlayutil.DrivingRouteOverlay;
import mapapi.overlayutil.OverlayManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.common.SocketUtil;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.custom.ExpandablePanel;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.YdLocaionBaseActivity;
import cn.com.easytaxi.ui.view.CancelBookDialog;
import cn.com.easytaxi.ui.view.CommonDialog;
import cn.com.easytaxi.ui.view.SuggestDialog;
import cn.com.easytaxi.util.TimeTool;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.easytaxi.etpassengersx.R;
import com.easytaxi.etpassengersx.wxapi.WXPayEntryActivity;

public class NewBookDetail extends YdLocaionBaseActivity implements OnClickListener, OnGetRoutePlanResultListener {
	private static final int REQUEST_CODE = 1;
	private static final int REQUEST_CODE_OFFLINE = 2;

	private static final int DEFAULT_ZOOM_FACTOR = 17;
	private static final String LEFT_BUTTON_TXT_CANCEL = "ȡ������";
	private static final String LEFT_BUTTON_TXT_SPECIAL_CANCEL = "����������";
	private static final String LEFT_BUTTON_TXT_EVALUATE = "����˾��";
	private static final String LEFT_BUTTON_TXT_BOOK_AGAIN = "�ٴ�Ԥ��";
	private static final String LEFT_BUTTON_TXT_DISAGREE = "˾��ΥԼ";

	private static final String RIGHT_BUTTON_TXT_DONE = "֧��";
	private static final String RIGHT_BUTTON_TXT_AGREE = "ͬ��";

	private static final int DIALOG_CANCEL_BOOK = 1;
	private static final int DIALOG_SUGGEST_BOOK = 2;

	// �Ƿ�������
	private static boolean isSuggested = false;
	// �Ƿ���ȡ��˾����Ϣ
	private boolean hasGetTaxiInfo = false;

	private String vCode = "";

	private int lat;
	private int lng;

	private List<String> actionTimeList = new ArrayList<String>();
	private List<TextView> actionTimeView = new ArrayList<TextView>();

	private ExpandablePanel panel;
	private LayoutInflater inflater;
	private TextView driverName;
	private TextView taxiNumber;
	private TextView tvUseTime;
	private TextView tvFee;
	private TextView tvCode;
	private ImageButton callTaxiPhone;
	private ImageButton location;

	// ���һ������
	private View lastestLine;

	// ����ʱ
	private TextView tvCountDownTime;
	private TextView tvCountDownTimeUnit;
	private TextView tvCountDownDistance;
	private TextView tvCountDownDistanceUnit;
	private LinearLayout bottomLayout;

	private Button leftButton;
	private Button rightButton;

	private LinearLayout optLayout;
	private LinearLayout optLayoutSuggest;

	private String phones;
	private TitleBar bar;

//	private List<Overlay> overlays;
	// ��ͼͼ��
//	private ItemizedOverlay<OverlayItem> overlayParent;
//	private OverlayItem taxiOverlay;
//	private OverlayItem passengerOverlay;
	
	List<OverlayOptions> overlayParent = null;
	OverlayOptions taxiOverlay;
	OverlayOptions passengerOverlay;

	private Resources res;

	// ��ͼ
	private BMapManager mBMapMan;
	private MapView mapView;
	BaiduMap mBaiduMap;
	boolean isFirstLoc = true; // �Ƿ��״ζ�λ
	
	RouteLine route = null;
	OverlayManager routeOverlay = null;
	
	
//	private MapController controller;
	// �ҵ�λ���ϱ���ʾ�ĵ�ַ��Ϣ��
//	private PopupOverlay pop;
	private RoutePlanSearch mMKSearch;
//	private MKSearch mMKSearch;
//	private MyMKSearchListener myMKSearchLis;
	// �ҵ�ǰλ�õ�����
//	private LocationData myLocData;
	private LocationClient myLocData;
	
	 public MyLocationListenner myListener = new MyLocationListenner();

	// �ϳ���ַ
	private String startAddress = "";
	// �³���ַ
	private String endAddress = "";
	// �ҵ�ǰλ�õı�ע
//	private MyLocationOverlay myLocationOverlay;
	private Callback<JSONObject> taxiLocationCallback = null;

	// ����
	private boolean isRunnable = true;
	private BookBean bookBean;
	private DisplayMetrics dm;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.arg1 == 1) {
				// requestNearbyTaxi(new NearByCallbacK());
			} else if (msg.arg1 == 2) {
				AppLog.LogD("refresh locaion car");
				// taxiLocationCallback = new TaxiLocationCallback();
				// requestTaxiLocation(currentTaxiId, taxiLocationCallback);
			} else if (msg.arg1 == 3) {
				setMyLocation(getCurrentlat(), getCurrentLng(), getCurrentRadius(), getCurrentDerect(), false);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_book_detail);

		dm = getDisplayMetrics();
		Intent i = this.getIntent();
		res = this.getResources();
		registReceiver();

		bookBean = (BookBean) i.getSerializableExtra("book");
		try {
			bookBean.setDyTime((TimeTool.DEFAULT_DATE_FORMATTER.parse(bookBean.getUseTime()).getTime() - System.currentTimeMillis()) / 1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (bookBean == null) {
			bookBean = new BookBean();
			bookBean.setId(i.getLongExtra("bookId", -1));
		} else {
			AppLog.LogD("xyw", "detail bean-->" + bookBean);
		}

		initViews();
		initDatas();
		initListner();
		getDetail(bookBean.getId());

	}

	/**
	 * ����bookFlags��ƫ��ȡ����
	 * 
	 * @param bookFlags
	 * @param actionType
	 * @return
	 * @return String
	 */
	public String getAction(long bookFlags, int actionType) {
		String action = "";
		boolean isDone = getActionFlag(bookFlags, actionType);
		switch (actionType) {
		case 0x00:
			action = isDone ? "������" : "δ����";
			action = "ϵͳ:-" + action;
			break;
		case 0x01:
			action = isDone ? "��ȡ������" : "";
			action = "��:-" + action;
			break;
		case 0x02:
			action = isDone ? "�ѽӵ�" : "";
			action = "˾��:-" + action;
			break;
		// case 0x03:
		// action = isDone ? "����ֹ����" : "";
		// action = "˾��:-" + action;
		// break;
		// case 0x04:
		// action = isDone ? "����ֹ����" : "";
		// action = "��:-" + action;
		// break;
		case 0x05:
			action = isDone ? "��ͬ����ֹ����" : "";
			action = "˾��:-" + action;
			break;
		case 0x06:
			action = isDone ? "��ͬ����ֹ����" : "";
			action = "��:-" + action;
			break;
		case 0x07:
			action = isDone ? "��ȷ���ϳ�" : "";
			action = "˾��:-" + action;
			break;
		case 0x08:
			action = isDone ? "��ȷ���ϳ�" : "";
			action = "��:-" + action;
			break;
		case 0x09:
			action = isDone ? "ȡ��������˾��û����" : "";
			action = "��:-" + action;
			break;
		case 0x0a:
			action = isDone ? "ȡ������������ԭ��)" : "";
			action = "��:-" + action;
			break;
		case 0x0b:
			break;
		case 0x0c:
			break;
		case 0x0d:
			break;
		case 0x0e:
			break;
		case 0x0f:
			break;
		case 0x10:
			action = isDone ? "pos���տ���" : "";
			action = "˾��:-" + action;
			break;
		case 0x11:
			action = isDone ? "ȡ���������˿�ΥԼ��" : "";
			action = "˾��:-" + action;
			break;
		case 0x12:
			action = isDone ? "ȡ������������ԭ��" : "";
			action = "˾��:-" + action;
			break;
		case 0x13:
			break;
		case 0x14:
			break;
		case 0x15:
			break;
		case 0x16:
			break;
		case 0x17:
			break;
		case 0x18:
			break;
		case 0x19:
			action = isDone ? "˾������" : "";
			action = "˾��:-" + action;
			break;
		case 0x1a:
			action = isDone ? "�˿�����" : "";
			action = "��:-" + action;
			break;
		case 0x1b:
			action = isDone ? "˾��������ȷ��֤��" : "";
			action = "˾��:-" + action;
			break;
		case 0x1c:
			action = isDone ? "��ʱ" : "";
			action = "ϵͳ:-" + action;
			break;
		case 0x1d:
			action = isDone ? "�����֧��" : "";
			action = "��:-" + action;
			break;
		default:
			break;

		}

		return action;
	}

	private boolean getActionFlag(long bookFlags, int offSize) {
		String flags = Long.toBinaryString(bookFlags);
		if (TextUtils.isEmpty(flags)) {
			return false;
		}

		int length = flags.length();
		if (length <= offSize) {
			return false;
		}

		char bitChar = flags.charAt(length - offSize - 1);
		if (bitChar == '1') {
			return true;
		} else {
			return false;
		}
	}

	private void initMap() {

		mapView = (MapView) findViewById(R.id.book_detail_map);
		overlayParent = new ArrayList<OverlayOptions>();
		
		
		mBaiduMap = mapView.getMap();
	        // ������λͼ��
	    mBaiduMap.setMyLocationEnabled(true);
	        
	    
	 // ��ʼ������ģ�飬ע���¼�����
	    mMKSearch = RoutePlanSearch.newInstance();
	    mMKSearch.setOnGetRoutePlanResultListener(this);

	}

	public void initViews() {
		panel = (ExpandablePanel) findViewById(R.id.book_detail_panel);
		location = (ImageButton) findViewById(R.id.map_current_pos);
		driverName = (TextView) findViewById(R.id.book_detail_drivername);
		tvCode = (TextView) findViewById(R.id.book_detail_auth_code);
		taxiNumber = (TextView) findViewById(R.id.book_detail_taxinumber);
		tvUseTime = (TextView) findViewById(R.id.book_detail_time);
		tvFee = (TextView) findViewById(R.id.book_detail_fee);
		callTaxiPhone = (ImageButton) findViewById(R.id.book_detail_calltaxi);

		tvCountDownTime = (TextView) findViewById(R.id.book_detail_time_number);
		tvCountDownTimeUnit = (TextView) findViewById(R.id.book_detail_time_unit);
		tvCountDownDistance = (TextView) findViewById(R.id.book_detail_distance_number);
		tvCountDownDistanceUnit = (TextView) findViewById(R.id.book_detail_distance_unit);

		bar = new TitleBar(NewBookDetail.this);
		bar.setTitleName("��������");

		optLayout = (LinearLayout) findViewById(R.id.detai_layout);
		optLayoutSuggest = (LinearLayout) findViewById(R.id.detai_layout_suggest);
		leftButton = (Button) findViewById(R.id.book_detail_leftbtn);
		rightButton = (Button) findViewById(R.id.book_detail_rightbtn);

		bottomLayout = (LinearLayout) findViewById(R.id.bottom_time);
	}

	private void initDatas() {
		inflater = LayoutInflater.from(this);
		initMap();
		setDetail(true);

		lat = bookBean.getStartLatitude();
		lng = bookBean.getStartLongitude();
		moveToCenter();
	}

	private void initListner() {
		callTaxiPhone.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		location.setOnClickListener(this);
	}

	protected void callDriver(final String phones) {
		// TODO Auto-generated method stub
		try {
			Window.callTaxi(NewBookDetail.this, phones);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "˾��������������ϵ�ͷ���", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		AppLog.LogD("xyw", "onclick");
		int id = v.getId();

		try {
			switch (id) {
			case R.id.map_current_pos:
				moveToCenter();
				break;
			case R.id.book_detail_calltaxi:
				callDriver(phones);
				break;
			case R.id.book_detail_rightbtn:
				String txt = ((Button) v).getText().toString();
				if (txt.equals(RIGHT_BUTTON_TXT_DONE)) {// �����ϳ�
					// selectPayMode();
					// ��Ϊ���¡�����֧���ϲ�
					gotoCharge(String.valueOf(bookBean.getId()));
				} else {
					// do nothing
				}
				break;
			case R.id.book_detail_leftbtn:
				String msg = ((Button) v).getText().toString();
				if (msg.equals(LEFT_BUTTON_TXT_CANCEL)) {
					cancelBook(bookBean.getId(), 0x400);
				} else if (msg.equals(LEFT_BUTTON_TXT_BOOK_AGAIN)) {

				} else if (msg.equals(LEFT_BUTTON_TXT_SPECIAL_CANCEL)) {
					showDialog(DIALOG_CANCEL_BOOK);
				} else if (msg.equals(LEFT_BUTTON_TXT_DISAGREE)) {
					int action = 1 << 0x1a;
					long flags = bookBean.getBookFlags();
					flags = flags | action;
					comfirmFinishedBook(bookBean.getId(), flags);
				} else if (msg.equals(LEFT_BUTTON_TXT_EVALUATE)) {
					// ����
					showDialog(DIALOG_SUGGEST_BOOK);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void moveToCenter() {
//		controller.setCenter(new GeoPoint(lat, lng));
	}

	@Override
	protected void onDestroy() {
		bar.close();

		if (reloadReceiver != null) {
			unregisterReceiver(reloadReceiver);
			reloadReceiver = null;
		}

		if (mapView != null) {
			mapView.onDestroy();
			mMKSearch.destroy();
		}
		isRunnable = false;
		super.onDestroy();
	}

	// ��ʼ��״̬
	private void setMyLocation(int lat, int lng, float radius, float derect, boolean isCenter) {
		AppLog.LogD("setMyLocation = " + lat);
		if (lat != 0 && lng != 0) {
			if (isCenter) {
//				controller.setCenter(new GeoPoint(lat, lng));
			}
			
			
			myLocData = new LocationClient(this);
			myLocData.registerLocationListener(myListener);
	        LocationClientOption option = new LocationClientOption();
	        option.setOpenGps(true); // ��gps
	        option.setCoorType("bd09ll"); // ������������
	        option.setScanSpan(1000);
	        myLocData.setLocOption(option);
	        myLocData.start();
			
//			myLocData.latitude = lat / 1e6;
//			myLocData.longitude = lng / 1e6;
//			myLocData.accuracy = radius;
//			myLocData.direction = derect;
//			myLocationOverlay.setData(myLocData);
//			// ��ʾ��ǰλ�õ���ϸ��Ϣ
//			if (myLocData.latitude > 0) {
//				if (startAddress.length() > 0) {
//					float i = (float) dm.heightPixels / 800.0f;
//					Bitmap b = BitmapUtil.writeWordsOnBitmap(NewBookDetail.this.getBaseContext(), R.drawable.my_adrress_ontaxi, "����" + "  " + startAddress + "  " + "����", (int) (22 * i), Color.BLACK, (int) (22 * i), (int) (22 * i - 3), (int) (15 * i), (int) (15 * i), 3);
//					Bitmap arrowScale = BitmapUtil.scaleBitmap(NewBookDetail.this.getBaseContext(), R.drawable.my_address_arrow, (int) (22 * 2 * i), (int) (22 * 2 * i));
//					Bitmap c = BitmapUtil.createBitmapWithArrow(b, arrowScale, (int) (14 * i));
//					pop.showPopup(BitmapUtil.createBitmapShadow(Color.BLACK, c), new GeoPoint(lat, lng), 32);
//				}
//			}
//			mapView.refresh();
		}

		Message msg = Message.obtain();
		msg.arg1 = 3;
		msg.what = 3;

		handler.sendMessageDelayed(msg, 15000);

	}

	@Override
	protected void initListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUserData() {
		// TODO Auto-generated method stub

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
	 * ȡ������
	 * 
	 * @param id
	 * @param reason
	 *            // ˾��δ����0x200��������ԭ��0x400��
	 * @return void
	 */
	private void cancelBook(final long id, int reason) {
		try {
			// showDialog(0);
			JSONObject json = new JSONObject();
			json.put("action", "scheduleAction");
			json.put("method", "cancelBookByPassenger");
			json.put("bookId", id);
			json.put("reason", reason);

			json.put("cityId", cn.com.easytaxi.platform.MainActivityNew.cityId);
			json.put("cityName", cn.com.easytaxi.platform.MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			AppLog.LogD("xyw", "cancelBook json-->" + json.toString());
			SocketUtil.getJSONObject(Long.valueOf(getPassengerId()), json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {
					try {
						if (param != null) {
							JSONObject result = (JSONObject) param;
							AppLog.LogD("xyw", "cancelBook--->" + result.toString());
							if (result.getInt("error") != 0) {
								Toast.makeText(NewBookDetail.this, "ȡ������ʧ��", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(NewBookDetail.this, "ȡ���ɹ�", Toast.LENGTH_SHORT).show();
								int bookState = result.getInt("bookState");
								int bookFlags = result.getInt("bookFlags");
								setState(bookState, bookFlags);
								setDetail(false);

								ETApp.getInstance().sendBroadcast(new Intent(cn.com.easytaxi.common.Const.BOOK_STATE_CHANGED_LIST));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(NewBookDetail.this, "ȡ������ʧ��", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void error(Throwable e) {
					// TODO Auto-generated method stub
					super.error(e);
					e.printStackTrace();
					Toast.makeText(NewBookDetail.this, "ȡ������ʧ��", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
					// try {
					// dismissDialog(0);
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void selectPayMode() {
		final String[] items = new String[] { "����֧��", "����֧��" };
		showDlgList(this, "ѡ��֧����ʽ", items, new Callback<String>() {

			@Override
			public void handle(String param) {
				// TODO Auto-generated method stub.
				if (param.equals(items[0])) {
					gotoCharge(String.valueOf(bookBean.getId()));
				} else {
					// ����֧��
					finishBook(bookBean.getId(), getPassengerId());
				}
			}
		});
	}

	public void showDlgList(final Context context, final String title, final String[] items, final Callback<String> callback) {
		try {
			// �Զ���title��������ʾ
			TextView tvTitle = new TextView(context);
			tvTitle.setGravity(Gravity.CENTER);
			tvTitle.setTextSize(24);
			tvTitle.setPadding(0, 10, 0, 10);
			tvTitle.setText(title);

			new AlertDialog.Builder(context).setCustomTitle(tvTitle).setItems(items, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					callback.handle(items[which]);
				}
			}).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void gotoCharge(String bookId) {
		Intent i = new Intent(NewBookDetail.this, WXPayEntryActivity.class);
		i.putExtra("bookId", bookId);
		if (bookBean.getOrder_type() == 1) {
			i.putExtra("fee", bookBean.getFee());
		}
		NewBookDetail.this.startActivityForResult(i, REQUEST_CODE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE) {

			boolean isOffline = data.getBooleanExtra("isOffline", false);
			if (isOffline) {
				// ����֧��
				finishBook(bookBean.getId(), getPassengerId());
			} else {
				getDetail(bookBean.getId());
			}
		}
	};

	/**
	 * ��ɶ���
	 * 
	 * @param bookId
	 * @return void
	 */
	private void finishBook(final long bookId, String taxiId) {
		try {
			// showDialog(0);
			JSONObject json = new JSONObject();
			json.put("action", "scheduleAction");
			json.put("method", "posPayBookByTaxi");
			json.put("bookId", String.valueOf(bookId));
			json.put("taxiId", taxiId);
			json.put("cityId", cn.com.easytaxi.platform.MainActivityNew.cityId);
			json.put("cityName", cn.com.easytaxi.platform.MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			AppLog.LogD("xyw", "finishBook json-->" + json.toString());
			SocketUtil.getJSONObject(Long.valueOf(getPassengerId()), json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {
					try {
						if (param != null) {
							JSONObject result = (JSONObject) param;
							AppLog.LogD("xyw", "finishBook--->" + result.toString());
							if (result.getInt("error") != 0) {
								Toast.makeText(NewBookDetail.this, "����ʧ��", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(NewBookDetail.this, "�����ɹ�", Toast.LENGTH_SHORT).show();
								int bookState = result.getInt("bookState");
								int bookFlags = result.getInt("bookFlags");
								setState(bookState, bookFlags);
								setDetail(false);

								ETApp.getInstance().sendBroadcast(new Intent(cn.com.easytaxi.common.Const.BOOK_STATE_CHANGED_LIST));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(NewBookDetail.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void error(Throwable e) {
					super.error(e);
					e.printStackTrace();
					Toast.makeText(NewBookDetail.this, "����ʧ��", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ȷ�Ͻ�������
	 * 
	 * @param bookId
	 * @param bookFlags
	 * 
	 * @return void
	 */
	private void comfirmFinishedBook(final long bookId, long bookFlags) {
		try {
			// showDialog(0);
			JSONObject json = new JSONObject();
			json.put("action", "scheduleAction");
			json.put("method", "finishBookComfirmByPassenger");
			json.put("bookId", bookId);
			json.put("bookFlags", bookFlags);

			json.put("cityId", cn.com.easytaxi.platform.MainActivityNew.cityId);
			json.put("cityName", cn.com.easytaxi.platform.MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			SocketUtil.getJSONObject(Long.valueOf(getPassengerId()), json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {
					try {
						if (param != null) {
							JSONObject result = (JSONObject) param;
							AppLog.LogD("xyw", "comfirmFinishedBook--->" + result.toString());
							if (result.getInt("error") != 0) {
								Toast.makeText(NewBookDetail.this, "����ʧ��", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(NewBookDetail.this, "�����ɹ�", Toast.LENGTH_SHORT).show();
								int bookState = result.getInt("bookState");
								int bookFlags = result.getInt("bookFlags");
								setState(bookState, bookFlags);
								setDetail(false);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(NewBookDetail.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void error(Throwable e) {
					super.error(e);
					e.printStackTrace();
					Toast.makeText(NewBookDetail.this, "����ʧ��", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ӷ���˻�ȡ��������
	 * 
	 * @param bookId
	 * @return void
	 */
	private void getDetail(long bookId) {
		try {
			// showDialog(0);
			JSONObject json = new JSONObject();
			json.put("action", "proxyAction");
			json.put("method", "query");
			json.put("op", "getBookInfoByPassenger");
			json.put("bookId", bookId);

			json.put("cityId", cn.com.easytaxi.platform.MainActivityNew.cityId);
			json.put("cityName", cn.com.easytaxi.platform.MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			SocketUtil.getJSONObject(Long.valueOf(getPassengerId()), json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {
					try {
						if (param != null) {
							JSONObject result = (JSONObject) param;

							AppLog.LogD("xyw", "book detail--->" + result.toString());
							if (result.getInt("error") != 0) {
								throw new Exception(result.toString());
							} else {
								result = result.getJSONArray("datas").getJSONObject(0);
								vCode = getJsonString(result, "vCode");
								AppLog.LogD("xyw", "vCode--->" + vCode);
								setBookBeanByJson(result);
								setDetail(false);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(NewBookDetail.this, "��ȡ��������ʧ��", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void error(Throwable e) {
					super.error(e);
					e.printStackTrace();
					Toast.makeText(NewBookDetail.this, "��ȡ��������ʧ��", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getBookSuggest(bookBean.getId());
			}
		}, 500);
	}

	/**
	 * �ӷ���˻�ȡ��������
	 * 
	 * @param bookId
	 * @return void
	 */
	private void getBookSuggest(long bookId) {
		try {
			JSONObject json = new JSONObject();
			json.put("action", "proxyAction");
			json.put("method", "query");
			json.put("op", "getSuggestByBookId");
			json.put("bookId", bookId);

			json.put("cityId", cn.com.easytaxi.platform.MainActivityNew.cityId);
			json.put("cityName", cn.com.easytaxi.platform.MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			SocketUtil.getJSONObject(Long.valueOf(getPassengerId()), json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {
					try {
						if (param != null) {
							JSONObject result = (JSONObject) param;

							AppLog.LogD("xyw", "getBookSuggest result--->" + result.toString());
							if (result.getInt("error") != 0) {
								throw new Exception(result.toString());
							} else {
								setSuggestItem(result.getJSONArray("datas"));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(NewBookDetail.this, "��ȡ��������ʧ��", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void error(Throwable e) {
					super.error(e);
					e.printStackTrace();
					Toast.makeText(NewBookDetail.this, "��ȡ��������ʧ��", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setState(int bookState, int bookFlags) {
		bookBean.setState(bookState);
		bookBean.setBookFlags(bookFlags);
	}

	private void setBookBeanByJson(JSONObject jsonObjectBookBean) {
		bookBean.setEvaluate(getJsonInt(jsonObjectBookBean, "evaluate"));
		bookBean.setStartAddress(getJsonString(jsonObjectBookBean, "startAddr"));
		bookBean.setEndLongitude(getJsonInt(jsonObjectBookBean, "endLng"));
		bookBean.setPassengerId(getJsonLong(jsonObjectBookBean, "passengerId"));
		bookBean.setSubmitTime(getJsonString(jsonObjectBookBean, "submitTime"));
		bookBean.setScore(getJsonInt(jsonObjectBookBean, "credits"));
		bookBean.setForecastPrice(getJsonInt(jsonObjectBookBean, "forecastPrice"));
		bookBean.setBookType(getJsonInt(jsonObjectBookBean, "bookType"));
		bookBean.setReplyerNumber(getJsonString(jsonObjectBookBean, "replyerNumber"));
		bookBean.setEndAddress(getJsonString(jsonObjectBookBean, "endAddr"));
		bookBean.setPoints(getJsonInt(jsonObjectBookBean, "credits"));
		bookBean.setId(getJsonLong(jsonObjectBookBean, "id"));
		bookBean.setReplyerId(getJsonLong(jsonObjectBookBean, "replyerId"));
		bookBean.setReplyerPhone(getJsonString(jsonObjectBookBean, "replyerPhone"));
		bookBean.setReplyerName(getJsonString(jsonObjectBookBean, "replyerName"));
		// �°�bookStateȡ���ɰ�state
		bookBean.setState(getJsonInt(jsonObjectBookBean, "bookState"));
		bookBean.setEndLatitude(getJsonInt(jsonObjectBookBean, "endLat"));
		try {
			String time = getJsonString(jsonObjectBookBean, "useTime");
			bookBean.setUseTime(time);
		} catch (Exception e) {
			AppLog.LogD("xyw", "detail-useTime:" + getJsonString(jsonObjectBookBean, "useTime"));
			e.printStackTrace();
		}
		try {
			bookBean.setOrder_type(getJsonInt(jsonObjectBookBean, "orderType"));
			bookBean.setFee(getJsonString(jsonObjectBookBean, "fee"));
		} catch (Exception e) {
		}

		bookBean.setPassengerPhone(getJsonString(jsonObjectBookBean, "passengerPhone"));
		bookBean.setReplyerCompany(getJsonString(jsonObjectBookBean, "replyerCompany"));
		bookBean.setStartLongitude(getJsonInt(jsonObjectBookBean, "startLng"));
		bookBean.setReplyTime(getJsonString(jsonObjectBookBean, "replyTime"));
		bookBean.setPriceMode(getJsonInt(jsonObjectBookBean, "priceMode"));
		bookBean.setForecastDistance(getJsonInt(jsonObjectBookBean, "forecastDistance"));
		bookBean.setPreTime(getJsonString(jsonObjectBookBean, "currentDate"));
		bookBean.setStartLatitude(getJsonInt(jsonObjectBookBean, "startLat"));
		bookBean.setPrice(getJsonInt(jsonObjectBookBean, "earnst"));
		bookBean.setBookFlags(getJsonInt(jsonObjectBookBean, "bookFlags"));
	}

	public String getJsonString(JSONObject jsonObject, String name) {
		try {
			return jsonObject.getString(name);
		} catch (Exception e) {
			return "";
		}
	}

	public int getJsonInt(JSONObject jsonObject, String name) {
		try {
			return Integer.parseInt(jsonObject.getString(name));
		} catch (Exception e) {
			return -1;
		}
	}

	public long getJsonLong(JSONObject jsonObject, String name) {
		try {
			return Long.parseLong(jsonObject.getString(name));
		} catch (Exception e) {
			return -1;
		}
	}

	private void showPanel() {
		handler.postDelayed(expandRunnable, 500);
	}

	private Runnable expandRunnable = new Runnable() {
		public void run() {
			panel.expand(true);
		}
	};

	/**
	 * ������ʾ����
	 * 
	 * @param isFirst
	 *            �Ƿ��һ�μ���
	 * @return void
	 */
	private void setDetail(boolean isFirst) {

		AppLog.LogD("bookState-->" + bookBean.getState());
		AppLog.LogD("vCode22-->" + vCode);
		tvCode.setText("�ϳ���֤��Ϊ��" + vCode);

		phones = bookBean.getReplyerPhone();

		try {
			setDetailItem(bookBean.getBookFlags());
			if (!isFirst) {
				getActionTimes(bookBean.getId());
				changeOneTaxiState(bookBean.getId(), bookBean.getState());
				// ��һ�β��滮·��
				requestTaxiRoutePlan(bookBean.getCityName(), bookBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (bookBean.getState()) {
		// ˾���ӵ�ǰ
		case 0x01:
			// panel.expand(true);
			showPanel();
			driverName.setText("����Ϊ�����ȳ���...");
			taxiNumber.setVisibility(View.GONE);
			tvUseTime.setText(bookBean.getUseTime());
			tvFee.setText("+" + bookBean.getPrice() + "Ԫ");
			callTaxiPhone.setVisibility(View.GONE);
			rightButton.setVisibility(View.GONE);
			leftButton.setVisibility(View.VISIBLE);
			leftButton.setText(LEFT_BUTTON_TXT_CANCEL);
			break;
		// ����ʧ��
		case 0x03:
			// panel.expand(true);
			showPanel();

			driverName.setText("����ʧ�ܣ�������ԤԼ����");
			taxiNumber.setVisibility(View.GONE);
			tvUseTime.setText(bookBean.getUseTime());
			tvFee.setText("+" + bookBean.getPrice() + "Ԫ");

			rightButton.setVisibility(View.GONE);
			leftButton.setVisibility(View.GONE);
			leftButton.setText(LEFT_BUTTON_TXT_BOOK_AGAIN);
			break;
		// δ�ӵ��˿�ȡ��
		case 0x04:
			// panel.expand(true);
			showPanel();

			driverName.setText("����ȡ������");
			taxiNumber.setVisibility(View.GONE);
			tvUseTime.setText(bookBean.getUseTime());
			tvFee.setText("+" + bookBean.getPrice() + "Ԫ");

			rightButton.setVisibility(View.GONE);
			leftButton.setVisibility(View.GONE);
			leftButton.setText(LEFT_BUTTON_TXT_BOOK_AGAIN);
			break;

		// ִ�ж���-��˾���ӵ�
		case 0x05:
			// panel.expand(true);
			showPanel();
			callTaxiPhone.setVisibility(View.VISIBLE);
			showTaxiInfo();

			if (isPayState(bookBean.getBookFlags(), 0x05)) {
				rightButton.setVisibility(View.GONE);
				// leftButton.setText(LEFT_BUTTON_TXT_EVALUATE);
				leftButton.setVisibility(View.GONE);
			} else {
				rightButton.setVisibility(View.VISIBLE);
				rightButton.setText(RIGHT_BUTTON_TXT_DONE);
				leftButton.setText(LEFT_BUTTON_TXT_SPECIAL_CANCEL);
				leftButton.setVisibility(View.VISIBLE);
			}
			break;
		// ����������-�˿�ȷ���ϳ���˾��ȷ���ϳ����˿���ֹ����/˾����ֹ������
		case 0x06: // ??????????????????????????
			// panel.expand(true);
			showPanel();
			callTaxiPhone.setVisibility(View.VISIBLE);
			showTaxiInfo();
			// ˾��Ͷ��
			if (isArgueState(bookBean.getBookFlags(), 0x06)) {
				leftButton.setText(LEFT_BUTTON_TXT_DISAGREE);
				leftButton.setVisibility(View.VISIBLE);
				rightButton.setText(RIGHT_BUTTON_TXT_DONE);
				rightButton.setVisibility(View.VISIBLE);
			} else {// �˿�ȡ������
				leftButton.setText(LEFT_BUTTON_TXT_EVALUATE);
				if (isSuggested) {
					leftButton.setVisibility(View.GONE);
				} else {
					leftButton.setVisibility(View.VISIBLE);
				}
				rightButton.setVisibility(View.GONE);
			}
			break;
		// �������-˾��ȷ���ϳ�/�˿�ȷ���ϳ�
		case 0x07:
			// panel.expand(true);
			callTaxiPhone.setVisibility(View.VISIBLE);
			showTaxiInfo();
			rightButton.setVisibility(View.GONE);
			leftButton.setText(LEFT_BUTTON_TXT_EVALUATE);
			if (isSuggested) {
				leftButton.setVisibility(View.GONE);
			} else {
				leftButton.setVisibility(View.VISIBLE);
			}
			break;
		// ����ִ��ʧ��-�˿�ͬ����ֹ����/˾��ͬ����ֹ�����������ٲ�
		case 0x08:
			// panel.expand(true);
			showPanel();
			callTaxiPhone.setVisibility(View.VISIBLE);
			showTaxiInfo();
			rightButton.setVisibility(View.GONE);
			leftButton.setText(LEFT_BUTTON_TXT_EVALUATE);
			if (isSuggested) {
				leftButton.setVisibility(View.GONE);
			} else {
				leftButton.setVisibility(View.VISIBLE);
			}
			break;
		// ����-�˿Ͳ�ͬ����ֹ����/˾����ͬ����ֹ������˾��δȷ���ϳ�/�˿�δȷ���ϳ�������ִ�г�ʱ
		case 0x09:
			// panel.expand(true);
			showPanel();
			callTaxiPhone.setVisibility(View.VISIBLE);
			showTaxiInfo();
			rightButton.setVisibility(View.GONE);
			leftButton.setText(LEFT_BUTTON_TXT_EVALUATE);
			if (isSuggested) {
				leftButton.setVisibility(View.GONE);
			} else {
				leftButton.setVisibility(View.VISIBLE);
			}
			break;
		default:
			try {
				showPanel();
				driverName.setText("״̬δ֪");
				taxiNumber.setVisibility(View.GONE);
				tvUseTime.setText(bookBean.getUseTime());
				tvFee.setText("+" + bookBean.getPrice() + "Ԫ");
				rightButton.setVisibility(View.GONE);
				leftButton.setVisibility(View.GONE);
			} catch (Exception e) {
			}
			break;
		}

		handler.post(countDownTimeRunnable);
	}

	private Runnable countDownTimeRunnable = new Runnable() {
		public void run() {
			try {
				if (isRunnable) {
					if (bookBean.getDyTime() == 0) {
						long beTime = TimeTool.DEFAULT_DATE_FORMATTER.parse(bookBean.getUseTime()).getTime() - System.currentTimeMillis();
						if (beTime <= 0) {
							bookBean.setDyTime(-1);
						} else {
							bookBean.setDyTime(beTime / 1000);
						}
					}

					if (bookBean.getDyTime() < 0) {// �ѳ�ʱ
						tvCountDownTime.setText("�ѳ�ʱ");
						tvCountDownTimeUnit.setText("");
					} else {
						long limitTime = bookBean.getDyTime() / 60;
						if (limitTime >= 60) {
							tvCountDownTime.setText(BookUtil.getDecimalNumber(limitTime, 60.0f));
							tvCountDownTimeUnit.setText("Сʱ");
						} else {
							tvCountDownTime.setText(String.valueOf(limitTime));
							tvCountDownTimeUnit.setText("����");
						}
					}
					tvCountDownDistance.setText(BookUtil.getDecimalNumber(bookBean.getForecastDistance()));
					handler.postDelayed(this, 30000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void showTaxiInfo() {
		tvUseTime.setText(bookBean.getUseTime());
		tvFee.setText("+" + bookBean.getPrice() + "Ԫ");
		driverName.setText(bookBean.getReplyerName());
		taxiNumber.setText(bookBean.getReplyerNumber());
		taxiNumber.setVisibility(View.VISIBLE);
	}

	private void setDetailItem(long bookFlags) {
		if (bookFlags < 0) {
			return;
		}

		int length = Long.toBinaryString(bookFlags).length();
		length = Math.min(length, 32);

		optLayout.removeAllViews();

		synchronized (actionTimeView) {
			actionTimeView.clear();
			for (int i = 0; i < length; i++) {
				String action = getAction(bookFlags, i);
				if (TextUtils.isEmpty(action))
					continue;

				String[] details = action.split("-");
				if (details.length == 2 && !TextUtils.isEmpty(details[1])) {
					LinearLayout row = (LinearLayout) inflater.inflate(R.layout.book_detail_row, null);
					TextView tvTime = (TextView) row.findViewById(R.id.book_detail_row_time);
					TextView tvName = (TextView) row.findViewById(R.id.book_detail_row_name);
					TextView tvAction = (TextView) row.findViewById(R.id.book_detail_row_action);
					lastestLine = (View) row.findViewById(R.id.book_detail_row_line);
					tvTime.setText("");
					tvName.setText(details[0]);
					tvAction.setText(details[1]);

					actionTimeView.add(tvTime);
					optLayout.addView(row);
				}
			}
		}
		// panel.expand(true);
	}

	private void setSuggestItem(JSONArray array) {
		if (array == null) {
			return;
		}

		optLayoutSuggest.removeAllViews();
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = null;
			try {
				object = array.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}

			AppLog.LogD("xyw", object.toString());
			if (object != null) {
				LinearLayout row = (LinearLayout) inflater.inflate(R.layout.book_detail_row, null);
				TextView tvTime = (TextView) row.findViewById(R.id.book_detail_row_time);
				TextView tvName = (TextView) row.findViewById(R.id.book_detail_row_name);
				TextView tvAction = (TextView) row.findViewById(R.id.book_detail_row_action);

				try {
					long time = object.getLong("_CREATE_TIME");
					String comment = object.getString("_CONTENT");
					String passengerId = object.getString("_SUGGESTER_ID");

					if (String.valueOf(bookBean.getPassengerId()).equals(passengerId)) {
						tvName.setText("��:");
						isSuggested = true;
					} else {
						tvName.setText("˾��:");
					}
					tvTime.setText(TimeTool.getDateString(time));
					tvAction.setText(comment);
					optLayoutSuggest.addView(row);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		// ��ʾ���۰�ť
		if (leftButton.getText().equals(LEFT_BUTTON_TXT_EVALUATE)) {
			if (isPayState(bookBean.getBookFlags(), 0x05)) {
				leftButton.setVisibility(View.GONE);
			} else {
				if (isSuggested) {
					leftButton.setVisibility(View.GONE);
				} else {
					leftButton.setVisibility(View.VISIBLE);
				}
			}

		}

		showPanel();
	}

	public void cancelBookWindow(final Context context) {

		LayoutInflater factory = LayoutInflater.from(context);
		final Dialog dlg = new Dialog(context, R.style.Customdialog);
		final View dialogView = factory.inflate(R.layout.dialog_detail_cancel_book, null);
		dlg.setContentView(dialogView);

		RadioGroup group = (RadioGroup) dialogView.findViewById(R.id.new_detail_radioGroup);
		Button btnComfirm = (Button) dialogView.findViewById(R.id.stopservice_comfirm);
		Button btnBack = (Button) dialogView.findViewById(R.id.stopservice_cancel);

		int reason = 0x400;

		int id = group.getCheckedRadioButtonId();
		switch (id) {
		case R.id.book_detail_cancel_radio1:
			reason = 0x400;
			break;
		case R.id.book_detail_cancel_radio2:
			reason = 0x09;
			break;
		default:
			break;
		}
		final int result = reason;
		btnComfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				cancelBook(bookBean.getId(), result);
				dlg.dismiss();
			}
		});

		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});

		dlg.show();
	}

	// ���������У���˾��Ͷ��ʱ
	private boolean isArgueState(long bookFlags, int bookState) {
		if (bookState == 0x06) {
			if (getActionFlag(bookFlags, 0x11)) {
				return true;
			}
		}

		return false;
	}

	// ˾���ѽӵ�������ѡ������֧��
	private boolean isPayState(long bookFlags, int bookState) {
		if (bookState == 0x05) {
			if (getActionFlag(bookFlags, 0x10)) {
				return true;
			}
		}

		return false;
	}

	private void getRoutePlan(int startLat, int startLng, int endLat, int endLng) {

	}

	/**
	 * ��ȡ���⳵λ��
	 * 
	 * @param taxiId
	 * @param callback
	 * @throws Exception
	 */
	private void getTaxiLocation(final long taxiId, Callback<JSONObject> callback) throws Exception {
		JSONObject json = new JSONObject();
		json.put("action", "locationAction");
		json.put("method", "getTaxiLocation");
		json.put("taxiId", taxiId);

		long id = 1;
		try {
			id = Long.valueOf(session.get("_MOBILE"));
		} catch (Exception e) {
			id = 1;
		}

		SocketUtil.getJSONObject(id, json, callback);
	}

	/**
	 * ��˾���ӵ�ʱ����ֹ�ص�·���滮
	 * 
	 * @param cityName
	 * @param bean
	 * @return void
	 */
	private void requestRoutePlan(String cityName, BookBean bean) {
		try {
			// String number =
			// BookUtil.getDecimalNumber(bean.getForecastDistance());
			// ����������100���������·���滮
			// if (((int) Double.parseDouble(number)) > 100) {
			// return;
			// }

			clearRoutePlan();

			String startAddr = bean.getStartAddress();
			String endAddr = bean.getEndAddress();
			GeoPoint startGeoPoint = null;
			GeoPoint endGeoPoint = null;

			if (bean.getStartLatitude() == 0 || bean.getStartLongitude() == 0) {
				startGeoPoint = null;
			} else {
				startGeoPoint = new GeoPoint(bean.getStartLatitude(), bean.getStartLongitude());
			}

			if (bean.getEndLatitude() == 0 || bean.getEndLongitude() == 0) {
				endGeoPoint = null;
			} else {
				endGeoPoint = new GeoPoint(bean.getEndLatitude(), bean.getEndLongitude());
			}

			// �������յ���Ϣ������tranist search ��˵��������������
	        PlanNode stNode = PlanNode.withCityNameAndPlaceName("�Ĵ�", startAddr);
	        PlanNode enNode = PlanNode.withCityNameAndPlaceName("�Ĵ�", endAddr);
			
//			mMKSearch.transitSearch((new TransitRoutePlanOption())
//                    .from(stNode).city("ɽ��").to(enNode));
			 mMKSearch.drivingSearch((new DrivingRoutePlanOption())
	                    .from(stNode).to(enNode));
			
//			MKPlanNode startNode = new MKPlanNode();
//			MKPlanNode endNode = new MKPlanNode();
//
//			// ������յ��name���и�ֵ��Ҳ����ֱ�Ӷ����긳ֵ����ֵ�����򽫸��������������
//			// start
//			if (startGeoPoint == null) {
//				startNode.name = startAddr;
//			} else {
//				startNode.pt = startGeoPoint;
//			}
//
//			// end
//			if (endGeoPoint == null) {
//				endNode.name = endAddr;
//			} else {
//				endNode.pt = endGeoPoint;
//			}
//			mMKSearch.drivingSearch(cityName, startNode, cityName, endNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��˾���ӵ�ʱ��˾��λ�õ�
	 * 
	 * @param cityName
	 * @param bean
	 * @return void
	 */
	private void requestTaxiRoutePlan(final String cityName, final BookBean bean) {
		clearRoutePlan();

		try {
			// ����˾���ӵ�״̬
			if (bean.getState() != 5) {
				requestRoutePlan(cityName, bean);
				return;
			}

			long taxiId = bean.getReplyerId();
			getTaxiLocation(taxiId, new Callback<JSONObject>() {
				@Override
				public void handle(JSONObject param) {

					if (param == null) {
						Toast.makeText(NewBookDetail.this, "��ܰ��ʾ���޷���λ���⳵λ�ã�", Toast.LENGTH_LONG).show();
						return;
					} else {
						try {
							if (param.getInt("error") == 0) {
								int lng = param.getInt("longitude");
								int lat = param.getInt("latitude");
//								taxiOverlay = new TaxiOverlayItem(new GeoPoint(lat, lng), "", "", res);
//								passengerOverlay = new PassengerOverlayItem(new GeoPoint(bean.getStartLatitude(), bean.getStartLongitude()), "", "", res);
								LatLng pointtaxi = new LatLng(lng, lat);
								LatLng pointpassenger = new LatLng(bean.getStartLatitude(), bean.getStartLongitude());
								
								BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.mycar);;
								
								taxiOverlay = new MarkerOptions().position(pointtaxi).icon(bitmap);
								passengerOverlay = new MarkerOptions().position(pointpassenger).icon(bitmap);
								
								overlayParent.add(taxiOverlay);
								overlayParent.add(passengerOverlay);
//								overlayParent.addItem(taxiOverlay);
//								overlayParent.addItem(passengerOverlay);
//								mapView.refresh();

								String startAddr = bean.getStartAddress();
								String endAddr = bean.getEndAddress();
								GeoPoint startGeoPoint = null;
								GeoPoint endGeoPoint = null;

								if (bean.getStartLatitude() == 0 || bean.getStartLongitude() == 0) {
									startGeoPoint = null;
								} else {
									startGeoPoint = new GeoPoint(bean.getStartLatitude(), bean.getStartLongitude());
								}

								if (lng == 0 || lat == 0) {
									endGeoPoint = null;
								} else {
									endGeoPoint = new GeoPoint(lat, lng);
								}

								// �������յ���Ϣ������tranist search ��˵��������������
						        PlanNode stNode = PlanNode.withCityNameAndPlaceName("�Ĵ�", startAddr);
						        PlanNode enNode = PlanNode.withCityNameAndPlaceName("�Ĵ�", endAddr);
						        
//						        mMKSearch.transitSearch((new TransitRoutePlanOption())
//					                    .from(stNode).city("ɽ��").to(enNode));
						        mMKSearch.drivingSearch((new DrivingRoutePlanOption())
					                    .from(stNode).to(enNode));
								
//								MKPlanNode hotelNode = new MKPlanNode();
//								MKPlanNode taxiNode = new MKPlanNode();
//
//								// ������յ��name���и�ֵ��Ҳ����ֱ�Ӷ����긳ֵ����ֵ�����򽫸��������������
//								// start
//								if (startGeoPoint == null) {
//									hotelNode.name = startAddr;
//								} else {
//									hotelNode.pt = startGeoPoint;
//								}
//
//								// end
//								if (endGeoPoint == null) {
//									taxiNode.name = "";
//								} else {
//									taxiNode.pt = endGeoPoint;
//								}
//
//								mMKSearch.drivingSearch(cityName, taxiNode, cityName, hotelNode);
							} else {
								AppLog.LogD("��ܰ��ʾ���޷���λ���⳵λ�ã�");
								requestRoutePlan(cityName, bean);
							}
						} catch (Exception e) {
							e.printStackTrace();
							AppLog.LogD("��ܰ��ʾ���޷���λ���⳵λ�ã�");
							Toast.makeText(NewBookDetail.this, "��ܰ��ʾ���޷���λ���⳵λ�ã�", Toast.LENGTH_LONG).show();
						}
					}
				}

				@Override
				public void error(Throwable e) {
					super.error(e);
					AppLog.LogD("��ܰ��ʾ���޷���λ���⳵λ�ã�");
					requestRoutePlan(cityName, bean);
				}
			});
		} catch (Exception e) {
			AppLog.LogD("��ܰ��ʾ���޷���λ���⳵λ�ã�");
			requestRoutePlan(cityName, bean);
		}
	}

	private void clearRoutePlan() {
//		RouteOverlay route = myMKSearchLis.getRouteOverlay();
//		if (route != null) {
//			overlays.remove(route);
			route = null;
			routeOverlay.removeFromMap();
			mBaiduMap.clear();
//		}
//		overlays.remove(passengerOverlay);
//		overlays.remove(taxiOverlay);
//		mapView.refresh();
	}

	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
	}

	private Dialog createCancelDialog() {
		Callback<Object> comfirmCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				try {
					int reason = (Integer) param;
					long useTime = TimeTool.DEFAULT_DATE_FORMATTER.parse(bookBean.getUseTime()).getTime();
					long minites = (useTime - System.currentTimeMillis()) / (1000 * 60);
					if (reason == CancelBookDialog.CANCEL_REASON_PERSONAL) {
						// if (minites > 10) {
						// cancelBook(bookBean.getId(), reason);
						// } else {
						// showDialog(NewBookDetail.this, "��ʾ",
						// "�����볬ʱ����10��������ʱ�������Ը���ԭ��Ϊ��ȡ������", "ȷ��", "", "");
						// }

						// ȥ������ԭ���ʱ������
						cancelBook(bookBean.getId(), reason);
					} else if (reason == CancelBookDialog.CANCEL_REASON_TAXI) {
						if (minites < -15) {
							cancelBook(bookBean.getId(), reason);
						} else {
							showDialog(NewBookDetail.this, "��ʾ", "˾���ڶ�����ʱ15�����Ժ�û��ʱ��������˾��û��Ϊ��ȡ������", "ȷ��", "", "");
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		CancelBookDialog cancelBookDialog = new CancelBookDialog(NewBookDetail.this, comfirmCallback, null);
		return cancelBookDialog;
	}

	private Dialog createSuggestDialog() {
		Callback<Object> comfirmCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				JSONObject object = (JSONObject) param;
				String comment = "";
				int value = 0;
				try {
					comment = object.getString("comment");
					value = object.getInt("value");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (NetChecker.getInstance(ETApp.getInstance()).isAvailableNetwork()) {
					AppLog.LogD("xyw", bookBean.toString());
					try {
						String cityId = session.get("_CITY_ID");
						suggestBook(bookBean.getId(), cityId, bookBean.getPassengerId(), bookBean.getReplyerId(), comment, value, 7);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// д����־
					ActionLogUtil.writeActionLog(NewBookDetail.this, R.array.more_history_evaluate, "");
				} else {
					Toast.makeText(NewBookDetail.this, "���粻����", Toast.LENGTH_SHORT).show();
				}
			}
		};

		return new SuggestDialog(NewBookDetail.this, comfirmCallback, null);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_CANCEL_BOOK:
			return createCancelDialog();
		case DIALOG_SUGGEST_BOOK:
			return createSuggestDialog();
		default:
			break;
		}

		return super.onCreateDialog(id, args);
	}

	/**
	 * ���� /** ��������: 1.��ʱ�򳵽��� 2.��ͨͶ�ߺͽ��� 3.Ͷ��˾�� 4.Ͷ�߿ͷ� 5.������� 6.������ѯ 7.��������
	 * 
	 * @param bookId
	 * @param cityId
	 * @param passengerId
	 * @param taxiId
	 * @param info
	 * @param value
	 * @param type
	 * @return void
	 */
	public void suggestBook(final long bookId, final String cityId, final long passengerId, final long taxiId, final String info, final int value, final int type) {
		try {
			JSONObject json = new JSONObject();
			json.put("action", "proxyAction");
			json.put("method", "query");
			json.put("op", "suggestByBookId");
			json.put("type", type);
			json.put("bookId", bookId);
			json.put("_SUGGESTER_ID", passengerId);
			json.put("_USER_ID", taxiId);

			json.put("content", info);
			json.put("evaluate", value);

			json.put("cityId", Integer.valueOf(cityId));
			json.put("cityName", cn.com.easytaxi.platform.MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			SocketUtil.getJSONObject(passengerId, json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {
					try {
						if (param != null) {
							JSONObject result = (JSONObject) param;

							AppLog.LogD("xyw", "book suggest--->" + result.toString());
							if (result.getInt("error") != 0) {
								throw new Exception(result.toString());
							} else {
								Toast.makeText(NewBookDetail.this, "���۳ɹ�", Toast.LENGTH_SHORT).show();
								leftButton.setVisibility(View.GONE);
								getBookSuggest(bookBean.getId());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(NewBookDetail.this, "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void error(Throwable e) {
					// TODO Auto-generated method stub
					super.error(e);
					e.printStackTrace();
					Toast.makeText(NewBookDetail.this, "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(NewBookDetail.this, "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ��ȡ���鶯��ʱ���б�
	 * 
	 * @param bookId
	 * @return void
	 */
	public void getActionTimes(final long bookId) {
		try {
			JSONObject json = new JSONObject();
			json.put("action", "proxyAction");
			json.put("method", "query");
			json.put("op", "getBookActionTimeList");
			json.put("bookId", bookId);

			json.put("cityId", cn.com.easytaxi.platform.MainActivityNew.cityId);
			json.put("cityName", cn.com.easytaxi.platform.MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			SocketUtil.getJSONObject(Long.valueOf(getPassengerId()), json, new Callback<JSONObject>() {
				@Override
				public void handle(JSONObject param) {
					try {
						if (param != null) {
							JSONObject result = (JSONObject) param;
							AppLog.LogD("xyw", "getActionTimes--->" + result.toString());
							if (result.getInt("error") != 0) {
								throw new Exception(result.toString());
							} else {
								JSONArray array = result.getJSONArray("datas");
								if (array == null) {
									return;
								}

								synchronized (actionTimeList) {
									actionTimeList.clear();
									for (int i = 0; i < array.length(); i++) {
										long time = ((JSONObject) array.get(i)).getLong("_CREATE_TIME");
										actionTimeList.add(TimeTool.getDateString(time));
									}
								}

								synchronized (actionTimeView) {
									int len = actionTimeView.size() < actionTimeList.size() ? actionTimeView.size() : actionTimeList.size();
									for (int i = 0; i < len; i++) {
										actionTimeView.get(i).setText(actionTimeList.get(i));
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void error(Throwable e) {
					// TODO Auto-generated method stub
					super.error(e);
					e.printStackTrace();
					Toast.makeText(NewBookDetail.this, "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(NewBookDetail.this, "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ȡ����ʱ��״̬
	 * 
	 * @param bookId
	 * @param state
	 * @return void
	 */
	private void changeOneTaxiState(long bookId, int state) {
		cn.com.easytaxi.onetaxi.common.BookBean bk = ETApp.getInstance().getCacheBookbean();
		if (bk != null) {
			long currentBookId = bk.getId();
			if (bookId == currentBookId) {
				if (!(state == 1 || state == 5)) {
					AppLog.LogD("xyw", "change onetaxi book state");
					ETApp.getInstance().setCacheBookbean(null);
				}
			}
		}
	}

	private BroadcastReceiver reloadReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			AppLog.LogD("xyw", "change onetaxi--->onReceive");
			getDetail(bookBean.getId());
		}
	};

	private void registReceiver() {
		IntentFilter filter = new IntentFilter("cn.com.easytaxi.book.state.changed");
		filter.addAction(Intent.ACTION_SCREEN_ON);
		this.registerReceiver(reloadReceiver, filter);
	}

	/**
	 * ��ʾ��Ϣ��ʾdialog
	 */
	public void showDialog(final Context context, String titile, String content, String btn1, String btn2, final String gotoClassName) {
		Callback<Object> okBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				CommonDialog dialog = (CommonDialog) param;
				dialog.dismiss();
			}
		};

		Dialog dialog = new CommonDialog(NewBookDetail.this, titile, content, btn1, btn2, R.layout.dlg_close, okBtnCallback, null);
		dialog.show();
	}

	/**
	 * �ӷ���˻�ȡ˾������
	 * 
	 * @param bookId
	 * @return void
	 */
	private void getTaxiInfo(Long taxiId) {
		if (taxiId == null || taxiId == 0) {
			return;
		}

		try {
			JSONObject json = new JSONObject();
			json.put("action", "proxyAction");
			json.put("method", "query");
			json.put("op", "getTaxiInfo");
			json.put("taxiId", taxiId);

			json.put("cityId", cn.com.easytaxi.platform.MainActivityNew.cityId);
			json.put("cityName", cn.com.easytaxi.platform.MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			AppLog.LogD("xyw", "request taxi detail json--->" + json.toString());
			SocketUtil.getJSONObject(Long.valueOf(getPassengerId()), json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {
					try {
						if (param != null) {
							JSONObject result = (JSONObject) param;

							AppLog.LogD("xyw", "taxi detail--->" + result.toString());
							if (result.getInt("error") != 0) {
								throw new Exception(result.toString());
							} else {
								hasGetTaxiInfo = true;
								result = result.getJSONArray("datas").getJSONObject(0);

								// show taxi info
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(NewBookDetail.this, "��ȡ��������ʧ��", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void error(Throwable e) {
					// TODO Auto-generated method stub
					super.error(e);
					e.printStackTrace();
					Toast.makeText(NewBookDetail.this, "��ȡ��������ʧ��", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
					// try {
					// dismissDialog(0);
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DisplayMetrics getDisplayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}
	
	/**
     * ��λSDK��������
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ���ٺ��ڴ����½��յ�λ��
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

	@Override
	public void onGetBikingRouteResult(BikingRouteResult arg0) {
		
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(NewBookDetail.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		
	}
	// ����RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }
    }

}
