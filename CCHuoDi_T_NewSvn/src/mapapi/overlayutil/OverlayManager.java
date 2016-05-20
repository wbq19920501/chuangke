package mapapi.overlayutil;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnPolylineClickListener;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLngBounds;

/**
 * 璇ョ被鎻愪緵涓�涓兘澶熸樉绀哄拰绠＄悊澶氫釜Overlay鐨勫熀绫�
 * <p>
 * 澶嶅啓{@link #getOverlayOptions()} 璁剧疆娆叉樉绀哄拰绠＄悊鐨凮verlay鍒楄〃
 * </p>
 * <p>
 * 閫氳繃
 * {@link mapapi.map.BaiduMap#setOnMarkerClickListener(mapapi.map.BaiduMap.OnMarkerClickListener)}
 * 灏嗚鐩栫墿鐐瑰嚮浜嬩欢浼犻�掔粰OverlayManager鍚庯紝OverlayManager鎵嶈兘鍝嶅簲鐐瑰嚮浜嬩欢銆�
 * <p>
 * 澶嶅啓{@link #onMarkerClick(mapapi.map.Marker)} 澶勭悊Marker鐐瑰嚮浜嬩欢
 * </p>
 */
public abstract class OverlayManager implements OnMarkerClickListener, OnPolylineClickListener {

    BaiduMap mBaiduMap = null;
    private List<OverlayOptions> mOverlayOptionList = null;

    List<Overlay> mOverlayList = null;

    /**
     * 閫氳繃涓�涓狟aiduMap 瀵硅薄鏋勯��
     * 
     * @param baiduMap
     */
    public OverlayManager(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
        // mBaiduMap.setOnMarkerClickListener(this);
        if (mOverlayOptionList == null) {
            mOverlayOptionList = new ArrayList<OverlayOptions>();
        }
        if (mOverlayList == null) {
            mOverlayList = new ArrayList<Overlay>();
        }
    }

    /**
     * 瑕嗗啓姝ゆ柟娉曡缃绠＄悊鐨凮verlay鍒楄〃
     * 
     * @return 绠＄悊鐨凮verlay鍒楄〃
     */
    public abstract List<OverlayOptions> getOverlayOptions();

    /**
     * 灏嗘墍鏈塐verlay 娣诲姞鍒板湴鍥句笂
     */
    public final void addToMap() {
        if (mBaiduMap == null) {
            return;
        }

        removeFromMap();
        List<OverlayOptions> overlayOptions = getOverlayOptions();
        if (overlayOptions != null) {
            mOverlayOptionList.addAll(getOverlayOptions());
        }

        for (OverlayOptions option : mOverlayOptionList) {
            mOverlayList.add(mBaiduMap.addOverlay(option));
        }
    }

    /**
     * 灏嗘墍鏈塐verlay 浠� 鍦板浘涓婃秷闄�
     */
    public final void removeFromMap() {
        if (mBaiduMap == null) {
            return;
        }
        for (Overlay marker : mOverlayList) {
            marker.remove();
        }
        mOverlayOptionList.clear();
        mOverlayList.clear();

    }

    /**
     * 缂╂斁鍦板浘锛屼娇鎵�鏈塐verlay閮藉湪鍚堥�傜殑瑙嗛噹鍐�
     * <p>
     * 娉細 璇ユ柟娉曞彧瀵筂arker绫诲瀷鐨刼verlay鏈夋晥
     * </p>
     * 
     */
    public void zoomToSpan() {
        if (mBaiduMap == null) {
            return;
        }
        if (mOverlayList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Overlay overlay : mOverlayList) {
                // polyline 涓殑鐐瑰彲鑳藉お澶氾紝鍙寜marker 缂╂斁
                if (overlay instanceof Marker) {
                    builder.include(((Marker) overlay).getPosition());
                }
            }
            mBaiduMap.setMapStatus(MapStatusUpdateFactory
                    .newLatLngBounds(builder.build()));
        }
    }

}
