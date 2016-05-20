package cn.com.easytaxi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;

public class RetryAndLoadBar extends LinearLayout {
	  private LinearLayout layout;
	  private TextView loadingTile = null;
	  private Context mContext = null;
	  private LinearLayout shortLayout;
	  private TextView shortText;
	  
	  private FrameLayout thisView;

	  public RetryAndLoadBar(Context context)
	  {
	    super(context);
	    this.mContext = context;
	    initView();
	  }

	  public RetryAndLoadBar(Context context, AttributeSet attributeSet)
	  {
	    super(context, attributeSet);
	    this.mContext = context;
	    initView();
	  }
 

	  public void dismiss()
	  {
	    this.thisView.setVisibility(View.GONE);
	  }

	  public void initView()
	  { 
	    LayoutInflater.from(this.mContext).inflate(R.layout.wb_load_retry_footer, this, true);
	    this.loadingTile = ((TextView)findViewById(R.id.loading_textview));
	    this.thisView = ((FrameLayout)findViewById(R.id.loading_and_retry_bar));
	    this.layout = ((LinearLayout)findViewById(R.id.layout));
	    this.shortLayout = ((LinearLayout)findViewById(R.id.layout_short));
	    this.shortText = ((TextView)findViewById(R.id.loading_textview_short));
	    this.loadingTile.setText(getContext().getString(R.string.load_ing));
	  }

	  public void setErrorMsg(String paramString)
	  {
	    this.thisView.setVisibility(View.VISIBLE);
	    this.thisView.setEnabled(true);
	    this.layout.setVisibility(View.GONE);
	    this.shortLayout.setVisibility(View.VISIBLE);
	    this.shortText.setText(getContext().getString(R.string.load_error));
	  }

	  public void showNoDateMsg()
	  {
	    this.thisView.setVisibility(View.VISIBLE);
	    this.thisView.setEnabled(false);
	    this.layout.setVisibility(View.GONE);
	    this.shortText.setText(getContext().getString(R.string.load_empty));
	    this.shortLayout.setVisibility(View.VISIBLE);
	    this.thisView.invalidate();
	  }

	  public void setRetryButtonOnClickListener(View.OnClickListener onClickListener)
	  {
	    this.loadingTile.setOnClickListener(onClickListener);
	  }

	  public void showLoadingBar()
	  {
	    this.thisView.setVisibility(0);
	    this.layout.setVisibility(0);
	    this.shortLayout.setVisibility(8);
	    this.loadingTile.setText("正在获取更多...");
	  }
	  public void showFullMsg()
	  {
		    this.thisView.setVisibility(View.VISIBLE);
		    this.thisView.setEnabled(false);
		    this.layout.setVisibility(View.GONE);
		    this.shortText.setText("已加载全部");
		    this.shortLayout.setVisibility(View.VISIBLE);
		    this.thisView.invalidate();
	  }

	  
	  public void showMoreMsg()
	  {
		  this.thisView.setVisibility(View.VISIBLE);
		  this.thisView.setEnabled(false);
		  this.layout.setVisibility(View.GONE);
		  this.shortText.setText("更多");
		  this.shortLayout.setVisibility(View.VISIBLE);
		  this.thisView.invalidate();
	  }
	  
	  
	  
	  
	  public void showNoIndicatorBar(String info)
	  {
	    this.thisView.setVisibility(View.VISIBLE);
	    this.layout.setVisibility(View.GONE);
	    this.shortLayout.setVisibility(View.VISIBLE);
	    this.shortText.setText(info.trim());
	  }
}
