package com.renjie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.renjie.adapter.DiaryAdapter;
import com.renjie.adapter.GongguoAdapter;
import com.renjie.tool.MoneyDAO;
import com.renjie.tool.Tool;

/**
 * 更多的九宫格布局.
 * 
 * @author Administrator
 * 
 */
public class MorePage extends BaseActivity implements Runnable, OnClickListener {
	int[] allMages = { R.drawable.item_1, R.drawable.item_2, R.drawable.item_3,
			R.drawable.item_1, R.drawable.item_2, R.drawable.item_3,
			R.drawable.item_1, R.drawable.item_2, R.drawable.item_3 };
	int[] allitem = { R.string.more_item1, R.string.more_item2,
			R.string.more_item3, R.string.more_item4, R.string.more_item5,
			R.string.more_item6, R.string.more_item7, R.string.more_item8,
			R.string.more_item9 };
	private MoneyDAO myDb;
	private Button saveGonguo_btn, saveDiary_btn;
	String remoteUrl = "http://REMOTEIP:8080/money/superconsole!importMoneyFromPhone.do";
	private final static int SUCCESS = 1;
	private int myyear;
	private int mymonth;
	private int myday;
	private Button dateBtn;
	private Button timeBtn;
	private ImageView jiamiImg;
	private EditText contentEdit;
	private Button returnbtn;
	private ListView gongguolist;
	private ListView diarylist;
	Intent intent;
	static final int DATE_DIALOG_ID = 0;
	private TableLayout table;

	/**
	 * 保存金额数据到远程.
	 */
	public void run() {
		String result = "未找到主机,请检查网络！";
		SharedPreferences settings = getSharedPreferences(Tool.CONFIG, 0);
		String remoteIp = settings.getString(Tool.REMOTEIP, "192.168.1.101");

		String moneys = myDb.allMoney();
		HttpPost post = new HttpPost(remoteUrl.replace("REMOTEIP", remoteIp));
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 将金额字符串放在表单里面的moneys参数里面传递到远程服务器.
		params.add(new BasicNameValuePair("moneyStr", moneys));

		try {
			if ("".equals(moneys)) {
				result = "已经全部保存到服务端！";
			} else {
				post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpResponse response = new DefaultHttpClient().execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					// 得到服务器端返回的结果字符串.
					result = EntityUtils.toString(response.getEntity());
					// 更新保存到远程端之后的状态为1
					myDb.updateMoneyStatusAfterSave();
				} else {
					result = "出现错误，错误代码是:"
							+ response.getStatusLine().getStatusCode();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 在接受完毕了服务器返回的数据之后，调用回调函数弹出返回的信息！
			Message message = new Message();
			message.what = MorePage.SUCCESS;
			message.obj = result;
			handler.sendMessage(message);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MorePage.SUCCESS:
				myDialog.dismiss();
				alert(msg.obj.toString());
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 删除全部的收支信息
	 */
	private void deleteAll() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_name)
				.setMessage(R.string.delete_all_confirm)
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
								myDb.deleteAllMoney();
							}
						}).show();
	}

	ProgressDialog myDialog = null;

	/**
	 * 保存到远程的GAE服务端.
	 */
	private void saveToServer() {
		myDialog = new ProgressDialog(MorePage.this);
		myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		myDialog.setTitle(getStr(R.string.waitting));// 设置标题
		myDialog.setMessage(getStr(R.string.sendingdata));
		myDialog.setIndeterminate(false);// 设置进度条是否为不明确
		myDialog.show();

		// 启动多线程，进行服务器端的请求。
		Thread thread = new Thread(MorePage.this);
		thread.start();
	}

	/**
	 * 备份金额数据.
	 */
	private void backup() {
		Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
		String money = myDb.allMoney();
		mailIntent.setType("plain/text");
		mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { "lishuiqing110@163.com" });
		mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getToday()
				+ "money");
		mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, money);
		startActivity(Intent.createChooser(mailIntent,
				getStr(R.string.sendmoney)));
	}

	/**
	 * 返回今天的时间.
	 * 
	 * @return
	 */
	public String getToday() {
		final Calendar today = Calendar.getInstance();
		myyear = today.get(Calendar.YEAR);
		mymonth = today.get(Calendar.MONTH) + 1;
		myday = today.get(Calendar.DAY_OF_MONTH);
		return new StringBuilder().append(myyear).append("-").append(mymonth)
				.append("-").append(myday).toString();
	}

	/**
	 * 添加功过里面的全部数据
	 */
	private void addRow() {
		final Resources res = getResources();
		// 准备从资源文件中读取数组.
		String[] gongguoname = res.getStringArray(R.array.gongguo);
		String[] gongguoId = res.getStringArray(R.array.gongguoId);
		for (int i = 0, j = gongguoname.length; i < j; i++) {
			TableRow tableRow = new TableRow(this);
			TextView textView = new TextView(this);
			ImageView img = new ImageView(this);
			// 设置id
			textView.setTag(gongguoId[i]);
			textView.setTextColor(Color.RED);
			textView.setText(gongguoname[i]);
			// 设置默认的为没有点击
			img.setBackgroundDrawable(res.getDrawable(R.drawable.off_bg));
			img.setTag("false");
			img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ImageView _img = (ImageView) view;
					if ("false".equals(_img.getTag())) {
						_img.setTag("true");
						_img.setBackgroundDrawable(res
								.getDrawable(R.drawable.on_bg));
					} else {
						_img.setTag("false");
						_img.setBackgroundDrawable(res
								.getDrawable(R.drawable.off_bg));
					}
				}
			});
			tableRow.addView(textView);
			tableRow.addView(img);
			table.addView(tableRow);
		}
	}

	/**
	 * 显示功过录的界面.
	 */
	private void gotoGongguo() {
		setContentView(R.layout.gongguo_index);
		dateBtn = (Button) findViewById(R.id.chooseTime_btn);
		saveGonguo_btn = (Button) findViewById(R.id.saveGonguo_btn);
		table = (TableLayout) findViewById(R.id.gongguo_table);
		addRow();
		dateBtn.setText(getToday());
		// 调用绑定事件的私有方法。
		prepareListener();
	}

	/**
	 * 跳转到日记本程序.
	 */
	private void gotoDiary() {
		setContentView(R.layout.diary_index);
		dateBtn = (Button) findViewById(R.id.chooseDate_btn);
		timeBtn = (Button) findViewById(R.id.chooseTime_btn);
		jiamiImg = (ImageView) findViewById(R.id.jiami);
		contentEdit = (EditText) findViewById(R.id.diaryContent);
		saveDiary_btn = (Button) findViewById(R.id.saveDiary_btn);
		dateBtn.setText(getToday());
		// 调用绑定事件的私有方法。
		prepareListener();
	}

	/**
	 * 远程保存功过信息.
	 */
	private void sendGongguo() {
		setContentView(R.layout.gongguo_index);
		dateBtn = (Button) findViewById(R.id.chooseTime_btn);
		saveGonguo_btn = (Button) findViewById(R.id.saveGonguo_btn);
		table = (TableLayout) findViewById(R.id.gongguo_table);
		addRow();
		dateBtn.setText(getToday());
		// 调用绑定事件的私有方法。
		prepareListener();
	}

	/**
	 * 功过列表
	 */
	private void gongguoList() {
		setContentView(R.layout.gongguo_list);
		gongguolist = (ListView) findViewById(R.id.ListView);
		returnbtn = (Button) findViewById(R.id.returnbtn);

		initGongguoList();

		prepareListener();
	}

	/**
	 * 显示功过列表.
	 */
	private void initGongguoList() {
		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		Cursor allGongguo = myDb.selectGonguoTimeAndStatus();

		if (allGongguo.getCount() >= 1) {
			allGongguo.moveToFirst();
			do {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("status", allGongguo.getString(2));
				map.put("time", allGongguo.getString(1));
				map.put("sno", allGongguo.getString(0));
				listItem.add(map);
			} while (allGongguo.moveToNext());
		}
		myDb.close();
		GongguoAdapter adapter = new GongguoAdapter(listItem, this);
		gongguolist.setAdapter(adapter);
	}

	/**
	 * 显示日志列表.
	 */
	private void initDiaryList() {
		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		Cursor allDiary = myDb.selectDiary();

		if (allDiary.getCount() >= 1) {
			allDiary.moveToFirst();
			do {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("date", allDiary.getString(2));
				map.put("time", allDiary.getString(1));
				map.put("sno", allDiary.getString(0));
				map.put("status", allDiary.getString(5));
				map.put("content", allDiary.getString(4));
				map.put("jiami", allDiary.getString(3));
				listItem.add(map);
			} while (allDiary.moveToNext());
		}
		myDb.close();
		DiaryAdapter adapter = new DiaryAdapter(listItem, this);
		diarylist.setAdapter(adapter);
	}

	/**
	 * 日记本列表.
	 */
	private void diaryList() {
		setContentView(R.layout.diary_list);
		diarylist = (ListView) findViewById(R.id.ListView);
		returnbtn = (Button) findViewById(R.id.returnbtn);
		initDiaryList();
		// 调用绑定事件的私有方法。
		prepareListener();
	}

	protected void onResume() {
		super.onResume();
		initFirstPage();
	}

	/**
	 * 初始化九宫格布局.
	 */
	private void initFirstPage() {
		setContentView(R.layout.more);
		initGridView();
		prepareListener();
	}

	/**
	 * 初始化九宫格布局.
	 */
	private void initGridView() {
		GridView gridview = (GridView) findViewById(R.id.GridView);
		ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 9; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", allMages[i % 3]);
			map.put("ItemText", getText(allitem[i]).toString());
			meumList.add(map);
		}

		SimpleAdapter saItem = new SimpleAdapter(this, meumList, // 数据源
				R.layout.more_item, // xml实现
				new String[] { "ItemImage", "ItemText" }, // 对应map的Key
				new int[] { R.id.ItemImage, R.id.ItemText }); // 对应R的Id

		// 添加Item到网格中
		gridview.setAdapter(saItem);
		// 添加点击事件
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent openUrl = new Intent();
				switch (arg2) {
				// 打开配置信息
				case 0:
					openUrl.setClass(MorePage.this, SaveConfig.class);
					startActivity(openUrl);
					break;
				case 1:
					deleteAll();
					break;
				// 进行远程保存金额信息
				case 2:
					saveToServer();
					break;
				// 备份金额数据
				case 3:
					backup();
					break;
				// 功过记录
				case 4:
					gotoGongguo();
					break;
				// 私人日记本
				case 5:
					gotoDiary();
					break;
				// 发送功过信息
				case 6:
					sendGongguo();
					break;
				// 功过列表
				case 7:
					gongguoList();
					break;
				// 日记本列表
				case 8:
					diaryList();
					break;
				default:
					break;
				}
			}
		});
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initFirstPage();
		// 实例化数据库
		myDb = new MoneyDAO(this, MoneyDAO.VERSION);

	}

	/**
	 * 定义弹出来的日期选择框的单击事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			myyear = year;
			mymonth = monthOfYear;
			myday = dayOfMonth;
			dateBtn.setText(new StringBuilder().append(myyear).append("-")
					.append(mymonth + 1).append("-").append(myday));
		}
	};

	/**
	 * 自定义弹出日期选择框
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			System.out.println("myyear=" + myyear + ",mymonth=" + mymonth);
			return new DatePickerDialog(this, mDateSetListener, myyear,
					mymonth, myday);
		}
		return null;
	}

	/**
	 * 跳转到保存金额信息.
	 */
	public void goSaveMoney() {
		Intent openUrl = new Intent();
		openUrl.setClass(MorePage.this, SaveMoney.class);
		startActivity(openUrl);
		MorePage.this.finish();
	}

	// 调用绑定事件的私有方法。
	protected void prepareListener() {
		if (dateBtn != null) {
			dateBtn.setOnClickListener(this);
		}
		if (saveGonguo_btn != null) {
			saveGonguo_btn.setOnClickListener(this);
		}
		if (returnbtn != null) {
			returnbtn.setOnClickListener(this);
		}
		if (saveDiary_btn != null) {
			saveDiary_btn.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.chooseTime_btn) {
			showDialog(DATE_DIALOG_ID);
		} else if (v.getId() == R.id.saveGonguo_btn) {
			String time = dateBtn.getText().toString();
			int ct = table.getChildCount();
			for (int i = 0; i < ct; i++) {
				TableRow row = (TableRow) table.getChildAt(i);
				TextView textView = (TextView) row.getChildAt(0);
				ImageView img = (ImageView) row.getChildAt(1);
				myDb.insertGonguo(time, textView.getTag() + "", textView
						.getText().toString(), "0", img.getTag() + "");
			}
		}
		// 点击返回按钮，就退回原来的初始页面布局.
		else if (v.getId() == R.id.returnbtn) {
			initFirstPage();
		}
		// 保存日记
		else if (v.getId() == R.id.saveDiary_btn) {
			String time = timeBtn.getText().toString();
			String date = dateBtn.getText().toString();
			String content = contentEdit.getText().toString();
			String jiami = jiamiImg.getTag() + "";
			myDb.insertDiary(date, time, content, "0", jiami);
		}
	}
}