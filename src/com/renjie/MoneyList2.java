package com.renjie;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.alibaba.fastjson.JSONArray;
import com.renjie.adapter.IMoneyData;
import com.renjie.adapter.MoneyNewAdapter;
import com.renjie.tool.HttpRequire;
import com.renjie.tool.MoneyDAO;
import com.renjie.tool.Tool;

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
	private boolean isSuper;
	private static int maxLevel = 3;
	private DecimalFormat df = new DecimalFormat("#.00");
	private String y = null, m = null, d = null;
	public Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mAdatper = new MoneyList2Adatper(manager, MoneyList2.this,
						true, false, false);
				list.setAdapter(mAdatper);
				break;
			case 2:
				mAdatper = new MoneyList2Adatper(manager, MoneyList2.this,
						false, true, true);
				list.setAdapter(mAdatper);
				break;
			case 3:
				backBtn.setVisibility(View.VISIBLE);
				break;
			case 4:
				backBtn.setVisibility(View.GONE);
				break;
			default:
				super.hasMessages(msg.what);
				break;
			}
		}
	};

	/**
	 * 查询年视图，显示每年的开支
	 */
	private void queryYearNew() {
		// 如果是超级用户，就查询远程数据
		if (isSuper)
			new MyListLoader(true, 1, null, null, null).execute("");
		// 否则查询本地数据库
		else {
			listItem = new LinkedList<IMoneyData>();
			manager = new ArrayList<Node>();
			// 实例化数据库
			myDb = new MoneyDAO(this, MoneyDAO.VERSION);
			Cursor allDatas = myDb.selectAlloutMoneyByYear();

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
			myHandler.sendEmptyMessage(1);
		}
	}

	/**
	 * 解析远程的数据
	 */
	private void queryYear() {
		y = null;
		m = null;
		d = null;
		try {
			listItem = new LinkedList<IMoneyData>();
			manager = new ArrayList<Node>();
			JSONArray arr = HttpRequire.getReport(settings);
			for (int i = 0, j = arr.size(); i < j; i++) {
				Node node1 = new Node();
				JSONArray a = arr.getJSONArray(i);
				node1.setName("[" + a.get(0) + "]年");
				node1.setId("" + a.get(0));
				node1.setLevel(1);
				node1.setCode(a.get(1) + "");
				manager.add(node1);
			}
			myHandler.sendEmptyMessage(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询某一年的月视图数据
	 * 
	 * @param year
	 */
	private void queryListByYearNew(String year) {
		y = year;
		m = null;
		d = null;
		// 生成动态数组，加入数据
		manager = new ArrayList<Node>();
		if (!isSuper) {
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
			myHandler.sendEmptyMessage(1);

			backBtn.setVisibility(View.VISIBLE);
		} else {
			new MyListLoader(true, 2, year, null, null).execute("");
		}
	}

	/**
	 * 解析远程的数据.
	 * 
	 * @param year
	 */
	private void queryListByYear(String year) {
		y = year;
		m = null;
		d = null;
		// 生成动态数组，加入数据
		manager = new ArrayList<Node>();
		JSONArray arr = HttpRequire.getReportByYear(settings, year);
		for (int i = 0, j = arr.size(); i < j; i++) {
			Node node1 = new Node();
			JSONArray a = arr.getJSONArray(i);
			node1.setName(year + "-" + a.get(0));
			node1.setId(year + "," + a.get(0));
			node1.setLevel(2);
			node1.setCode(a.get(1) + "");
			manager.add(node1);
		}
		myHandler.sendEmptyMessage(1);
	}

	/**
	 * 查询某一个月的每天的数据.
	 * 
	 * @param year
	 * @param month
	 */
	private void queryListByYearAndMonthNew(String year, String month) {
		y = year;
		m = month;
		d = null;
		// 生成动态数组，加入数据
		manager = new ArrayList<Node>();
		if (!isSuper) {
			Cursor allDatas = myDb.selectAlloutMoneyByMonthAndDay(year, month);
			if (allDatas.getCount() >= 1) {
				allDatas.moveToFirst();
				do {
					Node node1 = new Node();
					node1.setName(year + "-" + month + "-"
							+ allDatas.getString(0));
					node1.setId(year + "," + month + ","
							+ allDatas.getString(0));
					node1.setLevel(3);
					node1.setCode(df.format(allDatas.getDouble(1)));
					manager.add(node1);
				} while (allDatas.moveToNext());
			}
			allDatas.close();
			myDb.close();
			backBtn.setVisibility(View.VISIBLE);
			myHandler.sendEmptyMessage(1);
		} else {
			new MyListLoader(true, 3, year, month, null).execute("");
		}
	}

	private void queryListByYearAndMonth(String year, String month) {
		y = year;
		m = month;
		d = null;
		// 生成动态数组，加入数据
		manager = new ArrayList<Node>();
		JSONArray arr = HttpRequire.getReportByMonth(settings, year, month);
		for (int i = 0, j = arr.size(); i < j; i++) {
			Node node1 = new Node();
			JSONArray a = arr.getJSONArray(i);
			node1.setName("" + a.get(0));
			node1.setId(("" + a.get(0)).replace("-", ","));
			node1.setLevel(3);
			node1.setCode(a.get(1) + "");
			manager.add(node1);
		}
		myHandler.sendEmptyMessage(1);
	}

	/**
	 * 查询某一天的数据.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	private void queryListByYearAndMonthAndDayNew(String year, String month,
			String day) {
		y = year;
		m = month;
		d = day;
		// 生成动态数组，加入数据
		manager = new ArrayList<Node>();
		if (!isSuper) {
			Cursor allDatas = myDb.selectAlloutMoneyByMonthAndDayDetail(year,
					month, day);
			if (allDatas.getCount() >= 1) {
				allDatas.moveToFirst();
				do {
					Node node1 = new Node();
					node1.setName(allDatas.getString(2) + ","
							+ allDatas.getString(3));
					node1.setId(allDatas.getString(0));
					node1.setParam1(allDatas.getString(4));
					node1.setLevel(4);
					node1.setCode(df.format(allDatas.getDouble(1)));
					manager.add(node1);
				} while (allDatas.moveToNext());
			}
			allDatas.close();
			myDb.close();
			backBtn.setVisibility(View.VISIBLE);
			myHandler.sendEmptyMessage(2);
		} else {
			new MyListLoader(true, 4, y, m, d).execute("");
		}
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

		JSONArray arr = HttpRequire.getReportByDay(settings, year + "-" + month
				+ "-" + day);
		for (int i = 0, j = arr.size(); i < j; i++) {
			Node node1 = new Node();
			JSONArray a = arr.getJSONArray(i);
			node1.setName(a.get(0) + "," + a.get(1));
			node1.setId(a.get(0) + "," + a.get(1));
			node1.setLevel(4);
			node1.setParam1(a.get(3) + "");
			node1.setCode(a.get(2) + "");
			manager.add(node1);
		}
		myHandler.sendEmptyMessage(2);
	}

	public void onResume() {
		super.onResume();
		queryYearNew();
	}

	class MoneyList2Adatper extends BaseAdapter {
		private List<Node> data;// 用于接收传递过来的Context对象
		private Context context;
		private boolean showNext, showSpan, showDesc;

		/**
		 * 
		 * @param data
		 *            数据
		 * @param context
		 * @param showNext
		 *            是否显示下一级别
		 * @param showSpan
		 *            是否显示首页
		 * @param showDesc
		 *            是否显示描述信息.
		 */
		public MoneyList2Adatper(List<Node> data, Context context,
				boolean showNext, boolean showSpan, boolean showDesc) {
			super();
			this.data = data;
			this.context = context;
			this.showSpan = showSpan;
			this.showNext = showNext;
			this.showDesc = showDesc;
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
				viewHolder.desc = (TextView) convertView
						.findViewById(R.id.m_desc);
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

			viewHolder.desc.setVisibility(View.GONE);

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
				String str = markerItem.getParam1();
				if (str != null && !"null".equals(str) && !"".equals(str)) {
					viewHolder.desc.setVisibility(View.VISIBLE);
					viewHolder.desc.setText(str);
				} else
					viewHolder.desc.setVisibility(View.GONE);
			}

			return convertView;
		}

		class ViewHolder {
			// 时间
			TextView time;
			// 是否出现打开下一级别的图标
			ImageView next;
			// 金额
			TextView money;
			TextView span;
			// 描述信息
			TextView desc;
		}

	}

	private SharedPreferences settings;
	private static final int DIALOG_KEY = 0;
	private ProgressDialog dialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_KEY: {
			dialog = new ProgressDialog(this);
			dialog.setMessage("正在查询...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}
		}
		return null;
	}

	private class MyListLoader extends AsyncTask<String, String, String> {

		private boolean showDialog;
		private int type;
		private String y;
		private String m;
		private String d;

		public MyListLoader(boolean showDialog, int type, String year,
				String month, String day) {
			this.showDialog = showDialog;
			this.type = type;
			this.y = year;
			this.m = month;
			this.d = day;
		}

		@Override
		protected void onPreExecute() {
			// 执行过程中显示进度栏.
			if (showDialog) {
				showDialog(DIALOG_KEY);
			}
		}

		public String doInBackground(String... p) {
			if (type == 1)
				queryYear();
			else if (type == 2) {
				queryListByYear(y);
			} else if (type == 3) {
				queryListByYearAndMonth(y, m);
			} else if (type == 4) {
				queryListByYearAndMonthAndDay(y, m, d);
			}
			return "";
		}

		@Override
		public void onPostExecute(String Re) {
			/**
			 * 完成的时候就取消进度栏.
			 */
			if (showDialog) {
				removeDialog(DIALOG_KEY);
			}
			if (type == 4 || type == 2 || type == 3) {
				myHandler.sendEmptyMessage(3);
			} else {
				myHandler.sendEmptyMessage(4);
			}
		}

		@Override
		protected void onCancelled() {
			// 取消进度栏.
			if (showDialog) {
				removeDialog(DIALOG_KEY);
			}
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
		settings = getSharedPreferences(Tool.CONFIG, 0);
		queryYearNew();
		isSuper = getIntent().getBooleanExtra(Tool.SUPERPASS, false);
		registerForContextMenu(list);
		prepareListener();
	}

	protected void prepareListener() {
		// 设计返回按钮
		topBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				queryYearNew();
			}
		});

		// 点击上级按钮.
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (y != null && m != null) {
					queryListByYearNew(y);
				} else if (y != null && m == null) {
					queryYearNew();
				}
			}
		});
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final String id = "" + arg1.findViewById(R.id.next).getTag();
				String level = "" + arg1.findViewById(R.id.money).getTag();
				if ("1".equals(level)) {
					queryListByYearNew(id + "");
				} else if ("2".equals(level)) {
					String[] datas = id.split(",");
					queryListByYearAndMonthNew(datas[0], datas[1]);
				} else if ("3".equals(level)) {
					String[] datas = id.split(",");
					queryListByYearAndMonthAndDayNew(datas[0], datas[1],
							datas[2]);
				} else if ("4".equals(level)) {
					confirm(new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							myDb.deleteMoney(Integer.parseInt(id));
							showMess(R.string.delete_success);
							queryListByYearAndMonthAndDayNew(y, m, d);
						}
					},R.string.delete_confirm);
				}
			}
		});
	}
}