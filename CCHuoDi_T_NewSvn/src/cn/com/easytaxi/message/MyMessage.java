package cn.com.easytaxi.message;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.ui.MoreWebviewActivity;
import cn.com.easytaxi.ui.adapter.LoadCallback;
import cn.com.easytaxi.ui.bean.MsgBean;
import cn.com.easytaxi.ui.bean.MsgData;
import cn.com.easytaxi.ui.view.OrientListView;
import cn.com.easytaxi.ui.view.OrientListView.OnRefreshListener;
import cn.com.easytaxi.workpool.BaseActivity;

public class MyMessage extends BaseActivity implements OnItemClickListener, OnCheckedChangeListener, OnRefreshListener, OnPageChangeListener, android.widget.CompoundButton.OnCheckedChangeListener {
	private static final int LIST_PAGE_SIZE = 5;
	private TitleBar titleBar;
	private ViewPager mPager;
	private MsgViewPagerAdapter mAdapter;
	private OrientListView systemMsglistView;
	private OrientListView personalMsglistView;
	private MyMessage self;
	private RadioGroup radioGroup;

	private RadioButton systemRadio;
	private RadioButton personalRadio;

	private BadgeView systemBadge;
	private BadgeView personalBadge;

	private ArrayList<MsgBean> systemMessageList;
	private ArrayList<MsgBean> personalMessageList;

	private MyMessageAdapter systemMessageAdapter;
	private MyMessageAdapter personalMessageAdapter;

	private Context context;

	public static final String UPDATE_TIME = "msg_update_time ";

	private MsgData msgData;

	Handler handler = new Handler();

	// private static int anim_switch = 1;
	/**
	 * 未读易达公告条数
	 */
	private int unreadSystemMessageSize = 0;
	/**
	 * 未读个人消息条数
	 */
	private int unreadPersonalMessageSize = 0;
	private SessionAdapter session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mymsg_main);
		self = this;
		titleBar = new TitleBar(this);
		titleBar.setTitleName("公告");
		context = this;

		msgData = new MsgData();

		session = new SessionAdapter(context);

		// try {
		// anim_switch =
		// Integer.parseInt(session.get(Setting._ANIMATION_SWITCH));
		// } catch (Exception e) {
		// session.set(Setting._ANIMATION_SWITCH, "1");
		// }

		systemMsglistView = createListView();
		personalMsglistView = createListView();

		systemMessageList = new ArrayList<MsgBean>();
		systemMsglistView.setOnItemClickListener(this);
		personalMessageList = new ArrayList<MsgBean>();
		personalMsglistView.setOnItemClickListener(this);

		systemMessageAdapter = new MyMessageAdapter(self, systemMessageList);
		personalMessageAdapter = new MyMessageAdapter(self, personalMessageList);

		systemMsglistView.setAdapter(systemMessageAdapter);
		personalMsglistView.setAdapter(personalMessageAdapter);

		// ListView加载动画
		// if (anim_switch == 1) {
		// systemMsglistView.setLayoutAnimation(TabAdapter.getListAnim());
		// personalMsglistView.setLayoutAnimation(TabAdapter.getListAnim());
		// }

		mPager = (ViewPager) findViewById(R.id.message_pager);

		mAdapter = new MsgViewPagerAdapter(self, new View[] { systemMsglistView, personalMsglistView });

		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(this);

		radioGroup = (RadioGroup) findViewById(R.id.msg_group_type);
		radioGroup.setOnCheckedChangeListener(this);

		systemRadio = (RadioButton) findViewById(R.id.msg_type_system);
		personalRadio = (RadioButton) findViewById(R.id.msg_type_personal);

		systemRadio.setOnCheckedChangeListener(this);
		personalRadio.setOnCheckedChangeListener(this);

		systemBadge = new BadgeView(this, systemRadio);
		systemBadge.setBackgroundResource(R.drawable.tabgroup_number_bg);
		systemBadge.setTextSize(16);

		personalBadge = new BadgeView(this, personalRadio);
		personalBadge.setBackgroundResource(R.drawable.tabgroup_number_bg);
		personalBadge.setTextSize(16);
		systemBadge.setVisibility(View.GONE);
		personalBadge.setVisibility(View.GONE);

		systemMsglistView.setonRefreshListener(this);
		personalMsglistView.setonRefreshListener(this);

		radioGroup.check(R.id.msg_type_system);
		systemMsglistView.onLoading();
		doNextPage();
	}

	private OrientListView createListView() {
		OrientListView list = new OrientListView(self);
		list.setCacheColorHint(Color.parseColor("#00000000"));
		list.setDivider(getResources().getDrawable(R.drawable.normal_line));
		list.setFooterDividersEnabled(false);
		// list.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT));
		return list;
	}

	protected void doRefresh() {
		msgData.getMsgList(0, -1, getCityId(), getPassengerId(), true, new LoadCallback<List<MsgBean>>() {

			@Override
			public void handle(List<MsgBean> result) {
				if (result != null && !result.isEmpty()) {
//					systemMessageList.clear();//如果在这里清空，然后再上拉加载更多会导致数据不对,所以注销掉
//					personalMessageList.clear();
					
					for (MsgBean msgBean : result) {
						if (msgBean.getMsgType() == 1) {
							systemMessageList.add(0, msgBean);
							if(!msgBean.getRead()){
								unreadSystemMessageSize++;
							}
						} else {
							personalMessageList.add(0,msgBean);
							if(!msgBean.getRead()){
								unreadPersonalMessageSize++;
							}
						}
					}
//					systemBadge.setText("" + unreadSystemMessageSize);
//					personalBadge.setText("" + unreadPersonalMessageSize);
//					systemBadge.show();
//					personalBadge.show();
					systemMessageAdapter.notifySortDataSetChanged();
					personalMessageAdapter.notifySortDataSetChanged();

				}
			}

			@Override
			public void onStart() {

			}

			@Override
			public void complete() {
				systemMsglistView.onRefreshComplete();
				personalMsglistView.onRefreshComplete();
			}
		});
	}

	protected void doNextPage() {
		msgData.getMsgList(systemMessageList.size() + personalMessageList.size(), LIST_PAGE_SIZE, getCityId(), getPassengerId(), false, new LoadCallback<List<MsgBean>>() {

			@Override
			public void handle(List<MsgBean> result) {
				if (result != null && !result.isEmpty()) {
					for (MsgBean msgBean : result) {
						if (msgBean.getMsgType() == 1) {
							systemMessageList.add(msgBean);
							if(!msgBean.getRead()){
								unreadSystemMessageSize++;
							}
						} else {
							personalMessageList.add(msgBean);
							if(!msgBean.getRead()){
								unreadPersonalMessageSize++;
							}
						}
					}
//					systemBadge.setText("" + unreadSystemMessageSize);
//					personalBadge.setText("" + unreadPersonalMessageSize);
//					systemBadge.show();
//					personalBadge.show();
					systemMessageAdapter.notifySortDataSetChanged();
					personalMessageAdapter.notifySortDataSetChanged();

				}
			}

			@Override
			public void onStart() {

			}

			@Override
			public void complete() {
				systemMsglistView.onNextPageComplete();
				personalMsglistView.onNextPageComplete();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		session.close();
		titleBar.close();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MsgBean msg = (MsgBean) parent.getItemAtPosition(position);
		if (msg == null)
			return;

		// 标记已读
		if (!msg.getRead()) {
			msg.setRead(true);
			msgData.markRead(msg.getCacheId(), null);
			if(msg.getMsgType() == 1){//系统
				unreadSystemMessageSize --;
			}else{
				unreadPersonalMessageSize --;
			}
		}

//		systemBadge.setText("" + unreadSystemMessageSize);
//		personalBadge.setText("" + unreadPersonalMessageSize);
		systemMessageAdapter.notifyDataSetChanged();
		personalMessageAdapter.notifyDataSetChanged();

		if (!TextUtils.isEmpty(msg.getUrl())) {
			Intent intent = new Intent(MyMessage.this, MoreWebviewActivity.class );
			intent.putExtra("title", "消息详情");
			intent.putExtra("uri", msg.getUrl());
			startActivity(intent );
		} else {
			Window.showMessageDialog(MyMessage.this,"消息详情", msg.getBody(), "确定", null, null, null);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (checkedId == R.id.msg_type_system) {
			mPager.setCurrentItem(0, true);
			//写入日志
			ActionLogUtil.writeActionLog(self,R.array.more_my_sys_message,"");
		} else if (checkedId == R.id.msg_type_personal) {
			mPager.setCurrentItem(1, true);
			//写入日志
			ActionLogUtil.writeActionLog(self,R.array.more_my_self_message,"");
		}
	}

	@Override
	public void onRefresh() {
		doRefresh();
	}

	@Override
	public void onNextPage() {
		doNextPage();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 0) {
			radioGroup.check(R.id.msg_type_system);
		} else {
			radioGroup.check(R.id.msg_type_personal);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			radioGroup.check(buttonView.getId());
		}
	}
}
