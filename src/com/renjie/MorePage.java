package com.renjie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.renjie.adapter.DiaryAdapter;
import com.renjie.adapter.GongguoAdapter;
import com.renjie.adapter.MoneyAdapter;
import com.renjie.tool.MoneyDAO;
import com.renjie.tool.Tool;

/**
 * 更多的九宫格布局.
 * 
 * @author Administrator
 * 
 */
public class MorePage extends BaseActivity implements OnClickListener {
	int[] allMages = { R.drawable.item_1, R.drawable.item_2, R.drawable.item_3 };
	int[] allitem = { R.string.more_gongguo, R.string.more_diary,
			R.string.more_backupall, R.string.more_sendmoney,
			R.string.more_senddiary, R.string.more_sendgongguo,
			R.string.more_gongguolist, R.string.more_diarylist,
			R.string.more_moneylist, R.string.more_config, R.string.more_tree };
	private MoneyDAO myDb;
	private Button saveGonguo_btn, saveDiary_btn;
	String remoteMoneyUrl = "http://REMOTEIP:PORT/money/superconsole!importPhoneMoney.do";
	String remoteDiaryUrl = "http://REMOTEIP:PORT/money/superconsole!importPhoneDiary.do";
	String remoteGongguoUrl = "http://REMOTEIP:PORT/money/superconsole!importPhoneGongguo.do";
	private final static int SUCCESS = 1;
	private int myyear;
	private int mymonth;
	private int myday;
	private int hour, minute;
	private Button dateBtn;
	private Button timeBtn;
	private ImageView jiamiImg;
	private EditText contentEdit;
	private Button returnbtn;
	private ListView gongguolist;
	private ListView diarylist, moneylist;
	Intent intent;
	static final int DATE_DIALOG_ID = 0;
	private TableLayout table;
	private SharedPreferences settings;

	/**
	 * 调用远程服务端.
	 * 
	 * @author Administrator
	 * 
	 */
	private class SendToServer implements Runnable {
		private String type;

		public SendToServer(String type) {
			this.type = type;
		}

		@Override
		public void run() {
			String result = "未找到主机,请检查网络！";
			String remoteIp = settings
					.getString(Tool.REMOTEIP, "192.168.1.101");
			String port = settings.getString(Tool.PORT, "9999");
			String diarys = null;
			String url = null;
			if ("money".equals(type)) {
				diarys = myDb.allMoney();
				url = remoteMoneyUrl.replace("REMOTEIP", remoteIp).replace(
						"PORT", port);
			} else if ("diary".equals(type)) {
				diarys = myDb.allDiary();
				url = remoteDiaryUrl.replace("REMOTEIP", remoteIp).replace(
						"PORT", port);
			} else if ("gongguo".equals(type)) {
				diarys = myDb.allGongguo();
				url = remoteGongguoUrl.replace("REMOTEIP", remoteIp).replace(
						"PORT", port);
			}
			HttpPost post = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// 将金额字符串放在表单里面的moneys参数里面传递到远程服务器.
			params.add(new BasicNameValuePair("moneyStr", diarys));

			try {
				if ("".equals(diarys)) {
					result = "已经全部保存到服务端！";
				} else {
					post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					HttpResponse response = new DefaultHttpClient()
							.execute(post);
					if (response.getStatusLine().getStatusCode() == 200) {
						// 得到服务器端返回的结果字符串.
						result = EntityUtils.toString(response.getEntity());
						// 更新保存到远程端之后的状态为1
						if ("money".equals(type)) {
							myDb.updateMoneyStatusAfterSave();
						} else if ("diary".equals(type)) {
							myDb.updateDiaryStatusAfterSave();
						} else if ("gongguo".equals(type)) {
							myDb.updateGonguoStatusAfterSave();
						}
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
		confirm(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				myDb.deleteAllMoney();
			}
		});
	}

	ProgressDialog myDialog = null;

	/**
	 * 保存到远程的GAE服务端.
	 */
	private void saveMoneyToServer() {
		myDialog = new ProgressDialog(MorePage.this);
		myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		myDialog.setTitle(getStr(R.string.waitting));// 设置标题
		myDialog.setMessage(getStr(R.string.sendingdata));
		myDialog.setIndeterminate(false);// 设置进度条是否为不明确
		myDialog.show();

		// 启动多线程，进行服务器端的请求。
		Thread thread = new Thread(new SendToServer("money"));
		thread.start();
	}

	/**
	 * 远程保存功过信息.
	 */
	private void saveGonguoToServer() {
		myDialog = new ProgressDialog(MorePage.this);
		myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		myDialog.setTitle(getStr(R.string.waitting));// 设置标题
		myDialog.setMessage(getStr(R.string.sendingdata));
		myDialog.setIndeterminate(false);// 设置进度条是否为不明确
		myDialog.show();

		// 启动多线程，进行服务器端的请求。
		Thread thread = new Thread(new SendToServer("gongguo"));
		thread.start();
	}

	private void saveDiaryToServer() {
		myDialog = new ProgressDialog(MorePage.this);
		myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		myDialog.setTitle(getStr(R.string.waitting));// 设置标题
		myDialog.setMessage(getStr(R.string.sendingdata));
		myDialog.setIndeterminate(false);// 设置进度条是否为不明确
		myDialog.show();

		// 启动多线程，进行服务器端的请求。
		Thread thread = new Thread(new SendToServer("diary"));
		thread.start();
	}

	/**
	 * 备份金额数据.
	 */
	private void backup() {
		Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
		String money = myDb.allMoney(true) + "\n\n\n" + myDb.allDiary(true) + "\n\n\n"
				+ myDb.allGongguo(true);

		mailIntent.setType("plain/text");
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
		mymonth = today.get(Calendar.MONTH);
		myday = today.get(Calendar.DAY_OF_MONTH);

		hour = today.get(Calendar.HOUR_OF_DAY);
		minute = today.get(Calendar.MINUTE);
		return new StringBuilder().append(myyear).append("-")
				.append(mymonth + 1).append("-").append(myday).toString();
	}

	/**
	 * 添加功过里面的全部数据
	 */
	private void addRow() {
		final Resources res = getResources();
		// 准备从资源文件中读取数组.
		String[] gongguoname = res.getStringArray(R.array.gongguo);
		String[] gongguoId = res.getStringArray(R.array.gongguoId);
		TableRow tableRow = null;
		TextView textView = null;
		TextView count = null;
		TextView continueTv = null;
		ImageView img = null;
		Cursor c = null;

		for (int i = 0, j = gongguoname.length; i < j; i++) {
			int trueC = 0;
			int falseC = 0;
			tableRow = new TableRow(this);
			textView = new TextView(this);
			continueTv = new TextView(this);
			img = new ImageView(this);
			count = new TextView(this);
			// 设置id
			textView.setTag(gongguoId[i]);
			textView.setTextColor(Color.RED);
			textView.setText(gongguoname[i]);
			c = myDb.groupByValue(gongguoId[i]);
			if (c.getCount() >= 1) {
				c.moveToFirst();
				do {
					if ("true".equals(c.getString(0)))
						trueC = Integer.parseInt(c.getString(1));
					else if ("false".equals(c.getString(0)))
						falseC = Integer.parseInt(c.getString(1));
				} while (c.moveToNext());
			}
			c.close();
			continueTv.setTextColor(Color.GREEN);
			continueTv.setText("已坚持"
					+ myDb.continueByValue(dateBtn.getText().toString(),
							gongguoId[i]));
			count.setTextColor(Color.RED);
			count.setText(trueC + "/" + falseC);
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
			tableRow.addView(continueTv);
			tableRow.addView(count);
			table.addView(tableRow);
		}
	}

	private EditText plan;
	private TextView planShow;

	/**
	 * 显示功过录的界面.
	 */
	private void gotoGongguo() {
		setContentView(R.layout.gongguo_index);
		dateBtn = (Button) findViewById(R.id.chooseTime_btn);
		plan = (EditText) findViewById(R.id.plan);
		planShow = (TextView) findViewById(R.id.plan_show);
		saveGonguo_btn = (Button) findViewById(R.id.saveGonguo_btn);
		table = (TableLayout) findViewById(R.id.gongguo_table);
		dateBtn.setText(getToday());
		String lastPlan = settings.getString(afterAnyDay(new Date(), -1)
				+ "plan", "无");
		planShow.setText(lastPlan);
		addRow();
		// 调用绑定事件的私有方法。
		prepareListener();
	}

	public static final long ONEDAYMILLISECONDS = 86400000;

	public static String afterAnyDay(Date dd, int days) {
		Date d = afterDate(dd, days);
		GregorianCalendar ca = new GregorianCalendar();
		ca.setTime(d);
		int y = ca.get(Calendar.YEAR);
		int m = ca.get(Calendar.MONTH);
		int ddd = ca.get(Calendar.DAY_OF_MONTH);
		return new StringBuilder().append(y).append("-").append(m + 1)
				.append("-").append(ddd).toString();
	}

	public static Date afterDate(Date date, int days) {
		if (date == null)
			return null;
		Date newDate = new Date();
		long tp = date.getTime();
		tp = tp + days * ONEDAYMILLISECONDS;
		newDate.setTime(tp);
		return newDate;
	}

	private Spinner diaryType;

	/**
	 * 跳转到日记本程序.
	 */
	private void gotoDiary() {
		setContentView(R.layout.diary_index);
		dateBtn = (Button) findViewById(R.id.diaryDate);
		timeBtn = (Button) findViewById(R.id.diaryTime);
		jiamiImg = (ImageView) findViewById(R.id.jiami);
		contentEdit = (EditText) findViewById(R.id.diaryContent);
		diaryType = (Spinner) findViewById(R.id.diary_tp);
		saveDiary_btn = (Button) findViewById(R.id.saveDiary_btn);
		dateBtn.setText(getToday());
		timeBtn.setText(hour + ":" + minute);
		createSimpleSipnner(diaryType, R.array.diarytype);

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
	 * 删除日记.
	 * 
	 * @param time
	 */
	private void deleteDiary(final long sno) {
		confirm(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				myDb.deleteDiary(sno);
				showMess(R.string.delete_success);
				initDiaryList();
			}
		});
	}

	private void deleteMoney(final long sno) {
		confirm(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				myDb.deleteMoney(sno);
				Toast.makeText(getApplicationContext(),
						getText(R.string.delete_success).toString(),
						Toast.LENGTH_SHORT).show();
				initMoneyList();
			}
		});
	}

	/**
	 * 删除功过信息.
	 * 
	 * @param id
	 */
	private void deleteGonguo(final String time) {
		confirm(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				myDb.deleteGonguoByTime(time);
				showMess(R.string.delete_success);
				initGongguoList();
			}
		});
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
		allGongguo.close();
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
				map.put("type", allDiary.getString(6));
				listItem.add(map);
			} while (allDiary.moveToNext());
		}
		myDb.close();
		DiaryAdapter adapter = new DiaryAdapter(listItem, this);
		diarylist.setAdapter(adapter);
	}

	private void initMoneyList() {
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
		moneylist.setAdapter(adapter);
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

	private void moneyList() {
		setContentView(R.layout.list_main_old);
		moneylist = (ListView) findViewById(R.id.ListView);
		returnbtn = (Button) findViewById(R.id.returnbtn);
		initMoneyList();
		// 调用绑定事件的私有方法。
		prepareListener();
	}

	/**
	 * 重新返回页面的时候，直接显示九宫格布局.
	 */
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

	private void goHualun() {
		Intent openUrl = new Intent();
		openUrl.setClass(MorePage.this, DateActivity.class);
		startActivity(openUrl);
	}

	/**
	 * 初始化九宫格布局.
	 */
	private void initGridView() {
		GridView gridview = (GridView) findViewById(R.id.GridView);
		ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 11; i++) {
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
				// 功过记录
				case 0:
					gotoGongguo();
					break;
				// 私人日记本
				case 1:
					gotoDiary();
					break;
				// 备份金额数据
				case 2:
					backup();
					break;
				// 进行远程保存金额信息
				case 3:
					saveMoneyToServer();
					break;
				case 4:
					saveDiaryToServer();
					break;
				// 发送功过信息
				case 5:
					saveGonguoToServer();
					break;
				// 功过列表
				case 6:
					gongguoList();
					break;
				// 日记本列表
				case 7:
					diaryList();
					break;// 日记本列表
				case 8:
					moneyList();
					break;// 日记本列表
				// 打开配置信息
				case 9:
					openUrl.setClass(MorePage.this, SaveConfig.class);
					startActivity(openUrl);
					break;
				case 10:
					openUrl.setClass(MorePage.this, TreeListDemoActivity.class);
					startActivity(openUrl);
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
		settings = getSharedPreferences(Tool.CONFIG, 0);
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
			String d = new StringBuilder().append(myyear).append("-")
					.append(mymonth + 1).append("-").append(myday).toString();
			dateBtn.setText(d);

			if (planShow != null) {
				String lastPlan = settings.getString(
						afterAnyDay(getDate(d), -1) + "plan", "无");
				planShow.setText(lastPlan);
			}
		}
	};

	public static Date getDate(String dateStr) {
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = formatter2.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 自定义弹出日期选择框
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
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
		if (jiamiImg != null) {
			jiamiImg.setTag("false");
			jiamiImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if ("false".equals(v.getTag())) {
						jiamiImg.setTag("true");
						jiamiImg.setSelected(true);
					} else {
						jiamiImg.setTag("false");
						jiamiImg.setSelected(false);
					}
				}
			});
		}
		if (gongguolist != null)
			gongguolist
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							String sno = ""
									+ arg1.findViewById(R.id.time).getTag();
							deleteGonguo(sno);
						}
					});
		if (diarylist != null)
			diarylist
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							String sno = ""
									+ arg1.findViewById(R.id.time).getTag();
							deleteDiary(Long.parseLong(sno));
						}
					});
		if (moneylist != null)
			moneylist
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							String sno = ""
									+ arg1.findViewById(R.id.time).getTag();
							deleteMoney(Long.parseLong(sno));
						}
					});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.chooseTime_btn || v.getId() == R.id.diaryDate) {
			showDialog(DATE_DIALOG_ID);
		}
		// 保存功过信息.
		else if (v.getId() == R.id.saveGonguo_btn) {
			String time = dateBtn.getText().toString();
			int ct = table.getChildCount();
			// 先删除已经存在的记录.
			myDb.deleteGonguoByTime(time);
			for (int i = 0; i < ct; i++) {
				TableRow row = (TableRow) table.getChildAt(i);
				TextView textView = (TextView) row.getChildAt(0);
				ImageView img = (ImageView) row.getChildAt(1);
				myDb.insertGonguo(time, textView.getTag() + "", textView
						.getText().toString(), "0", img.getTag() + "");
			}
			showMess(R.string.save_success);
			String _p = plan.getText().toString();
			// String lastPlan = settings.getString(time+"plan", "无");
			// 保存当前的计划.
			if(!"".equals(_p)){
				settings.edit().putString(time + "plan", _p).commit();
				// 保存当前计划到日志里面.
				myDb.insertDiary(time, "0:0", plan.getText().toString(), "0",
						"false", Tool.DIARY_TYPE_PLAN+"");
			}
			initFirstPage();
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
			int dt = "普通".equals(diaryType.getSelectedItem().toString()) ? Tool.DIARY_TYPE_COMMON
					: Tool.DIARY_TYPE_LICAI;
			myDb.insertDiary(date, time, content, "0", jiami, dt+"");
			showMess(R.string.save_success);

			initFirstPage();
		}
	}
}
