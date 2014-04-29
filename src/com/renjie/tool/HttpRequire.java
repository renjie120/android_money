package com.renjie.tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.renjie.MorePage;

public class HttpRequire {

	public static JSONArray getReport(SharedPreferences set) {
		String remoteIp = set.getString(Tool.REMOTEIP, "192.168.1.101");
		String port = set.getString(Tool.PORT, "9999");
		try {

			String str = request(MorePage.REMOTEREPORT_OUT.replace("REMOTEIP",
					remoteIp).replace("PORT", port));
			return JSON.parseArray(str);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static JSONArray getReportByYear(SharedPreferences set, String year) {
		String remoteIp = set.getString(Tool.REMOTEIP, "192.168.1.101");
		String port = set.getString(Tool.PORT, "9999");
		try {
			String str = request(MorePage.REMOTEREPORT_BY_YEAR
					.replace("REMOTEIP", remoteIp).replace("PORT", port)
					.replace("YEAR", year));
			return JSON.parseArray(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static JSONArray getReportByMonth(SharedPreferences set,
			String year, String month) {
		String remoteIp = set.getString(Tool.REMOTEIP, "192.168.1.101");
		String port = set.getString(Tool.PORT, "9999");
		try {
			String str = request(MorePage.REMOTEREPORT_BY_MONTH
					.replace("REMOTEIP", remoteIp).replace("PORT", port)
					.replace("YEAR", year).replace("MONTH", month));
			return JSON.parseArray(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static JSONArray getReportByDay(SharedPreferences set, String day) {
		String remoteIp = set.getString(Tool.REMOTEIP, "192.168.1.101");
		String port = set.getString(Tool.PORT, "9999");
		try {
			String str = request(MorePage.REMOTEREPORT_BY_DAY
					.replace("REMOTEIP", remoteIp).replace("PORT", port)
					.replace("DAY", day));
			return JSON.parseArray(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static String request(String url) throws Exception {
		// 得到url请求. 

		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httpost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent(), "GBK"));
			return br.readLine();
		} catch (Exception e) {
			throw e;
		} finally {
			// 关闭连接.
			httpclient.getConnectionManager().shutdown();
		}
	}

}
