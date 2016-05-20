package cn.com.easytaxi.onetaxi;

import com.baidu.mapapi.map.Overlay;

public class PassengerOverlays extends Overlay {

	private int lng;
	private int lat;

	public PassengerOverlays(int lng, int lat) {
		this.lat = lat;
		this.lng = lng;
	}

//	@Override
//	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
//		Projection projection = mapView.getProjection();
//		Point point = new Point();
//		GeoPoint geoPoint=new GeoPoint(lat, lng);
//		projection.toPixels(geoPoint, point);
//		
//		Bitmap bitmap = BitmapFactory.decodeResource(mapView.getResources(), R.drawable.my);
//		Paint paint = new Paint();
//		canvas.drawBitmap(bitmap, point.x - bitmap.getWidth() / 2, point.y - bitmap.getHeight() /2, paint);
//	}
}
