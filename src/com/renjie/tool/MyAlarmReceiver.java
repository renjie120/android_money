package com.renjie.tool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.renjie.NewHomePage;
import com.renjie.R;
import com.renjie.ReminderInfo;

public class MyAlarmReceiver extends BroadcastReceiver {
	private static int COMMON = 1;

	/**
	 * 出现一个提示信息.
	 */
	private void showNotify(Context ct, String content, String date,
			String time, String tp) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager = (NotificationManager) ct
				.getSystemService(ns);
		Notification notification = new Notification(R.drawable.ic_launcher,
				ct.getString(R.string.app_name), System.currentTimeMillis());
		// 点击通知之后的操作，打开一个intent
		Intent intent = new Intent(ct, ReminderInfo.class);
		intent.putExtra("time", time);
		intent.putExtra("date", date);
		intent.putExtra("tip_tp", tp);
		intent.putExtra("tipContent", content);

		// notification.flags = Notification.FLAG_ONGOING_EVENT; // 设置常驻 Flag
		notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后自动消失
		notification.defaults = Notification.DEFAULT_SOUND;// 默认声音
		// 下面的1表示是自动进行的提醒要进行填报数据.。
		PendingIntent contextIntent = PendingIntent.getActivity(ct, COMMON,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(ct.getApplicationContext(),
				ct.getString(R.string.app_name), content, contextIntent);
		notificationManager.notify(R.string.app_name, notification);
	}

	@Override
	public void onReceive(Context context, Intent arg1) {
		showNotify(context, arg1.getStringExtra("tipContent"),
				arg1.getStringExtra("time"), arg1.getStringExtra("date"),
				arg1.getStringExtra("tip_tp"));
	}
}
