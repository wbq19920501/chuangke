package cn.com.easytaxi.airport.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 跑马灯效果的TextView
 * @author longhuaiyang
 *
 */
public class ScrollingTextView extends TextView {
	
	public ScrollingTextView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
   
    public ScrollingTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
   
    public ScrollingTextView(Context context) {  
        super(context);  
    }  
    
    /**
     * 重写此方法：返回true，因为TextView的跑马灯效果是在判断其获取焦点的时候移动
     */
    @Override  
    public boolean isFocused() {  
        return true;  
    }
	
}