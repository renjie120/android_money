package com.renjie;

import java.util.ArrayList;
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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.renjie.tool.MoneyDAO;

/**
 * 更多的九宫格布局.
 * 
 * @author Administrator
 * 
 */
public class MorePage extends BaseActivity implements Runnable {
	int[] allMages = { R.drawable.item_1, R.drawable.item_2, R.drawable.item_3 };
	int[] allitem = { R.string.more_item1, R.string.more_item2,
			R.string.more_item3, R.string.more_item4, R.string.more_item5 };
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more);
		// 实例化数据库
		myDb = new MoneyDAO(this, 1);

		GridView gridview = (GridView) findViewById(R.id.GridView);
		ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 3; i++) {
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
					// String moneys = myDb.allMoney();
					// alert(moneys);
					// HttpPost post = new HttpPost(
					// "http://192.168.1.101:8080/money/superconsole!importMoneyFromPhone.do");
					// List<NameValuePair> params = new
					// ArrayList<NameValuePair>();
					// params.add(new BasicNameValuePair("moneyStr", moneys));
					// HttpResponse response;
					// try {
					// post.setEntity(new UrlEncodedFormEntity(params,
					// HTTP.UTF_8));
					// response = new DefaultHttpClient().execute(post);
					// if (response.getStatusLine().getStatusCode() == 200) {
					// // 得到服务器端返回的结果字符串.
					// String result = EntityUtils.toString(response
					// .getEntity());
					// } else {
					// System.out.println("出现错误，错误代码是:"
					// + response.getStatusLine().getStatusCode());
					// }
					// } catch (ClientProtocolException e) {
					// e.printStackTrace();
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
					// Intent openUrl = new Intent();
					// openUrl.setClass(MorePage.this, SaveConfig.class);
					// startActivity(openUrl);
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

	public void goSaveMoney() {
		Intent openUrl = new Intent();
		openUrl.setClass(MorePage.this, SaveMoney.class);
		startActivity(openUrl);
		MorePage.this.finish();
	}

	// 调用绑定事件的私有方法。
	protected void prepareListener() {

	}
}
