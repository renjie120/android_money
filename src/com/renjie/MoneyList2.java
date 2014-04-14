package com.renjie;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.renjie.adapter.IMoneyData;
import com.renjie.adapter.MoneyNewAdapter;
import com.renjie.tool.MoneyDAO;

/**
 * 显示理财信息的多层次列表界面.
 * 
 * @author lsq
 * 
 */
public class MoneyList2 extends BaseActivity {
	private MoneyDAO myDb;
	private Button topBtn, backBtn;
	private ListView list;
	// 生成动态数组，加入数据
	LinkedList<IMoneyData> listItem;
	private MoneyNewAdapter adapter;
	private List<Node> manager;
	private MoneyList2Adatper mAdatper;
	private Node currentNode;
	private static int maxLevel = 3;
	private DecimalFormat df = new DecimalFormat("#.00");
	private String y = null, m = null, d = null;

	private void queryYear() {
		y = null;
		m = null;
		d = null;
		// 实例化数据库
		myDb = new MoneyDAO(this, MoneyDAO.VERSION);

		listItem = new LinkedList<IMoneyData>();
		Cursor allDatas = myDb.selectAlloutMoneyByYear();
		manager = new ArrayList<Node>();

		if (allDatas.getCount() >= 1) {
			allDatas.moveToFirst();
			do {
				Node node1 = new Node();
				node1.setName("[" + allDatas.getString(0) + "]年");
				node1.setId(allDatas.getString(0));
				node1.setLevel(1);
				node1.setCode(df.format(allDatas.getDouble(1)));
				manager.add(node1);
			} while (allDatas.moveToNext());
		}
		allDatas.close();
		myDb.close();
		backBtn.setVisibility(View.GONE);
		mAdatper = new MoneyList2Adatper(manager, this, true, false);
		list.setAdapter(mAdatper);
	}

	private void queryListByYear(String year) {
		y = year;
		m = null;
		d = null;
		// 生成动态数组，加入数据
		manager = new ArrayList<Node>();
		Cursor allDatas = myDb.selectAlloutMoneyByMonth(year);
		if (allDatas.getCount() >= 1) {
			allDatas.moveToFirst();
			do {
				Node node1 = new Node();
				node1.setName(year + "-" + allDatas.getString(0));
				node1.setId(year + "," + allDatas.getString(0));
				node1.setLevel(2);
				node1.setCode(df.format(allDatas.getDouble(1)));
				manager.add(node1);
			} while (allDatas.moveToNext());

		}
		allDatas.close();
		myDb.close();
		backBtn.setVisibility(View.VISIBLE);
		mAdatper = new MoneyList2Adatper(manager, this, true, false);
		list.setAdapter(mAdatper);
	}

	private void queryListByYearAndMonth(String year, String month) {
		y = year;
		m = month;
		d = null;
		// 生成动态数组，加入数据
		manager = new ArrayList<Node>();
		Cursor allDatas = myDb.selectAlloutMoneyByMonthAndDay(year, month);
		if (allDatas.getCount() >= 1) {
			allDatas.moveToFirst();
			do {
				Node node1 = new Node();
				node1.setName(year + "-" + month + "-" + allDatas.getString(0));
				node1.setId(year + "," + month + "," + allDatas.getString(0));
				node1.setLevel(3);
				node1.setCode(df.format(allDatas.getDouble(1)));
				manager.add(node1);
			} while (allDatas.moveToNext());
		}
		allDatas.close();
		myDb.close();
		backBtn.setVisibility(View.VISIBLE);
		mAdatper = new MoneyList2Adatper(manager, this, true, false);
		list.setAdapter(mAdatper);
	}

	/**
	 * 查询某一天的具体数据.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	private void queryListByYearAndMonthAndDay(String year, String month,
			String day) {
		y = year;
		m = month;
		d = day;
		// 生成动态数组，加入数据
		manager = new ArrayList<Node>();
		Cursor allDatas = myDb.selectAlloutMoneyByMonthAndDayDetail(year,
				month, day);
		if (allDatas.getCount() >= 1) {
			allDatas.moveToFirst();
			do {
				Node node1 = new Node();
				node1.setName(allDatas.getString(2) + ","
						+ allDatas.getString(3));
				node1.setId(allDatas.getString(0));
				node1.setLevel(4);
				node1.setCode(df.format(allDatas.getDouble(1)));
				manager.add(node1);
			} while (allDatas.moveToNext());
		}
		allDatas.close();
		myDb.close();
		backBtn.setVisibility(View.VISIBLE);
		mAdatper = new MoneyList2Adatper(manager, this, false, true);
		list.setAdapter(mAdatper);
	}

	public void onResume() {
		super.onResume();
		queryYear();
	}

	class MoneyList2Adatper extends BaseAdapter {
		private List<Node> data;// 用于接收传递过来的Context对象
		private Context context;
		private boolean showNext, showSpan;

		public MoneyList2Adatper(List<Node> data, Context context,
				boolean showNext, boolean showSpan) {
			super();
			this.data = data;
			this.context = context;
			this.showSpan = showSpan;
			this.showNext = showNext;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (null != data) {
				count = data.size();
			}
			return count;
		}

		@Override
		public Node getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (null == convertView) {
				viewHolder = new ViewHolder();
				LayoutInflater mInflater = LayoutInflater.from(context);
				convertView = mInflater
						.inflate(R.layout.list_money_item2, null);

				viewHolder.time = (TextView) convertView
						.findViewById(R.id.time);
				viewHolder.money = (TextView) convertView
						.findViewById(R.id.money);
				viewHolder.next = (ImageView) convertView
						.findViewById(R.id.next);
				viewHolder.span = (TextView) convertView
						.findViewById(R.id.span);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (showNext)
				viewHolder.next.setVisibility(View.VISIBLE);
			else
				viewHolder.next.setVisibility(View.GONE);

			if (showSpan)
				viewHolder.span.setVisibility(View.VISIBLE);
			else
				viewHolder.span.setVisibility(View.GONE);

			Node markerItem = getItem(position);
			if (null != markerItem) {
				if (showSpan) {
					String[] s = markerItem.getName().split(",");
					viewHolder.time.setText(s[0]);
					viewHolder.span.setText(s[1]);
				} else {
					viewHolder.time.setText(markerItem.getName());
				}
				viewHolder.money.setText(markerItem.getCode());
				viewHolder.money.setTag(markerItem.getLevel());
				viewHolder.next.setTag(markerItem.getId());
			}

			return convertView;
		}

		class ViewHolder {
			TextView time;
			ImageView next;
			TextView money;
			TextView span;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全局的列表页面布局
		setContentView(R.layout.list_money2);
		topBtn = (Button) findViewById(R.id.top_btn);
		list = (ListView) findViewById(R.id.ListView);
		backBtn = (Button) findViewById(R.id.backbtn);
		// 初始化的时候不显示上级按钮.
		backBtn.setVisibility(View.GONE);
		queryYear();
		registerForContextMenu(list);
		prepareListener();
	}

	protected void prepareListener() {
		// 设计返回按钮
		topBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				queryYear();
			}
		});

		// 点击上级按钮.
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (y != null && m != null) {
					queryListByYear(y);
				} else if (y != null && m == null) {
					queryYear();
				}
			}
		});
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final String id = "" + arg1.findViewById(R.id.next).getTag();
				String level = "" + arg1.findViewById(R.id.money).getTag();
				if ("1".equals(level)) {
					queryListByYear("" + id);
				} else if ("2".equals(level)) {
					String[] datas = id.split(",");
					queryListByYearAndMonth(datas[0], datas[1]);
				} else if ("3".equals(level)) {
					String[] datas = id.split(",");
					queryListByYearAndMonthAndDay(datas[0], datas[1], datas[2]);
				} else if ("4".equals(level)) {
					confirm(new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							myDb.deleteMoney(Integer.parseInt(id));
							showMess(R.string.delete_success);
							queryListByYearAndMonthAndDay(y, m, d);
						}
					});
				}
			}
		});
	}
}