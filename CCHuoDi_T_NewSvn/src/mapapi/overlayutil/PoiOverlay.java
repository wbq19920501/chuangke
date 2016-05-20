package mapapi.overlayutil;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.search.poi.PoiResult;

/**
 * 鐢ㄤ簬鏄剧ずpoi鐨刼verly
 */
public class PoiOverlay extends OverlayManager {

    private static final int MAX_POI_SIZE = 10;

    private PoiResult mPoiResult = null;

    /**
     * 鏋勯�犲嚱鏁�
     * 
     * @param baiduMap
     *            璇� PoiOverlay 寮曠敤鐨� BaiduMap 瀵硅薄
     */
    public PoiOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    /**
     * 璁剧疆POI鏁版嵁
     * 
     * @param poiResult
     *            璁剧疆POI鏁版嵁
     */
    public void setData(PoiResult poiResult) {
        this.mPoiResult = poiResult;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {
        if (mPoiResult == null || mPoiResult.getAllPoi() == null) {
            return null;
        }
        List<OverlayOptions> markerList = new ArrayList<OverlayOptions>();
        int markerSize = 0;
        for (int i = 0; i < mPoiResult.getAllPoi().size()
                && markerSize < MAX_POI_SIZE; i++) {
            if (mPoiResult.getAllPoi().get(i).location == null) {
                continue;
            }
            markerSize++;
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            markerList.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_mark"
                            + markerSize + ".png")).extraInfo(bundle)
                    .position(mPoiResult.getAllPoi().get(i).location));
            
        }
        return markerList;
    }

    /**
     * 鑾峰彇璇� PoiOverlay 鐨� poi鏁版嵁
     * 
     * @return
     */
    public PoiResult getPoiResult() {
        return mPoiResult;
    }

    /**
     * 瑕嗗啓姝ゆ柟娉曚互鏀瑰彉榛樿鐐瑰嚮琛屼负
     * 
     * @param i
     *            琚偣鍑荤殑poi鍦�
     *            {@link mapapi.search.poi.PoiResult#getAllPoi()} 涓殑绱㈠紩
     * @return
     */
    public boolean onPoiClick(int i) {
//        if (mPoiResult.getAllPoi() != null
//                && mPoiResult.getAllPoi().get(i) != null) {
//            Toast.makeText(BMapManager.getInstance().getContext(),
//                    mPoiResult.getAllPoi().get(i).name, Toast.LENGTH_LONG)
//                    .show();
//        }
        return false;
    }

    @Override
    public final boolean onMarkerClick(Marker marker) {
        if (!mOverlayList.contains(marker)) {
            return false;
        }
        if (marker.getExtraInfo() != null) {
            return onPoiClick(marker.getExtraInfo().getInt("index"));
        }
        return false;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        // TODO Auto-generated method stub
        return false;
    }
}
