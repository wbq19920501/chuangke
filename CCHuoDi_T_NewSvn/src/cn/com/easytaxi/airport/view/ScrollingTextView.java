package cn.com.easytaxi.airport.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * �����Ч����TextView
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
     * ��д�˷���������true����ΪTextView�������Ч�������ж����ȡ�����ʱ���ƶ�
     */
    @Override  
    public boolean isFocused() {  
        return true;  
    }
	
}