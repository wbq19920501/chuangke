package cn.com.easytaxi.phone.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Pair;
import cn.com.easytaxi.phone.common.DaoAdapter;

public class PhoneListAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;

	private View[] views;

	private CheckBox[] checkBoxs;

	private DaoAdapter dao;

	public PhoneListAdapter(Context context, DaoAdapter dao, String cityId, List<Pair<String, String>> companys, List<Pair<String, String>> drivers) {

		layoutInflater = LayoutInflater.from(context);

		this.dao = dao;

		List<View> viewList = new ArrayList<View>();
		if (companys.size() > 0) {
			View companyView = layoutInflater.inflate(R.layout.phone_item_title, null);
			TextView textView = (TextView) companyView.findViewById(R.id.phone_item_title);
			textView.setText("出租车叫车热线");
			viewList.add(companyView);
		}
		for (Pair<String, String> company : companys) {
			View companyView = layoutInflater.inflate(R.layout.phone_item_c, null);

			TextView nameTextView = (TextView) companyView.findViewById(R.id.phone_item_name);
			nameTextView.setText(company.key);

			TextView phoneTextView = (TextView) companyView.findViewById(R.id.phone_item_phone);
			phoneTextView.setText("电话：" + company.value);

			TextView numberTextView = (TextView) companyView.findViewById(R.id.phone_item_number);
			numberTextView.setText("呼叫次数：" + company.data+"次");

			View button = companyView.findViewById(R.id.phone_item_button);
			button.setOnClickListener(new PhoneOnClickListener(context, cityId, company.key, company.value));
			viewList.add(companyView);
		}
		{
			View companyView = layoutInflater.inflate(R.layout.phone_item_title, null);
			TextView textView = (TextView) companyView.findViewById(R.id.phone_item_title);
			textView.setText("我收藏的司机");
			viewList.add(companyView);
		}
		{
			checkBoxs = new CheckBox[drivers.size()];

			int i = 0;
			for (Pair<String, String> driver : drivers) {
				View companyView = layoutInflater.inflate(R.layout.phone_item_d, null);

				Object[] ds = (Object[]) driver.data;

				checkBoxs[i] = (CheckBox) companyView.findViewById(R.id.phone_item_checkbox);
				checkBoxs[i].setTag(driver.value);

				TextView nameTextView = (TextView) companyView.findViewById(R.id.phone_item_name);
				nameTextView.setText(driver.key);

				TextView callTextView = (TextView) companyView.findViewById(R.id.phone_item_call);
				callTextView.setText("呼叫次数：" + ds[2] + "次 ");

				TextView lastTextView = (TextView) companyView.findViewById(R.id.phone_item_last);
				if (ds[3] == null) {
					lastTextView.setVisibility(View.GONE);
				} else {
					lastTextView.setVisibility(View.VISIBLE);
					lastTextView.setText("最后呼叫时间：" + ds[3]);
				}

				TextView phoneTextView = (TextView) companyView.findViewById(R.id.phone_item_phone);
				phoneTextView.setText("电话：" + driver.value);

				TextView taxinumTextView = (TextView) companyView.findViewById(R.id.phone_item_taxinum);
				taxinumTextView.setText("车牌：" + (ds[0].equals("") ? "无" : ds[0]));

				ImageView headImageView = (ImageView) companyView.findViewById(R.id.phone_item_sex);
				if (ds[1].equals(1)) {
					headImageView.setImageResource(R.drawable.pic052);
				} else {
					headImageView.setImageResource(R.drawable.pic053);
				}

				View button = companyView.findViewById(R.id.phone_item_button);
				button.setOnClickListener(new PhoneOnClickListener(context, cityId, driver.key, driver.value));
				viewList.add(companyView);

				i++;
			}
		}
		views = viewList.toArray(new View[0]);
	}

	public int getCount() {
		return views.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return views[position];
	}

	public void selectAll() {
		for (int i = 0; i < checkBoxs.length; i++) {
			checkBoxs[i].setChecked(true);
		}
	}

	public void unSelect() {
		for (int i = 0; i < checkBoxs.length; i++) {
			checkBoxs[i].setChecked(!checkBoxs[i].isChecked());
		}
	}

	public List<String> getSelectedPhones() {
		List<String> phones = new ArrayList<String>();
		for (int i = 0; i < checkBoxs.length; i++) {
			if (checkBoxs[i].isChecked()) {
				phones.add(checkBoxs[i].getTag().toString());
			}
		}
		return phones;
	}

	private class PhoneOnClickListener implements View.OnClickListener {

		private Context context;
		
		//呼叫电话号码
		private String phone;
		//呼叫姓名
		private String name;
		//城市id
		private String cityId;

		public PhoneOnClickListener(Context context, String cityId, String name, String phone) {
			this.context = context;
			this.phone = phone;
			this.name = name;
			this.cityId = cityId;
		}

		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
			context.startActivity(intent);
			dao.saveHistory(cityId, name, phone);
			//写入日志
			ActionLogUtil.writeActionLog(context, R.array.phone_call,phone);
		
			new AsyncTask<Object, Object, Object>(){
				
				@Override
				protected void onCancelled() {
					// TODO Auto-generated method stub
					super.onCancelled();
				}

				@Override
				protected void onPostExecute(Object result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
				}

				@Override
				protected void onPreExecute() {
					// TODO Auto-generated method stub
					super.onPreExecute();
				}

				@Override
				protected void onProgressUpdate(Object... values) {
					// TODO Auto-generated method stub
					super.onProgressUpdate(values);
				}

				@Override
				protected Object doInBackground(Object... params) {
					// TODO Auto-generated method stub
					return null;
				}
				
			};
		
		}
		
	}

}
