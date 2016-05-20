package cn.com.easytaxi.message;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.ui.bean.MsgBean;
import cn.com.easytaxi.ui.bean.MsgData;
import cn.com.easytaxi.util.TimeTool;

/**
 * @author shiner
 * 
 */
public class MyMessageAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MsgBean> data;

	private LayoutInflater inflater;

	public void notifySortDataSetChanged() {
		Collections.sort(data, new MsgData.ComparatorId());
		super.notifyDataSetChanged();
	}

	public MyMessageAdapter(Context context, ArrayList<MsgBean> data) {
		this.context = context;
		this.data = data;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public MsgBean getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.mymsg_item, null);
		}

		TextView date = (TextView) convertView.findViewById(R.id.message_date);
		ImageView state = (ImageView) convertView.findViewById(R.id.message_state);
		TextView type = (TextView) convertView.findViewById(R.id.message_type);
		TextView info = (TextView) convertView.findViewById(R.id.message_info);
		MsgBean message = data.get(position);
		
		if (position % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.normal_list_item_single);
		} else {
			convertView.setBackgroundResource(R.drawable.normal_list_item_twin);
		}

		date.setText(TimeTool.getListTime(message.getDate().getTime()));
		state.setImageResource(message.getRead() ? R.drawable.msg_read : R.drawable.msg_unread);
		setType(message.getMsgSubType(), type);
		info.setText(message.getBody());

		return convertView;
	}

	/**
	 * 1=公告 2=推荐 3=服务 4=新闻 5=恭喜 6=验证系统
	 * 
	 * @param msgSubType
	 * @param typeTv
	 */
	private void setType(Integer msgSubType, TextView typeTv) {
		switch (msgSubType) {
		case 1:
			typeTv.setText("公告");
			break;
		case 2:
			typeTv.setText("推荐");
			break;
		case 3:
			typeTv.setText("服务");
			break;
		case 4:
			typeTv.setText("新闻");
			break;
		case 5:
			typeTv.setText("恭喜");
			break;
		case 6:
			typeTv.setText("验证系统");
			break;

		default:
			break;
		}
	}

}
