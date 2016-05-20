package cn.com.easytaxi.ui.adapter;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.platform.comapi.map.B;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.book.BookBean;
import cn.com.easytaxi.book.BookUtil;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.platform.MainActivityNew;
import cn.com.easytaxi.ui.OrderHistory;
import cn.com.easytaxi.ui.SuggestionActivity;
import cn.com.easytaxi.ui.view.OrientListView;
import cn.com.easytaxi.ui.view.PingJiaDlg;
import cn.com.easytaxi.ui.view.PingJiaDlg.MyDialogListener;
import cn.com.easytaxi.util.InfoTool;
 

public class HistoryAdapter extends BaseAdapter {
	
	private ArrayList<BookBean> data;
	private LayoutInflater inflater;
	private Resources resources;
	private OrderHistory context;
	private String cityId;
	private String passengerId;
	public ViewsHolder viewsHolder;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
				case SuggestionActivity.FEEDBACK_OK:
				try {
					//评分
					int evaluate = msg.arg1;
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					int error = Integer.parseInt(jsonObject.get("error").toString());
					
					if(0 == error){//评论成功
						//更新UI
						viewsHolder.evaluate.setVisibility(View.GONE);
						viewsHolder.haveEvaluated.setVisibility(View.VISIBLE);
						viewsHolder.back.setVisibility(View.VISIBLE);
						Toast.makeText(context, "评价成功", Toast.LENGTH_LONG).show();
						//更新缓存
						BookBean b = data.get(viewsHolder.position);
						b.setEvaluate(evaluate);
					    ArrayList<BookBean> bookBeans = new ArrayList<BookBean>();
					    bookBeans.add(b);
						ETApp.bds.insert(bookBeans);
					}else{
						Toast.makeText(context, "评价失败...", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(context, "评价失败...", Toast.LENGTH_LONG).show();
				}
					break;
			}
		}
		
	};
	
	public HistoryAdapter( OrderHistory context ,String cityId, String passengerId,ArrayList<BookBean> data ){
		this.data = data;
		this.context = context;
		inflater = LayoutInflater.from(context);
		resources = context.getResources();
		this.cityId = cityId;
		this.passengerId = passengerId;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if( convertView == null ){
			convertView = inflater.inflate(R.layout.order_history_item , null);
		}
		
		TextView start = (TextView) convertView.findViewById( R.id.history_list_item_start_address );
		TextView end = (TextView) convertView.findViewById( R.id.history_list_item_end_address );
		TextView time = (TextView) convertView.findViewById( R.id.history_list_item_time );
		TextView price = (TextView) convertView.findViewById( R.id.history_list_item_price );
		View endView = convertView.findViewById( R.id.history_list_item_end_view );
		
		Button complaints = (Button) convertView.findViewById( R.id.history_list_item_complaints );
		Button evaluate = (Button) convertView.findViewById( R.id.history_list_item_evaluate );
		TextView haveEvaluated = (TextView)convertView.findViewById( R.id.haveEvaluated_order_history );
		LinearLayout back = (LinearLayout)convertView.findViewById( R.id.pingjia_back_order_history );
		
		BookBean bean = data.get(position);
		final Long bookId = bean.getId();
		final Long taxiId = bean.getReplyerId();
		//判断是否被接单，接了才能评价
		if (null == taxiId ) {
			haveEvaluated.setVisibility(View.GONE);
			evaluate.setVisibility(View.GONE);
			back.setVisibility(View.GONE);
		}else{
			//判断是否已评价:已评价
			if (BookUtil.isEffective(bean.getEvaluate()) && BookUtil.isEffective(bean.getReplyerId())) {
				haveEvaluated.setVisibility(View.VISIBLE);
				evaluate.setVisibility(View.GONE);
				back.setVisibility(View.VISIBLE);
			}else{//未评价
				haveEvaluated.setVisibility(View.GONE);
				evaluate.setVisibility(View.VISIBLE);
				back.setVisibility(View.VISIBLE);
			}
		}

		start.setText( bean.getStartAddress() );
		if( bean.getEndAddress() == null || "".equals( bean.getEndAddress() ) ){
			endView.setVisibility( View.GONE );
		}else{
			end.setText( bean.getEndAddress() );
			endView.setVisibility( View.VISIBLE );
		}
		
		
		AppLog.LogD(new Date(Long.valueOf(bean.getUseTime())).toString());
//		
		time.setText(InfoTool.friendlyDate(resources,  Long.valueOf(bean.getUseTime()) ));
//		
		price.setText( bean.getPrice() + "" );
		
		complaints.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				
			}
		} );
		
		ViewsHolder holder = new ViewsHolder();
		holder.haveEvaluated = (TextView) convertView.findViewById( R.id.haveEvaluated_order_history );
		holder.evaluate = (Button) convertView.findViewById( R.id.history_list_item_evaluate );
		holder.back = (LinearLayout) convertView.findViewById( R.id.pingjia_back_order_history );
		holder.position = position;
		evaluate.setTag(holder);
		evaluate.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				viewsHolder = (ViewsHolder)v.getTag();
				Window.stopServiceWindow(context,3, new Callback<JSONObject>() {
					@Override
					public void handle(JSONObject param) {

						String comment = "";
						int value = 0;
						try {
							comment = param.getString("comment");
							value = param.getInt("value");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						if(NetChecker.getInstance(ETApp.getInstance()).isAvailableNetwork()){
							 
							NewNetworkRequest.suggest(handler, bookId, taxiId, value);
							Toast.makeText(context, "已提交", Toast.LENGTH_SHORT).show();
							
							//写入日志
							ActionLogUtil.writeActionLog(context,R.array.more_history_evaluate,"");
						}else{
							Toast.makeText(context, "网络不给力", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		} );
		
		return convertView;
	}
	
	class ViewsHolder{
		Button evaluate ;
		TextView haveEvaluated;
		LinearLayout back;
		int position;
	}

}
