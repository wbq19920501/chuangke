package cn.com.easytaxi.expandable;

import java.util.List;

/**
 * @ClassName: Group 
 * @Description: TODO
 * @author Brook xu
 * @date 2013-7-4 ÏÂÎç1:53:01 
 * @version 1.0
 */
public class Group {
	private String groupName;
	private List<Child> childrenList;
	private int index;
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<Child> getChildrenList() {
		return childrenList;
	}

	public void setChildrenList(List<Child> childrenList) {
		this.childrenList = childrenList;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
