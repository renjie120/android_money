package com.renjie;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.renjie.adapter.MoneyList2Adatper;
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
	private ListView list;// ,list2; 
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
				// list2.setAdapter(mAdatper);
				break;
			case 2:
				mAdatper = new MoneyList2Adatper(manager, MoneyList2.this,
						false, true, true);
				list.setAdapter(mAdatper);
				// list2.setAdapter(mAdatper);
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
		currentLevel="1"; 
		// 如果是超级用户，就查询远程数据
		if (isSuper)
			new MyListLoader(true, 1, null, null, null).execute("");
		// 否则查询本地数据库
		else { 
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
		currentLevel = "2"; 
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
		currentLevel = "3"; 
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
		currentLevel = "4";
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
		// list2 = (ListView) findViewById(R.id.ListView2);
		backBtn = (Button) findViewById(R.id.backbtn);
		// 初始化的时候不显示上级按钮.
		backBtn.setVisibility(View.GONE);
		settings = getSharedPreferences(Tool.CONFIG, 0);
		queryYearNew();
		isSuper = getIntent().getBooleanExtra(Tool.SUPERPASS, false);
		registerForContextMenu(list);
		prepareListener();
	}

	private String currentLevel = "1";

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
				System.out.println(currentLevel+"----currentLevel(点击前。。。。)");
				if ("3".equals(currentLevel)) {
					queryListByYearNew(y);
				} else if ("2".equals(currentLevel)) {
					queryYearNew();
				} else if ("4".equals(currentLevel)) {
					queryListByYearAndMonthNew(y, m);
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
				} else if ("4".equals(level)&&!isSuper) {
					confirm(new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							myDb.deleteMoney(Integer.parseInt(id));
							showMess(R.string.delete_success);
							queryListByYearAndMonthAndDayNew(y, m, d);
						}
					}, R.string.delete_confirm);
				}
			}
		});
	}
}