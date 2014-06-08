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

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.renjie.adapter.DiaryAdapter;
import com.renjie.adapter.GongguoAdapter;
import com.renjie.adapter.MoneyAdapter;
import com.renjie.adapter.MoneyList2Adatper;
import com.renjie.tool.HttpRequire;
import com.renjie.tool.MoneyDAO;
import com.renjie.tool.Reminder;
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
			R.string.more_moneylist, R.string.more_config, R.string.more_tree,
			R.string.more_moneyreport, R.string.more_tip };
	private MoneyDAO myDb;
	private Button saveGonguo_btn, saveDiary_btn, saveTip_btn;
	String remoteMoneyUrl = "http://REMOTEIP:PORT/money/superconsole!importPhoneMoney.do";
	String remoteDiaryUrl = "http://REMOTEIP:PORT/money/superconsole!importPhoneDiary.do";
	String remoteGongguoUrl = "http://REMOTEIP:PORT/money/superconsole!importPhoneGongguo.do";
	public static final String REMOTEREPORT_OUT = "http://REMOTEIP:PORT/money/superconsole!getReportOut.do";
	public static final String REMOTEREPORT_BY_YEAR = "http://REMOTEIP:PORT/money/superconsole!getReportOutByYear.do?year=YEAR";
	public static final String REMOTEREPORT_BY_MONTH = "http://REMOTEIP:PORT/money/superconsole!getReportOutByMonth.do?year=YEAR&month=MONTH";
	public static final String REMOTEREPORT_BY_DAY = "http://REMOTEIP:PORT/money/superconsole!getReportOutInDay.do?day=DAY";
	public static final String REMOTEREPORT_BIG_TYPE = "http://REMOTEIP:PORT/money/superconsole!reportSumByBigType.do";
	public static final String REMOTEREPORT_SMALL_TYPE = "http://REMOTEIP:PORT/money/superconsole!reportSumBySmallType.do?bigType=BIGTYPE";
	public static final String REMOTEREPORT_TYPE_YEAR = "http://REMOTEIP:PORT/money/superconsole!reportSumBySmallTypeInYear.do?tallyType=TALLYTYPE";
	public static final String REMOTEREPORT_TYPE_MONTH = "http://REMOTEIP:PORT/money/superconsole!reportSumBySmallTypeInMonth.do?tallyType=TALLYTYPE&year=YEAR";
	public static final String REMOTEREPORT_TYPE_DAY = "http://REMOTEIP:PORT/money/superconsole!reportSumBySmallTypeInDay.do?tallyType=TALLYTYPE&year=YEAR&month=MONTH";
	public static final String REMOTEREPORT_TYPE_IN_DAY = "http://REMOTEIP:PORT/money/superconsole!reportSumBySmallTypeInSomeDay.do?tallyType=TALLYTYPE&year=YEAR&month=MONTH&day=DAY";

	private final static int SUCCESS = 1;
	private int myyear;
	private int mymonth;
	private int myday;
	private int hour, minute;
	private Button dateBtn, leavediary, leavegongguo;
	private Button timeBtn, bbackBtn, top_btn;
	private ImageView jiamiImg;
	private EditText contentEdit, tipContent;
	private Button returnbtn;
	private TextView report_title;
	private ListView gongguolist;
	private ListView diarylist, moneylist, reportMoneylist;
	Intent intent;
	static final int DATE_DIALOG_ID = 0, TIME_DIALOG_ID = 2;
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
		}, R.string.delete_confirm);
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
		String money = myDb.allMoney(true) + "\n\n\n" + myDb.allDiary(true)
				+ "\n\n\n" + myDb.allGongguo(true);

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
	private TextView planShow, mk_plan;

	/**
	 * 显示功过录的界面.
	 */
	private void gotoGongguo() {
		setContentView(R.layout.gongguo_index);
		leavegongguo = (Button) findViewById(R.id.leavegongguo_btn);
		dateBtn = (Button) findViewById(R.id.chooseTime_btn);
		plan = (EditText) findViewById(R.id.plan);
		planShow = (TextView) findViewById(R.id.plan_show);
		mk_plan = (TextView) findViewById(R.id.mk_plan);
		saveGonguo_btn = (Button) findViewById(R.id.saveGonguo_btn);
		table = (TableLayout) findViewById(R.id.gongguo_table);
		dateBtn.setText(getToday());
		String lastPlan = settings.getString(afterAnyDay(new Date(), -1)
				+ "plan", "无");
		planShow.setText(lastPlan);
		addRow();
		plan.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mk_plan.setText("制定计划\n(" + s.length() + ")");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}

		});
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

	private Spinner diaryType, tip_tp;

	/**
	 * 跳转到日记本程序.
	 */
	private void gotoDiary() {
		setContentView(R.layout.diary_index);
		leavediary = (Button) findViewById(R.id.leavediary_btn);
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
		}, R.string.delete_confirm);
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
		}, R.string.delete_confirm);
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
		}, R.string.delete_confirm);
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
		MoneyAdapter adapter = new MoneyAdapter(listItem, isSuper, this);
		moneylist.setAdapter(adapter);
	}

	/* 下面是全部的显示列表的东西 */
	private static final int DIALOG_KEY = 1;
	private ProgressDialog dialog;
	private String y, m, d, moneyType, bigType, currentLevel = null;
	private List<Node> manager;
	private MoneyList2Adatper mAdatper;
	private Button topBtn, backBtn;

	public Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mAdatper = new MoneyList2Adatper(manager, MorePage.this, true,
						false, false);
				reportMoneylist.setAdapter(mAdatper);
				// list2.setAdapter(mAdatper);
				break;
			case 2:
				mAdatper = new MoneyList2Adatper(manager, MorePage.this, false,
						true, true);
				reportMoneylist.setAdapter(mAdatper);
				// list2.setAdapter(mAdatper);
				break;
			case 3:
				bbackBtn.setVisibility(View.VISIBLE);
				break;
			case 4:
				bbackBtn.setVisibility(View.GONE);
				break;
			default:
				super.hasMessages(msg.what);
				break;
			}
		}
	};

	/**
	 * 解析远程的数据
	 */
	private void queryInBigType() {
		y = null;
		m = null;
		d = null;
		moneyType = null;
		bigType = null;
		currentLevel = "1";
		try {
			manager = new ArrayList<Node>();
			JSONArray arr = HttpRequire.getReportByBigType(settings);
			for (int i = 0, j = arr.size(); i < j; i++) {
				Node node1 = new Node();
				JSONArray a = arr.getJSONArray(i);
				node1.setName("[" + a.get(0) + "]");
				node1.setId("" + a.get(2));
				node1.setLevel(1);
				node1.setCode(a.get(1) + "");
				manager.add(node1);
			}
			myHandler.sendEmptyMessage(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void queryInSmallType(String bg) {
		y = null;
		m = null;
		d = null;
		bigType = bg;
		currentLevel = "2";
		moneyType = null;
		try {
			manager = new ArrayList<Node>();
			JSONArray arr = HttpRequire.getReportBySmallType(settings, bg);
			for (int i = 0, j = arr.size(); i < j; i++) {
				Node node1 = new Node();
				JSONArray a = arr.getJSONArray(i);
				node1.setName("[" + a.get(0) + "]");
				node1.setId(bg + "," + a.get(2));
				node1.setLevel(2);
				node1.setCode(a.get(1) + "");
				manager.add(node1);
			}
			myHandler.sendEmptyMessage(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void queryInSmallTypeInYear(String bg) {
		y = null;
		m = null;
		d = null;
		currentLevel = "3";
		moneyType = bg;
		try {
			manager = new ArrayList<Node>();
			JSONArray arr = HttpRequire
					.getReportBySmallTypeInYear(settings, bg);
			for (int i = 0, j = arr.size(); i < j; i++) {
				Node node1 = new Node();
				JSONArray a = arr.getJSONArray(i);
				node1.setName("[" + a.get(0) + "]年");
				node1.setId(bg + "," + a.get(0));
				node1.setLevel(3);
				node1.setCode(a.get(1) + "");
				manager.add(node1);
			}
			myHandler.sendEmptyMessage(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void queryInSmallTypeInMonth(String bg, String year) {
		y = year;
		m = null;
		d = null;
		currentLevel = "4";
		moneyType = bg;
		try {
			manager = new ArrayList<Node>();
			JSONArray arr = HttpRequire.getReportBySmallTypeInMonth(settings,
					bg, y);
			for (int i = 0, j = arr.size(); i < j; i++) {
				Node node1 = new Node();
				JSONArray a = arr.getJSONArray(i);
				node1.setName(year + "-" + a.get(0) + "月");
				node1.setId(bg + "," + year + "," + a.get(0));
				node1.setLevel(4);
				node1.setCode(a.get(1) + "");
				manager.add(node1);
			}
			myHandler.sendEmptyMessage(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询一天的具体消费记录.
	 * 
	 * @param bg
	 * @param year
	 * @param month
	 */
	private void queryInSmallTypeInADay(String bg, String year, String month,
			String day) {
		y = year;
		m = month;
		currentLevel = "6";
		d = day;
		moneyType = bg;
		try {
			manager = new ArrayList<Node>();
			JSONArray arr = HttpRequire.getReportBySmallTypeInSomeDay(settings,
					bg, year, month, day);
			for (int i = 0, j = arr.size(); i < j; i++) {
				Node node1 = new Node();
				JSONArray a = arr.getJSONArray(i);

				node1.setName(a.get(0) + "," + a.get(1));
				node1.setId(a.get(0) + "," + a.get(1));
				node1.setLevel(6);
				node1.setParam1(a.get(3) + "");
				node1.setCode(a.get(2) + "");

				manager.add(node1);
			}
			myHandler.sendEmptyMessage(2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void queryInSmallTypeInDay(String bg, String year, String month) {
		y = year;
		m = month;
		currentLevel = "5";
		d = null;
		moneyType = bg;
		try {
			manager = new ArrayList<Node>();
			JSONArray arr = HttpRequire.getReportBySmallTypeInDay(settings, bg,
					year, month);
			for (int i = 0, j = arr.size(); i < j; i++) {
				Node node1 = new Node();
				JSONArray a = arr.getJSONArray(i);
				node1.setName("[" + a.get(0) + "]");
				node1.setId(bg + "," + year + "," + month + "," + a.get(0));
				node1.setLevel(5);
				node1.setCode(a.get(1) + "");
				manager.add(node1);
			}
			myHandler.sendEmptyMessage(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示出现按照金额类别分类的金额报表.
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyListLoader extends AsyncTask<String, String, String> {

		private boolean showDialog;
		private int type;
		private String y;
		private String m;
		private String d;
		private String tp, bigTp;

		public MyListLoader(boolean showDialog, int type, String year,
				String month, String tp, String bigTp, String day) {
			this.showDialog = showDialog;
			this.type = type;
			this.y = year;
			this.bigTp = bigTp;
			this.m = month;
			this.d = day;
			this.tp = tp;
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
				queryInBigType();
			else if (type == 2) {
				queryInSmallType(bigTp);
			} else if (type == 3) {
				queryInSmallTypeInYear(tp);
			} else if (type == 4) {
				queryInSmallTypeInMonth(tp, y);
			} else if (type == 5) {
				queryInSmallTypeInDay(tp, y, m);
			} else if (type == 6) {
				queryInSmallTypeInADay(tp, y, m, d);
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
			if (type == 4 || type == 2 || type == 3 || type == 5 || type == 6) {
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

	private void initMoneyList3() {
		new MyListLoader(true, 1, null, null, null, null, null).execute("");
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
	 * 显示按照类别分类的金额报表.
	 */
	private void moneyList3() {
		setContentView(R.layout.list_money3);
		reportMoneylist = (ListView) findViewById(R.id.ListView);
		bbackBtn = (Button) findViewById(R.id.bbackbtn);
		report_title = (TextView) findViewById(R.id.report_title);
		top_btn = (Button) findViewById(R.id.top_btn);
		initMoneyList3();

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
		for (int i = 0; i < allitem.length; i++) {
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
				case 11:
					if (isSuper) {
						moneyList3();
					} else {
						alert("没有权限");
					}
					break;
				case 12:
					goTip();
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * 打开提醒界面
	 * 
	 */
	public void goTip() {
		setContentView(R.layout.tip_index);
		dateBtn = (Button) findViewById(R.id.tipDate);
		timeBtn = (Button) findViewById(R.id.tipTime);
		tipContent = (EditText) findViewById(R.id.tipContent);
		tip_tp = (Spinner) findViewById(R.id.tip_tp);
		saveTip_btn = (Button) findViewById(R.id.saveTip_btn);
		dateBtn.setText(getToday());
		timeBtn.setText(hour + ":" + minute);
		createSimpleSipnner(tip_tp, R.array.tiptype);

		// 调用绑定事件的私有方法。
		prepareListener();
	}

	private boolean isSuper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		settings = getSharedPreferences(Tool.CONFIG, 0);
		initFirstPage();
		isSuper = getIntent().getBooleanExtra(Tool.SUPERPASS, false);
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

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker arg0, int h, int m) {
			hour = h;
			minute = m;
			String d = new StringBuilder().append(hour).append(":")
					.append(minute).toString();
			timeBtn.setText(d);
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
		case DIALOG_KEY:
			dialog = new ProgressDialog(this);
			dialog.setMessage("正在查询...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, myyear,
					mymonth, myday);
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, hour, minute,
					true);
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
		if (saveTip_btn != null) {
			saveTip_btn.setOnClickListener(this);
		}
		if (timeBtn != null) {
			timeBtn.setOnClickListener(this);
		}
		if (leavegongguo != null) {
			leavegongguo.setOnClickListener(this);
		}
		if (report_title != null)
			report_title.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					initFirstPage();
				}
			});
		if (saveGonguo_btn != null) {
			saveGonguo_btn.setOnClickListener(this);
		}
		if (leavediary != null) {
			leavediary.setOnClickListener(this);
		}
		if (returnbtn != null) {
			returnbtn.setOnClickListener(this);
		}
		if (backBtn != null) {
			backBtn.setOnClickListener(this);
		}
		if (top_btn != null) {
			top_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					initMoneyList3();
				}
			});
		}
		if (bbackBtn != null) {
			bbackBtn.setOnClickListener(this);
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
		if (reportMoneylist != null)
			reportMoneylist
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							final String id = ""
									+ arg1.findViewById(R.id.next).getTag();
							String level = ""
									+ arg1.findViewById(R.id.money).getTag();
							if ("1".equals(level)) {
								new MyListLoader(true, 2, null, null, null, id
										+ "", null).execute("");
							} else if ("2".equals(level)) {
								String[] datas = id.split(",");
								new MyListLoader(true, 3, null, null, datas[1],
										datas[0], null).execute("");
							} else if ("3".equals(level)) {
								String[] datas = id.split(",");
								new MyListLoader(true, 4, datas[1], null,
										datas[0], null, null).execute("");
							} else if ("4".equals(level)) {
								String[] datas = id.split(",");
								new MyListLoader(true, 5, datas[1], datas[2],
										datas[0], null, null).execute("");
							} else if ("5".equals(level)) {
								String[] datas = id.split(",");
								new MyListLoader(true, 6, datas[1], datas[2],
										datas[0], null, datas[3]).execute("");
							}
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
		if (v.getId() == R.id.chooseTime_btn || v.getId() == R.id.diaryDate
				|| v.getId() == R.id.tipDate) {
			showDialog(DATE_DIALOG_ID);
		} else if (v.getId() == R.id.tipTime || v.getId() == R.id.diaryTime) {
			showDialog(TIME_DIALOG_ID);
		} else if (v.getId() == R.id.leavediary_btn
				|| v.getId() == R.id.leavegongguo_btn) {
			initFirstPage();
		} else if (v.getId() == R.id.bbackbtn) {
			if ("2".equals(currentLevel)) {
				new MyListLoader(true, 1, null, null, null, null, null)
						.execute("");
			} else if ("3".equals(currentLevel)) {
				// 按照小类别进行统计.
				new MyListLoader(true, 2, null, null, null, bigType, null)
						.execute("");
			} else if ("4".equals(currentLevel)) {
				// 按照小类别还有年份统计.
				new MyListLoader(true, 3, null, null, moneyType, null, null)
						.execute("");
			} else if ("5".equals(currentLevel)) {
				new MyListLoader(true, 4, y, null, moneyType, null, null)
						.execute("");
			} else if ("6".equals(currentLevel)) {
				new MyListLoader(true, 5, y, m, moneyType, null, null)
						.execute("");
			}
		}
		// 保存功过信息.
		else if (v.getId() == R.id.saveGonguo_btn) {
			String _p = plan.getText().toString();
			if (_p.length() > 65) {
				alert("计划不可以超出65个字.");
				return;
			}
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
			// String lastPlan = settings.getString(time+"plan", "无");
			// 保存当前的计划.
			if (!"".equals(_p)) {
				settings.edit().putString(time + "plan", _p).commit();
				// 保存当前计划到日志里面.
				myDb.insertDiary(time, "0:0", plan.getText().toString(), "0",
						"false", Tool.DIARY_TYPE_PLAN + "");
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
			myDb.insertDiary(date, time, content, "0", jiami, dt + "");
			showMess(R.string.save_success);

			initFirstPage();
		}// 保存提醒记录
		else if (v.getId() == R.id.saveTip_btn) {
			String time = timeBtn.getText().toString();
			String date = dateBtn.getText().toString();
			String _tipContent = tipContent.getText().toString();
			int _tip_tp = 0;
			for (String s : Tool.TIP_TYPES) {
				if (s.equals(tip_tp.getSelectedItem().toString())) {
					break;
				} else {
					_tip_tp++;
				}
			}
			String _h = time.split(":")[0];
			String _mi = time.split(":")[1];
			String _m = date.split("-")[1];
			String _y = date.split("-")[0];
			String _d = date.split("-")[2];
			try {
				if ("一次".equals(tip_tp.getSelectedItem().toString())) {
					AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent("com.renjie.money");
					intent.putExtra("tipContent", _tipContent);
					intent.putExtra("time", time);
					intent.putExtra("date", date);
					intent.putExtra("tip_tp", _tip_tp+"");
					SimpleDateFormat formatter2 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					Date d = formatter2.parse(date + " " + time); 

					PendingIntent sender = PendingIntent.getBroadcast(
							MorePage.this, 0, intent,
							PendingIntent.FLAG_CANCEL_CURRENT);
					// 闹铃间隔， 这里设为1分钟闹一次，在第2步我们将每隔1分钟收到一次广播
					am.set(AlarmManager.RTC_WAKEUP, d.getTime(), sender);
				} else if ("每天".equals(tip_tp.getSelectedItem().toString())) {
					AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent("com.renjie.money");
					intent.putExtra("tipContent", _tipContent);
					intent.putExtra("time", time);
					intent.putExtra("date", date);
					intent.putExtra("tip_tp", _tip_tp+"");
					SimpleDateFormat formatter2 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					Date d = formatter2.parse(date + " " + time); 

					PendingIntent sender = PendingIntent.getBroadcast(
							MorePage.this, 0, intent,
							PendingIntent.FLAG_CANCEL_CURRENT);
					// 闹铃间隔， 这里设为1分钟闹一次，在第2步我们将每隔1分钟收到一次广播
					am.setRepeating(AlarmManager.RTC_WAKEUP, d.getTime(),
							1000 * 60 * 60 * 24, sender);
				} else if ("每周".equals(tip_tp.getSelectedItem().toString())) {
					AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent("com.renjie.money");
					intent.putExtra("tipContent", _tipContent);
					intent.putExtra("time", time);
					intent.putExtra("date", date);
					intent.putExtra("tip_tp", _tip_tp+"");
					
					SimpleDateFormat formatter2 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					Date d = formatter2.parse(date + " " + time); 

					PendingIntent sender = PendingIntent.getBroadcast(
							MorePage.this, 0, intent,
							PendingIntent.FLAG_CANCEL_CURRENT);
					// 闹铃间隔， 这里设为1分钟闹一次，在第2步我们将每隔1分钟收到一次广播
					am.setRepeating(AlarmManager.RTC_WAKEUP, d.getTime(), 1000
							* 60 * 60 * 24 * 7, sender);
				} else if ("每月".equals(tip_tp.getSelectedItem().toString())) {
					AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent("com.renjie.money");
					intent.putExtra("time", time);
					intent.putExtra("date", date);
					intent.putExtra("tip_tp", _tip_tp+"");
					intent.putExtra("tipContent", _tipContent);
					SimpleDateFormat formatter2 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					Date d = formatter2.parse(date + " " + time); 

					PendingIntent sender = PendingIntent.getBroadcast(
							MorePage.this, 0, intent,
							PendingIntent.FLAG_CANCEL_CURRENT);
					// 闹铃间隔， 这里设为1分钟闹一次，在第2步我们将每隔1分钟收到一次广播
					am.setRepeating(AlarmManager.RTC_WAKEUP, d.getTime(), 1000
							* 60 * 60 * 24 * 30, sender);
				} else if ("自定义".equals(tip_tp.getSelectedItem().toString())) {
					 
				}
				DbUtils db = DbUtils.create(MorePage.this);
				db.configAllowTransaction(true);
				Reminder r = new Reminder();
				r.setFlg(Reminder.VALID);
				r.setHour(_h);
				r.setMinute(_mi);
				r.setMonth(_m);
				r.setContent(_tipContent);
				r.setYear(_y);
				r.setDay(_d);
				r.setReminderType(_tip_tp);
				db.save(r);

				List<Reminder> list = db.findAll(Selector.from(Reminder.class)
						.orderBy("id").limit(10));
				if (list.size() > 0) {
					System.out.println(list);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showMess(R.string.save_success);

			initFirstPage();
		}
	}
}
