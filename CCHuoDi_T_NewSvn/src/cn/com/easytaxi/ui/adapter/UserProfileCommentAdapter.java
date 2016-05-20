package cn.com.easytaxi.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.ui.bean.YDUserComments.YDUserComment;
import cn.com.easytaxi.util.InfoTool;

public class UserProfileCommentAdapter extends BaseAdapter {

	private ArrayList<YDUserComment> userCommnets = new ArrayList<YDUserComment>(12);
	private Context context;
	private LayoutInflater inflater;
	private Resources resources;
	private Drawable goodFlag;
	private Drawable badFlag;

	private static final String tag = "UserProfileCommentAdapter";

	public UserProfileCommentAdapter(Context context, ArrayList<YDUserComment> data) {
		userCommnets = data;
		this.context = context;
		inflater = LayoutInflater.from(context);
		resources = context.getResources();
		goodFlag = resources.getDrawable(R.drawable.pic1008);
		badFlag = resources.getDrawable(R.drawable.pic1007);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userCommnets.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return userCommnets.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getUserCommentView(position, convertView, parent);

	}

	private View getUserCommentView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final YDUserComment comment = (YDUserComment) getItem(position);
		if (convertView == null || convertView.getTag() == null) {
			convertView = inflater.inflate(R.layout.p_user_profile_comment_item, null);
			holder = new ViewHolder();
			holder.comment_flag = ((ImageView) convertView.findViewById(R.id.comment_flag));
			holder.comment_user_name = ((TextView) convertView.findViewById(R.id.comment_user));
			holder.comment_time = ((TextView) convertView.findViewById(R.id.comment_time));
			holder.comment_content = ((TextView) convertView.findViewById(R.id.comment_content));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.comment_user_name.setText(TextUtils.isEmpty(comment.userName) ? comment.suggesterId + "" : comment.userName);
		holder.comment_content.setText(comment.commentContent);
		holder.comment_time.setText(InfoTool.friendlyDate(resources, comment.commentTime));

		// บรฦภ
		final int commentType = comment.commentType;
		if (commentType >= 80) {
			
			holder.comment_flag.setImageDrawable(goodFlag);
		} else {
			holder.comment_flag.setImageDrawable(badFlag);
		}
		
		if(position%2 != 0){
			convertView.findViewById(R.id.bggggggg).setBackgroundColor(Color.rgb(210, 210, 210));
		}else{
			convertView.findViewById(R.id.bggggggg).setBackgroundColor(Color.rgb(243, 243, 243));
			
		}
		

		return convertView;
	}

	class ViewHolder {

		public TextView comment_content;
		public TextView comment_time;
		public TextView comment_user_name;
		public ImageView comment_flag;

	}

}
