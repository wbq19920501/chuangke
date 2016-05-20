package cn.com.easytaxi.platform;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.PinyinConvertor;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.common.Cities;
import cn.com.easytaxi.platform.common.Cities.City;
import cn.com.easytaxi.platform.common.CityTool;
import cn.com.easytaxi.platform.service.SystemService;
import cn.com.easytaxi.platform.view.AlphabetBar;
import cn.com.easytaxi.platform.view.AlphabetBar.OnSelectedListener;

public class CitySelectActivity extends YdBaseActivity implements OnSelectedListener {
	public static final String tag = "CitySelectActivity";

	public boolean isFirst = true;

	private ListView cityListView;
	private AlphabetBar alphabetBar;
	private TextView currentChar;
	private SectionAdapter cityAdapter;
	private TitleBar bar;
	private ArrayList<City> itemList = new ArrayList<Cities.City>(0);

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
			Object obj = cityListView.getItemAtPosition(position);
			if (obj != null) {
				City city = (City) obj;
				if (city.cityNameSimple.equals("全部城市") || city.cityNameSimple.equals("热门城市")) {
					return;
				}

				Intent intent = new Intent("cn.com.easytaxi.passenger.getcity");
				intent.putExtra("selType", "user");
				intent.putExtra("getcity", city.cityNameSimple);
				intent.putExtra("cityId", String.valueOf(city.id));
				sendBroadcast(intent);

				Intent cityIntent = new Intent("cn.com.easytaxi.cityswitch.user");
				cityIntent.putExtra("_CITY_NAME", city.cityNameSimple);
				cityIntent.putExtra("_CITY_ID", String.valueOf(city.id));
				sendBroadcast(cityIntent);

				ETApp.getInstance().saveCacheString("_CITY_NAME", city.cityNameSimple);
				ETApp.getInstance().saveCacheString("_CITY_ID", String.valueOf(city.id));

				Intent cityInfo = new Intent();
				cityInfo.putExtra("cityName", city.name);
				cityInfo.putExtra("cityId", String.valueOf(city.id));
				setResult(RESULT_OK, cityInfo);
				finish();
				// self.sendBroadcast(intent);
			}

		}

	};

	@Override
	protected void onDestroy() {
		if (bar != null) {
			bar.close();
			bar = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.platform_city_select);
		initViews();
		initUserData();
	}

	@Override
	public void initUserData() {
		itemList = ETApp.getInstance().getCityCache();
		if (itemList == null || itemList.size() <= 0) {
			initCitys();
			ETApp.getInstance().setCityCache(itemList);
		}

		cityAdapter = new SectionAdapter(this, 0, itemList);
		alphabetBar.setOnSelectedListener(this);
		alphabetBar.setList(cityListView);
		cityListView.setAdapter(cityAdapter);
		cityListView.setDividerHeight(0);
		cityListView.setOnItemClickListener(itemClickListener);
	}

	private void initCitys() {
		Cities.PinyinCompare comparator = new Cities.PinyinCompare();
		itemList = session.getCityList(0); // normal city
		Collections.sort(itemList, comparator);
		City header = new City();
		header.cityNameSimple = "全部城市";

		header.isHeader = true;
		itemList.add(0, header);
		// Collections.sort(itemList, comparator);
		itemList.addAll(0, session.getCityList(1));

		City header_hot = new City();
		header_hot.cityNameSimple = "热门城市";
		header_hot.isHeader = true;
		itemList.add(0, header_hot);
	}

	@Override
	public void initViews() {
		cityListView = (ListView) this.findViewById(R.id.city_list);
		cityListView.setAnimationCacheEnabled(true);
		cityListView.setAlwaysDrawnWithCacheEnabled(true);
		alphabetBar = (AlphabetBar) this.findViewById(R.id.sideBar);
		currentChar = (TextView) this.findViewById(R.id.currentChar);

		bar = new TitleBar(this);
		bar.setTitleName("城市选择");
		bar.switchToCityButton();
		bar.getRightCityButton().setVisibility(View.GONE);
		bar.getRightHomeButton().setVisibility(View.GONE);
		bar.setBackCallback(new Callback<Object>() {

			@Override
			public void handle(Object param) {
				// TODO Auto-generated method stub
				// doBack();
				finish();
			}
		});
	}

	@Override
	public void onSelected(int index, String name) {
		if (currentChar.getVisibility() != View.VISIBLE) {
			currentChar.setVisibility(View.VISIBLE);

		}
		currentChar.setText(name);
	}

	@Override
	public void onUnselected() {

		currentChar.setVisibility(View.INVISIBLE);
		currentChar.setText("");

	}

	public static class SectionAdapter extends ArrayAdapter<City> implements SectionIndexer {

		private static final int TYPE_SECTION_HEADER = 0;
		private static final int TYPE_LIST_ITEM = 1;

		int defaultRowLayoutResID;
		Context context;
		LayoutInflater inflater;
		ArrayList<City> items;
		int resource;

		public SectionAdapter(Context context, int resourceId, ArrayList<City> items) {
			super(context, resourceId, items);
			this.context = context;
			this.resource = resourceId;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.items = items;
		}

		@Override
		public City getItem(int position) {
			// TODO Auto-generated method stub
			return items.get(position);
			// return super.getItem(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// return super.getCount();
			return items.size();
		}

		@Override
		public int getViewTypeCount() {

			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			City item = items.get(position);
			if (item.isHeader) {
				return TYPE_SECTION_HEADER;
			} else {
				return TYPE_LIST_ITEM;
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			City item = getItem(position);

			if (convertView == null) {
				if (item.isHeader) {

					convertView = inflater.inflate(R.layout.platform_city_select_item, null);
					holder = new ViewHolder();
					holder.title = (TextView) convertView.findViewById(R.id.row_item_title);

					// holder.subtitle =
					// (TextView)convertView.findViewById(R.id.row_item_subtitle);
					convertView.setTag(holder);
				} else {
					convertView = inflater.inflate(R.layout.platform_city_select_item_title, null);
					holder = new ViewHolder();
					holder.title = (TextView) convertView.findViewById(R.id.row_item_title);
					// holder.subtitle =
					// (TextView)convertView.findViewById(R.id.row_item_subtitle);
					convertView.setTag(holder);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText(item.cityNameSimple);

			return convertView;
		}

		// Sort the city
		@Override
		public int getPositionForSection(int section) {
			String[] letters = (String[]) getSections();
			if (letters[section].equals("热门")) {
				return 0;
			}

			// 0 热门
			// 1 - A 2 -B , 3 - C ,4 - D
			for (int i = 14; i < items.size(); i++) {
				String le = PinyinConvertor.cn2py(items.get(i).cityNameSimple, 0);
				if (le != null && letters[section] != null)
					if (le.equalsIgnoreCase(letters[section])) {
						return i;
					}
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return new String[] { "热门", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
					"U", "V", "W", "X", "Y", "Z" };
		}

		private class ViewHolder {
			public TextView title;
		}

	}

	@Override
	protected void initListeners() {
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
