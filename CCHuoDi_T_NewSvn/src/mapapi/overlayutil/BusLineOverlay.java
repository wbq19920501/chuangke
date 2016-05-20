package mapapi.overlayutil;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;

/**
 * 鐢ㄤ簬鏄剧ず涓�鏉″叕浜よ鎯呯粨鏋滅殑Overlay
 */
public class BusLineOverlay extends OverlayManager {

    private BusLineResult mBusLineResult = null;

    /**
     * 鏋勯�犲嚱鏁�
     * 
     * @param baiduMap
     *            璇usLineOverlay鎵�寮曠敤鐨� BaiduMap 瀵硅薄
     */
    public BusLineOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    /**
     * 璁剧疆鍏氦绾挎暟鎹�
     * 
     * @param result
     *            鍏氦绾胯矾缁撴灉鏁版嵁
     */
    public void setData(BusLineResult result) {
        this.mBusLineResult = result;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {

        if (mBusLineResult == null || mBusLineResult.getStations() == null) {
            return null;
        }
        List<OverlayOptions> overlayOptionses = new ArrayList<OverlayOptions>();
        for (BusLineResult.BusStation station : mBusLineResult.getStations()) {
            overlayOptionses.add(new MarkerOptions()
                    .position(station.getLocation())
                            .zIndex(10)
                                    .anchor(0.5f, 0.5f)
                                            .icon(BitmapDescriptorFactory
                                                    .fromAssetWithDpi("Icon_bus_station.png")));
        }

        List<LatLng> points = new ArrayList<LatLng>();
        for (BusLineResult.BusStep step : mBusLineResult.getSteps()) {
            if (step.getWayPoints() != null) {
                points.addAll(step.getWayPoints());
            }
        }
        if (points.size() > 0) {
            overlayOptionses
                    .add(new PolylineOptions().width(10)
                            .color(Color.argb(178, 0, 78, 255)).zIndex(0)
                                    .points(points));
        }
        return overlayOptionses;
    }

    /**
     * 瑕嗗啓姝ゆ柟娉曚互鏀瑰彉榛樿鐐瑰嚮琛屼负
     * 
     * @param index
     *            琚偣鍑荤殑绔欑偣鍦�
     *            {@link mapapi.search.busline.BusLineResult#getStations()}
     *            涓殑绱㈠紩
     * @return 鏄惁澶勭悊浜嗚鐐瑰嚮浜嬩欢
     */
    public boolean onBusStationClick(int index) {
        if (mBusLineResult.getStations() != null
                && mBusLineResult.getStations().get(index) != null) {
            Log.i("baidumapsdk", "BusLineOverlay onBusStationClick");
        }
        return false;
    }

    public final boolean onMarkerClick(Marker marker) {
        if (mOverlayList != null && mOverlayList.contains(marker)) {
            return onBusStationClick(mOverlayList.indexOf(marker));
        } else {
            return false;
        }
        
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        // TODO Auto-generated method stub
        return false;
    }
}
