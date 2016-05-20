package cn.com.easytaxi.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.YdBaseActivity;
import cn.com.easytaxi.ui.adapter.LoadCallback;
import cn.com.easytaxi.ui.adapter.MessageAdapter;
import cn.com.easytaxi.ui.bean.MsgBean;
import cn.com.easytaxi.ui.bean.MsgData;
import cn.com.easytaxi.ui.view.OrientListView;
import cn.com.easytaxi.ui.view.OrientListView.OnRefreshListener;

public class Message extends YdBaseActivity {
	
	private OrientListView listView;
	
	private MessageAdapter adapter;
	
	private Context context;
	
	private ArrayList<MsgBean> messageList;
	
	public static final String UPDATE_TIME = "msg_update_time ";

	private static final int LIST_PAGE_SIZE = 5 ;
	
	private MsgData msgData;
	
	Handler handler = new Handler();
	
	private static int anim_switch = 1;
	
	private TitleBar bar; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView( R.layout.msg_mgr );
		
		context = this;
		
		msgData=new MsgData();
		
		bar = new TitleBar(this);
		bar.setTitleName("我的消息");
	 
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		listView = (OrientListView) findViewById( R.id.message_list );
		
		messageList = new ArrayList<MsgBean>();
		
 
		adapter = new MessageAdapter(context, messageList);
		
		listView.setAdapter( adapter); 
	 
		
		listView.setonRefreshListener( new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				doRefresh();
			}
			
			@Override
			public void onNextPage() {
				doNextPage();
			}
		} );
		listView.onLoading();
		doNextPage();
	}

	protected void doRefresh() {
		msgData.getMsgList(messageList.size(), -1, getCityId(),getPassengerId(),true, new LoadCallback<List<MsgBean>>() {
			
			@Override
			public void handle(List<MsgBean> param) {
//				messageList.clear();
				messageList.addAll(0,param);
				adapter.notifyDataSetChanged();
			}
			
			@Override
			public void onStart() {
				
			}
			@Override
			public void complete() {
				listView.onRefreshComplete();
			}
		});
	}

	protected void doNextPage() {
		msgData.getMsgList(messageList.size(),  LIST_PAGE_SIZE,  getCityId(),getPassengerId(),false, new LoadCallback<List<MsgBean>>() {
			
			@Override
			public void handle(List<MsgBean> param) {
				messageList.addAll(param);
				adapter.notifyDataSetChanged();
			}
			
			@Override
			public void onStart() {
				
			}
			@Override
			public void complete() {
				listView.onNextPageComplete();
			}
		});
	}

	@Override
	protected void onStop() {
		 
		super.onStop();
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		
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
	
}
