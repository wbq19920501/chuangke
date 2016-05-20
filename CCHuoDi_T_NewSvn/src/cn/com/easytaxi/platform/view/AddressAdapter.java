package cn.com.easytaxi.platform.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.workpool.bean.GeoPointLable;

public class AddressAdapter extends ArrayAdapter<GeoPointLable> {
	Context ctx;
	ArrayList<GeoPointLable> datas = new ArrayList<GeoPointLable>();
	LayoutInflater mInflater;
	int layout;
	int viewId;
	SessionAdapter session;

	public AddressAdapter(Context ctx, int layout, int viewId ,SessionAdapter session) {
		super(ctx, layout, viewId);
		this.layout = layout;
		this.viewId = viewId;
		this.ctx = ctx;
		this.session = session;
		mInflater = LayoutInflater.from(ctx);
	}

	public int getCount() {
		return datas.size();
	}

	public GeoPointLable getItem(int position) {
		return datas.get(position);
	}

	public GeoPointLable getGeo(int position ,String name) {
		return new GeoPointLable(getItem(position).getLat(), getItem(position).getLog(), name, "");
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		TextView text;
		final GeoPointLable gp =  getItem(position);
		Button btn;
		if (convertView == null) {
			view = mInflater.inflate(layout, parent, false);
		} else {
			view = convertView;
		}
		text = (TextView) view.findViewById(viewId);
		btn = (Button)view.findViewById(R.id.button_addd);
		text.setText(getItem(position).getName());
		 
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				session.savePoi(session.get("_CITY_NAME"), session.get("_MOBILE"), 2, gp)  ;
				Intent intent = new Intent("cn.com.easytaxi.action.add.usual.address");
				ctx.sendBroadcast(intent);
			}
		});
		
		
		view.setTag(getGeo(position, getItem(position).getName()));
		
		
		
		return view;
	}

	public ArrayList<GeoPointLable> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<GeoPointLable> datas) {
		this.datas = datas;
	}

}
