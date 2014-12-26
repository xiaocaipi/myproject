package org.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class CommonUtil {

	public static String futureDate(int interval) {

		Date date = new Date();
		date.setTime(date.getTime() + TimeUnit.DAYS.toMillis(interval));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	public static String futureDateByMonthDay(int month, int day) {

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, month);
		c.add(Calendar.MONTH, day);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(c.getTime());
	}
	
	
	public static String futureDateByMonthDay(String dateString , int month, int day) {
		
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		Date date;
		try {
			date = ft.parse(dateString);
			c.setTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		c.add(Calendar.DATE, day);
		c.add(Calendar.MONTH, month);

		
		return sdf.format(c.getTime());
	}

	/**
	 * 根据提供的日期返回日期是星期几
	 * 
	 * @param dateStr
	 * @return <ul>
	 *         <li>1</li> 星期日
	 *         <li>2</li> 星期一
	 *         <li>3</li> 星期二
	 *         <li>4</li> 星期三
	 *         <li>5</li> 星期四
	 *         <li>6</li> 星期五
	 *         <li>7</li> 星期六
	 *         </ul>
	 */
	public static int getWeekDay(String dateStr) {
		int day = 0;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
			day = calendar.get(Calendar.DAY_OF_WEEK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return day;
	}

	/**
	 * 根据提供的日期，判断这个日期的周日是否已经是过去的日期了
	 * 
	 * @param dateStr
	 * @return 过了 true 没过 false
	 */
	public static boolean weekendIsFuture(String dateStr) {

		boolean isFuture = false;
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			if (day == 0) {
				day = 7;
			}
			Date sunday = new Date();
			sunday.setTime(date.getTime() + TimeUnit.DAYS.toMillis(7 - day + 1));

			isFuture = new Date().getTime() > sunday.getTime();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// isFuture = false;
		return isFuture;
	}

	public static boolean dateIsMonthLastDay(String dateStr) {
		boolean ret = false;
		try {
			int day = 0;
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			day = calendar.get(Calendar.DAY_OF_MONTH);

			int lastDay = calendar.getActualMaximum(Calendar.DATE);

			Date lastDate = new Date();
			lastDate.setTime(date.getTime()
					+ TimeUnit.DAYS.toMillis(lastDay - day + 1));
			// 是最后一天，或者这个月已经过完
			ret = lastDay == day || new Date().getTime() > lastDate.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static boolean isNotEmpty(String s) {
		return StringUtils.isNotEmpty(s);
	}

	public static String convertNull(String s) {
		if (StringUtils.isNotEmpty(s)) {
			return s;
		} else {
			return "";
		}

	}

	public static Integer obj2IntegerForPageIndex(Object obj) {
		if (obj == null || obj.equals("") || "undefined".equals(obj)) {
			return 1;
		}
		return obj2Integer(obj);
	}

	public static Integer obj2Integer(Object obj) {
		if (obj == null || obj.equals("")) {
			return 0;
		}
		return new Integer(obj.toString());
	}

	public static double get2pointnum(Double f) {
		BigDecimal bg = new BigDecimal(f);
		double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}

	public static String changeSourceYj(String f) {
		String returncode = "";
		if ("direct".equals(f)) {
			returncode = "直接访问";
		} else if ("focus".equals(f)) {
			returncode = "焦点矩阵";
		} else if ("sohu".equals(f)) {
			returncode = "搜狐矩阵";
		} else if ("searchengine".equals(f)) {
			returncode = "搜索引擎";
		} else if ("paysearchengine".equals(f)) {
			returncode = "付费搜索引擎";
		} else if ("other".equals(f)) {
			returncode = "其他";
		} else if ("nav".equals(f)) {
			returncode = "导航页";
		} else if ("corporation".equals(f)) {
			returncode = "合作伙伴";
		}
		return returncode;
	}

	private static Map<String, String> channelMapping = new HashMap<String, String>();
	static {
		channelMapping.put("agent", "经纪人");
		channelMapping.put("main", "首页");
		channelMapping.put("map", "地图");
		channelMapping.put("news", "资讯");
		channelMapping.put("sale", "出售");
		channelMapping.put("view", "房源单页");
		channelMapping.put("vip", "焦点通");
		channelMapping.put("xiaoqu", "小区");
		channelMapping.put("-1", "其它");
	}

	public static String changeChannel(String channel) {

		String str = channelMapping.get(channel);
		return str == null ? channel : str;
	}

	private static Map<String, String> sourceMapping = new HashMap<String, String>();
	static {
		sourceMapping.put("sohucom", "搜狐首页房产模块");
		sourceMapping.put("sohunav", "搜狐首页导航");
		sourceMapping.put("baidu", "百度");
		sourceMapping.put("sosocom", "搜搜");
		sourceMapping.put("paysearchengine", "付费搜索引擎");
		sourceMapping.put("other", "其他");
		sourceMapping.put("nav", "导航页");
		sourceMapping.put("corporation", "合作伙伴");
		sourceMapping.put("minisohu", "微门户");
		sourceMapping.put("newssohucom", "搜狐新闻");
		sourceMapping.put("(direct)", "直接访问");
		sourceMapping.put("socom", "360搜索");
		sourceMapping.put("sogou", "搜狗");
		sourceMapping.put("bing", "必应");
		sourceMapping.put("youdaocom", "有道");
		sourceMapping.put("zufocus", "焦点租房");
		sourceMapping.put("homefocus", "焦点家居");
		sourceMapping.put("esffocus", "焦点二手房");
		sourceMapping.put("vipfocus", "焦点通");
		sourceMapping.put("housefocus", "焦点新房");
		sourceMapping.put("-1", "未知");
	}

	public static String changeSource(String f) {
		String str = sourceMapping.get(f);
		return str == null ? f : str;
	}

	public static String obj2string(Object f) {
		String returnString = "";
		if (f instanceof Long) {
			long tmp1 = (Long) f;
			returnString = String.valueOf(tmp1);
		} else if (f instanceof Integer) {
			int tmp1 = (Integer) f;
			returnString = String.valueOf(tmp1);
		} else if (f instanceof BigDecimal) {
			BigDecimal tmp1 = (BigDecimal) f;
			returnString = tmp1.toString();
		} else if (f instanceof BigInteger) {
			BigInteger tmp1 = (BigInteger) f;
			returnString = tmp1.toString();
		} else {
			returnString = (String) f;
		}
		if (StringUtils.isEmpty(returnString)) {
			returnString = "-1";
		}
		return returnString;
	}

	public static String float2ZB(float comZB) {
		BigDecimal bigDecimal = new BigDecimal(comZB).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		return bigDecimal.toString();
	}

	public static String getMonthLastDay(String dateStr, String format) {
		String lastDayStr = "";
		try {
			int day = 0;
			Date date = new SimpleDateFormat("format").parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			day = calendar.get(Calendar.DAY_OF_MONTH);

			int lastDay = calendar.getActualMaximum(Calendar.DATE);

			Date lastDate = new Date();
			lastDate.setTime(date.getTime()
					+ TimeUnit.DAYS.toMillis(lastDay - day + 1));

			lastDayStr = (new SimpleDateFormat(format)).format(lastDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lastDayStr;
	}

	public static long getDayBetween(String time2, String time1, String format) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat(format);
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return quot;
	}

	public static String getDay(String time, int interval, String format) {
		SimpleDateFormat ft = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = ft.parse(time);
			date.setTime(date.getTime() + TimeUnit.DAYS.toMillis(interval));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ft.format(date);
	}

	public static Map<String, String> sourceNews2Mapping = new HashMap<String, String>();

	static {
		sourceNews2Mapping.put("view", "房源单页");
		sourceNews2Mapping.put("daogouhome", "导购首页");
		sourceNews2Mapping.put("minisohu", "微门户");
		sourceNews2Mapping.put("sohu", "搜狐首页");
		sourceNews2Mapping.put("news", "导购单页");
		sourceNews2Mapping.put("sale", "二手房列表页");
		sourceNews2Mapping.put("other", "其他");
		sourceNews2Mapping.put("全部", "全部");
	}

	public static Map<String, String> typeNews2Mapping = new HashMap<String, String>();
	static {
		typeNews2Mapping.put("6", "板块置业");
		typeNews2Mapping.put("3", "小区榜单");
		typeNews2Mapping.put("4", "精选房源");
		typeNews2Mapping.put("全部", "全部");
	}

	public static String doubleTrans(Double d) {
		if(d == null){
			return "0";
		}
		if (Math.round(d) - d == 0) {
			return String.valueOf(Math.round(d));
		}
		return String.valueOf(d);
	}

	public static String getFirstDayOfWeek(String dateString , String format) {
		SimpleDateFormat ft = new SimpleDateFormat(format);
		Date date;
		Calendar c = new GregorianCalendar();
		try {
			date = ft.parse(dateString);
			c.setFirstDayOfWeek(Calendar.MONDAY);
			c.setTime(date);
			c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ft.format(c.getTime());
	}

	/**
	 * 取得指定日期所在周的最后一天
	 */
	public static String getLastDayOfWeek(String dateString , String format) {
		SimpleDateFormat ft = new SimpleDateFormat(format);
		Date date;
		Calendar c = new GregorianCalendar();
		try {
			date = ft.parse(dateString);
			c.setFirstDayOfWeek(Calendar.MONDAY);
			c.setTime(date);
			c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ft.format(c.getTime());
	}
	
	
	public static String getLastDayOfMonth(String dateString , String format) {
		SimpleDateFormat ft = new SimpleDateFormat(format);
		Date date;
		Calendar cal = Calendar.getInstance();
		try {
			date = ft.parse(dateString);
	        cal.setTime(date);
	        int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	        cal.set(Calendar.DAY_OF_MONTH, value);
			date = ft.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return ft.format(cal.getTime());
    }
	
	public static String getFirstDayOfMonth(String dateString , String format) {
		SimpleDateFormat ft = new SimpleDateFormat(format);
		Date date;
		Calendar cal = Calendar.getInstance();
		try {
			date = ft.parse(dateString);
	        cal.setTime(date);
	        int value = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
	        cal.set(Calendar.DAY_OF_MONTH, value);
			date = ft.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return ft.format(cal.getTime());
    }
	
	public static String regex(String patternString,String content){
		Pattern pattern  = Pattern.compile(patternString); 
		Matcher matcher = pattern.matcher(content);
		String returnString="";
		if(matcher.matches()){
			returnString =matcher.group();
		}
		return returnString;
		
	}

	public static void main(String[] args) {
		String aa ="http://data.eastmoney.com/stock/lhb,2014-12-02,000562.html";
		System.out.println(regex("http://data.eastmoney.com/stock/lhb,[0-9]{4}-[0-9]{2}-[0-9]{2},[0-9]*.html", aa));
//		System.out.println(regex("<a class="(.+?)" href="/stock/lhb,[0-9]{4}-[0-9]{2}-[0-9]{2},[0-9]*.html">交易明细</a>", aa));
	}
}
