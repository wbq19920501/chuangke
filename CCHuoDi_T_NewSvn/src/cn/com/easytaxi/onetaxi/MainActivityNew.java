package cn.com.easytaxi.onetaxi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mapapi.overlayutil.DrivingRouteOverlay;
import mapapi.overlayutil.OverlayManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.NewNetworkRequest.TipPrices;
import cn.com.easytaxi.airport.view.ChangingTextView;
import cn.com.easytaxi.airport.view.ChangingTextView.TimeCallback;
import cn.com.easytaxi.airport.view.ScrollingTextView;
import cn.com.easytaxi.book.BookUtil;
import cn.com.easytaxi.book.NewBookDetail;
import cn.com.easytaxi.client.channel.TcpClient;
import cn.com.easytaxi.client.common.MsgConst;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.ToolUtil;
import cn.com.easytaxi.common.User;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.expandable.Child;
import cn.com.easytaxi.expandable.Group;
import cn.com.easytaxi.onetaxi.common.BookBean;
import cn.com.easytaxi.onetaxi.common.PcmRecorder;
import cn.com.easytaxi.platform.RegisterActivity;
import cn.com.easytaxi.platform.YdLocaionBaseActivity;
import cn.com.easytaxi.platform.service.EasyTaxiCmd;
import cn.com.easytaxi.platform.service.MainService;
import cn.com.easytaxi.platform.service.OneBookService;
import cn.com.easytaxi.platform.view.AddressEditView;
import cn.com.easytaxi.platform.view.AddressEditView.OnEditOverListner;
import cn.com.easytaxi.ui.BookPublishFragement;
import cn.com.easytaxi.ui.view.MoneyWheelDlg;
import cn.com.easytaxi.ui.view.MoneyWheelDlg.MoneyDialogListener;
import cn.com.easytaxi.util.InfoConfig;
import cn.com.easytaxi.util.InfoTool;
import cn.com.easytaxi.workpool.bean.GeoPointLable;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
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
import com.google.gson.JsonObject;

public class MainActivityNew extends YdLocaionBaseActivity implements OnEditOverListner, TimeCallback, OnGetRoutePlanResultListener, OnMapClickListener {

	public static int mCarType = 1;

	/** 搬运工人数 */
	public static int mNeedPerson = 0;

	/**
	 * 是否可以下单
	 */
	private boolean isCanBook = true;

	/**
	 * 获取以前发过的历史订单
	 */
	private ProgressDialog mProgressDialog;
	private static final String SOUND_FILE_NAME = "/audio_huang.wav";
	public static final String SOUND_FILE_NAME_spx = "voice_44.wav";
	/**
	 * 是否强制在线支付： 0代表不需要在线支付；1表示必须在线支付
	 */
	public int onlinePayment = 0;
	private MainActivityNew self = this;
	private String tempstr = "高峰期适当加价，有助于更快坐到车";
	// 语音打车时，车型
	private String voiceCarType = "";
	// 地图
	private BMapManager mBMapMan;
	private MapView mapView;
//	private MapController controller;
	// 我的位置上边显示的地址信息框
//	private PopupOverlay pop;

//	private PoiSearch mMKSearch;
	
	 // 搜索相关
    RoutePlanSearch mMKSearch = null;    // 搜索模块，也可去掉地图模块独立使用

	// 是否播放声音

	private TitleBar bar;

	private Button cancelButton;
	private Button telButton;

//	private RouteOverlay routeOverlay;
	private OverlayManager routeOverlay;

	// 取消即时订单
	private ImageButton mBtnCancel;
	// private Button tackButton;
	// private Button nearButton;

	// 信息提示
	private View taxiInfoView;
	private TextView taxiNumber;
	private TextView driverName;
	private TextView company;
	private TextView addressTextView;// 信息提示框(当前位置提示，出租车与打车位置距离提示)
	private View infoLayout;

	public boolean isDestroy = false;
	long nearByCarRefresInterval = InfoConfig.NEARBY_TAXI_REFRESH_INTERVAL;

	/**
	 * 我当前位置的标注
	 */
//	private MyLocationOverlay myLocationOverlay;

	/**
	 * 我当前位置的数据
	 */
//	private LocationData myLocData;
	private LocationClient myLocData;
	boolean isFirstLoc = true; // 是否首次定位
	
	public MyLocationListenner myListener = new MyLocationListenner();

//	private ItemizedOverlayTool itemizedOverlayTool;
	ArrayList<BitmapDescriptor> itemizedOverlayTool = new ArrayList<BitmapDescriptor>();

	/**
	 * 接我单的出租车的标注
	 */
	private TaxisOverlay myTaxiOverlay;

	/**
	 * 周围出租车的标注
	 */
	private TaxisOverlay nearByTaxiOverlay;

	/**
	 * 我的上车地点标注
	 */
	private OverlayOptions myStartAddressOverlay;

	/**
	 * 我下车位置（目的地）标注
	 */
	private OverlayOptions myEndAddressOverlay;

	/**
	 * 我搜索的位置的标注
	 */
	private OverlayOptions mySearchAddressOverlay;

//	private MySearchListener myMKSearchLis;

	/**
	 * 上车点到接单车的路径规划；
	 */
//	private RouteOverlay routeOverlayMy2Taxi;
//	private RouteOverlay routeOverlayStart2End;
	private OverlayManager routeOverlayMy2Taxi;
	private OverlayManager routeOverlayStart2End;

//	private List<Overlay> overlays;

	// 会话ID
	String callid;
	String taxiid;
	String mobile;// 本机电话号码

	// 乘客位置信息（通过监听平台广播获得的,第一次直接取平台的）
	int p_lat = 0;
	int p_lng = 0;
	// 乘客打车位置
	int u_lat;
	int u_lng;

	private View progress_alarm;

	private ProgressBar progress_time;

	private TextView progress_info;

	private View one_speak;

	private View linearLayout_start_end;

	private AddressEditView editText_start;

	// private ImageButton editText_start_btn;

	private ImageButton clear_start;

	private AddressEditView editText_end;

	// private ImageButton editText_end_btn;

	private ImageButton clear_end;

	private Button one_send;

	private ImageButton one_send_mode;

	private ImageButton map_current_pos;

	private View progress_rcd_voice;

	/**
	 * 显示正在录音的提示View
	 */
	private View voice_inpuuting_show;
	private ChangingTextView changing_text_view;

	// private View voice_rcd_hint_loading;

	// private RecognizerDialog recognizerDialog;

	private static final int DEFAULT_ZOOM_FACTOR = 14;

	public static final String tag = "MainActivityNew";

	// private final static String KEY_GRAMMAR_ID = "grammar_id";

	// private final static String App_ID = "4f6833a5";

	protected static final int VOICE = 0;

	protected static final int WORD = 1;

	private static final int TRAFFIC_OFF = 0;

	private static final int TRAFFIC_ON = 1;

	private static final int CANEL_SERVICE_DLG = 0;

	// --------------------订单位置相关---------------------//

	private String currentAddress = "";

	// 上车地址
	private String startAddress = "";
	// 上车地址附加描述位置信息
	public static String extraAddress = "";
	// 下车地址
	private String endAddress = "";
	// 上车地点
	private int startLatitude = 0;
	// 上策地点
	private int startLongitude = 0;
	// 下车地点
	private int endLatitude = 0;
	// 下车地点
	private int endLongitude = 0;
	// --------------------订单位置相关结束---------------------//

	protected int priceKey = 0;

	protected String priceValue = "0";

	private String sendMode = "voiceInput";

	private long currentBookId = 0;

	protected long currentTaxiId;

	protected int replyerLongitude;

	protected int replyerLatitude;

	private int currentStat = 0;

	// private BookBean bookbean;

	// BOOK_STAT_NORMAL - > BOOK_STAT_SENDING - > BOOK_STAT_WAITINGRESP - >
	// BOOK_STAT_RECEIVED- >BOOK_STAT_CANCELING->BOOK_STAT_CANCELED

	public static final int BOOK_STAT_NORMAL = 0; // 没有发起订单的正常状态

	public static final int BOOK_STAT_SENDING = 1; // 订单发送中

	public static final int BOOK_STAT_WAITINGRESP = 2;// 订单发送成功 等待回应中 等待司机响应

	public static final int BOOK_STAT_RECEIVED = 3;// 接到司机接单信息

	public static final int BOOK_STAT_TIMEOUT = 4;// 等待订单超时

	public static final int BOOK_STAT_REBOOK = 5;// 超时重新发单

	public static final int BOOK_STAT_CANCELING = 6;// 取消订单

	public static final int BOOK_STAT_CANCELED = 7;// 订单已经取消

	public static final int BOOK_STAT_NOT_ENOUGH = 8;// 订单已经取消

	public static final int BOOK_STAT_DRIVER_CANCELBOOK = 9;// 订单已经取消

	public static final int DRIVER_CANCEL_SERVICE_DLG = 10;

	public static final int CONFIRM_TO_SEND_SOUND = 11;

	public static final int CONFIRM_TO_SUBMIT = 12;

	public static final int BOOK_NET_INVALIDBLE = 13;

	public static final int GPS_DLG = 100;

	public static final int COMFIRM_ENDADDRESS = 101;

	// protected List<DiaoDuPrice> priceList = new
	// ArrayList<NewNetworkRequest.DiaoDuPrice>(12);
	private int[] priceList;
	public SessionAdapter session;
	// 城市id
	private String cityId;
	/** price list pager */
	private LinearLayout chat_addmoney_pager;

	private BroadcastReceiver onBookAction;

	private Callback<JSONObject> taxiLocationCallback = null;

	private Callback<Object> markCallback = null;

	private Callback<Object> nearByCallbacK = new NearByCallbacK();

	Callback<String> firstAddressCallback = null;

	private boolean isFisrtRun = true;

	private AddressEditView addrView;

	private CheckBox map_traffic_state;

	private TextView advice_add_money;

	protected boolean isSelectEndOK;

	protected boolean isSelectStartOK;

	protected String soundPath;

	protected Animation anim;
	private ToggleButton payMode;
	/** price list pager */
	private LinearLayout chatAddMoneyPager;
	private DisplayMetrics dm;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.arg1 == 1) {
				requestNearbyTaxi(new NearByCallbacK());
			}
			if (msg.arg1 == 2) {
				AppLog.LogD("refresh locaion car");
				taxiLocationCallback = new TaxiLocationCallback();
				requestTaxiLocation(currentTaxiId, taxiLocationCallback);
			}

			if (msg.arg1 == 3) {

//				setMyLocation(getCurrentlat(), getCurrentLng(), getCurrentRadius(), getCurrentDerect(), false);

			}

			if (msg.arg1 == 4) {
				// 关闭录音界面
				try {
					stopRecordSound();
					// 关闭录音界面
					voice_inpuuting_show.setVisibility(View.GONE);
					changing_text_view.stopChangingText();
					showDialog(CONFIRM_TO_SEND_SOUND);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	};

	/**
	 * 滚动提示文字：显示加价上面的提示文字
	 */
	private ScrollingTextView scrollingTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.onetaxi_new);
		mProgressDialog = new ProgressDialog(MainActivityNew.this);
		mProgressDialog.setMessage("请稍后...");
		dm = getDisplayMetrics();
		initSystemConfig();
		initViews();
		regReceiver();
		initListeners();
		initUserData();

		new LoadBooks().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	
	private void setOnlineState() {
		// 大于0时，表示必须在线支付
		if (onlinePayment > 0) {
			payMode.setChecked(true);
			payMode.setEnabled(false);
		} else {
			payMode.setChecked(false);
			payMode.setEnabled(true);
		}
	}

	private Callback<Object> priceCallback = new Callback<Object>() {
		@Override
		public void handle(Object param) {
			try {
				if (param != null) {
					TipPrices tipPrices = (NewNetworkRequest.TipPrices) param;
					if (tipPrices.error == 0) {// 有价格列表
						priceList = tipPrices.priceList;
						tempstr = tipPrices.msg;
						onlinePayment = tipPrices.onlinePayment;
						setOnlineState();
					} else {// 没获取到价格列表
						priceList = new int[] { 0 };
						Toast.makeText(MainActivityNew.this, tipPrices.errormsg, Toast.LENGTH_LONG).show();
					}
					priceKey = priceList[0];
					priceValue = priceList[0] + "";
					initPriveViewcell(priceList);
					initScrollingTextView(tempstr);
				}
			} catch (Exception e) {
				Toast.makeText(MainActivityNew.this, "获取价格列表出错", Toast.LENGTH_LONG).show();
			}

		}
	};

	/**
	 * 新的解析价格列表的方法
	 * 
	 * @param priceList
	 *            价格列表
	 */
	private void initPriveViewcell(int[] priceList) {
		LayoutInflater inflater = LayoutInflater.from(MainActivityNew.this);
		chatAddMoneyPager.removeAllViews();
		for (Integer p : priceList) {
			View view = inflater.inflate(R.layout.p_price_item, null);
			RadioButton cb = (RadioButton) view.findViewById(R.id.price_item);
			cb.setTag(p);
			cb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					RadioButton cbb = (RadioButton) arg0;
					if (cbb != null) {
						int ppp = (Integer) cbb.getTag();
						restet(chatAddMoneyPager);
						cbb.setText(ppp + "");
						priceKey = ppp;
						priceValue = ppp + "";
						cbb.setChecked(true);
					}
				}
			});
			if (priceKey == p) {
				priceValue = p + "";
				cb.setChecked(true);
			}

			cb.setText(p + "");
			chatAddMoneyPager.addView(view);
		}
	}

	private void dispatchStat(int stat) {
		AppLog.LogD("xyw", "dispatchStat stat ---=========------- " + stat);
		BookBean bk = ETApp.getInstance().getCacheBookbean();
		if (bk != null) {
			bk.setDispStat(stat);
			AppLog.LogD(bk.toString());
		}

		// currentStat = stat;
		switch (stat) {

		// 提交订单 等过程中出现网络不可用的情况
		case BOOK_NET_INVALIDBLE:
			Toast.makeText(MainActivityNew.this, R.string.network_notgood, Toast.LENGTH_SHORT).show();
			setSendMode(sendMode);
			infoLayout.setVisibility(View.GONE);
			progress_rcd_voice.setVisibility(View.GONE);
			break;

		// 一般情况 ，改情况发生于，第一次提交订单，或者某订单提交成功后，用户取消该订单，则进入该状态
		case BOOK_STAT_NORMAL:
			clearRoutePlan();
			progress_rcd_voice.setVisibility(View.GONE);
			editText_start.getEditText().setHint("上车地点");
			editText_end.getEditText().setHint("下车地点");
			editText_start.setText(startAddress);
			editText_end.setText("");
			tvBanyungong.setText("");
			rgCarType.clearCheck();
			infoLayout.setVisibility(View.GONE);
			setSendMode(sendMode);

//			hideStartPoint();
//			hideEndPoint();
//			hideMyTaxi();
			break;

		// 订单提提交中...
		case BOOK_STAT_SENDING:

			progress_rcd_voice.setVisibility(View.VISIBLE);
			re_one_send_parent.setVisibility(View.GONE);
			one_speak.setVisibility(View.GONE);
			infoLayout.setVisibility(View.GONE);

			break;
		// 订单提交成功，等待各种回应中... waitting
		case BOOK_STAT_WAITINGRESP:
			// setSendMode(sendMode);

			if (bk != null) {
				int timeout = (int) bk.getTimeOut();
				progress_time.setMax(timeout);
				textView_time.setText(InfoTool.friendTime(timeout));
				progress_info.setText(bk.getUdp003Info());
//				displayEndPoint(bk.getEndLatitude(), bk.getEndLongitude(), bk.getEndAddress());
				doRoutePlan();
			} else {
				AppLog.LogD("bk == null");
			}

			progress_alarm.startAnimation(anim);
			progress_alarm.setVisibility(View.VISIBLE);
			progress_rcd_voice.setVisibility(View.GONE);
			one_speak.setVisibility(View.GONE);
			re_one_send_parent.setVisibility(View.GONE);
			if (mapView != null)
				mapView.postInvalidate();
			break;

		// 等待提交成功后，等待司机回应，司机成功接单...
		case BOOK_STAT_RECEIVED:
			one_speak.setVisibility(View.GONE);
			progress_rcd_voice.setVisibility(View.GONE);
			// re_one_send.setVisibility(View.GONE);
			progress_alarm.setVisibility(View.GONE);
			re_one_send_parent.setVisibility(View.GONE);
			infoLayout.setVisibility(View.GONE);
//			infoLayout.startAnimation(anim);

			BookBean bookBean = ETApp.getInstance().getCacheBookbean();

			if (bookBean != null) {
				//新版，直接进入详情
				boolean isShow = true;
				if(isShow){
					//由下一步更改为跳转到详情页面
					Intent intent = new Intent(MainActivityNew.this, NewBookDetail.class);
					intent.putExtra("bookId", bookBean.getId());
					MainActivityNew.this.startActivity(intent);
					MainActivityNew.this.finish();
				}else{

					setDriverInfo(bookBean);

					currentTaxiId = bookBean.getReplyerId();
					replyerLongitude = bookBean.getReplyerLongitude();
					replyerLatitude = bookBean.getReplyerLatitude();
					endLatitude = bookBean.getEndLatitude();
					endLongitude = bookBean.getEndLongitude();
					
//					displayStartPoint(bookBean.getStartLatitude(), bookBean.getStartLongitude());
//					displayMyTaxi(replyerLatitude, replyerLongitude);
//					displayEndPoint(endLatitude, endLongitude, endAddress);
					doRoutePlan();
//					planRoute(bookBean.getStartLongitude(), bookBean.getStartLatitude(), bookBean.getReplyerLongitude(), bookBean.getReplyerLatitude());

					// planStart2EndRoute(bookBean.getStartLongitude(),
					// bookBean.getStartLatitude(), bookBean.getStartAddress(),
					// bookBean.getEndLongitude(), bookBean.getEndLatitude(),
					// bookBean.getEndAddress());
					// 路径规划
					doRoutePlan();
					markCallback = new MarkCallback();
					NewNetworkRequest.getMark(bookBean.getReplyerLatitude(), bookBean.getReplyerLongitude(), bookBean.getStartLatitude(), bookBean.getStartLongitude(), bookBean.getEndLatitude(), bookBean.getEndLongitude(), markCallback);
					// 接单成功后获取当前的接单的车的位置
					startTaxiLocationTimer();
				
				}
			}

			break;
		// 取消订单中
		case BOOK_STAT_CANCELING:
			infoLayout.setVisibility(View.GONE);
			progress_rcd_voice.setVisibility(View.GONE);
			re_one_send_parent.setVisibility(View.GONE);
			break;

		// 成功取消订单
		case BOOK_STAT_CANCELED:
			progress_rcd_voice.setVisibility(View.GONE);
			infoLayout.setVisibility(View.GONE);

			break;
		// 订单等待回应后，订单超时了..
		case BOOK_STAT_TIMEOUT:

			progress_rcd_voice.setVisibility(View.GONE);
			progress_alarm.setVisibility(View.GONE);
			progress_info.setText(R.string.diaoduding);
			progress_time.setProgress(0);
			setSendMode(sendMode);
			editText_end.setText("");
			re_one_send_parent.setVisibility(View.VISIBLE);
			advice_add_money.setVisibility(View.VISIBLE);
			infoLayout.setVisibility(View.GONE);

			break;

		// 订单因为超时，需要重新提交订单进入该状态，该状态与normal不同是，有“加价提示”
		case BOOK_STAT_REBOOK:
			NewNetworkRequest.cancelBook(currentBookId, currentTaxiId, mobile, null);
			progress_rcd_voice.setVisibility(View.GONE);
			progress_alarm.setVisibility(View.GONE);
			setSendMode(sendMode);
			editText_end.setText("");
			if (bk != null) {
				startAddress = bk.getStartAddress();
				startLatitude = bk.getStartLatitude();
				startLongitude = bk.getStartLongitude();
				endAddress = bk.getEndAddress();
				endLatitude = bk.getEndLatitude();
				endLongitude = bk.getEndLongitude();
				editText_start.getEditText().setHint("上车地点");
				editText_end.getEditText().setHint("下车地点");
				editText_start.setText(startAddress);
				editText_end.setText(endAddress);
//				displayEndPoint(endLatitude, endLongitude, endAddress);
				doRoutePlan();
			}

			// one_speak.setVisibility(View.GONE);
			if (sendMode.equals("wordInput")) {
				re_one_send.setVisibility(View.GONE);
				re_one_send_parent.setVisibility(View.GONE);
			} else {
				re_one_send.setVisibility(View.VISIBLE);
				re_one_send_parent.setVisibility(View.VISIBLE);
			}

			advice_add_money.setVisibility(View.GONE);
			infoLayout.setVisibility(View.GONE);
			break;
		case BOOK_STAT_NOT_ENOUGH:
			setSendMode(sendMode);
			progress_rcd_voice.setVisibility(View.GONE);
			re_one_send_parent.setVisibility(View.GONE);
			infoLayout.setVisibility(View.GONE);
			break;

		// 司机取消订单，老客户端才有这种情况
		case BOOK_STAT_DRIVER_CANCELBOOK:
			showDialog(DRIVER_CANCEL_SERVICE_DLG);
			break;

		default:
			break;
		}
	}

	private Timer timer;

	private void startTaxiLocationTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				BookBean bk = ETApp.getInstance().getCacheBookbean();
				if (bk.getDispStat() == BOOK_STAT_RECEIVED) {

					msg.arg1 = 2;
					msg.what = 2;
					handler.sendMessage(msg);
				} else {

//					hideMyTaxi();

				}
			}
		}, 15 * 1000, 15 * 1000);

	}

	/**
	 * 1、检测gps是否打开； 2、初始化声音 3、获取初始其实地址（）；
	 * 
	 */
	private void initSystemConfig() {

		Intent service = new Intent(MainActivityNew.this, MainService.class);
		startService(service);

//		mBMapMan = ETApp.getInstance().getBMapManager();
//		mBMapMan.start();

		checkNetwork();
		checkGps();
		initSound();

		String dir = ETApp.getInstance().getMobileInfo().getSDCardPath();
		soundPath = dir + SOUND_FILE_NAME;
		Intent intent = getIntent();
		startAddress = intent.getStringExtra("startAddress");
		if (null == startAddress) {
			startAddress = "";
		}
		anim = AnimationUtils.loadAnimation(MainActivityNew.this, R.anim.up_enter);

	}

	private void checkGps() {
		if (ETApp.getInstance().getMobileInfo().isGpgOpened()) {
		} else {
			if (ETApp.getInstance().getCacheInt("gps_no") == 1) {

			} else {
				showDialog(GPS_DLG);
			}
		}
	}

	public static byte[] getFileData(File file) throws IOException {
		AppLog.LogD("---==========0000000000" + file.getAbsolutePath());

		InputStream is = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			is = new FileInputStream(file);// pathStr 文件路径
			byte[] b = new byte[1024];
			int n;
			while ((n = is.read(b)) != -1) {
				out.write(b, 0, n);
			}// end while
		} catch (IOException e) {

			// throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {

				}
			}
		}

		return out.toByteArray();

	}

	@Override
	protected void initUserData() {
		session = new SessionAdapter(this);
		cityId = session.get("_CITY_ID");
		if (cityId == null || cityId.equals("")) {// 默认为北京市
			cityId = "1";
		}

		sendMode = ETApp.getInstance().getSendMode();
		int currentDispStat = BOOK_STAT_NORMAL;

//		setMyLocation(getCurrentlat(), getCurrentLng(), getCurrentRadius(), getCurrentDerect(), true);

		startRefreshNearbyTaxi(10);

		if (currentDispStat == BOOK_STAT_NORMAL) {
			// 当前的位置
			firstAddressCallback = new FirstAddressCallback();
			requestCurrentLoacionAddress(firstAddressCallback);
			startLatitude = getCacheStartLat();
			startLongitude = getCacheStartLng();

			AppLog.LogD("startLatitude" + startLatitude + "   startLongitude" + startLongitude);
		}
	}

	private void startRefreshNearbyTaxi(long interval) {
		Message msg = Message.obtain();
		msg.what = 1;
		msg.arg1 = 1;

		BookBean bk = ETApp.getInstance().getCacheBookbean();
		if (bk == null) {

			handler.sendMessageDelayed(msg, interval);
		} else if (bk.getDispStat() != BOOK_STAT_RECEIVED) {

			handler.sendMessageDelayed(msg, interval);
		} else {
//			hideNearByTaxis();
		}
	}

	/**
	 * 新的初始化设置调度列表数据
	 * 
	 * @param priceListCacheName
	 */
	private void initPrice(String priceListCacheName) {
		try {
			TipPrices tips = new TipPrices();
			JSONObject js = new JSONObject(ETApp.getInstance().getCacheString(priceListCacheName));
			tips.error = js.getInt("error");
			tips.errormsg = js.getString("errormsg");
			tips.onlinePayment = js.getJSONObject("datas").getInt("onlinePayment");
			JSONArray jsArray = js.getJSONObject("datas").getJSONArray("priceList");
			int length = jsArray.length();
			tips.priceList = new int[length];
			for (int i = 0; i < length; i++) {
				tips.priceList[i] = jsArray.getInt(i);
			}

			if (null != tips) {
				priceList = tips.priceList;
			}

			if (null != priceList && priceList.length > 0) {
				priceKey = priceList[0];
				priceValue = priceList[0] + "";
				tempstr = js.getJSONObject("datas").getString("msg");
				initScrollingTextView(tempstr);
				initPriveViewcell(priceList);
				onlinePayment = tips.onlinePayment;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化滚动提示加价的ScrollingTextView内容
	 * 
	 * @param price
	 */
	private void initScrollingTextView(String msg) {
		scrollingTextView.setText(msg);
	}

	/**
	 * 旧的初始化设置调度列表数据
	 */

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 补充起始地址附加信息的页面返回值
		if (requestCode == 1 && resultCode == RESULT_OK) {
			extraAddress = data.getStringExtra("extraAddress");
			if (null == startAddress) {
				startAddress = "";
			}
			editText_start.setText(startAddress + extraAddress);
		}
		// 下车地址返回值
		if (requestCode == 2 && resultCode == RESULT_OK) {
//			hideEndPoint();
			endAddress = data.getStringExtra("address");
			endLatitude = data.getIntExtra("lat", 0);
			endLongitude = data.getIntExtra("lng", 0);
			editText_end.setText(endAddress);
			moveCenter(endLatitude, endLongitude);

//			displayEndPoint(endLatitude, endLongitude, endAddress);
			// planStart2EndRoute(startLongitude, startLatitude, startAddress,
			// endLongitude, endLatitude, endAddress);

			// 路径规划
			clearRoutePlan();
			doRoutePlan();
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void initListeners() {

		map_traffic_state.setOnClickListener(new TrafficButtonListener());

		telButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String phoneNum = (String) v.getTag();
				if (!StringUtils.isEmpty(phoneNum)) {

					try {
						Window.callTaxi(MainActivityNew.this, phoneNum);
					} catch (Exception e) {
						e.printStackTrace();
						Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
						startActivity(intent);
					}

				}

			}
		});

		addrView.setOnEditOverListner(this);

		addrView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// if (selectMethod == SelectMethod.MAP) {// 上一次是通过地图选择则清空输入框
				// addrView.setText("");
				// }
				// selectMethod = SelectMethod.INPUT;
				return false;
			}
		});

		editText_start.setOnEditOverListner(new OnEditOverListner() {

			@Override
			public void OnEditOver(AddressEditView view) {
				mapView.postInvalidate();
				if (view.getSelectedGeoPoint() != null) {

					result = new GeoPointLable((int)view.getSelectedGeoPoint().getLatitudeE6(), (int)view.getSelectedGeoPoint().getLongitudeE6(), view.getText().toString(),"");

//					hideStartPoint();
//					controller.animateTo(new GeoPoint(result.getLat(), result.getLog())); //

					startLatitude = result.getLat();
					startLongitude = result.getLog();
					startAddress = result.getName();
//					displayStartPoint(result.getLat(), result.getLog());

					// planStart2EndRoute(startLongitude, startLatitude,
					// startAddress, endLongitude, endLatitude, endAddress);
					// 路径规划
					doRoutePlan();
					// 写入日志
					ActionLogUtil.writeActionLog(self, R.array.ontaxi_editText_start, startAddress);
				} else {
					result = new GeoPointLable(0, 0, view.getText().toString(), "");
				}
			}
		});
		editText_start.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}
		});

		if (session == null) {
			session = new SessionAdapter(this);
		}
		editText_start.setTextViewOnclickListener(MainActivityNew.this, session.get("_CITY_NAME"), 1, "cn.com.easytaxi.onetaxi.SearchAddressNewActivity");
		editText_end.setTextViewOnclickListener(MainActivityNew.this, session.get("_CITY_NAME"), 2, "cn.com.easytaxi.onetaxi.SearchAddressActivity");

		editText_end.setOnEditOverListner(new OnEditOverListner() {

			@Override
			public void OnEditOver(AddressEditView view) {

				if (view.getSelectedGeoPoint() != null) {
					result = new GeoPointLable((int)view.getSelectedGeoPoint().getLatitudeE6(), (int)view.getSelectedGeoPoint().getLongitudeE6(), view.getText().toString(),"");

					if (result.getLat() != 0 && result.getLog() != 0) {

//						hideEndPoint();
//						controller.animateTo(new GeoPoint(result.getLat(), result.getLog())); //
						endLatitude = result.getLat();
						endLongitude = result.getLog();
						endAddress = result.getName();

//						displayEndPoint(endLatitude, endLongitude, endAddress);

						// planStart2EndRoute(startLongitude, startLatitude,
						// startAddress, endLongitude, endLatitude, endAddress);
						// 路径规划
						doRoutePlan();
						// 写入日志
						ActionLogUtil.writeActionLog(self, R.array.ontaxi_editText_end, endAddress);
					}
					isSelectEndOK = true;
				} else {
					result = new GeoPointLable(0, 0, view.getText().toString(),"");
					isSelectEndOK = true;
				}

			}
		});
		editText_end.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}
		});

		// editText_end = (AddressEditView) findViewById(R.id.editText_end);

		map_current_pos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				moveCenter(getCurrentlat(), getCurrentLng());
				// 写入日志
				ActionLogUtil.writeActionLog(self, R.array.ontaxi_map_current_pos, "");
			}
		});
		map_end_pos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BookBean bk = ETApp.getInstance().getCacheBookbean();

				if (bk != null && bk.getEndLatitude() != 0 && bk.getEndLongitude() != 0 && (bk.getDispStat() != BOOK_STAT_NORMAL || bk.getDispStat() != BOOK_STAT_NOT_ENOUGH)) {
//					displayEndPoint(bk.getEndLatitude(), bk.getEndLongitude(), bk.getEndAddress());
					doRoutePlan();
					moveCenter(bk.getEndLatitude(), bk.getEndLongitude());
					return;
				}
				if (bk == null) {
					if (endLatitude != 0 && endLongitude != 0) {
//						displayEndPoint(endLatitude, endLongitude, endAddress);
						doRoutePlan();
						moveCenter(endLatitude, endLongitude);
					}
				}

			}
		});

		clear_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String info = editText_end.getText().toString();
				if (!StringUtils.isEmpty(info)) {
					editText_start.setText("");
				}

			}
		});
		clear_end.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String info = editText_end.getText().toString();
				if (!StringUtils.isEmpty(info)) {
					editText_end.setText("");
				}
			}
		});
		one_send_mode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String tag = (String) v.getTag();
				if (tag == null)
					return;
				if (tag.equalsIgnoreCase("voiceInput")) {

					setSendMode("wordInput");
					editText_end.setText("");
				} else {
					// displayMyTaxi(30670800, 104031700);
					setSendMode("voiceInput");
				}

			}
		});

		re_one_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				one_speak.setVisibility(View.GONE);
				progress_rcd_voice.setVisibility(View.VISIBLE);
				if (sendMode.equals("wordInput")) {
					sendBook(false, "");
				} else {
					sendBook(true, voiceCarType);
				}
			}
		});

		// 按住说话

		one_send.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionevent) {
				String tag = (String) view.getTag();
				if (tag == null || tag.equals("wordInput")) {
					return false;
				}

				if (payMode.isChecked() && priceKey == 0) {
					Toast.makeText(self, "线上付不能为0元", Toast.LENGTH_SHORT).show();
					return false;
				}

				int action = motionevent.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					playSound(R.raw.on, true, new OnPreparedListener() {

						@Override
						public void onPrepared(MediaPlayer mp) {
							// TODO Auto-generated method stub
							// startRecordSound();
						}
					}, new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							// startRecordSound();
						}
					});

					startRecordSound();
					// 显示录音界面
					voice_inpuuting_show.setVisibility(View.VISIBLE);
					changing_text_view.startChangingText();
					// AppLog.LogD("ACTION_DOWN -----------");

					// Toast.makeText(MainActivityNew.this, "开始录音",
					// Toast.LENGTH_SHORT).show();
					break;
				case MotionEvent.ACTION_MOVE:
					// AppLog.LogD("ACTION_MOVE -----------");
					// Toast.makeText(MainActivityNew.this, "录音中",
					// Toast.LENGTH_SHORT).show();
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					// 关闭录音界面
					try {
						stopRecordSound();
						// 关闭录音界面
						voice_inpuuting_show.setVisibility(View.GONE);
						changing_text_view.stopChangingText();
						showDialog(CONFIRM_TO_SEND_SOUND);
						// 写入日志
						ActionLogUtil.writeActionLog(self, R.array.ontaxi_re_one_send_voiceInput, "");
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				default:
					break;
				}

				return false;
			}
		});

		// 发送非语音订单
		one_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!ETApp.getInstance().isLogin()) {
					Intent intent = new Intent(MainActivityNew.this, RegisterActivity.class);
					startActivity(intent);
				} else {

					String tag = (String) arg0.getTag();
					// 按住录音
					if (tag == null)
						return;

					if (tag.equals("voiceInput")) {
						// Toast.makeText(MainActivityNew.this, " 语音发送信息",
						// 100).show();

						// beginInputVoice();
					} else {
						// Toast.makeText(MainActivityNew.this, " 文本发送信息",
						// 100).show();
						beginSendWord();
						// 写入日志
						ActionLogUtil.writeActionLog(self, R.array.ontaxi_re_one_send_wordInput, "");
					}
				}

			}
		});

	}

//	protected void planStart2EndRoute(int slng, int slat, String sName, int elng, int elat, String eName) {
//
//		AppLog.LogD("slng " + slng + " , elng " + elng);
//
//		if (elng != 0 && slng != 0) {
//			MKPlanNode start = new MKPlanNode();
//			start.pt = new GeoPoint(slat, slng);
//			start.name = sName;
//			MKPlanNode end = new MKPlanNode();
//			end.pt = new GeoPoint(elat, elng);
//			start.name = eName;
//
//			mMKSearch.drivingSearch(null, start, null, end);
//		}
//
//	}

	protected void beginSendWord() {
		/*
		 * String currentAddress = editText_start.getText().toString().trim();
		 * if (currentAddress.equals("当前位置")) { currentAddress = null; }
		 * 
		 * startAddress = currentAddress;
		 */
		endAddress = editText_end.getText().toString().trim();
		sendBook(false, "");
	}

	private byte[] makeSoundData() {
		byte[] datas = null;
		try {
			File file = new File(ETApp.getInstance().getMobileInfo().getSDCardPath() + SOUND_FILE_NAME_spx);
			datas = getFileData(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] all = new byte[datas.length + 21];
		all[0] = 0;
		String city = session.get("_CITY_NAME");
		// all[20] = 1;
		// all[19] = 0;
		try {
			byte[] cityByte = city.getBytes("utf-8");
			System.arraycopy(cityByte, 0, all, 1, cityByte.length);

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		System.arraycopy(datas, 0, all, 21, datas.length);
		return all;
	}

	public static final String[] childName = new String[] { "司机没来", "司机绕道", "司机迟到", "其他", "已打到车", "行程有变", "其他" };

	/**
	 * 根据评价类型，生成评价选项
	 * 
	 * @param type
	 *            1为待接单状态只能取消，2为已接单状态有评价跟取消，3为已超时只评价
	 * @return
	 * @return List<Group>
	 */
	public static List<Group> getGroupData(int type) {
		// TODO Auto-generated method stub
		List<Group> group = new ArrayList<Group>();
		// 非常满意，一般满意，不太满意，取消订单
		String[] groupName = new String[] { "非常满意", "一般满意", "不太满意", "取消订单" };

		if (type == 1) {
			// group4-->取消订单
			Group group4 = new Group();
			List<Child> childrenList4 = new ArrayList<Child>();

			Child child41 = new Child();
			child41.setHeadImage(null);
			child41.setName(childName[4]);
			child41.setIndex(5);

			Child child42 = new Child();
			child42.setHeadImage(null);
			child42.setName(childName[5]);
			child42.setIndex(6);

			Child child43 = new Child();
			child43.setHeadImage(null);
			child43.setName(childName[6]);
			child43.setIndex(7);

			childrenList4.add(child41);
			childrenList4.add(child42);
			childrenList4.add(child43);

			group4.setGroupName(groupName[3]);
			group4.setChildrenList(childrenList4);
			group4.setIndex(4);

			group.add(group4);
		} else if (type == 2) {
			// group1-->非常满意
			Group group1 = new Group();
			List<Child> childrenList1 = new ArrayList<Child>();

			group1.setGroupName(groupName[0]);
			group1.setChildrenList(childrenList1);

			// group2-->一般满意
			Group group2 = new Group();
			List<Child> childrenList2 = new ArrayList<Child>();

			group2.setGroupName(groupName[1]);
			group2.setChildrenList(childrenList2);

			// group3-->不太满意
			Group group3 = new Group();
			List<Child> childrenList3 = new ArrayList<Child>();

			Child child31 = new Child();
			child31.setHeadImage(null);
			child31.setName(childName[0]);
			child31.setIndex(1);

			// Child child32 = new Child();
			// child32.setHeadImage(null);
			// child32.setName(childName[1]);
			// child32.setIndex(2);

			Child child33 = new Child();
			child33.setHeadImage(null);
			child33.setName(childName[2]);
			child33.setIndex(3);

			Child child34 = new Child();
			child34.setHeadImage(null);
			child34.setName(childName[3]);
			child34.setIndex(4);

			childrenList3.add(child31);
			// childrenList3.add(child32);
			childrenList3.add(child33);
			childrenList3.add(child34);

			group3.setGroupName(groupName[2]);
			group3.setChildrenList(childrenList3);

			// group4-->取消订单
			Group group4 = new Group();
			List<Child> childrenList4 = new ArrayList<Child>();

			Child child41 = new Child();
			child41.setHeadImage(null);
			child41.setName(childName[4]);
			child41.setIndex(5);

			Child child42 = new Child();
			child42.setHeadImage(null);
			child42.setName(childName[5]);
			child42.setIndex(6);

			Child child43 = new Child();
			child43.setHeadImage(null);
			child43.setName(childName[6]);
			child43.setIndex(7);

			childrenList4.add(child41);
			childrenList4.add(child42);
			childrenList4.add(child43);

			group4.setGroupName(groupName[3]);
			group4.setChildrenList(childrenList4);
			group1.setIndex(1);
			group2.setIndex(2);
			group3.setIndex(3);
			group4.setIndex(4);
			group.add(group1);
			group.add(group2);
			group.add(group3);
			group.add(group4);
		} else {
			// group1-->非常满意
			Group group1 = new Group();
			List<Child> childrenList1 = new ArrayList<Child>();

			group1.setGroupName(groupName[0]);
			group1.setChildrenList(childrenList1);

			// group2-->一般满意
			Group group2 = new Group();
			List<Child> childrenList2 = new ArrayList<Child>();

			group2.setGroupName(groupName[1]);
			group2.setChildrenList(childrenList2);

			// group3-->不太满意
			Group group3 = new Group();
			List<Child> childrenList3 = new ArrayList<Child>();

			Child child31 = new Child();
			child31.setHeadImage(null);
			child31.setName(childName[0]);
			child31.setIndex(1);

			// Child child32 = new Child();
			// child32.setHeadImage(null);
			// child32.setName(childName[1]);
			// child32.setIndex(2);

			Child child33 = new Child();
			child33.setHeadImage(null);
			child33.setName(childName[2]);
			child33.setIndex(3);

			Child child34 = new Child();
			child34.setHeadImage(null);
			child34.setName(childName[3]);
			child34.setIndex(4);

			childrenList3.add(child31);
			// childrenList3.add(child32);
			childrenList3.add(child33);
			childrenList3.add(child34);

			group3.setGroupName(groupName[2]);
			group3.setChildrenList(childrenList3);
			group1.setIndex(1);
			group2.setIndex(2);
			group3.setIndex(3);
			group.add(group1);
			group.add(group2);
			group.add(group3);
		}

		return group;
	}

	private Dialog driverStopServeWindow() {
		LayoutInflater factory = LayoutInflater.from(MainActivityNew.this);
		Dialog dlg = new Dialog(MainActivityNew.this, R.style.Customdialog);
		final View dialogView = factory.inflate(R.layout.onetaxi_cancel_tell_dlg, null);
		dlg.setContentView(dialogView);

		Button pop_ts_driver = (Button) dialogView.findViewById(R.id.pop_ts_driver);

		pop_ts_driver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dismissDialog(DRIVER_CANCEL_SERVICE_DLG);
				replyerLatitude = 0;
				replyerLongitude = 0;
//				displayMyTaxi(replyerLatitude, replyerLongitude);
				resetState();
				dispatchStat(BOOK_STAT_NORMAL);
				moveCenter(getCurrentlat(), getCurrentLng());
				// setSendMode(sendMode);
				cancelRoutePlan();
				ispop = false;
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "4009902633"));
				startActivity(intent);
			}
		});
		Button pop_cancel_driver = (Button) dialogView.findViewById(R.id.pop_cancel_driver);
		pop_cancel_driver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// NewNetworkRequest.cancelBook(currentBookId, currentTaxiId,
				// 10, "司机取消订单", null);
				dismissDialog(DRIVER_CANCEL_SERVICE_DLG);
				replyerLatitude = 0;
				replyerLongitude = 0;
//				hideMyTaxi();
				editText_start.setText(startAddress);
				requestCurrentLoacionAddress(new FirstAddressCallback());
				dispatchStat(BOOK_STAT_NORMAL);
				moveCenter(getCurrentlat(), getCurrentLng());
				// setSendMode(sendMode);
				ispop = false;
				cancelRoutePlan();
			}
		});

		return dlg;
	}

	protected void cancelBookCheckInfo(String cancelInfo, boolean isConfirm, int value) {

		if (StringUtils.isEmpty(cancelInfo) && isConfirm) {
			Toast.makeText(MainActivityNew.this, "请填写评价内容", Toast.LENGTH_SHORT).show();
		} else {
			// 提交 我认为应该后台提交 不用显示给用 正在提交
			NewNetworkRequest.cancelBook(currentBookId, currentTaxiId, mobile, null);
			clearStat();
//			hideMyTaxi();
			startRefreshNearbyTaxi(10);
			// displayMyTaxi(replyerLatitude, replyerLongitude);
			dispatchStat(BOOK_STAT_NORMAL);
			moveCenter(getCurrentlat(), getCurrentLng());
			startLatitude = getCurrentlat();
			startLongitude = getCurrentLng();
//			hideEndPoint();
			// displayEndPoint(0, 0, "");
			// setSendMode(sendMode);
			cancelRoutePlan();
		}
	}

	private void clearStat() {
		ETApp.getInstance().getCacheBookbean().setDispStat(BOOK_STAT_NORMAL);
		replyerLatitude = 0;
		replyerLongitude = 0;
		endAddress = "";
		endLatitude = 0;
		endLongitude = 0;
		moveCenter(getCurrentlat(), getCurrentLng());
	}

	private void cancelRoutePlan() {
		AppLog.LogD("cancelRoutePlan");

//		hideRouteMy2Taxi();
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case CANEL_SERVICE_DLG:
			// return stopServeWindow();
			return null;
		case DRIVER_CANCEL_SERVICE_DLG:
			return driverStopServeWindow();

		case CONFIRM_TO_SEND_SOUND:

			return confirmToSendSound();

		case CONFIRM_TO_SUBMIT:
			return confirmToSubmit();

		case GPS_DLG:

			return createGpsDlg();
		case COMFIRM_ENDADDRESS:

			return createEndAddressDlg();

		default:

			break;
		}

		return super.onCreateDialog(id);
	}

	private Dialog createMoneySelectDlg() {
		MoneyWheelDlg dlg = new MoneyWheelDlg(this, R.style.CustomDialog);

		dlg.setMontey(priceList, priceValue);

		dlg.setListener(new MoneyDialogListener() {

			@Override
			public void onClick(View view) {

				switch (view.getId()) {

				case R.id.button_ok:
					AppLog.LogD("button_ok");
					Integer ppp = (Integer) view.getTag();
					if (ppp != null) {
						priceKey = ppp;
						priceValue = ppp + "";
						initScrollingTextView(tempstr);
					}

					// 写入日志
					ActionLogUtil.writeActionLog(self, R.array.ontaxi_button_money, priceValue);
					// AppLog.LogD(priceKey + " , " + priceValue);

					break;
				case R.id.button_cancel:
					AppLog.LogD("button_cancel");
					break;
				}

			}
		});
		return dlg;
	}

	private AlertDialog createEndAddressDlg() {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setCancelable(false);
		alert.setMessage("输入下车地点能更快打到车哦,您确定不输入下车地点提交订单!");
		alert.setTitle("提示");

		alert.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				dismissDialog(COMFIRM_ENDADDRESS);
				startSendBookService(false, "");

			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dismissDialog(COMFIRM_ENDADDRESS);

			}
		});

		return alert.create();

	}

	private Dialog createGpsDlg() {

		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.gps_dlg, null);

		final CheckBox checkBox_gps = (CheckBox) v.findViewById(R.id.checkBox_gps);

		return new AlertDialog.Builder(this).setView(v).setTitle("提高定位精度").setPositiveButton("设置", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				if (checkBox_gps.isChecked()) {
					ETApp.getInstance().saveCahceInt("gps_no", 1);
				} else {
					ETApp.getInstance().saveCahceInt("gps_no", 0);
				}

				Intent myIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(myIntent);

			}
		}).setNegativeButton("跳过", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				if (checkBox_gps.isChecked()) {
					ETApp.getInstance().saveCahceInt("gps_no", 1);
				} else {
					ETApp.getInstance().saveCahceInt("gps_no", 0);
				}
			}
		}).create();

	}

	private Dialog confirmToSubmit() {
		// TODO Auto-generated method stub

		return null;
	}

	/**
	 * 语音打车，车型选择
	 * @return
	 */
	private Dialog confirmToSendSound() {

		LayoutInflater factory = LayoutInflater.from(MainActivityNew.this);
		Dialog dlg = new Dialog(MainActivityNew.this, R.style.Customdialog);
		final View dialogView = factory.inflate(R.layout.onetaxi_confirt_sendsound_dlg, null);
		dlg.setContentView(dialogView);

		final RadioGroup carTypeGroup = (RadioGroup) dialogView.findViewById(R.id.cartype_radiogroup);
		Button pop_ts_driver = (Button) dialogView.findViewById(R.id.pop_send);

		pop_ts_driver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (isLogin()) {

					dismissDialog(CONFIRM_TO_SEND_SOUND);
					byte[] all = makeSoundData();

					ETApp.getInstance().setSoundData(all);

					int id = carTypeGroup.getCheckedRadioButtonId();
					switch (id) {
					case R.id.cartype_rb_one:
						voiceCarType = "面包车";
						mCarType = 1;
						break;
					case R.id.cartype_rb_two:
						mCarType = 2;
						voiceCarType = "厢式车";
						break;

					}

					// 暂时不传用车类型，仅做界面展示
					sendBook(true, voiceCarType);

					// 写入日志
					ActionLogUtil.writeActionLog(self, R.array.ontaxi_sendBook_voiceInput, "");

				} else {

					Intent intent = new Intent(MainActivityNew.this, RegisterActivity.class);
					startActivity(intent);
				}

				/*
				 * NewNetworkRequest.sendSound(all, new Callback<Object>() {
				 * 
				 * @Override public void handle(Object param) { if (param !=
				 * null) { JSONObject F = (JSONObject) param;
				 * Toast.makeText(MainActivityNew.this, F.toString(),
				 * Toast.LENGTH_SHORT).show(); //showDialog(CONFIRM_TO_SUBMIT);
				 * 
				 * } } });
				 */
			}
		});
		Button pop_cancel_driver = (Button) dialogView.findViewById(R.id.pop_cancel);
		pop_cancel_driver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dismissDialog(CONFIRM_TO_SEND_SOUND);

			}
		});

		return dlg;

	}

	/*
	 * protected void beginInputVoice() {
	 * 
	 * recognizerDialog.setListener(mRecoListener);
	 * recognizerDialog.setEngine(null, "grammar_type=abnf", text);
	 * recognizerDialog.show();
	 * 
	 * }
	 */

	protected void restet(LinearLayout parent) {
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			View v = parent.getChildAt(i);
			RadioButton cb = (RadioButton) v.findViewById(R.id.price_item);
			cb.setChecked(false);
		}

	}

	private Button re_one_send;

	private TextView info_one_send;// 提交

	private TextView info_re_one_send;

	// private TextView textView_sample;

	private TextView map_address_ext;

	private View re_one_send_parent;

	private ImageButton one_send_img;
	private LinearLayout layoutBanyungong;
	private TextView tvBanyungong;

	private ImageButton map_end_pos;

	private TextView textView_time;
	private RadioGroup rgCarType;
	String checkedType = "";

	private OnTouchListener bygTouchLis = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (v instanceof RadioButton) {
					if (((RadioButton) v).isChecked()) {
						int height = MainActivityNew.this.getWindowManager().getDefaultDisplay().getHeight();
						int[] location = new int[2];
						v.getLocationOnScreen(location);
						if (v.getId() == R.id.cartype_rb_one) {
							Window.showCarDetails(MainActivityNew.this, 1, height - location[1]);
						} else if (v.getId() == R.id.cartype_rb_two) {
							Window.showCarDetails(MainActivityNew.this, 2, height - location[1]);
						}
					}
				}
			}
			return false;
		}
	};

	private BaiduMap mBaiduMap;
	
	RouteLine route = null;

	@Override
	protected void initViews() {
		initMapViewStuff();
		initButton();
		initTextView();

		findViewById(R.id.cartype_rb_one).setOnTouchListener(bygTouchLis);
		findViewById(R.id.cartype_rb_two).setOnTouchListener(bygTouchLis);
		rgCarType = (RadioGroup) findViewById(R.id.cartype_radiogroup);
		rgCarType.clearCheck();
		rgCarType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
				case R.id.cartype_rb_one:
					checkedType = "面包车";
					break;
				case R.id.cartype_rb_two:
					checkedType = "厢式车";
					break;
				default:
					// -1
					checkedType = "";
					return;
				}
			}
		});
		payMode = (ToggleButton) findViewById(R.id.pay_mode);
		chatAddMoneyPager = (LinearLayout) findViewById(R.id.chat_addmoney_pager);
		scrollingTextView = (ScrollingTextView) findViewById(R.id.jishi_scrollingTextView);
		chat_addmoney_pager = (LinearLayout) findViewById(R.id.chat_addmoney_pager);
		textView_time = (TextView) findViewById(R.id.textView_time);
		map_end_pos = (ImageButton) findViewById(R.id.map_end_pos);
		map_address_ext = (TextView) findViewById(R.id.map_address_ext);
		// textView_sample = (TextView) findViewById(R.id.textView_sample);
		/*
		 * editText_end_btn = (ImageButton) findViewById(R.id.editText_end_btn);
		 * editText_start_btn = (ImageButton)
		 * findViewById(R.id.editText_start_btn);
		 */
		layoutBanyungong = (LinearLayout) findViewById(R.id.layout_jishi_banyungong);
		tvBanyungong = (TextView) findViewById(R.id.tv_jishi_banyungong);
		layoutBanyungong.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int height = MainActivityNew.this.getWindowManager().getDefaultDisplay().getHeight();
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				int defaultPerson = 0;
				try {
					defaultPerson = Integer.parseInt(tvBanyungong.getText().toString().substring(0, 1));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Window.selectBanyungong(MainActivityNew.this, height - location[1], defaultPerson, new Callback<Integer>() {

					@Override
					public void handle(Integer param) {
						// TODO Auto-generated method stub
						tvBanyungong.setText("" + param);
					}
				});
			}
		});

		one_send_img = (ImageButton) findViewById(R.id.one_send_img);
		re_one_send_parent = findViewById(R.id.re_one_send_parent);
		info_one_send = (TextView) findViewById(R.id.info_one_send);
		info_re_one_send = (TextView) findViewById(R.id.info_re_one_send);
		re_one_send = (Button) findViewById(R.id.re_one_send);
		advice_add_money = (TextView) findViewById(R.id.advice_add_money);
		// 发送中。。窗口
		progress_rcd_voice = findViewById(R.id.progress_rcd_voice);

		// 发送中
		// voice_rcd_hint_loading = findViewById(R.id.voice_rcd_hint_loading);

		// 搜索
		addrView = (AddressEditView) findViewById(R.id.search_button_panel);
		addrView.setHint("搜索附近的美食、地点、超市、银行...");
		// 返回到当前位置
		map_current_pos = (ImageButton) findViewById(R.id.map_current_pos);

		// 调度容器
		progress_alarm = findViewById(R.id.progress_alarm);
		progress_time = (ProgressBar) findViewById(R.id.progress_time);
		progress_info = (TextView) findViewById(R.id.progress_info);
		mBtnCancel = (ImageButton) findViewById(R.id.ib_onetaxi_cancel);
		mBtnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Window.confirm(MainActivityNew.this, "温馨提示", "系统正在为您调度车辆，您确定现在取消该订单吗？", new Callback<Object>() {

					@Override
					public void handle(Object param) {
						// TODO Auto-generated method stub
						cancelImmediateBook();
						
					}
				}, null);
			}
		});

		// 发单 、语音发单 上下车，选调度费 的容器
		one_speak = findViewById(R.id.one_speak);
		// 上下车容器
		linearLayout_start_end = findViewById(R.id.linearLayout_start_end);
		editText_start = (AddressEditView) findViewById(R.id.editText_start);
		clear_start = (ImageButton) findViewById(R.id.clear_start);
		editText_end = (AddressEditView) findViewById(R.id.editText_end);
		clear_end = (ImageButton) findViewById(R.id.clear_end);

		// 语音发送 按住说话
		one_send = (Button) findViewById(R.id.one_send);
		// one_send.setTag("voice");
		one_send_mode = (ImageButton) findViewById(R.id.one_send_mode);
		// one_send_mode.setTag("voice");
		setSendMode("wordInput");

		// editText_start.setText("当前位置");
		// 显示正在录音界面
		voice_inpuuting_show = (View) findViewById(R.id.voice_inpuuting_show);
		changing_text_view = (ChangingTextView) findViewById(R.id.changing_text_view);
		changing_text_view.setmTimeCallback(this);
	}

	private void setSendMode(String modeTag) {

		sendMode = modeTag;
		one_send_mode.setTag(modeTag);
		one_send.setTag(modeTag);
		advice_add_money.setVisibility(View.INVISIBLE);
		one_speak.setVisibility(View.VISIBLE);
		re_one_send_parent.setVisibility(View.GONE);

		if (modeTag.equalsIgnoreCase("voiceInput")) {

			linearLayout_start_end.setVisibility(View.GONE);
			// textView_sample.setText(R.string.chat_example);
			// linearLayout_start_end.setVisibility(View.GONE);
			one_send_mode.setImageResource(R.drawable.one_keyboard_btn);
			// one_send.setText(R.string.chat_pressed_say);
			info_one_send.setText(R.string.chat_pressed_say);

			one_send_img.setBackgroundResource(R.drawable.pic134);

		}
		if (modeTag.equalsIgnoreCase("wordInput")) {
			// textView_sample.setText(R.string.chat_example_word);
			// editText_start.getEditText().setHighlightColor(Color.rgb(255,
			// 168, 0));
			// editText_start.getEditText().setHint(R.string.currentplace);
			linearLayout_start_end.setVisibility(View.VISIBLE);
			editText_start.setText(startAddress);

			one_send_mode.setImageResource(R.drawable.one_voice_btn);
			// one_send.setText(R.string.chat_pressed_submit);
			BookBean bk = ETApp.getInstance().getCacheBookbean();
			if (bk != null && bk.getDispStat() == BOOK_STAT_REBOOK) {
				info_one_send.setText("重新提交");
			} else {
				info_one_send.setText(R.string.chat_pressed_submit);// 提交
			}

			one_send_img.setBackgroundResource(R.drawable.pic135);

		}
		infoLayout.setVisibility(View.GONE);

	}

	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();

		if (chat_addmoney_pager != null) {
			chat_addmoney_pager.invalidate();
		}
		super.onResume();

	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isDestroy = true;
		if (timer != null) {
			timer.cancel();
		}

		handler.removeMessages(1);
		handler.removeMessages(1);
		handler.removeMessages(1);
		handler.removeMessages(1);
		handler.removeMessages(2);
		handler.removeMessages(2);
		handler.removeMessages(2);
		handler.removeMessages(2);
		handler.removeMessages(3);
		handler.removeMessages(3);
		handler.removeMessages(3);
		handler.removeMessages(3);

//		hideEndPoint();
//		hideMyTaxi();
//		hideRouteMy2Taxi();
//		hideSearchPoint();
//		hideStartPoint();
		setTrafficOff();

		itemizedOverlayTool = null;
		if (mapView != null) {
//			mapView.destroy();
			mMKSearch.destroy();
			mapView.onDestroy();
		}

		if (bar != null) {
			bar.close();
		}

		unRegReceiver();

		ETApp.getInstance().setSendMode(sendMode);
		BookBean bk = ETApp.getInstance().getCacheBookbean();
		if (bk != null) {
			bk.setSendMode(sendMode);
		}
		extraAddress = "";
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			doBack();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 需要保存的状态
	/*
	 * private String startAddress = "";
	 * 
	 * private String endAddress = "";
	 * 
	 * int startLatitude = 0;
	 * 
	 * int startLongitude = 0;
	 * 
	 * int endLatitude = 0;
	 * 
	 * int endLongitude = 0;
	 * 
	 * protected int priceKey = 3;
	 * 
	 * protected String priceValue = "3元";
	 * 
	 * private String sendMode = "voiceInput";
	 */

	public static class UserBookStat {
		public int currentStat = 0;

		public String startAddress = "";

		public String endAddress = "";

		public int startLatitude = 0;

		public int startLongitude = 0;

		public int endLatitude = 0;

		public int endLongitude = 0;

		public int priceKey = 0;

		public String priceValue = "0";

		public String sendMode = "voiceInput";

		public int replyerLongitude;

		public int replyerLatitude;

		public UserBookStat(int currentStat, String sendMode, String startAddress, String endAddress, int startLatitude, int startLongitude, int endLatitude, int endLongitude, int priceKey, String priceValue, int replyerLatitude, int replyerLongitude) {

			UserBookStat.this.currentStat = currentStat;
			UserBookStat.this.sendMode = sendMode;
			UserBookStat.this.startAddress = startAddress;
			UserBookStat.this.endAddress = endAddress;
			UserBookStat.this.startLatitude = startLatitude;
			UserBookStat.this.startLongitude = startLongitude;
			UserBookStat.this.endLatitude = endLatitude;
			UserBookStat.this.endLongitude = endLongitude;
			UserBookStat.this.priceKey = priceKey;
			UserBookStat.this.priceValue = priceValue;
			UserBookStat.this.replyerLatitude = replyerLatitude;
			UserBookStat.this.replyerLongitude = replyerLongitude;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(sendMode).append(",").append(startAddress).append(",").append(startLatitude).append(",").append(startLongitude).append(",").append(endLatitude).append(",").append(endLongitude).append(",").append(priceKey).append(",").append(priceValue).append(",").append(replyerLatitude).append(",").append(replyerLongitude).append(",");

			return super.toString();
		}

	}

	private void saveState(UserBookStat bookstat) {
		// ETApp.getInstance().setOneTaxiCache(bookstat);
	}

	// 初始化///////////////////////////////////////////

	// 初始化状态

//	private void setMyLocation(int lat, int lng, float radius, float derect, boolean isCenter) {
//
//		AppLog.LogD("setMyLocation = " + lat);
//		if (lat != 0 && lng != 0) {
//			if (isCenter) {
////				controller.setCenter(new GeoPoint(lat, lng));
//			}
//			myLocData.latitude = lat / 1e6;
//			myLocData.longitude = lng / 1e6;
//			myLocData.accuracy = radius;
//			myLocData.direction = derect;
//			myLocationOverlay.setData(myLocData);
//			// 显示当前位置的详细信息
//			if (myLocData.latitude > 0) {
//				if (startAddress.length() > 0) {
//					float i = (float) dm.heightPixels / 800.0f;
//					Bitmap b = BitmapUtil.writeWordsOnBitmap(MainActivityNew.this.getBaseContext(), R.drawable.my_adrress_ontaxi, "我在" + "  " + startAddress + "  " + "附近", (int) (22 * i), Color.BLACK, (int) (22 * i), (int) (22 * i - 3), (int) (15 * i), (int) (15 * i), 3);
//					Bitmap arrowScale = BitmapUtil.scaleBitmap(MainActivityNew.this.getBaseContext(), R.drawable.my_address_arrow, (int) (22 * 2 * i), (int) (22 * 2 * i));
//					Bitmap c = BitmapUtil.createBitmapWithArrow(b, arrowScale, (int) (14 * i));
////					pop.showPopup(BitmapUtil.createBitmapShadow(Color.BLACK, c), new GeoPoint(lat, lng), 32);
//				}
//			}
////			mapView.refresh();
//
//		}
//
//		Message msg = Message.obtain();
//		msg.arg1 = 3;
//		msg.what = 3;
//
//		handler.sendMessageDelayed(msg, 15000);
//
//	}

	// 初始化地图
	private void initMapViewStuff() {

		mapView = (MapView) findViewById(R.id.map);
		mBaiduMap = mapView.getMap();
		// 地图点击事件处理
		mBaiduMap.setOnMapClickListener(MainActivityNew.this);
//		mapView.setBuiltInZoomControls(false);
//		mapView.setDoubleClickZooming(true);
//		mapView.getController().enableClick(true);
//		mapView.setSatellite(false);
//		mapView.setOnTouchListener(null);
//		controller = mapView.getController();
//		controller.setZoom(DEFAULT_ZOOM_FACTOR);
		initOverlay();

		// 地图搜索功能
		mMKSearch = RoutePlanSearch.newInstance();
		// mMKSearch.init(mBMapMan, new MySearchListener());

		// 显示当前位置的信息框
//		pop = new PopupOverlay(mapView, null);
		// 路径规划
//		myMKSearchLis = new MySearchListener(MainActivityNew.this, mapView);
		
		
		 // 初始化搜索模块，注册事件监听
		mMKSearch = RoutePlanSearch.newInstance();
		mMKSearch.setOnGetRoutePlanResultListener(this);
		
//		mMKSearch.init(mBMapMan, myMKSearchLis);
	}

	// 按钮初始化
	private void initButton() {
		map_traffic_state = (CheckBox) findViewById(R.id.map_traffic_state);
		map_traffic_state.setChecked(false);
		map_traffic_state.setTag(TRAFFIC_ON);
		setTrafficOff();

		telButton = (Button) findViewById(R.id.map_tel_taxi);
		cancelButton = (Button) findViewById(R.id.map_cancel);

		cancelButton.setOnClickListener(new CancelButtonListener());

		bar = new TitleBar(MainActivityNew.this);

		bar.setTitleName("即时叫车");
		bar.switchToCityButton();
		bar.getRightCityButton().setVisibility(View.GONE);
		bar.getRightHomeButton().setVisibility(View.GONE);
		bar.setBackCallback(new Callback<Object>() {

			@Override
			public void handle(Object param) {

				doBack();
			}
		});

		// callButton.setEnabled(false);
	}

	private void setTrafficOff() {

		map_traffic_state.setChecked(false);
		if (mapView != null) {

//			mapView.setTraffic(false);
		}
	}

	private void initTextView() {
		addressTextView = (TextView) findViewById(R.id.map_address);
		taxiNumber = (TextView) findViewById(R.id.map_taxi_num);
		driverName = (TextView) findViewById(R.id.map_taxi_driver);
		company = (TextView) findViewById(R.id.map_taxi_company);
		taxiInfoView = findViewById(R.id.map_taxi_info);
		infoLayout = findViewById(R.id.map_info_layout);
	}

	// 初始化广播监听
	@Override
	protected void regReceiver() {
		if (onBookAction == null) {
			onBookAction = new OnBookActionReceiver();
			registerReceiver(onBookAction, new IntentFilter(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP));
		}

	}

	@Override
	protected void unRegReceiver() {

		if (onBookAction != null) {
			unregisterReceiver(onBookAction);
			onBookAction = null;
		}
	}

	// 初始化层
	private void initOverlay() {
//		overlays = mapView.getOverlays();

		myLocData = new LocationClient(this);
		myLocData.registerLocationListener(myListener);
		
		LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        myLocData.setLocOption(option);
        myLocData.start();

		HashMap<String, Drawable> markers = new HashMap<String, Drawable>(5);
		markers.put("taxi", getResources().getDrawable(R.drawable.mycar));
		markers.put("mytaxi", getResources().getDrawable(R.drawable.mycar));
		markers.put("start", getResources().getDrawable(R.drawable.my));
		markers.put("end", getResources().getDrawable(R.drawable.mydest));
		markers.put("search", getResources().getDrawable(R.drawable.pin_red));

		
		// displayMyTaxi(30670800, 104032300);
	}
	 /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
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
	// 取消订单
	private class CancelButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			// 直接跳详情
			Intent i = new Intent(MainActivityNew.this, NewBookDetail.class);
			i.putExtra("bookId", currentBookId);
			MainActivityNew.this.startActivity(i);
			MainActivityNew.this.finish();
		}
	}

	// 附近
	private class TrafficButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			Integer off_on = (Integer) v.getTag();
			if (off_on == TRAFFIC_ON) {
				setTrafficOn();
				v.setTag(TRAFFIC_OFF);
			} else {
				setTrafficOff();
				v.setTag(TRAFFIC_ON);
			}

			// 写入日志
			ActionLogUtil.writeActionLog(self, R.array.ontaxi_map_traffic_state, "");
		}
	}

	private void checkNetwork() {
		if (!new NetChecker(MainActivityNew.this).checkNetwork()) {
			Toast.makeText(MainActivityNew.this, R.string.network_notgood, Toast.LENGTH_SHORT).show();
		}
	}

	// //////////////////////////////////////////
//	private class MySearchListener implements MKSearchListener {
//
//		private Activity mActivity;
//		private MapView mMapView;
//
//		public MySearchListener(Activity mActivity, MapView mMapView) {
//			super();
//			this.mActivity = mActivity;
//			this.mMapView = mMapView;
//		}
//
//		public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
//
//			// 错误号可参考MKEvent中的定义
//			if (error != 0 || res == null) {
//				Toast.makeText(mActivity, "温馨提示：未找到路径规划！", Toast.LENGTH_SHORT).show();
//				AppLog.LogD("温馨提示：未找到路径规划！");
//				return;
//			}
//
//			AppLog.LogD("找到路劲");
//			MKRoute route = res.getPlan(0).getRoute(0);
//			int distance = route.getDistance();
//			AppLog.LogD("xyw", "距离提示：" + distance);
//			if (distance > 100000) {
//				AppLog.LogD("xyw", "距离提示：" + distance);
//				Toast.makeText(mActivity, "距离大于100公里，不显示路径规划", Toast.LENGTH_LONG).show();
//			} else {
//				routeOverlay = new RouteOverlay(mActivity, mMapView);
//				routeOverlay.setData(route);
//				mMapView.getOverlays().add(routeOverlay);
//				mMapView.refresh();
//				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
//				mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
//
//				GeoPoint pStart = res.getStart().pt;
//				GeoPoint pEnd = res.getEnd().pt;
//
//				int midLat = (pStart.getLatitudeE6() + pEnd.getLatitudeE6()) / 2;
//				int midLng = (pStart.getLongitudeE6() + pEnd.getLongitudeE6()) / 2;
//				mMapView.getController().animateTo(new GeoPoint(midLat, midLng));
//			}
//		}
//
//		public void onGetAddrResult(final MKAddrInfo result, int iError) {
//
//			try {
//
//				addressTextView.setText(result.strAddr);
//
//			} catch (Throwable e) {
//
//				e.printStackTrace();
//			}
//		}
//
//		public void onGetPoiResult(MKPoiResult result, int type, int iError) {
//		}
//
//		public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {
//		}
//
//		public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
//
//			if (result != null) {
//
//				MKRoute mroute = result.getPlan(0).getRoute(0);
//
//				// AppLog.LogD("000" + mroute.toString()+",");
//				displayRouteMy2Taxi(mroute);
//			}
//
//		}
//
//		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
//		}
//
//		@Override
//		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
//
//		}
//
//		@Override
//		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
//
//		}
//
//	}

	// 移动地图，使地图的中心位置为指定经纬度
	private void moveCenter(int lat, int lng) {
		if (lat != 0 && lng != 0) {
			GeoPoint point = new GeoPoint(lat, lng);
//			controller.animateTo(point);
		}
	}

//	private void planRoute(int slng, int slat, int elng, int elat) {
//		AppLog.LogD("planRoute ----" + slng + ", " + slat + " ..." + elng + ", " + elat);
//		MKPlanNode start = new MKPlanNode();
//		// start.pt = new GeoPoint(30592632, 104061340);
//		start.pt = new GeoPoint(slat, slng);
//		MKPlanNode end = new MKPlanNode();
//		// end.pt = new GeoPoint(30690378, 104100446);
//		end.pt = new GeoPoint(elat, elng);
//		mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
//		// mMKSearch.drivingSearch(null, start, null, end);
//		mMKSearch.walkingSearch(null, start, null, end);
//	}

	// 状态转换//////////////////////////////////

	// 移到初始状态
	private void resetState() {

	}

	private void resetFlag() {
		editText_end.setText("");
		editText_start.setText("");
		startAddress = "";
		endAddress = "";

		startLatitude = p_lat;
		endLongitude = p_lng;
		startLatitude = p_lat;
		endLatitude = p_lat;

		isSelectEndOK = false;
		isSelectStartOK = false;
	}

	// 菜单///////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// menu.add(0, 1, 1, "打车历史").setIcon(R.drawable.menu_icon_history);
		// menu.add(0, 2, 2, "参数设置").setIcon(R.drawable.menu_icon_setting);
		return true;
	}

	protected void comfirmGetOn(String msg) {

		Toast.makeText(MainActivityNew.this, "我已经上车了", Toast.LENGTH_SHORT).show();
		resetState();

	}

	/**
	 * This is simply adding JSON Object to Array
	 * 
	 * @param cols
	 * @param row
	 * @return
	 */
	public static JSONObject AddJSONObject(JSONArray cols, JSONArray row) {
		JSONObject ret = new JSONObject();
		for (int i = 0; i < cols.length(); i++) {
			try {
				ret.put((String) cols.get(i), row.get(i));
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return ret;
	}

	private void setTrafficOn() {
		map_traffic_state.setChecked(true);
//		mapView.setTraffic(true);
	}

	protected void sendBook(boolean isSound, String carType) {

		/**刷新全局变量值*/
		int id = rgCarType.getCheckedRadioButtonId();
		switch (id) {
		case R.id.cartype_rb_one:
			mCarType = 1;
			break;
		case R.id.cartype_rb_two:
			mCarType = 2;
			break;
		}

		try {
			mNeedPerson = Integer.parseInt(tvBanyungong.getText().toString().substring(0, 1));
		} catch (Exception e) {
			mNeedPerson = 0;
		}
		
		/**刷新全局变量值 end*/
		
		

		if (!isCanBook) {
			Toast.makeText(this, "服务端异常，不允许下单", Toast.LENGTH_LONG).show();
			return;
		}

		NetChecker nc = NetChecker.getInstance(ETApp.getInstance());
		if (!nc.isAvailableNetwork()) {
			dispatchStat(BOOK_NET_INVALIDBLE);
		} else {

			if (isSound) {
				startSendBookService(isSound, carType);
			} else {
				if (rgCarType.getCheckedRadioButtonId() == -1) {
					Toast.makeText(MainActivityNew.this, "请选择车型", Toast.LENGTH_LONG).show();
					return;
				}

				if (TextUtils.isEmpty(endAddress)) {
					showDialog(COMFIRM_ENDADDRESS);
				} else {
					startSendBookService(isSound, "");
				}
			}
		}
	}

	/**
	 * 开始发送订单 给service
	 * 
	 * @param isSound
	 */
	private void startSendBookService(boolean isSound, String carType) {
		if (payMode.isChecked() && priceKey == 0) {
			Toast.makeText(self, "线上付不能为0元", Toast.LENGTH_SHORT).show();
			return;
		}

		User user = ETApp.getInstance().getCurrentUser();
		Intent intent = new Intent(this, OneBookService.class);
		intent.setAction(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD);
		intent.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_REQ, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_REQ);
		mobile = user.getPhoneNumber("_MOBILE");
		BookBean bb = new BookBean();
		bb.setPassengerPhone(user.getPhoneNumber("_MOBILE")); // [必填]*
		bb.setPassengerName(StringUtils.isEmpty(user.getUserNickName()) ? mobile : user.getUserNickName());
		bb.setPassengerId(user.getPassengerId() > 10000 ? user.getPassengerId() : Long.valueOf(mobile));
		bb.setType(1);// [可选]订单类型
		bb.setSource(1);
		bb.setSourceName(BookConfig.ClientType.CLIENT_TYPE_PASSENGER + ETApp.getInstance().getMobileInfo().getVerisonCode());
		bb.setOnlinePayment(payMode.isChecked());
		if (null == startAddress) {
			startAddress = "";
		}
		bb.setStartAddress(startAddress + extraAddress);// startAddress为gps定位地址
		// extraAddress为用户添加的地址详细描述
		bb.setStartLatitude(startLatitude);
		bb.setStartLongitude(startLongitude);

		if (TextUtils.isEmpty(endAddress)) {
			endAddress = checkedType;
		} else {
			endAddress = endAddress.replace("面包车", "");
			endAddress = endAddress.replace("厢式车", "");
			endAddress = endAddress.replace("，", "");
			endAddress = endAddress + "，" + checkedType;
		}

		bb.setEndAddress(endAddress);
		bb.setEndLatitude(endLatitude);
		bb.setEndLongitude(endLongitude);

		// bb.setState(state)可选
		// bb.setPriceMode(priceMode);可选
		bb.setPrice(priceKey);
		String city = session.get("_CITY_NAME");
		String cityId = session.get("_CITY_ID");

		bb.setCityId(Integer.valueOf(cityId));
		bb.setCityName(city);

		bb.setDispStat(BOOK_STAT_SENDING);

		if (isSound) {
			bb.setEndAddress(carType);
			intent.putExtra("appsound", true);
		} else {
			intent.putExtra("appsound", false);
		}
		ETApp.getInstance().setCacheBookbean(bb);
		intent.putExtra("bookbean", bb);
		dispatchStat(BOOK_STAT_SENDING);
		startService(intent);
	}

	boolean isReceiverTaxiResponse = false;

	protected void startWait(long bookId, long timeout) {

		int displayTime = (int) (timeout);
		progress_time.setMax(displayTime);

	}

	int lat_cars = 0;
	int lng_cars = 0;

	public class FirstAddressCallback extends Callback<String> {

		@Override
		public void handle(String param) {
			if (param != null) {
				String startAddressTmp = String.valueOf(param);
				if (startAddressTmp.equalsIgnoreCase(startAddress)) {

				} else {
					currentAddress = startAddressTmp;
					startAddress = startAddressTmp;
					editText_start.setText(startAddressTmp);
				}

				startLatitude = getCacheStartLat();
				startLongitude = getCacheStartLng();
				// editText_start.getEditText().setText(String.valueOf(param));
			}

		}

	}

	GeoPointLable result;

	@Override
	public void OnEditOver(AddressEditView view) {
		mapView.postInvalidate();

		if (view.getSelectedGeoPoint() != null) {
			this.result = new GeoPointLable((int)view.getSelectedGeoPoint().getLatitudeE6(), (int)view.getSelectedGeoPoint().getLongitudeE6(), view.getText().toString(),"");
			moveCenter(result.getLat(), result.getLog());
//			hideSearchPoint();
//			displaySearchPoint(result.getLat(), result.getLog());
		} else {
			this.result = new GeoPointLable(0, 0, view.getText().toString(),"");
		}
	}

	void setDriverInfo(BookBean bookBean) {

		// 显示司机电话
		telButton.setVisibility(View.VISIBLE);
		String phoneNum = bookBean.getReplyerPhone();
		if (!StringUtils.isEmpty(phoneNum)) {
			telButton.setClickable(true);
			telButton.setTag(phoneNum);
		} else {
			// Toast.makeText(MainActivityNew.this, "司机的号码有误，请联系客服!",
			// Toast.LENGTH_SHORT).show();
			telButton.setClickable(false);
		}

		String strCarNumber = "车牌号码：" + bookBean.getReplyerNumber();
		taxiNumber.setText(strCarNumber);
		driverName.setText("司机姓名：" + bookBean.getReplyerName());
		String strCompany = "所属公司：" + bookBean.getReplyerCompany();
		company.setText(strCompany);
		taxiInfoView.setVisibility(View.VISIBLE);

	}

//	public void displayRouteStart2End(MKRoute mroute) {
//		if (mroute == null) {
//			AppLog.LogD("---- route is null");
//			return;
//		}
//
//		if (routeOverlayStart2End != null) {
//			AppLog.LogD("---- 11111");
//
//			routeOverlayStart2End.mRoute.clear();
//			// mapView.getOverlays().remove(routeOverlayStart2End);
//		} else {
//
//			AppLog.LogD("---- 22222222222");
//			routeOverlayStart2End = new RouteOverlay(MainActivityNew.this, mapView);
//			mapView.getOverlays().add(routeOverlayStart2End);
//		}
//		routeOverlayStart2End.setData(mroute);
//		// controller.zoomToSpan(mroute.getStart().getLatitudeE6(),
//		// mroute.getEnd().getLongitudeE6());
//		controller.zoomToSpan(routeOverlayStart2End.getLatSpanE6(), routeOverlayStart2End.getLonSpanE6());
//		mapView.refresh();
//	}

//	protected void hideRouteStart2End() {
//		if (routeOverlayStart2End != null) {
//
//			AppLog.LogD("cancelRoutePlan ---routeOverlayStart2End--");
//
//			routeOverlayStart2End.mRoute.clear();
//			mapView.getOverlays().remove(routeOverlayStart2End);
//			mapView.refresh();
//		}
//	}

//	protected void displayRouteMy2Taxi(MKRoute route) {
//
//		if (route == null) {
//			AppLog.LogD("---- route is null");
//			return;
//		}
//
//		if (routeOverlayMy2Taxi != null) {
//			AppLog.LogD("+++++++++++++ ---routeOverlayMy2Taxi--");
//
//			// mapView.getOverlays().remove(routeOverlayMy2Taxi);
//			routeOverlayMy2Taxi.mRoute.clear();
//		} else {
//			routeOverlayMy2Taxi = new RouteOverlay(MainActivityNew.this, mapView);
//			mapView.getOverlays().add(routeOverlayMy2Taxi);
//		}
//		routeOverlayMy2Taxi.setData(route);
//
//		mapView.refresh();
//	}

//	protected void hideRouteMy2Taxi() {
//		if (routeOverlayMy2Taxi != null) {
//
//			AppLog.LogD("cancelRoutePlan -----");
//
//			routeOverlayMy2Taxi.mRoute.clear();
//			mapView.getOverlays().remove(routeOverlayMy2Taxi);
//			mapView.refresh();
//		}
//	}
//
//	protected void displayStartPoint(int lat, int lng) {
//		itemizedOverlayTool.dispFlag(lat, lng, "start");
//	}
//
//	protected void hideStartPoint() {
//		itemizedOverlayTool.hideFlag("start");
//	}
//
//	protected void displayEndPoint(int endLat, int endLng, String address) {
//		if (endLat != 0 && endLng != 0) {
//			map_end_pos.setVisibility(View.VISIBLE);
//			itemizedOverlayTool.dispFlag(endLat, endLng, "end");
//		} else {
//			map_end_pos.setVisibility(View.INVISIBLE);
//
//		}
//	}
//
//	protected void displaySearchPoint(int lat, int lng) {
//		itemizedOverlayTool.dispFlag(lat, lng, "search");
//	}
//
//	protected void hideSearchPoint() {
//		itemizedOverlayTool.hideFlag("search");
//	}
//
//	protected void hideEndPoint() {
//		map_end_pos.setVisibility(View.INVISIBLE);
//		itemizedOverlayTool.hideFlag("end");// (endLat, endLng, "end");
//	}
//
//	private void displayNearByTaxis(JSONArray array) {
//		if (itemizedOverlayTool == null) {
//			return;
//		} else {
//
//			itemizedOverlayTool.dispNearbyTaxis(array);
//		}
//	}
//
//	private void hideNearByTaxis() {
//		itemizedOverlayTool.hideNearbyTaxis();
//	}
//
//	protected void displayMyTaxi(int lat, int lng) {
//		hideNearByTaxis();
//		itemizedOverlayTool.dispFlag(lat, lng, "mytaxi");
//	}
//
//	protected void hideMyTaxi() {
//		itemizedOverlayTool.hideFlag("mytaxi");
//	}

	public class MarkCallback extends Callback<Object> {

		@Override
		public void handle(Object param) {
			if (param != null) {
				JSONObject json = (JSONObject) param;
				try {
					int distance = json.getInt("distance");
					int priceOrg = json.getInt("price");

					int course = json.getInt("course");
					int time = json.getInt("time");
					int wait = json.getInt("wait");
					AppLog.LogD(json.toString());

					// String distanceString =
					// InfoTool.friendlyDistance(distance);
					String distanceString = BookUtil.getDecimalNumber(distance) + "公里";

					String disp = getResources().getString(R.string.one_taxi_mark_info, new String[] { distanceString, String.valueOf(wait) });

					if (course == 0 && time == 0 && priceOrg == 0 && priceKey == 0) {
						map_address_ext.setVisibility(View.GONE);
					} else {
						map_address_ext.setVisibility(View.VISIBLE);
					}

					String priceInfo = priceValue;
					if (priceKey >= 0) {
						priceInfo += "元";
					}
					String distanceCourse = InfoTool.friendlyDistance(course);

					String dispext = getResources().getString(R.string.one_taxi_mark_info_ext, new String[] { String.valueOf(distanceCourse), String.valueOf(time), String.valueOf(priceOrg), priceInfo });

					// infoLayout.setVisibility(View.VISIBLE);
					addressTextView.setText(disp);
					map_address_ext.setText(dispext);

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {

			}

		}
	}

	public void dispatchMessag(int msgId, JSONObject json) throws JSONException {

		long bookId = json.getLong("bookId");

		if (msgId == cn.com.easytaxi.platform.common.common.Const.UDP_BOOK_TAXI_SCHEDULE) {

			progress_info.setText(json.getString("msg"));

		}

		if (msgId == 0xFF0006) {
			if (!ispop && bookId == currentBookId) {
				dispatchStat(BOOK_STAT_DRIVER_CANCELBOOK);
				// showDialog(DRIVER_CANCEL_SERVICE_DLG);
				ispop = true;
			}
		}
	}

	boolean ispop = false;

	private void playSound(final int resId, boolean isSound, final OnPreparedListener startlistener, final OnCompletionListener endListener) {
		if (isSound) {
			ToolUtil.playSound(self, resId, startlistener, endListener);
		}
	}

	boolean test = false;
	boolean isReceiverResp = false;

	public class OnBookActionReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int cmd = intent.getIntExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, 0);
			switch (cmd) {
			// 订单提交成功
			case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_OK:
				BookBean bk = ETApp.getInstance().getCacheBookbean();
				currentBookId = bk.getId();
				dispatchStat(BOOK_STAT_WAITINGRESP);
				startWait(currentBookId, bk.getTimeOut());
				break;

			// 订单提交失败
			case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED:
				playSound(R.raw.playend, true, null, null);
				dispatchStat(BOOK_STAT_REBOOK);
				break;

			// 订单提交成功 等待司机接单中...
			case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_WAITTING:

				// AppLog.LogD("progress :  " + intent.getIntExtra("progress",
				// 0));

				if (intent.getLongExtra("bookId", currentBookId) == currentBookId) {
					int progress = intent.getIntExtra("progress", 0);
					progress_time.setProgress(progress);
					textView_time.setText(InfoTool.friendTime(progress));

				}
				break;

			// 订单提交后一直等待司机接单，但是由于没人接单，到时订单超时
			case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_TIMEOUT:
				if (intent.getLongExtra("bookId", currentBookId) == currentBookId) {
					// AppLog.LogD(tag, "订单超时了 需要用户加价重发" + currentBookId);
					progress_time.setProgress(0);
					playSound(R.raw.playend, true, null, null);
					// ETApp.getInstance().getCacheBookbean().stopWait();
					dispatchStat(BOOK_STAT_REBOOK);
				}
				break;

			// 订单提交后 ，等待司机接单，司机接成功接单后...
			case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_JIEDAN_OK:
				BookBean okBookbean = (BookBean) intent.getSerializableExtra("bookbean");
				if (okBookbean == null) {
					okBookbean = ETApp.getInstance().getCacheBookbean();
				}

				dispatchStat(BOOK_STAT_RECEIVED);

				break;

			// 订单在提交，等待，接单等过程中返回的UDP消息
			case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_UDP_RESP:
				int msgId = intent.getIntExtra("msgId", 0);
				byte[] message = intent.getByteArrayExtra("message");
				handleUdp(msgId, message);
				break;
			default:
				break;
			}

		}

	}

	protected void handleUdp(int msgId, byte[] message) {

		try {
			AppLog.LogD(" udp : " + msgId + " , msg : " + new String(message, "utf-8"));
			String udpReply = new String(message, "utf-8");

			JSONObject json = new JSONObject(udpReply);
			dispatchMessag(msgId, json);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	PcmRecorder recorderInstance = null;

	private void startRecordSound() {
		if (recorderInstance == null) {
			recorderInstance = new PcmRecorder();
			Thread th = new Thread(recorderInstance);
			th.start();
		}
		recorderInstance.setRecording(true);
	}

	private void stopRecordSound() {
		recorderInstance.setRecording(false);
		recorderInstance = null;
		playSound(R.raw.off, true, null, null);
	}

	public class NearByCallbacK extends Callback<Object> {
		@Override
		public void handle(Object param) {
			if (param != null) {
				try {
					JSONObject json = (JSONObject) param;
					if (json.isNull("interval")) {
						nearByCarRefresInterval = InfoConfig.NEARBY_TAXI_REFRESH_INTERVAL;
					} else {
						nearByCarRefresInterval = ((JSONObject) param).getLong("interval");
					}
					JSONArray array = ((JSONObject) param).getJSONArray("taxis");
					if (isDestroy) {
						return;
					} else {

//						displayNearByTaxis(array);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			long interval = nearByCarRefresInterval;

			startRefreshNearbyTaxi(interval);

		}

	}

	public class TaxiLocationCallback extends Callback<JSONObject> {

		@Override
		public void handle(JSONObject param) {
			if (param != null) {
				try {
					int latitude = param.getInt("latitude");
					int longitude = param.getInt("longitude");
//					displayMyTaxi(latitude, longitude);
					BookBean bookBean = ETApp.getInstance().getCacheBookbean();
					if (latitude != 0 && longitude != 0) {

//						planRoute(bookBean.getStartLongitude(), bookBean.getStartLatitude(), longitude, latitude);
					}

				} catch (Exception e) {

				}

			}

		}
	}

	/**
	 * 没有司机接单时，取消即时订单
	 * 
	 * @return void
	 */
	private void cancelImmediateBook() {
		cancelBookCheckInfo("", false, 500);
		progress_alarm.setVisibility(View.GONE);
	}

	/**
	 * 起止地点路径规划
	 * 
	 * @param cityName
	 * @param bean
	 * @return void
	 */
	public void requestRoutePlan(String cityName, BookBean bean) {
		try {
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
			
			// 设置起终点信息，对于tranist search 来说，城市名无意义
	        PlanNode stNode = PlanNode.withCityNameAndPlaceName("四川", startAddr);
	        PlanNode enNode = PlanNode.withCityNameAndPlaceName("四川", endAddr);
			
//			mMKSearch.transitSearch((new TransitRoutePlanOption())
//                    .from(stNode).city("山西").to(enNode));
			 mMKSearch.drivingSearch((new DrivingRoutePlanOption())
	                    .from(stNode).to(enNode));

//			MKPlanNode startNode = new MKPlanNode();
//			MKPlanNode endNode = new MKPlanNode();
//
//			// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
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
	 * 清空起始地址路径规划
	 */
	public void clearRoutePlan() {
		if (route != null) {
			route = null;
			routeOverlay.removeFromMap();
			mBaiduMap.clear();
		}
//		if (routeOverlay != null) {
//			mapView.getOverlays().remove(routeOverlay);
//			mapView.refresh();
//		}
	}

	/**
	 * 路径规划
	 */
	public void doRoutePlan() {
		BookBean bean = new BookBean();
		if (StringUtils.isEmpty(endAddress) && endLatitude == 0 && endLongitude == 0) {
			bean = ETApp.getInstance().getCacheBookbean();
		} else {
			bean.setStartAddress(startAddress);
			bean.setStartLatitude(startLatitude);
			bean.setStartLongitude(startLongitude);
			bean.setEndAddress(endAddress);
			bean.setEndLatitude(endLatitude);
			bean.setEndLongitude(endLongitude);
		}
		requestRoutePlan(session.get("_CITY_NAME"), bean);
	}

	public DisplayMetrics getDisplayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	@Override
	public void timeOver() {
		handler.sendEmptyMessage(4);
	}

	private class LoadBooks extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			JsonObject json = new JsonObject();
			json.addProperty("action", "scheduleAction");
			json.addProperty("method", "getActiveBookListByPassenger");
			json.addProperty("passengerId", getPassengerId());
			json.addProperty("size", 10);
			json.addProperty("startId", 0);
			try {
				byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
				if (response != null && response.length > 0) {
					JSONObject jsonObject = new JSONObject(new String(response, "UTF-8"));
					AppLog.LogD("xyw", "book list-->" + jsonObject.toString());
					if (jsonObject.getInt("error") == 0) {
						JSONArray jsonArray = jsonObject.getJSONArray("bookList");
						int length = jsonArray.length();
						JSONObject jsonObjectBookBean;
						for (int i = 0; i < length; i++) {
							jsonObjectBookBean = (JSONObject) jsonArray.get(i);
							int type = BookPublishFragement.getJsonInt(jsonObjectBookBean, "bookType");
							if (type == 1) {
								BookBean bookBean = setBookDatas(jsonObjectBookBean);
								ETApp.getInstance().setCacheBookbean(bookBean);
								return false;
							}
						}

						// 无即时订单，可下新订单
						return true;
					} else {
						// errorcode != 0
					}
				} else {
					// no datas
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			isCanBook = result;
			if (!result) {
				BookBean bk = ETApp.getInstance().getCacheBookbean();
				// 订单的状态
				int currentDispStat = BOOK_STAT_NORMAL;
				if (bk != null) {
					
//					currentBookId = bk.getId();
//					currentDispStat = bk.getDispStat();
//					dispatchStat(bk.getDispStat());
					
					//由下一步更改为跳转到详情页面
					Intent intent = new Intent(MainActivityNew.this, NewBookDetail.class);
					intent.putExtra("bookId", bk.getId());
					MainActivityNew.this.startActivity(intent);
					MainActivityNew.this.finish();
				} else {
					currentDispStat = BOOK_STAT_NORMAL;
					dispatchStat(currentDispStat);
				}
			}

			mProgressDialog.cancel();
		}
	}

	private BookBean setBookDatas(JSONObject jsonObjectBookBean) {
		BookBean bean = new BookBean();
		bean.setStartAddress(BookPublishFragement.getJsonString(jsonObjectBookBean, "startAddr"));
		bean.setStartLongitude(BookPublishFragement.getJsonInt(jsonObjectBookBean, "startLng"));
		bean.setStartLatitude(BookPublishFragement.getJsonInt(jsonObjectBookBean, "startLat"));
		bean.setEndAddress(BookPublishFragement.getJsonString(jsonObjectBookBean, "endAddr"));
		bean.setEndLongitude(BookPublishFragement.getJsonInt(jsonObjectBookBean, "endLng"));
		bean.setEndLatitude(BookPublishFragement.getJsonInt(jsonObjectBookBean, "endLat"));
		bean.setCityId(BookPublishFragement.getJsonInt(jsonObjectBookBean, "cityId"));
		bean.setPassengerId(BookPublishFragement.getJsonLong(jsonObjectBookBean, "passengerId"));
		bean.setUseTime(BookPublishFragement.getJsonString(jsonObjectBookBean, "useTime"));
		bean.setState(BookPublishFragement.getJsonInt(jsonObjectBookBean, "state"));
		bean.setBookType(BookPublishFragement.getJsonInt(jsonObjectBookBean, "bookType"));
		bean.setPassengerName(BookPublishFragement.getJsonString(jsonObjectBookBean, "contact"));
		bean.setId(BookPublishFragement.getJsonLong(jsonObjectBookBean, "id"));
		bean.setReplyerId(BookPublishFragement.getJsonLong(jsonObjectBookBean, "taxiId"));
		bean.setReplyerPhone(BookPublishFragement.getJsonString(jsonObjectBookBean, "taxiPhone"));
		bean.setReplyerNumber(BookPublishFragement.getJsonString(jsonObjectBookBean, "taxiNumber"));
		bean.setReplyerName(BookPublishFragement.getJsonString(jsonObjectBookBean, "replyerName"));
		bean.setReplyerCompany(BookPublishFragement.getJsonString(jsonObjectBookBean, "replyerCompany"));
		bean.setReplyerLongitude(BookPublishFragement.getJsonInt(jsonObjectBookBean, "replyerLongitude"));
		bean.setReplyerLatitude(BookPublishFragement.getJsonInt(jsonObjectBookBean, "replyerLatitude"));

		if (bean.getReplyerId() > 0) {
			bean.setDispStat(BOOK_STAT_RECEIVED);
		} else {
			bean.setDispStat(BOOK_STAT_WAITINGRESP);
		}
		return bean;
	}


	@Override
	public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
		
	}


	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivityNew.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
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
	public void onGetTransitRouteResult(TransitRouteResult result) {
		 
	}


	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		
	}


	@Override
	public void onMapClick(LatLng point) {
		 mBaiduMap.hideInfoWindow();
	}


	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		return false;
	}
	// 定制RouteOverly
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
