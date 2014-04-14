package com.renjie;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.renjie.treelistview.AbstractTreeViewAdapter;
import com.renjie.treelistview.InMemoryTreeStateManager;
import com.renjie.treelistview.TreeListView;
import com.renjie.treelistview.TreeNodeInfo;
import com.renjie.treelistview.TreeStateManager;

public class TreeListDemoActivity extends Activity {
	private TreeListView lv;
	private TreeStateManager<Node> manager;
	private TreeListAdatper mAdatper;
	private Node currentNode;

	private Button btn1;
	private Button btn2;
	private Button btn3;
	private boolean collapsible = true;

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn1:
				manager.expandEverythingBelow(null);
				break;
			case R.id.btn2:
				manager.collapseChildren(null);
				break;
			case R.id.btn3:
				if (collapsible) {
					btn3.setText("有折叠标签");
					lv.setCollapsible(false);
				} else {
					btn3.setText("无折叠标签");
					lv.setCollapsible(true);
				}
				collapsible = !collapsible;
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tree_list_main);
		lv = (TreeListView) findViewById(R.id.lv);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn3 = (Button) findViewById(R.id.btn3);
		btn1.setOnClickListener(onClickListener);
		btn2.setOnClickListener(onClickListener);
		btn3.setOnClickListener(onClickListener);

		manager = new InMemoryTreeStateManager<Node>();

		Node node1 = null;
		Node node2 = null;
		Node node3 = null;
		for (int i = 0; i < 3; i++) {
			node1 = new Node();
			node1.setName("老大" + "[" + i + "]");
			node1.setCode("1534454313");
			manager.addAfterChild(null, node1, null);
			for (int j = 0; j < 2; j++) {
				node2 = new Node();
				node2.setName("领导" + "[" + i + j + "]");
				node2.setCode("54735431365");
				manager.addAfterChild(node1, node2, null);
				for (int z = 0; z < 3; z++) {
					node3 = new Node();
					node3.setName("领导" + "[" + i + j + z + "]");
					node3.setCode("687635135");
					manager.addAfterChild(node2, node3, null);
				}
			}
		}
		mAdatper = new TreeListAdatper(this, manager, 4);
		lv.setAdapter(mAdatper);
		//registerForContextMenu(lv);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0: // 展开所有
			manager.expandEverythingBelow(currentNode);
			break;
		case 1: // 展开下一级
			manager.expandDirectChildren(currentNode);
			break;
		case 2: // 折叠所有
			manager.collapseChildren(currentNode);
			break;
		case 3: // 删除
			manager.removeNodeRecursively(currentNode);
			break;
		case 4: // 添加子节点
			for (int z = 0; z < 3; z++) {
				Node node3 = new Node();
				node3.setName("动态添加子节点：" + "["  + z + "]");
				node3.setCode("12222");
				manager.addAfterChild(currentNode, node3, null);
			}
			//manager.removeNodeRecursively(currentNode);
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		currentNode = (Node) info.targetView.getTag();
		TreeNodeInfo<Node> nodeInfo = manager.getNodeInfo(currentNode);
		if (!nodeInfo.isExpanded()) {
			menu.add(0, 0, 0, "展开所有");
			menu.add(0, 1, 1, "展开下一级");
			menu.add(0, 3, 2, "删除");
			menu.add(0, 4, 5, "添加子节点");
		} else {
			menu.add(0, 2, 0, "折叠所有");
			menu.add(0, 3, 1, "删除");
			menu.add(0, 4, 5, "添加子节点");
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class TreeListAdatper extends AbstractTreeViewAdapter<Node> {

		private int minItemW = 0;
		private int layoutW = 0;
		private Drawable drawable1;
		private Drawable drawable2;

		/**
		 * @param activity
		 * @param treeStateManager
		 * @param numberOfLevels
		 */
		public TreeListAdatper(Activity activity,
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
				Toast.makeText(this.getActivity(), "点击了:" + node.getName(),
						Toast.LENGTH_SHORT).show();
			}
		}

		class ViewHolder {
			TextView tv1;
			TextView tv2;
		}

	}
}
