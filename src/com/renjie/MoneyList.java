package com.renjie;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.renjie.adapter.MoneyAdapter;
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
		myDb = new MoneyDAO(this, MoneyDAO.VERSION);

		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		Cursor allDatas = myDb.selectAllMoney();

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
		MoneyAdapter adapter = new MoneyAdapter(listItem, this); 
		list.setAdapter(adapter);
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
		 

		// final ArrayList<HashMap<String, Object>> tempList = listItem;
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String sno = "" + arg1.findViewById(R.id.time).getTag(); 
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
								myDb.deleteMoney(sno); 
								Toast.makeText(getApplicationContext(),  getText(R.string.delete_success).toString() ,
									     Toast.LENGTH_SHORT).show(); 
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