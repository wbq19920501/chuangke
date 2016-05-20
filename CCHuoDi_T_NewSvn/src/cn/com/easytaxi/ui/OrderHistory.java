package cn.com.easytaxi.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.book.BookBean;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.YdBaseActivity;
import cn.com.easytaxi.ui.adapter.HistoryAdapter;
import cn.com.easytaxi.ui.adapter.LoadCallback;
import cn.com.easytaxi.ui.view.OrientListView;
import cn.com.easytaxi.ui.view.OrientListView.OnRefreshListener;

public class OrderHistory extends YdBaseActivity {

	public static final int BOOK_MSG_REFRESH = 700;
	public static final int BOOK_MSG_NEXTPAGE = 701;
	private OrientListView listView;
	private HistoryAdapter adapter;
	private Context context;
	private TitleBar bar;
	
	public static final int PINGJIA_DLG = 100;

	// private static int anim_switch = 1;

	private SessionAdapter session;

	private ArrayList<BookBean> historyList;

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.order_history);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		context = this;

		session = new SessionAdapter(context);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;

				switch (what) {
				case BOOK_MSG_REFRESH:

					break;
				case BOOK_MSG_NEXTPAGE:

					break;

				default:
					break;
				}
			}
		};

		historyList = new ArrayList<BookBean>();

		bar = new TitleBar(this);
		bar.setTitleName("历史订单");

		listView = (OrientListView) findViewById(R.id.history_list);

		adapter = new HistoryAdapter(this, getCityId() , getPassengerId(),historyList);

		listView.setAdapter(adapter);

		listView.setFooterDivider(R.drawable.history_list_line);

		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				doRefresh();
			}

			@Override
			public void onNextPage() {
				doNextPage();
			}
		});
		listView.onLoading();
		doNextPage();
	}

 
	
 

	protected void doRefresh() {
		ETApp.bds.getHistoryList(historyList.size(), true, getCityId(), getPassengerId(), new LoadCallback<List<BookBean>>() {

			@Override
			public void onStart() {

			}

			@Override
			public void handle(List<BookBean> param) {
//				historyList.clear();
				
				AppLog.LogD(param.size()+" ===  ");
				historyList.addAll(0, param);//加到list开始处
				adapter.notifyDataSetChanged();
			}

			@Override
			public void complete() {
				listView.onRefreshComplete();
			}
		});
	}

	protected void doNextPage() {
		ETApp.bds.getHistoryList(historyList.size(), false, getCityId(), getPassengerId(), new LoadCallback<List<BookBean>>() {

			@Override
			public void onStart() {

			}

			@Override
			public void handle(List<BookBean> param) {
				historyList.addAll(param);//加到list结尾处
				adapter.notifyDataSetChanged();
			}

			@Override
			public void complete() {
				listView.onNextPageComplete();
			}
		});

	}

	@Override
	protected void onStop() {
		session.close();
		super.onStop();
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub

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

	@Override
	protected void onDestroy() {
		if (bar != null) {
			bar.close();
		}
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
