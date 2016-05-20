package cn.com.easytaxi.onetaxi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.easytaxi.etpassengersx.R;


public class TaxisOverlay {
	
	public List<OverlayOptions> taxisList = new ArrayList<OverlayOptions>();

 
	public List<OverlayOptions> getTaxisList() {
		return taxisList;
	}

	public void setFakeData(Drawable marker){
		LatLng point = new LatLng(30670800, 104032400);
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.my);
		taxisList.add(new MarkerOptions().position(point).icon(bitmap));
//		taxisList.add(new TaxiOverlay(new GeoPoint(30670800, 104032400), "", "", marker));
	}
	
	public void setData(JSONArray cars , Drawable marker) {
		taxisList.clear();
		
		OverlayOptions taxi;
		final int len = cars.length();
		for (int i = 0; i < len; i++) {
//			taxi = new TaxiOverlay(new GeoPoint(30670884, 104032457), "", "", marker);
//			taxisList.add(taxi);
			try {
				final JSONObject object = (JSONObject) cars.get(i);

				final int carLat = object.getInt("lat");
				final int carLng = object.getInt("lng");
				final int state = object.getInt("state");
				 
				if (state == 0) {
					LatLng point = new LatLng(carLat, carLng);
					BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.my);
					taxi = new MarkerOptions().position(point).icon(bitmap);
//					taxi = new TaxiOverlay(new GeoPoint(carLat, carLng), "", "", marker);
					taxisList.add(taxi);
				 }
			} catch (JSONException e) {

				e.printStackTrace();
				 
			}
		}
		
	}
	
}
