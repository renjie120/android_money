package com.renjie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.renjie.tool.MoneyDAO;
import com.renjie.tool.Tool;

/**
 * 保存理财信息的主界面.
 * 
 * @author lsq
 * 
 */
public class SaveMoney extends BaseActivity {
	private EditText getMoneyText;
	private EditText getMoneyDescText;
	private Spinner sortSpinner;
	private Button dateBtn;
	private Button saveBtn;
	private int myyear;
	private int mymonth;
	private int myday;
	Intent intent;
	static final int DATE_DIALOG_ID = 0;
	private MoneyDAO myDb;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.money_index);
		// 实例化数据库
		myDb = new MoneyDAO(this, MoneyDAO.VERSION);
		intent = this.getIntent();
		getMoneyText = (EditText) findViewById(R.id.moneyText);
		getMoneyDescText = (EditText) findViewById(R.id.moneyDescText);
		// 下面是点击选择时间的按钮事件
		dateBtn = (Button) findViewById(R.id.chooseTime_btn2);
		sortSpinner = (Spinner) findViewById(R.id.spinner_sort);
		saveBtn = (Button) findViewById(R.id.savebtn);
		// backBtn = (Button) findViewById(R.id.back);
		// 调用绑定事件的私有方法。
		prepareListener();
		cancelKeyBoard();
	}

	public void cancelKeyBoard() {
		Log.e("SaveMoney", "cancelKeyBoard");
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private final int TOOLBAR_ITEM_SEE_ALL = 0;// 删除全部
	private final int TOOLBAR_ITEM_DELTELE_ALL = 1;// 删除全部
	private final int TOOLBAR_ITEM_SAVE_TO_SERVER = 2;// 保存到本地服务

	/** 底部菜单图片 **/
	int[] menu_toolbar_image_array = { R.drawable.nav_press_icon01,
			R.drawable.nav_press_icon01, R.drawable.nav_press_icon01 };
	/** 底部菜单文字 **/
	String[] menu_toolbar_name_array = { "查看全部", "删除全部", "同步" };

	OnItemClickListener toolBarListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			switch (arg2) {
			case TOOLBAR_ITEM_SEE_ALL:
				goToMoneyList();
				break;
			}
		}
	};

	private void goToMoneyList() {
		Intent openUrl = new Intent();
		openUrl.setClass(SaveMoney.this, MoneyList.class);
		startActivity(openUrl);
		SaveMoney.this.finish();
	}

	/**
	 * 设置按钮的单击事件
	 */
	protected void prepareListener() {
		/**
		 * 保存到本地sqlite数据库
		 */
		saveBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String money = getMoneyText.getText().toString();
				String time = dateBtn.getText().toString();
				String moneySort = sortSpinner.getSelectedItem().toString();
				String moneyDesc = getMoneyDescText.getText().toString();
				// 传入金额不得为空
				if (Tool.isEmpty(money)) {
					alert(getText(R.string.save_failure).toString());
					return;
				}
				// 收入
				if (Tool.isInType(moneySort))
					myDb.insertMoney(money, time, moneyDesc, moneySort, "0",
							"1");
				// 支出
				else
					myDb.insertMoney(money, time, moneyDesc, moneySort, "0",
							"0");
				showMess(R.string.save_success);
				// alert();
				getMoneyText.setText("");
				getMoneyDescText.setText("");
			}
		});

		/**
		 * 打开日期
		 */
		dateBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 弹出一个对话框,将会触发后面的onCreateDialog()
				showDialog(DATE_DIALOG_ID);
			}
		});

		// 下面是创建一个下拉菜单，使用了android.R.layout下面的两个常量，很奇怪的做法。
		// 数据是来源于配置文件中的数组！！
		createSimpleSipnner(sortSpinner, R.array.sorttype);

		// 展示当前日期
		dateBtn.setText(getToday());
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

	public String getToday() {
		final Calendar today = Calendar.getInstance();
		myyear = today.get(Calendar.YEAR);
		mymonth = today.get(Calendar.MONTH);
		myday = today.get(Calendar.DAY_OF_MONTH);
		return new StringBuilder().append(myyear).append("-")
				.append(mymonth + 1).append("-").append(myday).toString();
	}

	private void DisplayMoney(Cursor c) {
		Toast.makeText(
				this,
				"时间: " + c.getString(0) + "\n" + "金额: " + c.getString(1) + "\n"
						+ "类型: " + c.getString(2) + "\n" + "描述: "
						+ c.getString(3) + "\n" + "状态: " + c.getString(4),
				Toast.LENGTH_LONG).show();
	}

	protected void onPause() {
		super.onPause();
	}

	// 定义弹出来的日期选择框的单击事件
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
	 * 处理从下一个页面返回到本页面的情况！注意是重写的这个方法！
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			Bundle bundle = data.getExtras();
			double money = bundle.getDouble("money");
			dateBtn.setText(getToday());
			getMoneyText.setText(Double.toString(money));
			break;
		default:
			break;
		}
	}

	public static void main(String[] aa) {
		HttpPost post = new HttpPost(
				"http://client.gzife.edu.cn/ReaderLogin.aspx");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("DropDownList1", "?"));
		params.add(new BasicNameValuePair("TextBox1", "A200420003"));
		params.add(new BasicNameValuePair("TextBox2", "123456"));
		HttpResponse response;
		try {
			response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				// 得到服务器端返回的结果字符串.
				String result = EntityUtils.toString(response.getEntity());
			} else {
				System.out.println("出现错误，错误代码是:"
						+ response.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
