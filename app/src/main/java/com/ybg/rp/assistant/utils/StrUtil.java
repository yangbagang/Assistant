package com.ybg.rp.assistant.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.util.LogUtils;

// TODO: Auto-generated Javadoc

/**
 * 名称：StrUtil.java 描述：字符串处理类.
 *
 */
public class StrUtil {

	/**
	 * 描述：将null转化为“”.
	 *
	 * @param str
	 *            指定的字符串
	 * @return 字符串的String类型
	 */
	public static String parseEmpty(String str) {
		if (str == null || "null".equals(str.trim()) || "".equals(str)) {
			str = "";
		}
		return str.trim();
	}

	/**
	 * 计算银行卡尾号
	 * 
	 * @author ybg
	 * @param str
	 * @param tail
	 * @return “” || xxxx尾号
	 */
	public static String bankTailNum(String str, int tail) {
		if (isEmpty(str) || str.length() < tail) {
			return "";
		}
		String bankCard = str.substring(str.length() - tail, str.length());
		return bankCard;
	}

	/**
	 * 描述：将null转化为0.
	 * 
	 * @param lo
	 * @return
	 * @author ybg
	 * @date 2014年9月16日-上午9:54:41
	 */
	public static Long parseEmpty(Long lo) {
		if (null == lo) {
			return Long.parseLong("0");
		}
		return lo;
	}

	/**
	 * 保留两位小数
	 *
	 * @author ybg
	 * @param dou
	 * @return
	 */
	public static String doubleFormatTow(Double dou) {
		if (null == dou) {
			return "0.00";
		}
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(dou);
	}

	/**
	 * 隐藏手机号，前3 后4
	 *
	 * @author ybg
	 * @param phone
	 * @return
	 */
	public static String phoneHide(String phone) {
		if (!isEmpty(phone)) {
			int length = phone.length();
			String start = phone.substring(0, 3);
			String end = phone.substring(length - 4, length);
			return start + "****" + end;
		}
		return "";
	}

	/**
	 * 隐藏身份证生日部分
	 *
	 */
	public static String userIDHide(String idNum){
		if (!isEmpty(idNum)){
			String hide=idNum.substring(6,14);
			String str = idNum.replace(hide,"********");
			return str;
		}
		return "";
	}

	/**
	 * 隐藏用户名 位数>2
	 * 只显示第一位和最后一位
	 * @author ybg
	 * @param userName
	 * @return
	 */
	public static String userNameEHide(String userName) {
		if (!isEmpty(userName)) {
			int length = userName.length();
			String start = userName.substring(0, 1);
			String end = userName.substring(length - 1, length);
			length -= 2;
			String str = "";
			for (int i = 0; i < length; i++) {
				str+="*";
			}
			return start + str + end;
		}
		return "";
	}

	/**
	 * 隐藏关键字
	 **/
	public static String nameHide(String name) {
		if (!isEmpty(name)) {
			int length = name.length();
			if (length > 2) {
				String start = name.substring(0, 1);
				String end = name.substring(length - 1, length);
				int a = start.length();
				int b = start.length();
				StringBuffer str = new StringBuffer();
				for (int i = 0; i < 2; i++) {
					str.append("*");
				}
				return start + str + end;
			} else {
				String start = name.substring(0, 1);
				// String end = name.substring(length - 1, length);
				return start + "**";
			}
		}
		return "";
	}

	/**
	 * 转化金额显示
	 *
	 * @author ybg
	 * @param dou
	 * @return
	 */
	public static String doubleFormat(Double dou) {
		DecimalFormat df1 = new DecimalFormat("#,###,###,###,##0.00");
		DecimalFormat df2 = new DecimalFormat("#,###,###,###,##0.00");
		DecimalFormat df3 = new DecimalFormat("############0");
		if (null == dou) {
			return "0.00";
		} else {
			String mon = df3.format(dou);
			if (mon.length() > 12) {
				dou = dou / (100000000) / 10000;
				return df2.format(dou) + "万亿";
			} else if (mon.length() > 8) {
				dou = dou / 100000000.00;
				return df2.format(dou) + "亿";
			} else if (mon.length() > 4) {
				dou = dou / 10000.00;
				return df2.format(dou) + "万";
			}
			return df1.format(dou);
		}
	}

	/**
	 * 转化金额显示
	 */
	public static String doubleFormat1(Double dou) {
		DecimalFormat df1 = new DecimalFormat("#,###,###,###,##0.00");
		DecimalFormat df2 = new DecimalFormat("#,###,###,###,##0.00");
		DecimalFormat df3 = new DecimalFormat("############0");
		if (null == dou) {
			return "0.00";
		} else {
			String mon = df3.format(dou);
			if (mon.length() > 15) {
				dou = dou / (100000000) / 10000;
				return df2.format(dou) + "万亿";
			} else if (mon.length() > 11) {
				dou = dou / 100000000.00;
				return df2.format(dou) + "亿";
			} else if (mon.length() > 8) {
				dou = dou / 10000.00;
				return df2.format(dou) + "万";
			}
			return df1.format(dou);
		}
	}

	/**
	 * 描述：判断一个字符串是否为null或空值.
	 *
	 * @param str
	 *            指定的字符串
	 * @return true or false
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0 || "null".equals(str.trim());
	}

	/**
	 * 获取字符串中文字符的长度（每个中文算2个字符）.
	 *
	 * @param str
	 *            指定的字符串
	 * @return 中文字符的长度
	 */
	public static int chineseLength(String str) {
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		/* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
		if (!isEmpty(str)) {
			for (int i = 0; i < str.length(); i++) {
				/* 获取一个字符 */
				String temp = str.substring(i, i + 1);
				/* 判断是否为中文字符 */
				if (temp.matches(chinese)) {
					valueLength += 2;
				}
			}
		}
		return valueLength;
	}

	/**
	 * 描述：获取字符串的长度.
	 *
	 * @param str
	 *            指定的字符串
	 * @return 字符串的长度（中文字符计2个）
	 */
	public static int strLength(String str) {
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		if (!isEmpty(str)) {
			// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
			for (int i = 0; i < str.length(); i++) {
				// 获取一个字符
				String temp = str.substring(i, i + 1);
				// 判断是否为中文字符
				if (temp.matches(chinese)) {
					// 中文字符长度为2
					valueLength += 2;
				} else {
					// 其他字符长度为1
					valueLength += 1;
				}
			}
		}
		return valueLength;
	}

	/**
	 * 描述：获取指定长度的字符所在位置.
	 *
	 * @param str
	 *            指定的字符串
	 * @param maxL
	 *            要取到的长度（字符长度，中文字符计2个）
	 * @return 字符的所在位置
	 */
	public static int subStringLength(String str, int maxL) {
		int currentIndex = 0;
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
		for (int i = 0; i < str.length(); i++) {
			// 获取一个字符
			String temp = str.substring(i, i + 1);
			// 判断是否为中文字符
			if (temp.matches(chinese)) {
				// 中文字符长度为2
				valueLength += 2;
			} else {
				// 其他字符长度为1
				valueLength += 1;
			}
			if (valueLength >= maxL) {
				currentIndex = i;
				break;
			}
		}
		return currentIndex;
	}

	/**
	 * 描述：手机号格式验证.
	 *
	 * @param str
	 *            指定的手机号码字符串
	 * @return 是否为手机号码格式:是为true，否则false
	 */
	public static Boolean isMobileNo(String str) {
		Boolean isMobileNo = false;
		try {
			// 18[0,5-9]改成18[^4,\\D]
			Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[^4,\\D]))\\d{8}$");
			Matcher m = p.matcher(str);
			isMobileNo = m.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isMobileNo;
	}
	/**
	 * 判断手机号是否正确
	 *
	 * @param str
	 * @return
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,6,7,8][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 判断电话号码是否正确
	 *
	 * @param str
	 * @return
	 */
	public static boolean isPhone(String str) {
		Pattern p1 = null, p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^[0][1-9][0-9]{1,3}[0-9]{3,10}$");  // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{3,8}$");         // 验证没有区号的
		if (str.length() > 4) {
			m = p1.matcher(str);
			b = m.matches();
		} else {
			m = p2.matcher(str);
			b = m.matches();
		}
		return b;
	}
	/**
	 * 描述：是否只是字母和数字.
	 *
	 * @param str
	 *            指定的字符串
	 * @return 是否只是字母和数字:是为true，否则false
	 */
	public static Boolean isNumberLetter(String str) {
		Boolean isNoLetter = false;
		String expr = "^[A-Za-z0-9]+$";
		if (str.matches(expr)) {
			isNoLetter = true;
		}
		return isNoLetter;
	}

	/**
	 * 描述：是否只是数字.
	 *
	 * @param str
	 *            指定的字符串
	 * @return 是否只是数字:是为true，否则false
	 */
	public static Boolean isNumber(String str) {
		Boolean isNumber = false;
		String expr = "^[0-9]+$";
		if (str.matches(expr)) {
			isNumber = true;
		}
		return isNumber;
	}

	/**
	 * 描述：是否是邮箱.
	 *
	 * @param str
	 *            指定的字符串
	 * @return 是否是邮箱:是为true，否则false
	 */
	public static Boolean isEmail(String str) {
		Boolean isEmail = false;
		String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		if (str.matches(expr)) {
			isEmail = true;
		}
		return isEmail;
	}

	/**
	 * 描述：是否是中文.
	 *
	 * @param str
	 *            指定的字符串
	 * @return 是否是中文:是为true，否则false
	 */
	public static Boolean isChinese(String str) {
		Boolean isChinese = true;
		String chinese = "[\u0391-\uFFE5]";
		if (!isEmpty(str)) {
			// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
			for (int i = 0; i < str.length(); i++) {
				// 获取一个字符
				String temp = str.substring(i, i + 1);
				// 判断是否为中文字符
				if (temp.matches(chinese)) {
				} else {
					isChinese = false;
				}
			}
		}
		return isChinese;
	}

	/**
	 * 描述：是否包含中文.
	 *
	 * @param str
	 *            指定的字符串
	 * @return 是否包含中文:是为true，否则false
	 */
	public static Boolean isContainChinese(String str) {
		Boolean isChinese = false;
		String chinese = "[\u0391-\uFFE5]";
		if (!isEmpty(str)) {
			// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
			for (int i = 0; i < str.length(); i++) {
				// 获取一个字符
				String temp = str.substring(i, i + 1);
				// 判断是否为中文字符
				if (temp.matches(chinese)) {
					isChinese = true;
				} else {

				}
			}
		}
		return isChinese;
	}

	/**
	 * 描述：从输入流中获得String.
	 *
	 * @param is
	 *            输入流
	 * @return 获得的String
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			// 最后一个\n删除
			if (sb.indexOf("\n") != -1 && sb.lastIndexOf("\n") == sb.length() - 1) {
				sb.delete(sb.lastIndexOf("\n"), sb.lastIndexOf("\n") + 1);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 描述：标准化日期时间类型的数据，不足两位的补0.
	 *
	 * @param dateTime
	 *            预格式的时间字符串，如:2012-3-2 12:2:20
	 * @return String 格式化好的时间字符串，如:2012-03-20 12:02:20
	 */
	public static String dateTimeFormat(String dateTime) {
		StringBuilder sb = new StringBuilder();
		try {
			if (isEmpty(dateTime)) {
				return null;
			}
			String[] dateAndTime = dateTime.split(" ");
			if (dateAndTime.length > 0) {
				for (String str : dateAndTime) {
					if (str.indexOf("-") != -1) {
						String[] date = str.split("-");
						for (int i = 0; i < date.length; i++) {
							String str1 = date[i];
							sb.append(strFormat2(str1));
							if (i < date.length - 1) {
								sb.append("-");
							}
						}
					} else if (str.indexOf(":") != -1) {
						sb.append(" ");
						String[] date = str.split(":");
						for (int i = 0; i < date.length; i++) {
							String str1 = date[i];
							sb.append(strFormat2(str1));
							if (i < date.length - 1) {
								sb.append(":");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}

	/**
	 * 描述：不足2个字符的在前面补“0”.
	 *
	 * @param str
	 *            指定的字符串
	 * @return 至少2个字符的字符串
	 */
	public static String strFormat2(String str) {
		try {
			if (str.length() <= 1) {
				str = "0" + str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 描述：截取字符串到指定字节长度.
	 *
	 * @param str
	 *            the str
	 * @param length
	 *            指定字节长度
	 * @return 截取后的字符串
	 */
	public static String cutString(String str, int length) {
		return cutString(str, length, "");
	}

	/**
	 * 描述：截取字符串到指定字节长度.
	 *
	 * @param str
	 *            文本
	 * @param length
	 *            字节长度
	 * @param dot
	 *            省略符号
	 * @return 截取后的字符串
	 */
	public static String cutString(String str, int length, String dot) {
		int strBLen = strlen(str, "GBK");
		if (strBLen <= length) {
			return str;
		}
		int temp = 0;
		StringBuffer sb = new StringBuffer(length);
		char[] ch = str.toCharArray();
		for (char c : ch) {
			sb.append(c);
			if (c > 256) {
				temp += 2;
			} else {
				temp += 1;
			}
			if (temp >= length) {
				if (dot != null) {
					sb.append(dot);
				}
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 描述：截取字符串从第一个指定字符.
	 *
	 * @param str1
	 *            原文本
	 * @param str2
	 *            指定字符
	 * @param offset
	 *            偏移的索引
	 * @return 截取后的字符串
	 */
	public static String cutStringFromChar(String str1, String str2, int offset) {
		if (isEmpty(str1)) {
			return "";
		}
		int start = str1.indexOf(str2);
		if (start != -1) {
			if (str1.length() > start + offset) {
				return str1.substring(start + offset);
			}
		}
		return "";
	}

	/**
	 * 描述：获取字节长度.
	 *
	 * @param str
	 *            文本
	 * @param charset
	 *            字符集（GBK）
	 * @return the int
	 */
	public static int strlen(String str, String charset) {
		if (str == null || str.length() == 0) {
			return 0;
		}
		int length = 0;
		try {
			length = str.getBytes(charset).length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return length;
	}

	/**
	 * 获取大小的描述.
	 *
	 * @param size
	 *            字节个数
	 * @return 大小的描述
	 */
	public static String getSizeDesc(long size) {
		String suffix = "B";
		if (size >= 1024) {
			suffix = "K";
			size = size >> 10;
			if (size >= 1024) {
				suffix = "M";
				// size /= 1024;
				size = size >> 10;
				if (size >= 1024) {
					suffix = "G";
					size = size >> 10;
					// size /= 1024;
				}
			}
		}
		return size + suffix;
	}

	/** string 输入20141010 输出2014-10-10 */
	public static String stringFormat(String str) {
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-HH-dd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyHHdd");
		try {
			str = formatter1.format(formatter2.parse(str));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 描述：ip地址转换为10进制数.
	 *
	 * @param ip
	 *            the ip
	 * @return the long
	 */
	public static long ip2int(String ip) {
		ip = ip.replace(".", ",");
		String[] items = ip.split(",");
		return Long.valueOf(items[0]) << 24 | Long.valueOf(items[1]) << 16 | Long.valueOf(items[2]) << 8
				| Long.valueOf(items[3]);
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		System.out.println(dateTimeFormat("2012-3-2 12:2:20"));
	}

	/**
	 * read file content
	 *
	 * @param context   the context
	 * @param assetPath the asset path
	 * @return String string
	 */
	public static String readText(Context context, String assetPath) {
		LogUtils.debug("read assets file as text: " + assetPath);
		try {
			return ConvertUtils.toString(context.getAssets().open(assetPath));
		} catch (Exception e) {
			LogUtils.error(e);
			return "";
		}
	}
	/**
	 * 字符串工具
	 * 剪切字符串
	 */
	public static List<String> stringToArray(String birthday) {
		String[] split = birthday.split("-");
		List<String> itemDataList = new ArrayList<String>();
		for (int i = 0; i < split.length; i++) {
			itemDataList.add(split[i]);
		}
		return itemDataList;
	}

}
