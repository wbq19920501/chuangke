package cn.com.easytaxi;

import java.util.Date;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.easytaxi.bean.ScoreDetailBean;
import cn.com.easytaxi.client.channel.TcpClient;
import cn.com.easytaxi.client.common.MsgConst;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.ui.adapter.DateAdapter;
import cn.com.easytaxi.ui.adapter.LoadCallback;
import cn.com.easytaxi.util.AsyncUtil;
import cn.com.easytaxi.workpool.BaseActivity;

import com.easytaxi.etpassengersx.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * �ҵĻ���ҳ��
 * @author xxb
 * @version ����ʱ�䣺2015��9��10�� ����3:36:18
 */
public class MyScoreDetailActivity extends BaseActivity implements OnRefreshListener2<ListView> {
	TitleBar bar;
	private PullToRefreshListView pullListview;
	/**
	 * ����������
	 */
	private ScoreItemAdapter scoreadapter;
	/**
	 * �ܻ���
	 */
	private TextView all_score;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myscoredetail);
		initViews();
		getScoreDetail(0);

	}

	private void initViews() {

		bar = new TitleBar(this);
		bar.setTitleName("�ҵĻ���");
//		bar.setRightButtonVisible(View.VISIBLE);
		pullListview = (PullToRefreshListView) findViewById(R.id.pullListview);
		all_score = (TextView) findViewById(R.id.all_score);

		pullListview.getRefreshableView().setDividerHeight(0);
		pullListview.setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.BOTH);
		pullListview.setOnRefreshListener(this);
		//��ʼ������ˢ�£���ʾ��ֵ
		PullTool.PullListViewUtils.setPullListViewParams(pullListview);
		scoreadapter = new ScoreItemAdapter(this);
		pullListview.setAdapter(scoreadapter);
		TextView tv = (TextView) findViewById(R.id.jifenguize);
//		bar.getRightCityButton().setVisibility(View.VISIBLE);
//		bar.getRightCityButton().setText("���ֹ���");
//		bar.getRightCityButton().setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(self, JfGuizeHistory.class));
//			}
//		});
		tv.setText(Html.fromHtml("<u>���ֹ���</u>"));
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(self, JfGuizeHistory.class));
			}
		});
		
		doRefresh();
	}

	private void doRefresh() {
		getScoreDetail(0);
	}

	private void doNextPage(int startId) {
		getScoreDetail(startId);
	}

	private ScoreDetailBean bean;

	/**
	 * ��ȡ������ϸ��Ϣ
	 */
	private void getScoreDetail(int startId) {
		getScore(startId);
	}

	private Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, new DateAdapter()).create();

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		PullTool.PullListViewUtils.setPullDownTime(getApplicationContext(), refreshView);
		doRefresh();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				//������ˢ�»ָ�ԭ״
				pullListview.onRefreshComplete();
			}
		}, 1000);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (bean != null)
			doNextPage(bean.getStartId());
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				pullListview.onRefreshComplete();
			}
		}, 1000);
	}

	private Handler handler = new Handler();
	/**
	 * ���û�ȡ��Ϣ�ӿ�
	 * @param startId
	 * @return
	 * @throws Exception
	 */
	private ScoreDetailBean getScoreDetailImp(int startId) throws Exception {
		JSONObject jo = new JSONObject();
		jo.put("action", "userAction");
		jo.put("method", "getScoreList");
		jo.put("Id", getPassengerId());
		jo.put("startId", startId);
		jo.put("size", 20);
		byte[] results = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, jo.toString().getBytes("utf-8"));
		Log.d("xxb", "getScoreList req:" + jo.toString());
		String result = new String(results, "utf-8");
		Log.d("xxb", "getScoreList res:" + result);
		return gson.fromJson(result, ScoreDetailBean.class);
	}
	/**
	 * �첽ִ�в���
	 * @param startId
	 */
	public void getScore(final int startId) {
		AsyncUtil.goAsync(new Callable<ScoreDetailBean>() {

			@Override
			public ScoreDetailBean call() throws Exception {
				return getScoreDetailImp(startId);
			}
		}, new LoadCallback<ScoreDetailBean>() {

			@Override
			public void onStart() {

			}

			@Override
			public void handle(ScoreDetailBean param) {
				bean = param;
				if (bean != null) {
					all_score.setText("�ҵĻ���\n" + bean.getTotalScore() + "");
				}
				if (startId == 0) {
					scoreadapter.setDatas(param.getBooks()); 
				} else {
					scoreadapter.addDatas(param.getBooks());
				}
				scoreadapter.notifyDataSetChanged();
			}

			@Override
			public void complete() {
				super.complete();
				pullListview.onRefreshComplete();
			}
		});
	}

}
