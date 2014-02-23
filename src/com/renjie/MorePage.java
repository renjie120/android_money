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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.renjie.tool.MoneyDAO;

/**
 * 更多的九宫格布局.
 * 
 * @author Administrator
 * 
 */
public class MorePage extends BaseActivity implements Runnable {
	int[] allMages = { R.drawable.item_1, R.drawable.item_2, R.drawable.item_3,
			R.drawable.item_1, R.drawable.item_2, R.drawable.item_3 };
	int[] allitem = { R.string.more_item1, R.string.more_item2,
			R.string.more_item3, R.string.more_item4, R.string.more_item5,
			R.string.more_item6 };
	private MoneyDAO myDb;
	String remoteUrl = "http://REMOTEIP:8080/money/superconsole!importMoneyFromPhone.do";
	private final static int SUCCESS = 1;

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
					myDb.updateStatusAfterSave();
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
								myDb.deleteAll();
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
		mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				Tool.getToday() + "money");
		mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, money);
		startActivity(Intent.createChooser(mailIntent,
				getStr(R.string.sendmoney)));
	}

	private int myyear;
	private int mymonth;
	private int myday;
	private Button dateBtn;
	private Button saveBtn;
	Intent intent;
	static final int DATE_DIALOG_ID = 0;
	private TableLayout table;

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
		saveBtn = (Button) findViewById(R.id.savebtn);
		table = (TableLayout) findViewById(R.id.gongguo_table);
		addRow();
		dateBtn.setText(Tool.getToday());
		// 调用绑定事件的私有方法。
		prepareListener();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more);
		// 实例化数据库
		myDb = new MoneyDAO(this, 1);

		GridView gridview = (GridView) findViewById(R.id.GridView);
		ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 6; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", allMages[i % 6]);
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
				case 3:// 备份金额数据
					backup();
					break;
				case 4: // 功过记录
					gotoGongguo();
					break;
				case 5:// 日记本
					saveToServer();
					break;
				default:
					break;
				}
				// Toast用于向用户显示一些帮助/提示
			}
		});
		// 调用绑定事件的私有方法。
		prepareListener();
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
			return new DatePickerDialog(this, mDateSetListener, myyear,
					mymonth, myday);
		}
		return null;
	}

	public void goSaveMoney() {
		Intent openUrl = new Intent();
		openUrl.setClass(MorePage.this, SaveMoney.class);
		startActivity(openUrl);
		MorePage.this.finish();
	}

	// 调用绑定事件的私有方法。
	protected void prepareListener() {
		if (dateBtn != null) {
			dateBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// 弹出一个对话框,将会触发后面的onCreateDialog()
					showDialog(DATE_DIALOG_ID);
				}
			});
		}
	}
}
