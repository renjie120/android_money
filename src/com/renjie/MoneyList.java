package com.renjie;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

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

	private void queryList() {
		// 实例化数据库
		myDb = new MoneyDAO(this, 1);

		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		Cursor allDatas = myDb.selectAll();

		if (allDatas.getCount() >= 1) {
			allDatas.moveToFirst();
			do {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("money", allDatas.getString(1));// 图像资源的ID
				map.put("time", allDatas.getString(2));
				map.put("sno", allDatas.getString(0));
				map.put("moneytype", allDatas.getString(4));
				listItem.add(map);
			} while (allDatas.moveToNext());
		}
		myDb.close();
		MyImgAdapter adapter = new MyImgAdapter(listItem, this);
		// SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,//
		// 数据源
		// // 设置一行的显示布局!
		// R.layout.money_list,// ListItem的XML实现
		// // 动态数组与ImageItem对应的子项
		// new String[] { "money", "time", "moneytype" },
		// // ImageItem的XML文件里面的一个ImageView,两个TextView ID
		// new int[] { R.id.money, R.id.time, R.id.moneytype });
		// final SimpleAdapter listItemAdapter2 = listItemAdapter;
		list.setAdapter(adapter);
	}

	class MyImgAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> data;// 用于接收传递过来的Context对象
		private Context context;

		public MyImgAdapter(ArrayList<HashMap<String, Object>> data,
				Context context) {
			super();
			this.data = data;
			this.context = context;
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
		public HashMap<String, Object> getItem(int position) {
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
				convertView = mInflater.inflate(R.layout.money_list, null);

				viewHolder.time = (TextView) convertView
						.findViewById(R.id.time);
				viewHolder.moneytype = (TextView) convertView
						.findViewById(R.id.moneytype);
				viewHolder.money = (TextView) convertView
						.findViewById(R.id.money);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			HashMap<String, Object> markerItem = getItem(position);
			if (null != markerItem) {
				viewHolder.time.setTag(markerItem.get("sno"));
				viewHolder.time.setText("" + markerItem.get("time"));
				viewHolder.moneytype.setText("" + markerItem.get("moneytype"));
				viewHolder.money.setText("" + markerItem.get("money"));
			}
			return convertView;
		}
	}

	public final static class ViewHolder {
		public TextView time;
		public TextView moneytype;
		public TextView money;
	}

	public void onResume() {
		super.onResume();
		System.out.println("onResume");
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
		 

		// final ArrayList<HashMap<String, Object>> tempList = listItem;
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String sno = "" + arg0.findViewById(R.id.time).getTag();
				deleteMoney(Long.parseLong(sno));
			}
		});
		// 调用绑定事件的私有方法。
		prepareListener();
	}

	private void deleteMoney(final long sno) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_name)
				.setMessage(R.string.delete_confirm)
				.setNegativeButton(R.string.con_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				// 如果是确定退出就退出程序！
				.setPositiveButton(R.string.con_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								myDb.delete(sno);
								alert(getText(R.string.delete_success)
										.toString());
								queryList();
							}
						}).show();
	}

	protected void prepareListener() {
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