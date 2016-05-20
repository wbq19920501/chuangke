package cn.com.easytaxi.expandable;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;

/**
 * 多级列表适配器
 * 
 * @ClassName: ExpAdapter
 * @Description: TODO
 * @author Brook xu
 * @date 2013-7-4 下午1:54:04
 * @version 1.0
 */
public class ExpAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<Group> groupData;
	private LayoutInflater mInflater;
	private int selectChildIndex;
	private int selectGroupIndex;

	public ExpAdapter(Context context, List<Group> groups) {
		this.context = context;
		this.groupData = groups;
		mInflater = LayoutInflater.from(context);
		selectChildIndex = 1;
		selectGroupIndex = 0;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return groupData.get(groupPosition).getChildrenList().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ChildViewHolder viewHolder = null;
		Child child = groupData.get(groupPosition).getChildrenList().get(childPosition);

		if (convertView == null) {
			viewHolder = new ChildViewHolder();
			convertView = (View) mInflater.inflate(R.layout.stopservice_child_listitem, null);
			viewHolder.headImage = (ImageView) convertView.findViewById(R.id.child_image);
			viewHolder.contactName = (TextView) convertView.findViewById(R.id.child_text_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChildViewHolder) convertView.getTag();
		}

//		Bitmap image = child.getHeadImage();
//		if (image == null) {
//			viewHolder.headImage.setImageResource(R.drawable.pic_stopservice_item_default);
//		} else {
//			viewHolder.headImage.setImageBitmap(image);
//		}
		
		viewHolder.contactName.setText(child.getName());

		if (selectChildIndex == child.getIndex()) {
			viewHolder.headImage.setImageResource(R.drawable.pic_stopservice_item_checked);
			viewHolder.contactName.setTextColor(context.getResources().getColor(R.color.goupitem_txt_checked));
		} else {
			viewHolder.headImage.setImageResource(R.drawable.pic_stopservice_item_default);
			viewHolder.contactName.setTextColor(context.getResources().getColor(R.color.goupitem_txt_default));
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return groupData.get(groupPosition).getChildrenList().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groupData.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GroupViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new GroupViewHolder();
			convertView = (View) mInflater.inflate(R.layout.stopservice_group_listitem, null);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.child_image);
			viewHolder.groupName = (TextView) convertView.findViewById(R.id.child_text_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (GroupViewHolder) convertView.getTag();
		}

		if (isExpanded) {
			viewHolder.icon.setImageResource(R.drawable.pic_stopservice_item_checked);
			viewHolder.groupName.setTextColor(context.getResources().getColor(R.color.goupitem_txt_checked));
		} else {
			viewHolder.icon.setImageResource(R.drawable.pic_stopservice_item_default);
			viewHolder.groupName.setTextColor(context.getResources().getColor(R.color.goupitem_txt_default));
		}

		viewHolder.groupName.setText(groupData.get(groupPosition).getGroupName());

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	public static class GroupViewHolder {
		ImageView icon;
		TextView groupName;
	}

	public static class ChildViewHolder {
		ImageView headImage;
		TextView contactName;
	}

	public int getSelectChildIndex() {
		return selectChildIndex;
	}

	public void setSelectChildIndex(int selectChildIndex) {
		this.selectChildIndex = selectChildIndex;
	}

	public int getSelectGroupIndex() {
		return selectGroupIndex;
	}

	public void setSelectGroupIndex(int selectGroupIndex) {
		this.selectGroupIndex = selectGroupIndex;
	}

}