package cn.com.easytaxi.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.com.easytaxi.AppLog;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.ui.bean.MsgBean;
import cn.com.easytaxi.util.InfoTool;

public class MessageAdapter extends BaseAdapter {
	public final static SimpleDateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Context context;
	private ArrayList<MsgBean> data;
	private Resources resources;

	private LayoutInflater inflater;

	public MessageAdapter(Context context, ArrayList<MsgBean> data) {
		this.context = context;
		this.data = data;
		resources = context.getResources();
		inflater = LayoutInflater.from(context);
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

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.msg_item, null);
		}
		
		if(position%2==0){
			convertView.setBackgroundColor(context.getResources().getColor(R.color.list_item_white));
		}else{
			convertView.setBackgroundColor(context.getResources().getColor(R.color.list_item_dark));
		}

		TextView date = (TextView) convertView.findViewById(R.id.message_date);
		TextView info = (TextView) convertView.findViewById(R.id.message_info);

		try {
			MsgBean message = data.get(position);
			date.setText(InfoTool.friendlyDate(resources, message.getDate().getTime()));
			info.setText(message.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

}
