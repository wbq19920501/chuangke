package cn.com.easytaxi.mine.account;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.com.easytaxi.AppLog;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.ui.view.OrientListView.OnRefreshListener;

/**
 * @ClassName: MyAccountAdapter
 * @Description: TODO
 * @author Brook xu
 * @date 2013-7-31 ÏÂÎç3:41:27
 * @version 1.0
 */
public class MyAccountAdapter extends BaseAdapter{

	private Context context;
	private List<Account> data;

	private LayoutInflater inflater;

	public MyAccountAdapter(Context context, List<Account> data) {
		this.context = context;
		this.data = data;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Account getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.p_mine_account_list_item, null);
		}

		TextView date = (TextView) convertView.findViewById(R.id.p_mine_account_item_time);
		TextView score = (TextView) convertView.findViewById(R.id.p_mine_account_item_score);
		TextView tip = (TextView) convertView.findViewById(R.id.p_mine_account_item_tip);
		Account account = data.get(position);

//		if (position % 2 == 0) {
//			convertView.setBackgroundResource(R.drawable.normal_list_item_single);
//		} else {
//			convertView.setBackgroundResource(R.drawable.normal_list_item_twin);
//		}
		
		setValues(date,score,tip,account);
		
		return convertView;
	}

	private void setValues(TextView date, TextView etValue, TextView remart, Account account) {
		AppLog.LogD(account.toString());
		int type = account.getType();
		switch (type) {
		case Account.TYPE_ET_MONEY:
			EtMoneyBean money = (EtMoneyBean) account;
			date.setText(money.getDateString());
			remart.setText(money.getRemark());
			etValue.setText(String.valueOf(money.getEtMoney()));
			break;
		case Account.TYPE_ET_COIN:
			EtCoinBean coin = (EtCoinBean) account;
			date.setText(coin.getDateString());
			remart.setText(coin.getRemark());
			etValue.setText(String.valueOf(coin.getEtCoin()));
			break;
		case Account.TYPE_ET_SCORE:
			EtScoreBean score = (EtScoreBean) account;
			date.setText(score.getDateString());
			remart.setText(score.getRemark());
			etValue.setText(String.valueOf(score.getEtScore()));
			break;
		default:
			break;
		}
	}

	public List<Account> getData() {
		return data;
	}

	public void setData(List<Account> data) {
		this.data = data;
	}
}
