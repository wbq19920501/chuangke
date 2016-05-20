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
	 * ������¼���г��е��б��������б��еĿ�ʼλ��
	 */
	int startIndex;
	/**
	 * ���л����б�
	 */
	private ArrayList<AirportBean> airportBeanSessionList = new ArrayList<AirportBean>();
	/**
	 * ��ǰѡ�еĳ��еĻ����б�
	 */
	private ArrayList<AirportBean> airportBeanList = new ArrayList<AirportBean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.airport_list);
		//��ȡ��ǰ��������
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
	 * ��ʼ��View�ؼ�
	 */
	public void initViews() {
		airportListView = (ListView) this.findViewById(R.id.airport_listview);
		airportListView.setAnimationCacheEnabled(true);
		airportListView.setAlwaysDrawnWithCacheEnabled(true);
		
		alphabetBar = (AlphabetBarAirport) this.findViewById(R.id.sideBar);
		
		currentChar = (TextView) this.findViewById(R.id.currentChar);
		
		titleView = (TextView)findViewById(R.id.map_title_mid_tv);
		titleView.setText("�ӻ��ͻ�");
		
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
        //�����б�װ��Adapter
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
	 * ��ʼ�������嵥�б�
	 */
	private void initAirportList() {
		//����
		PinyinCompareAirport comparator = new PinyinCompareAirport();
		//��ʼ�����л����б�Session
		if (airportBeanSessionList == null || airportBeanSessionList.size() <= 0) {
			airportSessionAdapter = new AirportSessionAdapter(getBaseContext());
			airportBeanSessionList = airportSessionAdapter.getAirportList(); // normal city
			Collections.sort(airportBeanSessionList, comparator);
			ETApp.getInstance().setAirportListCache(airportBeanSessionList);
		}
		
		airportBeanList.addAll(0, airportBeanSessionList);
		AirportBean header = new AirportBean();
		header.name ="ȫ������";
		header.isHeader = true;
		airportBeanList.add(0, header);
		startIndex = startIndex + 1;
		
		//ƥ�䵱ǰ���еĻ����б�
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
			header1.name ="��ѡ���л���";
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
			Object obj = airportListView.getItemAtPosition(position);//�����adapter��getItem����
			if (obj != null) {
				AirportBean airportBean = (AirportBean) obj;
				if (airportBean.name.equals("ȫ������") || airportBean.name.equals("��ѡ���л���") || airportBean.name.equals(choosedAirportName)) {
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
