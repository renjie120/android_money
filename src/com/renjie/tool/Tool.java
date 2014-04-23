package com.renjie.tool;

public class Tool {
	public static final String CONFIG = "CONFIG_USER";
	public static final String USERNAME = "username";
	public static final String REMOTEIP = "remoteip";
	public static final String PORT = "port";
	public static final String PASS = "pass";
	public static final int DIARY_TYPE_COMMON = 0;
	public static final int DIARY_TYPE_LICAI = 1;
	public static final int DIARY_TYPE_PLAN = 2;
	public static final String[] DIARY_TYPES = { "普通", "理财", "计划" };
	public static final String[] inTypes = { "收入", "奖金", "工资", "报销差补" };

	/**
	 * 是否是收入的金额类型.
	 * @param str
	 * @return
	 */
	public static boolean isInType(String str) {
		for (int i = 0, j = Tool.inTypes.length; i < j; i++)
			if (str.equals(Tool.inTypes[i])) {
				return true;
			}
		return false;
	}
	
	/**
	 * 验证身份证号校验位的方法.
	 * 
	 * @param certiCode
	 * @return
	 */
	public static boolean checkIDParityBit(String certiCode) {
		boolean flag = false;
		if (certiCode == null || "".equals(certiCode))
			return false;
		int ai[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
		if (certiCode.length() == 18) {
			certiCode = certiCode.toUpperCase();
			int i = 0;
			for (int k = 0; k < 18; k++) {
				char c = certiCode.charAt(k);
				int j;
				if (c == 'X')
					j = 10;
				else if (c <= '9' || c >= '0')
					j = c - 48;
				else
					return flag;
				i += j * ai[k];
			}

			if (i % 11 == 1)
				flag = true;
		}
		return flag;
	}

	/**
	 * 得到身份证里面的生日信息.
	 * 
	 * @param certiCode
	 * @return
	 */
	public static String getBirthday(String certiCode) {
		return certiCode.substring(6, 13);
	}

	/**
	 * 得到身份证里面的省份信息.
	 * 
	 * @param certiCode
	 * @return
	 */
	public static String getPlace(String certiCode) {
		return certiCode.substring(0, 6);
	}

	/**
	 * 得到性别.
	 * 
	 * @param certiCode
	 * @return
	 */
	public static boolean getIsMan(String certiCode) {
		int num = Integer.parseInt(certiCode.substring(17));
		// 单数是男性.
		return num % 2 != 0;
	}

	/**
	 * 判断字符串是不是空串.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return null == str || "".equals(str);
	}
}
