package com.renjie.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoneyDAO extends SQLiteOpenHelper { 
	public static final String MONEYID = "sno";
	public static final String MONEY = "money";
	public static final String MONEYTIME="time";
	public static final String MONEYDESC="desc";
	public static final String MONEYTYPE="type";
	public static final String MONEYSTATUS="status";
	private static final String TABLENAME="money_t";
	private static final String DB_CREATE_MONEY="create table money_t  (sno integer primary key autoincrement,money text,"
		+"time text,desc text,type text,status text);";

	public MoneyDAO(Context context,int dbVersion) {
		super(context,"moneydb",null,dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE_MONEY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists money_t");
		onCreate(db);
	} 
	
	/**
	 * 返回全部金额的数据列对象.
	 * @return
	 */
	public Cursor selectAll(){
		SQLiteDatabase db = this.getReadableDatabase();
		//tablename columns, selection, selectionArgs, groupBy, having, orderBy
		Cursor cursor  =db.query(TABLENAME, null, null, null, null, null, null);
		return cursor;
	}
	
	/**
	 * 保存到远程之后修改状态码.
	 */
	public void updateStatusAfterSave() {  
		SQLiteDatabase db = this.getReadableDatabase(); 
		db.execSQL("update money_t  set status =? where status=?",new Object[]{"1","0"});
	}
	
	/**
	 * 返回全部的金额组成的字符串.
	 * @return
	 */
	public String allMoney(){
		SQLiteDatabase db = this.getReadableDatabase(); 
		String col[] = { MONEYTIME,MONEY,MONEYTYPE,MONEYDESC,MONEYSTATUS };
		Cursor cur = db.query(TABLENAME, col, MONEYSTATUS+"='0'", null, null, null, null);  
		StringBuilder sb = new StringBuilder(); 
		if(cur.getCount()<1){
			return "";
		}
		cur.moveToFirst();
		do{
			sb.append(cur.getString(0)+"$,"+cur.getString(1)+"$,"+cur.getString(2)+"$,"+cur.getString(3)+"$,"+cur.getString(4)+"$;");
		}while(cur.moveToNext());
		sb = sb.delete(sb.length()-2, sb.length());
		return sb.toString(); 
	}
	
	/**
	 * 新增金额信息.
	 * @param money
	 * @param time
	 * @param desc
	 * @param type
	 * @param status
	 * @return
	 */
	public long insert(String money,String time,String desc,String type,String status){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues initValues = new ContentValues();
		initValues.put(MONEYDESC, desc);
		initValues.put(MONEYTIME, time);
		initValues.put(MONEY, money);
		initValues.put(MONEYSTATUS, status);
		initValues.put(MONEYTYPE, type);
		//表名，允许插入的空置，参数
		long rr =  db.insert(TABLENAME, MONEYID, initValues);
		db.close();
		return rr;
	}
	
	/**
	 * 删除金额信息
	 * @param id
	 */
	public void delete(long id){
		SQLiteDatabase db = this.getWritableDatabase();
		String where =  "sno = ?"; 
		String[] whereValue = {Long.toString(id)}; 
		db.delete(TABLENAME, where, whereValue); 
		db.close();
		//return myDB.delete(DB_TABLE, MONEYID+"="+moneySno,null)>0;
	} 
	
	/**
	 * 删除全部的金额信息.
	 */
	public void deleteAll(){
		SQLiteDatabase db = this.getWritableDatabase();  
		db.delete(TABLENAME, null, null);  
		db.close();
	} 
	
	public void update(long id, String money, String time, String desc,
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
		db.update(TABLENAME, initValues, where, whereValue);
		db.close();
	} 
}
