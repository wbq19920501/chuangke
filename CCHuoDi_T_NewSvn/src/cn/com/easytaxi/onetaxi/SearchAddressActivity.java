package cn.com.easytaxi.onetaxi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.easytaxi.AppLog;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.platform.view.AddressEditView;
import cn.com.easytaxi.platform.view.AddressEditView.OnEditOverListner;
import cn.com.easytaxi.workpool.bean.GeoPointLable;

public class SearchAddressActivity extends Activity implements OnItemClickListener ,  OnEditOverListner{

	protected static final String tag = "SearchAddressActivity";
	private TitleBar bar;
	private SessionAdapter session;
	private String mobile;
	private RadioGroup radioGroup_choice;
	private ListView addressListView;
	private String cityName;
	private String cityId;
	private cn.com.easytaxi.platform.view.AddressEditView search_button_panel;

	private List<GeoPointLable> history = new ArrayList<GeoPointLable>(12);
	private List<GeoPointLable> usual = new ArrayList<GeoPointLable>(12);
	private List<GeoPointLable> current = new ArrayList<GeoPointLable>(12);
	
	private GeoLableAdapter currentAdapter;
	 
	private BroadcastReceiver addUsualReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			 if(intent != null){
				 String name = intent.getStringExtra("address");
				 int lat = intent.getIntExtra("lat", 0);
				 int lng = intent.getIntExtra("lng", 0);
				 if(lat != 0 && lng != 0 && StringUtils.isEmpty(name)){
					 GeoPointLable gp = new GeoPointLable(lat, lng, name,"");
					 if(usual != null){
						 usual.add(0,gp);
					 }
				 }
			 }
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ontexi_address_search);
		initDbData();
		initViews();
		initListener();
		initUserData();
		registerReceiver(addUsualReceiver, new IntentFilter("cn.com.easytaxi.action.add.usual.address"));
	}

	private void initUserData() {
		search_button_panel.setCityName(cityName);
		
		currentAdapter = new GeoLableAdapter(current,this);
		currentAdapter.setType(2);
		addressListView.setAdapter(currentAdapter);
		 //history = 1
		history = session.getPois(cityName, 1 ,mobile);
		//usual = 2
		usual = session.getPois(cityName, 2 ,mobile);
		
		refreshData(radioGroup_choice.getCheckedRadioButtonId() );
	}

	private void refreshData(int id) {
		if(id == R.id.radio_usual){
			current.clear();
			usual = session.getPois(cityName, 2 ,mobile);
			current.addAll(usual);
			currentAdapter.setType(2);
		}
		
		if(id == R.id.radio_history){
			current.clear();
			history = session.getPois(cityName, 1 ,mobile);
			current.addAll(history);
			currentAdapter.setType(1);
		}

		currentAdapter.notifyDataSetChanged();
	}

	private void initListener() {
		findViewById(R.id.ontexi_address_input).setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				try{
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						InputMethodManager imm = (InputMethodManager) SearchAddressActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					}
				}catch (Exception e) {
				}
				return false;
			}
		});
		
		search_button_panel.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		search_button_panel.setOnEditOverListner(new OnEditOverListner() {
			
			@Override
			public void OnEditOver(AddressEditView view) {
				Intent intent = new Intent();
				 
				GeoPointLable gp = (GeoPointLable) view.getTag();
				if(gp == null){
					return;
				}
				intent.putExtra("address", gp.getName());
				intent.putExtra("lat", gp.getLat());
				intent.putExtra("lng", gp.getLog());
				 
				
				AppLog.LogD("gp.getName() -000000000-- "+ gp.getName() + " ,lat ====  "+ gp.getLat() + " ; lng =="+gp.getLog());
				setResult(RESULT_OK, intent);
				 finish();
				
			}
		});
		
		
		addressListView.setOnItemClickListener(this);
		addressListView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideInputMethod(v);
				return false;
			}
		});
		
		radioGroup_choice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup view, int id) {
				refreshData(id);
				hideInputMethod(view);
			}
		});
		
		bar.setBackCallback(new Callback<Object>() {

			@Override
			public void handle(Object param) {

				doBack();
			}
		});
	}

	
	
	protected void doBack() {
		this.finish();
	}

	private void initViews() {
		 
		radioGroup_choice = (RadioGroup) findViewById(R.id.radioGroup_choice);
		addressListView = (ListView)findViewById(R.id.listView_address);

		bar = new TitleBar(this);
		bar.setTitleName("µÿ÷∑—°‘Ò");
		bar.switchToCityButton();
		bar.getRightCityButton().setVisibility(View.GONE);
		bar.getRightHomeButton().setVisibility(View.GONE);
		
		search_button_panel = (AddressEditView) findViewById(R.id.search_button_panel);
	}

	private void initDbData() {
		session = new SessionAdapter(SearchAddressActivity.this);
		mobile = session.get("_MOBILE");
		Intent intent = getIntent();
		cityName = intent.getStringExtra("cityName");
		if(TextUtils.isEmpty(cityName)){
			cityName = session.get("_CITY_NAME");
		}
		
		AppLog.LogD("initDbData  cityName " + cityName);
	}

	@Override
	protected void onDestroy() {

		if (bar != null) {
			bar.close();
		}
		if(addUsualReceiver != null){
			unregisterReceiver(addUsualReceiver);
		}
		if (session != null) {
			session.close();
			session = null;
		}

		super.onDestroy();
	}
	
	 class GeoLableAdapter extends BaseAdapter{
		private List<GeoPointLable> data;
		private Context context;
		private LayoutInflater inflater;
		private int type = 1;
		
		public GeoLableAdapter(List<GeoPointLable>  data , Context context){
			this.data = data;
			this.context = context;
		 	inflater = LayoutInflater.from(context);
		 	 
		}
		
		public void setType(int type){
			this.type = type;
		}
		
		@Override
		public int getCount() {
			return data == null ? 0 : data.size();
		}

		@Override
		public Object getItem(int location) {
		
			return data.get(location);
		}

		@Override
		public long getItemId(int arg0) {
		
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
 
			final GeoPointLable  gp = data.get(position);
			Holder holder ;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.onetaxi_search_address_item, null);
				holder = new Holder();
				holder.add = (Button) convertView.findViewById(R.id.search_add);
				holder.infoTv = (TextView)convertView.findViewById(R.id.search_address);
				holder.address_parent = (RelativeLayout)convertView.findViewById(R.id.address_parent);
				convertView.setTag(holder);
			}else{
				
			}
 	
			holder = (Holder) convertView.getTag();
		/*	if(position % 2 == 0){
				holder.address_parent.setBackgroundResource(R.drawable.btn038);
			}else{
				holder.address_parent.setBackgroundResource(R.drawable.btn037);
			}*/
			
			holder.infoTv.setText(gp.getName());
			holder.add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//session.savePoi(cityName, mobile, type, gp);
					session.deletePoi(cityName,mobile,type,gp); //type 1 or 2
					data.remove(gp);
					notifyDataSetChanged();
				}
			});
			
			return convertView;
		}
		
		
		class Holder{
			public TextView infoTv;
			public  RelativeLayout address_parent;
			public Button add;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
		Intent intent = new Intent();
		if(position >= 0 ){ 
			GeoPointLable gp = (GeoPointLable) currentAdapter.getItem(position);
			intent.putExtra("address", gp.getName());
			intent.putExtra("lat", gp.getLat());
			intent.putExtra("lng", gp.getLog());
			
			
//			AppLog.LogD("gp.getName() --- "+ gp.getName() + " ,lat ====  "+ gp.getLat() + " ; lng =="+gp.getLog());
			setResult(RESULT_OK, intent);
			this.finish();
		}
	}

	@Override
	public void OnEditOver(AddressEditView view) {
 
		Intent intent = new Intent();
		 
			GeoPointLable gp = (GeoPointLable) view.getTag();
			if(gp == null){
				return;
			}
			intent.putExtra("address", gp.getName());
			intent.putExtra("lat", gp.getLat());
			intent.putExtra("lng", gp.getLog());
			
			
			AppLog.LogD("gp.getName() --- "+ gp.getName() + " ,lat ====  "+ gp.getLat() + " ; lng =="+gp.getLog());
			setResult(RESULT_OK, intent);
			this.finish();
		 
	}
	
	public void hideInputMethod(View v){
		try{
			InputMethodManager imm = (InputMethodManager) SearchAddressActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}catch (Exception e) {
		}
	}
}
