package com.renjie;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.renjie.adapter.DayMoney;
import com.renjie.adapter.IMoneyData;
import com.renjie.adapter.MoneyNewAdapter;
import com.renjie.adapter.MonthMoney;
import com.renjie.adapter.YearMoney;
import com.renjie.tool.MoneyDAO;

/**
 * 显示理财信息的列表界面.
 * 
 * @author lsq
 * 
 */
public class MoneyList extends BaseActivity {
	private MoneyDAO myDb;
	private Button returnBtn;
	private ListView list;
	// 生成动态数组，加入数据
	LinkedList<IMoneyData> listItem;
	private MoneyNewAdapter adapter;

	private void queryList() {
		// 实例化数据库
		myDb = new MoneyDAO(this, MoneyDAO.VERSION);

		listItem = new LinkedList<IMoneyData>();
		Cursor allDatas = myDb.selectAlloutMoneyByYear();

		if (allDatas.getCount() >= 1) {
			allDatas.moveToFirst();
			do {
				YearMoney map = new YearMoney();
				map.setYear(allDatas.getString(0));
				map.setLevel("year");
				map.setIsClosed("true");
				map.setMoney(allDatas.getDouble(1));
				listItem.add(map);
			} while (allDatas.moveToNext());
		}
		allDatas.close();
		myDb.close();
		adapter = new MoneyNewAdapter(listItem, this);
		list.setAdapter(adapter);
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
		queryList();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全局的列表页面布局
		setContentView(R.layout.list_main);

		returnBtn = (Button) findViewById(R.id.returnbtn);

		list = (ListView) findViewById(R.id.ListView);

		prepareListener();
	}

	protected void prepareListener() {
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView tv = (TextView) arg1.findViewById(R.id.arrow);
				String isClosed = "" + tv.getTag();
				String level = "" + arg1.findViewById(R.id.money).getTag(); 
				if ("closed".equals(isClosed)) { 
					if ("year".equals(level)) {
						String year = arg1.findViewById(R.id.year).getTag()
								+ "";
						ArrayList<MonthMoney> thisYearMonthData = queryListByYear(year);
						listItem.addAll(thisYearMonthData);

						adapter.notifyDataSetChanged();
					} else if ("month".equals(level)) {
						String year = arg1.findViewById(R.id.year).getTag()
								+ "";
						String month = arg1.findViewById(R.id.month).getTag()
								+ "";
						ArrayList<DayMoney> thisYearMonthData = queryListByYearAndMonth(
								year, month);
						System.out.println(year + "," + month + "-----查询月份");
						listItem.addAll(thisYearMonthData);

						adapter.notifyDataSetChanged();
					}
				}
			}
		});
		// 设计返回按钮
		returnBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent openUrl = new Intent();
				openUrl.setClass(MoneyList.this, SaveMoney.class);
				startActivity(openUrl);
				MoneyList.this.finish();
			}
		});
	}
}