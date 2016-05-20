package cn.com.easytaxi.airport;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.airport.adpter.AirportSectionAdapter;
import cn.com.easytaxi.airport.bean.AirportBean;
import cn.com.easytaxi.airport.store.AirportSessionAdapter;
import cn.com.easytaxi.airport.util.PinyinCompareAirport;
import cn.com.easytaxi.airport.view.AlphabetBarAirport;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.airport.view.AlphabetBarAirport.OnSelectedListener;

public class AirportSelectActivity extends Activity implements OnSelectedListener {
	public static final String tag = "AirportSelectActivity";

	public boolean isFirst = true;
    private String cityName;
    private String choosedAirportName="";
	private ListView airportListView;
	private AlphabetBarAirport alphabetBar;
	private TextView currentChar;
	private AirportSectionAdapter airportListAdapter; 
	private TextView titleView;
	private ImageButton backButton;
	AirportSessionAdapter airportSessionAdapter;
	
	/**
	 * 用来记录所有城市的列表数据在列表中的开始位置
	 */
	int startIndex;
	/**
	 * 所有机场列表
	 */
	private ArrayList<AirportBean> airportBeanSessionList = new ArrayList<AirportBean>();
	/**
	 * 当前选中的城市的机场列表
	 */
	private ArrayList<AirportBean> airportBeanList = new ArrayList<AirportBean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.airport_list);
		//获取当前城市名字
		cityName = getIntent().getExtras().getString("cityName");
		choosedAirportName =  getIntent().getExtras().getString("choosedAirportName");
		initViews();
		initData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 初始化View控件
	 */
	public void initViews() {
		airportListView = (ListView) this.findViewById(R.id.airport_listview);
		airportListView.setAnimationCacheEnabled(true);
		airportListView.setAlwaysDrawnWithCacheEnabled(true);
		
		alphabetBar = (AlphabetBarAirport) this.findViewById(R.id.sideBar);
		
		currentChar = (TextView) this.findViewById(R.id.currentChar);
		
		titleView = (TextView)findViewById(R.id.map_title_mid_tv);
		titleView.setText("接机送机");
		
		backButton = (ImageButton)findViewById(R.id.map_title_leftbtn);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	public void initData() {
		airportBeanSessionList = ETApp.getInstance().getAirportListCache();
		initAirportList();
        //机场列表装载Adapter
		airportListAdapter = new AirportSectionAdapter(this, 0, airportBeanList);
		airportListAdapter.setChoosedAirportName(choosedAirportName);
		airportListAdapter.setStartIndex(startIndex);
		airportListView.setAdapter(airportListAdapter);
		airportListView.setDividerHeight(0);
		airportListView.setOnItemClickListener(itemClickListener);
		alphabetBar.setOnSelectedListener(this);
		alphabetBar.setList(airportListView);
	}
	
	/**
	 * 初始化机场清单列表
	 */
	private void initAirportList() {
		//排序
		PinyinCompareAirport comparator = new PinyinCompareAirport();
		//初始化城市机场列表Session
		if (airportBeanSessionList == null || airportBeanSessionList.size() <= 0) {
			airportSessionAdapter = new AirportSessionAdapter(getBaseContext());
			airportBeanSessionList = airportSessionAdapter.getAirportList(); // normal city
			Collections.sort(airportBeanSessionList, comparator);
			ETApp.getInstance().setAirportListCache(airportBeanSessionList);
		}
		
		airportBeanList.addAll(0, airportBeanSessionList);
		AirportBean header = new AirportBean();
		header.name ="全部机场";
		header.isHeader = true;
		airportBeanList.add(0, header);
		startIndex = startIndex + 1;
		
		//匹配当前城市的机场列表
		String cityNameStr = cityName.substring(0, 2);
		boolean isThereAirport = false;
		for(int i=airportBeanSessionList.size()-1; i>=0 ;i--){
			 if(airportBeanSessionList.get(i).getName().startsWith(cityNameStr)){
				 airportBeanList.add(0,airportBeanSessionList.get(i));
				 isThereAirport = true;
				 startIndex++;
			 }
		}
		if(isThereAirport){
			AirportBean header1 = new AirportBean();
			header1.name ="所选城市机场";
			header1.isHeader = true;
			airportBeanList.add(0, header1);
			startIndex++;
		}
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

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
			Object obj = airportListView.getItemAtPosition(position);//会调用adapter的getItem方法
			if (obj != null) {
				AirportBean airportBean = (AirportBean) obj;
				if (airportBean.name.equals("全部机场") || airportBean.name.equals("所选城市机场") || airportBean.name.equals(choosedAirportName)) {
					return;
				}
				
				Intent airportInfo = new Intent();
				airportInfo.putExtra("id", airportBean.id);
				airportInfo.putExtra("name", airportBean.name);
				airportInfo.putExtra("longitude", airportBean.longitude);
				airportInfo.putExtra("latitude", airportBean.latitude);
				setResult(RESULT_OK, airportInfo);
				finish();
			}

		}

	};

}
