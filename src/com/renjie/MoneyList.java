package com.renjie;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.renjie.adapter.DayMoney;
import com.renjie.adapter.IMoneyData;
import com.renjie.adapter.MoneyNewAdapter;
import com.renjie.adapter.MonthMoney;
import com.renjie.tool.MoneyDAO;
import com.renjie.treelistview.AbstractTreeViewAdapter;
import com.renjie.treelistview.InMemoryTreeStateManager;
import com.renjie.treelistview.TreeListView;
import com.renjie.treelistview.TreeNodeInfo;
import com.renjie.treelistview.TreeStateManager;

/**
 * 显示理财信息的列表界面.
 * 
 * @author lsq
 * 
 */
public class MoneyList extends BaseActivity {
	private MoneyDAO myDb;
	private Button returnBtn;
	private TreeListView list;
	// 生成动态数组，加入数据
	LinkedList<IMoneyData> listItem;
	private MoneyNewAdapter adapter;
	private TreeStateManager<Node> manager;
	private MoneyListAdatper mAdatper;
	private Node currentNode;
	private static int maxLevel = 3;
	private DecimalFormat df = new DecimalFormat("#.00");
	private void queryYear() {
		// 实例化数据库
		myDb = new MoneyDAO(this, MoneyDAO.VERSION);

		listItem = new LinkedList<IMoneyData>();
		Cursor allDatas = myDb.selectAlloutMoneyByYear();
		manager = new InMemoryTreeStateManager<Node>();

		if (allDatas.getCount() >= 1) {
			allDatas.moveToFirst();
			do {
				Node node1 = new Node();
				node1.setName("[" + allDatas.getString(0) + "]年");
				node1.setId(allDatas.getString(0));
				node1.setLevel(1);
				node1.setOpened(false);
				node1.setLoadChildren(false);
				node1.setParent(true);
				node1.setCode(df.format(allDatas.getDouble(1)));
				manager.addAfterChild(null, node1, null);
			} while (allDatas.moveToNext());
		}
		allDatas.close();
		myDb.close();

		mAdatper = new MoneyListAdatper(this, manager, maxLevel);
		 
		list.setAdapter(mAdatper);

	}

	private ArrayList<MonthMoney> queryListByYear(String year) {
		// 生成动态数组，加入数据
		ArrayList<MonthMoney> yearListItem = new ArrayList<MonthMoney>();
		Cursor allDatas = myDb.selectAlloutMoneyByMonth(year);
		if (allDatas.getCount() >= 1) {
			allDatas.moveToFirst();
			MonthMoney map = new MonthMoney();
			do {
				map.setMonth(year + "," + allDatas.getString(0));// 图像资源的ID
				map.setMoney(allDatas.getDouble(1));
				map.setIsClosed("true");
				map.setLevel("month");
				yearListItem.add(map);
			} while (allDatas.moveToNext());
			return yearListItem;
		}
		return null;
	}

	private ArrayList<DayMoney> queryListByYearAndMonth(String year,
			String month) {
		// 生成动态数组，加入数据
		ArrayList<DayMoney> yearListItem = new ArrayList<DayMoney>();
		Cursor allDatas = myDb.selectAlloutMoneyByMonthAndDay(year, month);
		if (allDatas.getCount() >= 1) {
			allDatas.moveToFirst();
			DayMoney map = new DayMoney();
			do {
				map.setDay(year + "," + month + "," + allDatas.getString(0));// 图像资源的ID
				map.setMoney(allDatas.getDouble(1));
				map.setIsClosed("true");
				map.setLevel("day");
				yearListItem.add(map);
			} while (allDatas.moveToNext());
			return yearListItem;
		}
		return null;
	}

	public void onResume() {
		super.onResume();
		queryYear();
	}

	class MoneyListAdatper extends AbstractTreeViewAdapter<Node> {

		private int minItemW = 0;
		private int layoutW = 0;
		private Drawable drawable1;
		private Drawable drawable2;

		/**
		 * @param activity
		 * @param treeStateManager
		 * @param numberOfLevels
		 */
		public MoneyListAdatper(Activity activity,
				TreeStateManager<Node> treeStateManager, int numberOfLevels) {
			super(activity, treeStateManager, numberOfLevels);
		}

		@Override
		public long getItemId(int position) {
			return getTreeId(position).hashCode();
		}

		@Override
		protected int getItemWidth() {
			if (minItemW == 0) {
				minItemW = getResources().getDisplayMetrics().widthPixels;
			}
			int itemW = super.getItemWidth();
			return itemW < minItemW ? minItemW : itemW;
		}

		@Override
		protected int getLayoutWidth() {
			if (layoutW == 0) {
				layoutW = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 330, getActivity()
								.getResources().getDisplayMetrics());
			}
			return layoutW;
		}

		@Override
		public View getNewChildView(TreeNodeInfo<Node> treeNodeInfo) {
			View view = getActivity().getLayoutInflater().inflate(
					R.layout.tree_lv_item, null);
			ViewHolder holder = new ViewHolder();
			holder.tv1 = (TextView) view.findViewById(R.id.tv1);
			holder.tv2 = (TextView) view.findViewById(R.id.tv2);
			view.setTag(holder);
			Node nd = treeNodeInfo.getId(); 
			return updateView(view, treeNodeInfo);
		}

		@Override
		public View updateView(View view, TreeNodeInfo<Node> treeNodeInfo) {
			ViewHolder holder = (ViewHolder) view.getTag();
			Node node = treeNodeInfo.getId();
			holder.tv1.setText(node.getName());
			holder.tv2.setText(node.getCode());
			return view;
		}

		@Override
		public Drawable getBackgroundDrawable(TreeNodeInfo<Node> treeNodeInfo) {

			if (treeNodeInfo.getLevel() == 0) {
				if (null == drawable1) {
					drawable1 = new ColorDrawable(Color.GRAY);
				}
				return drawable1;
			} else if (treeNodeInfo.getLevel() == 1) {
				if (null == drawable2) {
					drawable2 = new ColorDrawable(Color.LTGRAY);
				}
				return drawable2;
			}
			return null;
		}

		@Override
		public void handleItemClick(View view, Object id) { 
			if (!isCollapsible()) {
				super.handleItemClick(view, id);
			} else {
				Node node = (Node) id;
				// 查询月视图.
				if (node.getLevel() == 1) {
					// 如果是父亲节点
					if (node.isParent()) {
						// 并且没有打开过子节点.
						if (!node.isOpened()) {
							// 如果没有加载过子节点，就查询数据库进行动态加载.
							if (!node.isLoadChildren()) {
								Cursor allDatas = myDb
										.selectAlloutMoneyByMonth(node.getId());
								if (allDatas.getCount() >= 1) {
									allDatas.moveToFirst();
									do {
										Node node1 = new Node();
										node1.setName(allDatas.getString(0)
												+ " 月");
										node1.setId(node.getId() + ","
												+ allDatas.getString(0));
										node1.setLevel(2);
										node1.setOpened(false);
										node1.setLoadChildren(false);
										node1.setParent(true);
										node1.setCode(allDatas.getDouble(1)
												+ "");
										manager.addAfterChild(node, node1, null);
									} while (allDatas.moveToNext());
								}
								node.setOpened(true);
								node.setLoadChildren(true);
								allDatas.close();
								myDb.close();
							} else {
								node.setOpened(true);
								manager.expandDirectChildren(node);
							}
						}
						// 如果打开过节点，就直接收缩节点即可.
						else {
							manager.collapseChildren(node);
							node.setOpened(false);
						}
					}
				} else if (node.getLevel() == 2) {
					// 如果是父亲节点
					if (node.isParent()) {
						// 并且没有打开过子节点.
						if (!node.isOpened()) {
							// 如果没有加载过子节点，就查询数据库进行动态加载.
							if (!node.isLoadChildren()) {
								String nodeid = node.getId();
								String[] strs = nodeid.split(",");
								Cursor allDatas = myDb
										.selectAlloutMoneyByMonthAndDay(
												strs[0], strs[1]);
								if (allDatas.getCount() >= 1) {
									allDatas.moveToFirst();
									do {
										Node node1 = new Node();
										node1.setName(allDatas.getString(0) + "日");
										node1.setLevel(3);
										node1.setParent(false);
										node1.setCode(allDatas.getDouble(1)
												+ "");
										manager.addAfterChild(node, node1, null);
									} while (allDatas.moveToNext());
								}
								node.setOpened(true);
								node.setLoadChildren(true);
								allDatas.close();
								myDb.close();
							} else {
								node.setOpened(true);
								manager.expandDirectChildren(node);
							}
						}
						// 如果打开过节点，就直接收缩节点即可.
						else {
							manager.collapseChildren(node);
							node.setOpened(false);
						}
					}
				}
			}
		}

		class ViewHolder {
			TextView tv1;
			TextView tv2;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全局的列表页面布局
		setContentView(R.layout.list_main);

		returnBtn = (Button) findViewById(R.id.returnbtn);

		list = (TreeListView) findViewById(R.id.ListView);

		queryYear();
		registerForContextMenu(list);

		prepareListener();
	}

	protected void prepareListener() { 
		// 设计返回按钮
		returnBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent openUrl = new Intent();
				openUrl.setClass(MoneyList.this, NewHomePage.class);
				startActivity(openUrl);
				MoneyList.this.finish();
			}
		});
	}
}