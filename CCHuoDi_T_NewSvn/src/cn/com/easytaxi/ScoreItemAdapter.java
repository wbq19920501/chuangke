package cn.com.easytaxi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.com.easytaxi.bean.ScoreItem;

import com.easytaxi.etpassengersx.R;

/**
 * @author xxb
 * @version 1.0 ����ʱ�䣺2015��9��10�� ����5:07:16
 */
public class ScoreItemAdapter extends BaseAdapter {
	private Context context;
	private List<ScoreItem> datas;

	public ScoreItemAdapter(Context context) {
		this.context = context;
	}

	/**
	 * ��������
	 * 
	 * @param datas
	 */
	public void setDatas(List<ScoreItem> datas) {
		this.datas = datas;
	}

	/**
	 * ������ݵ�ĩβ
	 * 
	 * @param datas
	 */
	public void addDatas(List<ScoreItem> datas) {
		if (datas == null)
			return;
		if (this.datas == null)
			this.datas = new ArrayList<ScoreItem>();
		this.datas.addAll(datas);
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public ScoreItem getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.score_item, null);
			holder.type = (TextView) convertView.findViewById(R.id.type);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.number = (TextView) convertView.findViewById(R.id.jine);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ScoreItem score = datas.get(position);

		holder.type.setText("" + score.getTitle());
		holder.number.setText("" + getScoreString(score.getScore()));
		holder.time.setText("" + score.getUseTime());
		return convertView;
	}

	private class ViewHolder {
		// ���ͣ��¼�������
		TextView type, time, number;
	}

	/**
	 * ����ת����
	 */
	private String getScoreString(String score) {
		try {
			int sc = Integer.parseInt(score);
			if (sc >= 0)
				return "+" + sc;
		} catch (Exception e) {
		}
		return "";
	}

}
