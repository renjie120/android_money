package com.renjie;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author lsq
 * 
 */
public abstract class BaseActivity extends Activity {
	abstract void prepareListener();

	protected SimpleAdapter getMenu(String[] menuNameArray,
			int[] imageResourceArray, int layout) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data, layout,
				new String[] { "itemImage", "itemText" }, new int[] {
						R.id.item_image, R.id.item_text });
		return simperAdapter;
	}

	public String getStr(int id) {
		return getText(id).toString();
	}

	// 创建弹出的多选菜单.
	class MultiSelect implements OnClickListener {
		private ListView ggtitle_b10_ListView;
		private String[] arr;
		private Button btn;
		private Context c;

		public MultiSelect(String[] arr, Button btn, Context context) {
			this.arr = arr;
			this.btn = btn;
			this.c = context;
		}

		public void onClick(View v) {
			AlertDialog ad = new AlertDialog.Builder(c)
					.setTitle("选择区域")
					// 多选弹出框
					.setMultiChoiceItems(arr, null,
							new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton, boolean isChecked) {
									// 点击某个区域
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String result = "";
									// 得到多选框选择了的内容
									for (int i = 0; i < arr.length; i++) {
										if (ggtitle_b10_ListView
												.getCheckedItemPositions().get(
														i)) {
											result += ggtitle_b10_ListView
													.getAdapter().getItem(i)
													+ ",";
										} else {
											ggtitle_b10_ListView
													.getCheckedItemPositions()
													.get(i, false);
										}
									}
									// 删除最后的逗号
									if (result.length() > 0) {
										result = result.substring(0,
												result.lastIndexOf(","));
									}
									// 得到多选框选择的数目是否超过0个
									if (ggtitle_b10_ListView
											.getCheckedItemPositions().size() > 0) {
										// Toast.makeText(SaveGongguo.this,
										// result, Toast.LENGTH_LONG).show();
										btn.setText(result);
									} else {
										btn.setText("多选");
									}
									dialog.dismiss();
								}
							}).setNegativeButton("取消", null).create();
			ggtitle_b10_ListView = ad.getListView();
			ad.show();
		}
	}

	/**
	 * 弹出提示信息.
	 * 
	 * @param str
	 */
	public void showMess(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹出提示信息.
	 * 
	 * @param strId
	 */
	public void showMess(int strId) {
		Toast.makeText(getApplicationContext(), getText(strId).toString(),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹出确定提示框. 
	 * @param listener
	 */
	public void confirm(DialogInterface.OnClickListener listener,int msg) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_name)
				.setMessage(msg)
				.setNegativeButton(R.string.con_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				// 如果是确定退出就退出程序！
				.setPositiveButton(R.string.con_ok, listener).show();
	}

	// 点击关于程序的按钮
	public void openOptionDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_name)
				.setMessage(R.string.con_aboutStr)
				.setPositiveButton(R.string.con_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
	}

	/**
	 * 创建一个下拉菜单.
	 * 
	 * @param spinner
	 *            下拉菜单
	 * @param arrayId
	 *            组成菜单的数组
	 */
	public void createSimpleSipnner(Spinner spinner, int arrayId) {
		createSimpleSipnner(spinner, arrayId, 0);
	}

	/**
	 * 创建下拉菜单,并默认选择指定位置的项目
	 * 
	 * @param spinner
	 * @param arrayId
	 * @param selection
	 */
	public void createSimpleSipnner(Spinner spinner, int arrayId, int selection) {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, arrayId, android.R.layout.simple_spinner_item);
		// 下面是设置下拉菜单的显示样式
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(selection);
	}

	// 弹出提示框,有一个确定按钮.
	public void alert(String message) {
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.con_info_msg))
				.setMessage(message)
				.setPositiveButton(R.string.con_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
	}

	// 点击退出系统按钮
	public void leaveApp() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_name)
				.setMessage(R.string.con_quitStr)
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
								// 退出系统程序！
								finish();
							}
						}).show();
	}
}
