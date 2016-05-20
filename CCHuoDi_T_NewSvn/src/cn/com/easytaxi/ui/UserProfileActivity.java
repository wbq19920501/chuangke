package cn.com.easytaxi.ui;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.User;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.YdBaseActivity;
import cn.com.easytaxi.ui.adapter.UserProfileCommentAdapter;
import cn.com.easytaxi.ui.bean.YDUserComments;
import cn.com.easytaxi.ui.bean.YDUserComments.YDUserComment;
import cn.com.easytaxi.ui.bean.YDUserProfile;
import cn.com.easytaxi.ui.view.PullToRefreshListView;
import cn.com.easytaxi.ui.view.PullToRefreshListView.OnRefreshListener;
import cn.com.easytaxi.ui.view.RetryAndLoadBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserProfileActivity extends YdBaseActivity {

	public static final int USER_PROFILE = 888;
	public static final int USER_COMMENTS = 889;

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;

	public String order = "desc";

	private cn.com.easytaxi.ui.view.PullToRefreshListView listView;// 评论列表
	private View p_user_profile_userinfo;
	private ImageView profile_user_img;
	private TextView profile_user_name;
	private TextView profile_rank;
	private View cover_loading;
	private RetryAndLoadBar footer;

	private TitleBar bar;

	private View p_user_profile_function;

	private YDUserComments ydUserComments;
	private UserProfileCommentAdapter userProfileCommentAdapter;
	private Handler handler;
	private String cityId;
	private String passengerId;

	private YDUserProfile ydUserProfile;

	private int COUNTS = 8;
	private int curLvDataState;
	private TextView user_profile_rmdpass;
	private TextView user_profile_rmdtaxi;
	private TextView user_profile_bad;
	private TextView user_profile_good;
	private TextView user_profile_weiyue;
	private TextView user_profile_jiedan;
	private TextView profile_user_phone;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_user_profile);
		initViews();
		initListeners();
		initUserData();
		// TODO Auto-generated method st=
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(bar != null){
			bar.close();
		}
		super.onDestroy();
	}
	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		bar = new TitleBar(this);
		bar.setTitleName("我的信息");

		listView = (PullToRefreshListView) findViewById(R.id.profile_listview_comment);
		listView.setDivider(null);
		
		p_user_profile_function = (getLayoutInflater().inflate(R.layout.p_user_profile_function, null));

		user_profile_rmdpass = (TextView) p_user_profile_function.findViewById(R.id.user_profile_rmdpass);
		user_profile_rmdtaxi = (TextView) p_user_profile_function.findViewById(R.id.user_profile_rmdtaxi);
		user_profile_bad = (TextView) p_user_profile_function.findViewById(R.id.user_profile_bad);
		user_profile_good = (TextView) p_user_profile_function.findViewById(R.id.user_profile_good);

		user_profile_weiyue = (TextView) p_user_profile_function.findViewById(R.id.user_profile_weiyue);
		user_profile_jiedan = (TextView) p_user_profile_function.findViewById(R.id.user_profile_jiedan);

		p_user_profile_userinfo = (getLayoutInflater().inflate(R.layout.p_user_profile_title, null));
		profile_user_img = (ImageView) p_user_profile_userinfo.findViewById(R.id.profile_user_img);
		profile_user_name = (TextView) p_user_profile_userinfo.findViewById(R.id.progile_user_name);
		profile_user_phone= (TextView) p_user_profile_userinfo.findViewById(R.id.profile_user_phone);
		 
		profile_rank = (TextView) p_user_profile_userinfo.findViewById(R.id.profile_rank);
		cover_loading = findViewById(R.id.cover_loading);

		footer = new RetryAndLoadBar(this);
		footer.setVisibility(View.VISIBLE);

		listView.addHeaderView(p_user_profile_userinfo);
		listView.addHeaderView(p_user_profile_function);
		listView.addFooterView(footer);
	}

	@Override
	protected void initListeners() {

		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				AppLog.LogD("onRefresh -------------------");
				if(isNetAvailable()){
					getUserCommentMsgs(0, LISTVIEW_ACTION_REFRESH);
					
				}else{
					listView.onRefreshComplete();
				}
				
			}
		});

		listView.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				listView.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (ydUserComments.comments.size() == 0)
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				if (scrollEnd && curLvDataState == LISTVIEW_DATA_MORE) {
					listView.setTag(LISTVIEW_DATA_LOADING);
					footer.showLoadingBar();
					// lvComment_foot_more.setText(R.string.load_ing);
					// lvComment_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex

					getUserCommentMsgs(ydUserComments.comments.size(), LISTVIEW_ACTION_SCROLL);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				listView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}

		});
	}

	@Override
	protected void initUserData() {
		ydUserProfile = new YDUserProfile();
		
		cityId = getCityId();
		passengerId = getPassengerId();
		ydUserProfile.setUserName(passengerId);
		ydUserProfile.setUserPhone(passengerId);
		refreshUserProfileUi();
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case USER_PROFILE:
					dealUserInfo(msg.obj);
					break;

				case USER_COMMENTS:
					dealUserComment(msg.obj, msg.arg1);

					break;
				default:
					break;
				}

			}
		};

		ydUserComments = new YDUserComments();
		userProfileCommentAdapter = new UserProfileCommentAdapter(this, ydUserComments.comments);
		listView.setAdapter(userProfileCommentAdapter);
		getUserProfileInfo();
		getUserCommentMsgs(0, LISTVIEW_ACTION_INIT);
	}

	protected void dealUserComment(Object obj, int action) {
		if (obj != null) {
			ArrayList<YDUserComment> list = (ArrayList<YDUserComment>) obj;
			if (action == LISTVIEW_ACTION_INIT) {
				ydUserComments.comments.clear();
			} else if (action == LISTVIEW_ACTION_REFRESH) {
				ydUserComments.comments.clear();
			}
			if(null != list && list.size()>0){
				ydUserComments.comments.addAll(list);
			}
			
				curLvDataState = LISTVIEW_DATA_MORE;
				userProfileCommentAdapter.notifyDataSetChanged();
				footer.showMoreMsg();
		} else {

		}

		if (ydUserComments.comments.size() == 0) {
			curLvDataState = LISTVIEW_DATA_EMPTY;
			footer.showNoDateMsg();
		} else if (ydUserComments.comments.size() < COUNTS) {
			curLvDataState = LISTVIEW_DATA_FULL;
			userProfileCommentAdapter.notifyDataSetChanged();
			footer.showFullMsg();
		}
		if (action == LISTVIEW_ACTION_REFRESH) {
			listView.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
		}

	}

	protected void dealUserInfo(Object info) {
		
		
		if (info != null) {
			String myInfo = "";
			try {
				JSONObject jo = new JSONObject((String) info);
				if (jo.getInt("error") == 0) {
					JSONArray ja = jo.getJSONArray("datas");

					for (int i = 0; i < ja.length(); i++) {
						myInfo = ja.getJSONObject(i).toString();
						break;
					}

				} else {
				}
				
				Gson gons = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
				YDUserProfile yd = gons.fromJson((myInfo), YDUserProfile.class);
				//当yd为null时候默认初始化所有值
				if(null == yd){
					yd.setBadCommentCounts(0);
					yd.setGoodCommentCounts(0);
					yd.setSendBookCounts(0);
					yd.setWeiyueCounts(0);
					yd.setRmdTaxiCounts(0);
					yd.setRmdPassengerCounts(0);
					yd.setRank(0);
					yd.setMoney(0);
				}
				ydUserProfile.setUserProfile((YDUserProfile) yd);
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
		}

		refreshUserProfileUi();

	}

	private void refreshUserProfileUi() {
		// if(ydUserProfile == null){
		// return;
		// }

		User user = ETApp.getInstance().getCurrentUser();
		ydUserProfile.importUserInfo(user);

		profile_rank.setText(ydUserProfile.getRank() + "");
		profile_user_name.setText(ydUserProfile.getUserName());
		profile_user_phone.setText(ydUserProfile.getUserPhone());
		user_profile_rmdpass.setText(ydUserProfile.getRmdTaxiCounts() + "");

		user_profile_rmdtaxi.setText(ydUserProfile.getRmdTaxiCounts() + "");
		user_profile_bad.setText(ydUserProfile.getBadCommentCounts() + "");
		user_profile_good.setText(ydUserProfile.getGoodCommentCounts() + "");
		user_profile_weiyue.setText(ydUserProfile.getWeiyueCounts() + "");
		// user_profile_jiedan.setText(ydUserProfile.get);
	}

	private void getUserCommentMsgs(final int start, final int action) {
		if (!isNetAvailable()) {
			Toast.makeText(this, "网络不给力!", Toast.LENGTH_SHORT).show();
			footer.showMoreMsg();
		} else {
			NewNetworkRequest.requestUserComments(handler, action, cityId, passengerId, COUNTS, start, order); // 要拉几条数据，起点
		}
	}

	protected void getUserProfileInfo() {
		if (!isNetAvailable()) {
			Toast.makeText(this, "网络不给力!", Toast.LENGTH_SHORT).show();
			listView.onRefreshComplete();

		} else {
			NewNetworkRequest.requestUserfileInfo(handler, cityId, passengerId);
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

}
