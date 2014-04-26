package com.renjie.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoneyDAO extends SQLiteOpenHelper {
	public static final int VERSION = 5;
	/********** 下面是金额操作的相关表和字段 ************/
	public static final String MONEYID = "sno";
	public static final String MONEY = "money";
	public static final String MONEYTIME = "time";
	public static final String MONEYDESC = "desc";
	public static final String MONEY_M = "month";
	public static final String MONEY_Y = "year";
	public static final String MONEY_D = "day";
	// 收入还是支出
	public static final String MONEY_TP = "tp";
	public static final String MONEYTYPE = "type";
	public static final String MONEYSTATUS = "status";
	private static final String MONEY_TABLENAME = "money_t";
	private static final String DB_CREATE_MONEY = "create table money_t  (sno integer primary key autoincrement,money text,"
			+ "time text,desc text,type text,status text,year text,month text,day text,tp text);";

	/********** 下面是功过相关的表和字段. *************/
	public static final String GONGGUO_SNO = "sno";
	public static final String GONGGUO_TIME = "time";
	public static final String GONGGUO_DESC = "desc";
	public static final String GONGGUO_ID = "type";
	public static final String GONGGUO_VALUE = "value";
	public static final String GONGGUO_STATUS = "status";
	private static final String GONGGUO_TABLENAME = "gongguo_t";

	private static final String DB_CREATE_GONGGUO = "create table gongguo_t  (sno integer primary key autoincrement,time text,"
			+ "desc text,type text,value text,status text);";

	/********** 下面是日记本相关的表和字段. *************/
	public static final String DIARY_SNO = "sno";
	public static final String DIARY_TIME = "time";
	public static final String DIARY_DATE = "date";
	public static final String DIARY_JIAMI = "jiami";
	public static final String DIARY_CONTENT = "content";
	public static final String DIARY_STATUS = "status";
	public static final String DIARY_TYPE = "tp";
	private static final String DIARY_TABLENAME = "diary_t";

	private static final String DB_CREATE_DIARY = "create table diary_t  (sno integer primary key autoincrement,time text,"
			+ "date text,jiami text,content text,status text,tp text);";

	public MoneyDAO(Context context, int dbVersion) {
		super(context, "moneydb", null, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE_MONEY);
		db.execSQL(DB_CREATE_GONGGUO);
		db.execSQL(DB_CREATE_DIARY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists money_t");
		db.execSQL("drop table if exists gongguo_t");
		db.execSQL("drop table if exists diary_t");
		onCreate(db);
	}

	/************************** 下面是对金额信息操作的方法 ***********************************************/
	/**
	 * 返回全部金额的数据列对象.
	 * 
	 * @return
	 */
	public Cursor selectAllMoney() {
		SQLiteDatabase db = this.getReadableDatabase();
		// tablename columns, selection, selectionArgs, groupBy, having, orderBy
		Cursor cursor = db.query(MONEY_TABLENAME, null, null, null, null, null,
				null);
		return cursor;
	}

	/**
	 * 按年份查询支出的金额
	 * 
	 * @return
	 */
	public Cursor selectAlloutMoneyByYear() {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] cols = { MONEY_Y, "sum(money)" };
		String where = " tp=? ";
		// tablename columns, selection, selectionArgs, groupBy, having, orderBy
		Cursor cursor = db.query(MONEY_TABLENAME, cols, where,
				new String[] { "0" }, MONEY_Y, null, " year desc");
		return cursor;
	}

	/**
	 * 得到一个年里面的全部月份的数据统计.
	 * 
	 * @param year
	 * @return
	 */
	public Cursor selectAlloutMoneyByMonth(String year) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] cols = { MONEY_M, "sum(money)" };
		String where = " tp=? and year=? ";
		// tablename columns, selection, selectionArgs, groupBy, having, orderBy
		Cursor cursor = db.query(MONEY_TABLENAME, cols, where, new String[] {
				"0", year }, MONEY_M, null, " month");
		return cursor;
	}

	/**
	 * 按月统计消费金额
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public Cursor selectAlloutMoneyByMonthAndDay(String year, String month) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] cols = { MONEY_D, "sum(money)" };
		String where = " tp=? and year=? and month=? ";
		// tablename columns, selection, selectionArgs, groupBy, having, orderBy
		Cursor cursor = db.query(MONEY_TABLENAME, cols, where, new String[] {
				"0", year, month }, MONEY_D, null, " day");
		return cursor;
	}

	/**
	 * 查询某一天的具体的数据.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public Cursor selectAlloutMoneyByMonthAndDayDetail(String year,
			String month, String day) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] cols = { MONEYID, MONEY, MONEYTIME, MONEYTYPE };
		String where = " tp=? and year=? and month=?  and day=?";
		// tablename columns, selection, selectionArgs, groupBy, having, orderBy
		Cursor cursor = db.query(MONEY_TABLENAME, cols, where, new String[] {
				"0", year, month, day }, null, null, MONEYID);
		return cursor;
	}

	/**
	 * 保存到远程之后修改状态码.
	 */
	public void updateMoneyStatusAfterSave() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("update money_t  set status =? where status=?",
				new Object[] { "1", "0" });
	}

	/**
	 * 返回全部的金额组成的字符串. 只返回没有同步的数据.
	 * 
	 * @return
	 */
	public String allMoney() {
		return allMoney(false);
	}

	public String allMoney(boolean isAll) {
		SQLiteDatabase db = this.getReadableDatabase();
		String col[] = { MONEYTIME, MONEY, MONEYTYPE, MONEYDESC, MONEYSTATUS };
		Cursor cur = db.query(MONEY_TABLENAME, col, isAll ? null : MONEYSTATUS
				+ "='0'", null, null, null, MONEYTIME);
		StringBuilder sb = new StringBuilder();
		if (cur.getCount() < 1) {
			return "";
		}
		cur.moveToFirst();
		do {
			sb.append(cur.getString(0) + "$," + cur.getString(1) + "$,"
					+ cur.getString(2) + "$," + cur.getString(3) + "$,"
					+ cur.getString(4) + "$;");
		} while (cur.moveToNext());
		sb = sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	/**
	 * 新增金额信息.
	 * 
	 * @param money
	 * @param time
	 * @param desc
	 * @param type
	 * @param status
	 * @return
	 */
	public long insertMoney(String money, String time, String desc,
			String type, String status, String tp) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues initValues = new ContentValues();
		initValues.put(MONEYDESC, desc);
		initValues.put(MONEYTIME, time);
		initValues.put(MONEY, money);
		initValues.put(MONEYSTATUS, status);
		String[] temp = time.split("-");
		initValues.put(MONEY_M, temp[1]);
		initValues.put(MONEY_Y, temp[0]);
		initValues.put(MONEY_D, temp[2]);
		initValues.put(MONEY_TP, tp);
		initValues.put(MONEYTYPE, type);
		// 表名，允许插入的空置，参数
		long rr = db.insert(MONEY_TABLENAME, MONEYID, initValues);
		db.close();
		return rr;
	}

	/**
	 * 删除金额信息
	 * 
	 * @param id
	 */
	public void deleteMoney(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "sno = ?";
		String[] whereValue = { Long.toString(id) };
		db.delete(MONEY_TABLENAME, where, whereValue);
		db.close();
		// return myDB.delete(DB_TABLE, MONEYID+"="+moneySno,null)>0;
	}

	/**
	 * 删除全部的金额信息.
	 */
	public void deleteAllMoney() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(MONEY_TABLENAME, null, null);
		db.close();
	}

	public void updateMoney(long id, String money, String time, String desc,
			String type, String status) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "sno = ?";
		String[] whereValue = { Long.toString(id) };
		ContentValues initValues = new ContentValues();
		initValues.put(MONEYDESC, desc);
		initValues.put(MONEYTIME, time);
		initValues.put(MONEY, money);
		initValues.put(MONEYSTATUS, status);
		initValues.put(MONEYTYPE, type);
		db.update(MONEY_TABLENAME, initValues, where, whereValue);
		db.close();
	}

	/************************** 下面是对功过信息操作的方法 ***********************************************/
	/**
	 * 返回全部功过的数据列对象.
	 * 
	 * @return
	 */
	public Cursor selectGonguoTimeAndStatus() {
		SQLiteDatabase db = this.getReadableDatabase();
		String col[] = { GONGGUO_SNO, GONGGUO_TIME, GONGGUO_STATUS };
		String where = " sno in (select min(sno) from gongguo_t group by time,status) ";
		Cursor cursor = db.query(GONGGUO_TABLENAME, col, where, null, null,
				null, null);
		return cursor;
	}

	/**
	 * 统计一个功过类型的是否数量.
	 * 
	 * @param desc
	 * @return
	 */
	public Cursor groupByValue(String desc) {
		SQLiteDatabase db = this.getReadableDatabase();
		String col[] = { GONGGUO_VALUE, "count(time) " };
		String table = " (select time,value from gongguo_t where desc= '"
				+ desc + "') ";
		Cursor cursor = db.query(table, col, null, null, "value", null, null);
		return cursor;
	}

	/**
	 * 计算一个功过类型的之前是的数量.
	 * 
	 * @param desc
	 * @return
	 */
	public int continueByValue(String time, String desc) {
		SQLiteDatabase db = this.getReadableDatabase();
		String col[] = { "max(time)" };
		Cursor cursor = db.query(GONGGUO_TABLENAME, col, " time < '" + time
				+ "' and value= 'false' and desc='" + desc + "'", null, null,
				null, null);
		String maxTime = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			maxTime = cursor.getString(0);
		}
		cursor.close();
		String col2[] = { "count(distinct time)" };
		Cursor cursor2 = db.query(GONGGUO_TABLENAME, col2, " time < '" + time
				+ "' and value= 'true' and desc='" + desc + "'"
				+ (maxTime == null ? "" : " and time >= '" + maxTime + "'"),
				null, null, null, null);
		int result = 0;
		if (cursor2.getCount() > 0) {
			cursor2.moveToFirst();
			result = cursor2.getInt(0);
		}
		cursor2.close();
		return result;
	}

	/**
	 * 保存到远程之后修改状态码.
	 */
	public void updateGonguoStatusAfterSave() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("update gongguo_t  set status =? where status=?",
				new Object[] { "1", "0" });
	}

	/**
	 * 返回全部的功过组成的字符串.
	 * 
	 * @return
	 */
	public String allGongguo() {
		return allGongguo(false);
	}

	public String allGongguo(boolean isAll) {
		SQLiteDatabase db = this.getReadableDatabase();
		String col[] = { GONGGUO_TIME, GONGGUO_DESC, GONGGUO_ID, GONGGUO_VALUE,
				GONGGUO_STATUS };
		Cursor cur = db.query(GONGGUO_TABLENAME, col, isAll ? null
				: GONGGUO_STATUS + "='0'", null, null, null, GONGGUO_TIME);
		StringBuilder sb = new StringBuilder();
		if (cur.getCount() < 1) {
			return "";
		}
		cur.moveToFirst();
		do {
			sb.append(cur.getString(0) + "$," + cur.getString(1) + "$,"
					+ cur.getString(2) + "$," + cur.getString(3) + "$,"
					+ cur.getString(4) + "$;");
		} while (cur.moveToNext());
		cur.close();
		sb = sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	/**
	 * 新增功过信息.
	 * 
	 * @param money
	 * @param time
	 * @param desc
	 * @param type
	 * @param status
	 * @return
	 */
	public long insertGonguo(String time, String desc, String type,
			String status, String value) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues initValues = new ContentValues();
		initValues.put(GONGGUO_DESC, desc);
		initValues.put(GONGGUO_TIME, time);
		initValues.put(GONGGUO_STATUS, status);
		initValues.put(GONGGUO_ID, type);
		initValues.put(GONGGUO_VALUE, value);
		// 表名，允许插入的空置，参数
		long rr = db.insert(GONGGUO_TABLENAME, GONGGUO_SNO, initValues);
		db.close();
		return rr;
	}

	/**
	 * 删除功过信息
	 * 
	 * @param id
	 */
	public void deleteGonguoByTime(String time) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "time = ?";
		String[] whereValue = { time };
		db.delete(GONGGUO_TABLENAME, where, whereValue);
		db.close();
	}

	/**
	 * 删除全部的功过信息.
	 */
	public void deleteAllGonguo() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(GONGGUO_TABLENAME, null, null);
		db.close();
	}

	public void updateGonguo(long id, String time, String desc, String type,
			String status, String value) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "sno = ?";
		String[] whereValue = { Long.toString(id) };
		ContentValues initValues = new ContentValues();
		initValues.put(GONGGUO_DESC, desc);
		initValues.put(GONGGUO_TIME, time);
		initValues.put(GONGGUO_STATUS, status);
		initValues.put(GONGGUO_ID, type);
		initValues.put(GONGGUO_VALUE, value);
		db.update(GONGGUO_TABLENAME, initValues, where, whereValue);
		db.close();
	}

	/************************** 下面是对日志信息操作的方法 ***********************************************/
	/**
	 * 返回全部功过的数据列对象.
	 * 
	 * @return
	 */
	public Cursor selectDiary() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(DIARY_TABLENAME, null, null, null, null, null,
				null);
		return cursor;
	}

	/**
	 * 保存到远程之后修改状态码.
	 */
	public void updateDiaryStatusAfterSave() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("update " + DIARY_TABLENAME
				+ "  set status =? where status=?", new Object[] { "1", "0" });
	}

	/**
	 * 返回全部的功过组成的字符串.
	 * 
	 * @return
	 */
	public String allDiary() {
		return allDiary();
	}

	public String allDiary(boolean isAll) {
		SQLiteDatabase db = this.getReadableDatabase();
		String col[] = { DIARY_SNO, DIARY_DATE, DIARY_TIME, DIARY_JIAMI,
				DIARY_CONTENT, DIARY_TYPE };
		Cursor cur = db.query(DIARY_TABLENAME, col, isAll ? null
				: GONGGUO_STATUS + "='0'", null, null, null, DIARY_TIME);
		StringBuilder sb = new StringBuilder();
		if (cur.getCount() < 1) {
			cur.close();
			return "";
		}
		cur.moveToFirst();
		do {
			sb.append(cur.getString(0) + "$," + cur.getString(1) + "$,"
					+ cur.getString(2) + "$," + cur.getString(3) + "$,"
					+ cur.getString(4) + "$," + cur.getString(5) + "$;");
		} while (cur.moveToNext());
		cur.close();
		sb = sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	/**
	 * 新增日志信息.
	 * 
	 * @param money
	 * @param time
	 * @param desc
	 * @param type
	 * @param status
	 * @return
	 */
	public long insertDiary(String date, String time, String content,
			String status, String jiami, String type) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues initValues = new ContentValues();
		initValues.put(DIARY_STATUS, status);
		initValues.put(DIARY_DATE, date);
		initValues.put(DIARY_TIME, time);
		initValues.put(DIARY_JIAMI, jiami);
		initValues.put(DIARY_CONTENT, content);
		initValues.put(DIARY_TYPE, type);
		// 表名，允许插入的空置，参数
		long rr = db.insert(DIARY_TABLENAME, DIARY_SNO, initValues);
		db.close();
		return rr;
	}

	/**
	 * 删除日志信息
	 * 
	 * @param id
	 */
	public void deleteDiary(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "sno = ?";
		String[] whereValue = { Long.toString(id) };
		db.delete(DIARY_TABLENAME, where, whereValue);
		db.close();
	}

	/**
	 * 删除全部的日志信息.
	 */
	public void deleteAllDiary() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DIARY_TABLENAME, null, null);
		db.close();
	}

	/**
	 * 更新日志信息.
	 * 
	 * @param id
	 * @param date
	 * @param time
	 * @param content
	 * @param status
	 * @param jiami
	 */
	public void updateDiary(long id, String date, String time, String content,
			String status, String jiami) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "sno = ?";
		String[] whereValue = { Long.toString(id) };
		ContentValues initValues = new ContentValues();
		initValues.put(DIARY_STATUS, status);
		initValues.put(DIARY_DATE, date);
		initValues.put(DIARY_TIME, time);
		initValues.put(DIARY_JIAMI, jiami);
		initValues.put(DIARY_CONTENT, content);
		db.update(DIARY_TABLENAME, initValues, where, whereValue);
		db.close();
	}
}
