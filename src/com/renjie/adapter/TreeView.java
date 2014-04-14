package com.renjie.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class TreeView extends ListView implements OnItemClickListener {
	String TAG = "TreeView";
	List<TreeElement> treeElements = null;// 所有节点集合
	List<TreeElement> currentElements = null;// 当前显示的节点集合
	List<TreeElement> tempElements = null;// 用于临时存储
	List<TreeElement> treeElementsToDel; // 临时存储待删除的节点
	TreeViewAdapter adapter = null;// 用于数据填充
	LastLevelItemClickListener itemClickCallBack;// 用户点击事件回调

	public TreeView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "create with TreeView(Context context, AttributeSet attrs)");
		treeElements = new ArrayList<TreeElement>();
		currentElements = new ArrayList<TreeElement>();
		adapter = new TreeViewAdapter(context, currentElements);
		this.setAdapter(adapter);
		itemClickCallBack = new LastLevelItemClickListener() {
			@Override
			public void onLastLevelItemClick(int position,
					TreeViewAdapter adapter) {
				Log.d(TAG, "last level element "
						+ currentElements.get(position).getTitle()
						+ " is clicked");
				Toast.makeText(context,
						currentElements.get(position).getTitle(), 200).show();
			}
		};
		this.setOnItemClickListener(this);
	}

	public void initData(Context context, List<TreeElement> treeElements) {
		this.treeElements = treeElements;
		getFirstLevelElements(context);
		adapter.notifyDataSetChanged();
	}

	public void setLastLevelItemClickCallBack(
			LastLevelItemClickListener itemClickCallBack) {
		this.itemClickCallBack = itemClickCallBack;
	}

	public void getFirstLevelElements(Context context) {
		Log.d(TAG, "initCurrentElements");
		int size = treeElements.size();
		Log.d(TAG, "tree elements num is: " + size);
		if (currentElements == null) {
			currentElements = new ArrayList<TreeElement>();
		}
		currentElements.clear();
		for (int i = 0; i < size; i++) {
			if (treeElements.get(i).getLevel() == 1) {
				currentElements.add(treeElements.get(i));
				Log.d(TAG, "find a first level element: " + treeElements.get(i));
			}
		}
	}

	private List<TreeElement> getChildElementsFromAllById(String parentId) {
		tempElements = new ArrayList<TreeElement>();
		int size = treeElements.size();
		for (int i = 0; i < size; i++) {
			if (treeElements.get(i).getParentId().equalsIgnoreCase(parentId)) {
				tempElements.add(treeElements.get(i));
				Log.d(TAG, "find a child element： " + treeElements.get(i));
			}
		}
		return tempElements;
	}

	private List<TreeElement> getChildElementsFromCurrentById(String parentId) {
		Log.d(TAG, "getChildElementsFromCurrentById    parentId： " + parentId);
		if (tempElements == null) {
			tempElements = new ArrayList<TreeElement>();
		} else {
			tempElements.clear();
		}
		int size = currentElements.size();
		for (int i = 0; i < size; i++) {
			if (currentElements.get(i).getParentId().equalsIgnoreCase(parentId)) {
				tempElements.add(currentElements.get(i));
				Log.d(TAG,
						"find a child element to delete： "
								+ currentElements.get(i));
			}
		}
		return tempElements;
	}

	private synchronized boolean delAllChildElementsByParentId(String parentId) {
		Log.e(TAG, "delAllChildElementsByParentId: " + parentId);
		int size;
		TreeElement tempElement = currentElements
				.get(getElementIndexById(parentId));
		List<TreeElement> childElments = getChildElementsFromCurrentById(parentId);
		if (treeElementsToDel == null) {
			treeElementsToDel = new ArrayList<TreeElement>();
		} else {
			treeElementsToDel.clear();
		}
		size = childElments.size();
		Log.e(TAG, "childElments size : " + size);
		for (int i = 0; i < size; i++) {
			tempElement = childElments.get(i);
			if (tempElement.hasChild && tempElement.fold) {
				treeElementsToDel.add(tempElement);
			}
		}
		size = treeElementsToDel.size();
		Log.e(TAG, "treeElementsToDel size : " + size);
		for (int i = size - 1; i >= 0; i--) {
			delAllChildElementsByParentId(treeElementsToDel.get(i).getId());
		}
		delDirectChildElementsByParentId(parentId);
		return true;
	}

	private synchronized boolean delDirectChildElementsByParentId(
			String parentId) {
		Log.d(TAG, "delDirectChildElementsByParentId(): " + parentId);
		boolean success = false;
		if (currentElements == null || currentElements.size() == 0) {
			Log.d(TAG,
					"delChildElementsById() failed,currentElements is null or it's size is 0");
			return success;
		}
		synchronized (currentElements) {
			int size = currentElements.size();
			Log.d(TAG, "begin delete");
			for (int i = size - 1; i >= 0; i--) {
				if (currentElements.get(i).getParentId()
						.equalsIgnoreCase(parentId)) {
					currentElements.get(i).fold = false;// 记得隐藏子节点时把展开状态设为false
					currentElements.remove(i);
				}
			}
		}
		success = true;
		return success;
	}

	private int getElementIndexById(String id) {
		int num = currentElements.size();
		for (int i = 0; i < num; i++) {
			if (currentElements.get(i).getId().equalsIgnoreCase(id)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View convertView,
			int position, long id) {
		TreeElement element = currentElements.get(position);
		if (element.isHasChild()) {// 当前节点有子节点时只进行数据显示或隐藏等操作
			if (!element.isFold()) {// 当前父节点为未展开状态
				currentElements.addAll(position + 1,
						this.getChildElementsFromAllById(element.getId()));
			} else if (element.fold) {// 当前父节点为展开状态
				boolean success = this.delAllChildElementsByParentId(element
						.getId());
				// boolean success =
				// this.delDirectChildElementsByParentId(element
				// .getId());
				Log.d(TAG, "delete child state: " + success);
				if (!success) {
					return;
				}
			}
			// 调试信息
			// Log.d(TAG, "elements in currentElements:\n");
			// for (int i = 0; i < currentElements.size(); i++) {
			// Log.d(TAG + i, currentElements.get(i) + "\n");
			// }
			element.setFold(!element.isFold());// 设置反状态
			adapter.notifyDataSetChanged();// 刷新数据显示
		} else {// 当前节点有子节点时只进行用户自定义操作
			itemClickCallBack.onLastLevelItemClick(position, adapter);
		}
	}

	public interface LastLevelItemClickListener {
		public void onLastLevelItemClick(int position, TreeViewAdapter adapter);
	}
}