package com.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;


public class ModelUtils {

	// 字符串转换
	public static String listToString(String[] list, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.length; i++) {
			sb.append(list[i]);
			if (i < list.length - 1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	// 字符串转换
	public static String listToString(List list, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i < list.size() - 1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	// 转成map
	public static Map<String, Object> ConvertMap(String result) {
		if (result != null && !result.isEmpty()) {
			Map<String, Object> map = JSON.parseObject(result, Map.class);
			return map;
		}
		return null;
	}

	// 获取当前时间
	public static Date getCurrTime() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}

	// 获取当前时间转成double
	public static double getCurrTimeDouble() {
		Calendar cal = Calendar.getInstance();
		return (double) cal.getTimeInMillis();
	}

	// 获取时间转成double
	public static long getDateTimeDouble(String time) {
		Calendar cal = Calendar.getInstance();
		Date date = getDate(time);
		cal.setTime(date);
		return cal.getTimeInMillis();
	}

	// 获取时间转成double
	public static long getDateTimeDouble(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getTimeInMillis();
	}

	// 获取当前时间
	public static String getCurrTimeString() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat fmt = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		return fmt.format(cal.getTime());
	}

	// 把时间转换成
	public static String getDateToString(Date date) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		return fmt.format(date);
	}

	// 时间格式化
	public static String getDateToString(Date date, String fromart) {
		SimpleDateFormat fmt = new SimpleDateFormat(fromart);
		return fmt.format(date);
	}

	// 获取周几
	public static int getDateWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	// 转成int
	public static Integer getInteger(Object value) {
		try {
			if (value == null)
				return 0;
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return 0;
			return Integer.valueOf(strVal);
		} catch (Exception ex) {
			return 0;
		}
	}

	// 转成String
	public static String getString(Object value) {
		try {
			if (value == null)
				return "";
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return "";
			return strVal;
		} catch (Exception ex) {
			return "";
		}
	}

	// 获取时间
	public static Date getDate(Object value) {
		try {
			if (value == null)
				return null;
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return null;
			DateFormat fmt = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
			return fmt.parse(strVal);
		} catch (Exception ex) {
			return null;
		}
	}

	// 转成Long
	public static Long getLong(Object value) {
		try {
			if (value == null)
				return 0l;
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return 0l;
			return Long.valueOf(strVal);
		} catch (Exception ex) {
			return 0l;
		}
	}

	// 转成Decmail
	public static BigDecimal getDecimal(Object value) {
		try {
			if (value == null)
				return new BigDecimal(0);
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return new BigDecimal(0);
			return new BigDecimal(strVal);
		} catch (Exception ex) {
			return new BigDecimal(0);
		}

	}

	// 转成getBoolean
	public static Boolean getBoolean(Object value) {
		try {
			if (value == null)
				return false;
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return false;
			return Boolean.parseBoolean(strVal);
		} catch (Exception ex) {
			return false;
		}

	}

	// Decmaill转成float
	public static Float getDecimalToFloat(Object value) {
		try {
			if (value == null)
				return 0f;
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return 0f;
			BigDecimal decimal = new BigDecimal(strVal);
			return decimal.floatValue();
		} catch (Exception ex) {
			return 0f;
		}
	}

	// getShort
	public static Short getShort(Object value) {
		try {
			if (value == null)
				return 0;
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return 0;

			return Short.parseShort(strVal);
		} catch (Exception ex) {
			return 0;
		}
	}

	// getDouble
	public static Double getDouble(Object value) {
		try {
			if (value == null)
				return 0d;
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return 0d;

			return Double.parseDouble(strVal);
		} catch (Exception ex) {
			return 0d;
		}
	}

	// getFloat
	public static Float getFloat(Object value) {
		try {
			if (value == null)
				return 0f;
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return 0f;

			return Float.parseFloat(strVal);
		} catch (Exception ex) {
			return 0f;
		}
	}

	// getBigInteger
	public static BigInteger getBigInteger(Object value) {
		try {
			if (value == null)
				return BigInteger.valueOf(0);
			String strVal = value.toString();
			if (strVal == null || strVal.isEmpty())
				return BigInteger.valueOf(0);
			return BigInteger.valueOf(Long.valueOf(strVal));
		} catch (Exception ex) {
			return BigInteger.valueOf(0);
		}
	}

	// 循环字段值，补充数据
	public static <T> T Reflect(T e) {
		if (e == null)
			return null;
		try {

			Class cls = e.getClass();
			Field[] fields = cls.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				f.setAccessible(true);
				if (f.get(e) == null) {
					if (f.getType() == String.class) {
						f.set(e, "");
					} else if (f.getType() == Date.class) {
						f.set(e, new Date());
					} else if (f.getType() == long.class || f.getType() == Long.class) {
						f.set(e, 0l);
					} else if (f.getType() == float.class || f.getType() == Float.class) {
						f.set(e, 0f);
					} else if (f.getType() == byte.class || f.getType() == Byte.class) {
						f.set(e, (byte) 0);
					} else if (f.getType() == short.class || f.getType() == short.class) {
						f.set(e, (short) 0);
					} else if (f.getType() == BigDecimal.class || f.getType() == BigDecimal.class) {
						f.set(e, new BigDecimal(0));
					} else if (f.getType() == long[].class || f.getType() == Long[].class) {
						long[] array = { 0, 0, 0, 0 };
						f.set(e, array);
					} else if (f.getType() == float[].class || f.getType() == Float[].class) {
						float[] array = { 0, 0, 0, 0 };
						f.set(e, array);
					} else if (f.getType() == Date[].class) {
						Date[] array = { ModelUtils.getCurrTime(), ModelUtils.getCurrTime(), ModelUtils.getCurrTime(), ModelUtils.getCurrTime() };
						f.set(e, array);
					} else {
						f.set(e, 0);
					}

				}

			}
			return e;
		} catch (Exception ex) {

			return null;
		}
	}

	// 字符串map,转成对象
	public static Object mapToObject(Class clazz, String map) {
		if (map == null || map.isEmpty())
			return null;
		Map<String, Object> temp = ConvertMap(map);
		if (temp != null) {
			return mapToObject(clazz, temp);

		}
		return null;
	}

	/**
	 * 把Map<String,Object>处理成实体类
	 * 
	 * @param clazz
	 *            想要的实体类
	 * @param map
	 *            包含信息的Map对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object mapToObject(Class clazz, Map<String, Object> map) {

		if (null == map) {
			return null;
		}

		Field[] fields = clazz.getDeclaredFields(); // 取到类下所有的属性，也就是变量名
		Field field;
		Object o = null;
		try {
			o = clazz.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			String fieldName = field.getName();
			// 把属性的第一个字母处理成大写
			String stringLetter = fieldName.substring(0, 1).toUpperCase();
			// 取得set方法名，比如setBbzt
			String setterName = "set" + stringLetter + fieldName.substring(1);
			// 真正取得set方法。
			Method setMethod = null;
			Class fieldClass = field.getType();
			try {
				if (isHaveSuchMethod(clazz, setterName)) {
					if (fieldClass == String.class) {
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, getString(map.get(fieldName)));// 为其赋值
					} else if (fieldClass == Integer.class || fieldClass == int.class) {
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, getInteger(map.get(fieldName)));// 为其赋值
					} else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, getBoolean(map.get(fieldName)));// 为其赋值
					} else if (fieldClass == Short.class || fieldClass == short.class) {
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, getShort(map.get(fieldName)));// 为其赋值
					} else if (fieldClass == Long.class || fieldClass == long.class) {
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, getLong(map.get(fieldName)));// 为其赋值
					} else if (fieldClass == Double.class || fieldClass == double.class) {
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, getDouble(map.get(fieldName)));// 为其赋值
					} else if (fieldClass == Float.class || fieldClass == float.class) {
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, getFloat(map.get(fieldName)));// 为其赋值
					} else if (fieldClass == BigInteger.class) {
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, getBigInteger(map.get(fieldName)));// 为其赋值
					} else if (fieldClass == BigDecimal.class) {
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, getDecimal(map.get(fieldName)));// 为其赋值
					} else if (fieldClass == Date.class) {
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, getDate(map.get(fieldName)));// 为其赋值

					} else if (fieldClass == long[].class) {
						Object obj = map.get(fieldName);
						long[] array = { 0, 0, 0, 0 };
						if (obj != null) {
							List<Long> arraylist = JSON.parseArray(obj.toString(), Long.class);
							if (arraylist != null && arraylist.size() > 0) {
								for (int j = 0; j < array.length; j++) {
									array[j] = arraylist.get(j).longValue();
								}
							}
						}
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, array);// 为其赋值
					} else if (fieldClass == float[].class) {
						Object obj = map.get(fieldName);
						float[] array = { 0f, 0f, 0f, 0f };

						if (obj != null) {
							List<Float> arraylist = JSON.parseArray(obj.toString(), Float.class);
							if (arraylist != null && arraylist.size() > 0) {
								for (int j = 0; j < array.length; j++) {
									array[j] = arraylist.get(j).floatValue();
								}
							}
						}
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, array);// 为其赋值
					} else if (fieldClass == Date[].class) {
						Object obj = map.get(fieldName);
						Date[] array = { ModelUtils.getCurrTime(), ModelUtils.getCurrTime(), ModelUtils.getCurrTime(), ModelUtils.getCurrTime() };
						if (obj != null) {
							List<Date> arraylist = JSON.parseArray(obj.toString(), Date.class);
							if (arraylist != null && arraylist.size() > 0) {
								for (int j = 0; j < array.length; j++) {
									array[j] = arraylist.get(j);
								}
							}
						}
						setMethod = clazz.getMethod(setterName, fieldClass);
						setMethod.invoke(o, new Object[] { array });// 为其赋值
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}
		return o;
	}

	/**
	 * 判断某个类里是否有某个方法
	 * 
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	public static boolean isHaveSuchMethod(Class<?> clazz, String methodName) {
		Method[] methodArray = clazz.getMethods();
		boolean result = false;
		if (null != methodArray) {
			for (int i = 0; i < methodArray.length; i++) {
				if (methodArray[i].getName().equals(methodName)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	// 获取日期与当前日期的差值
	public static long getDateSubDAY(Date date) {
		if (date == null)
			return 0;
		long milliSecondSub = date.getTime() - System.currentTimeMillis();
		if (milliSecondSub < 0) {
			milliSecondSub = -milliSecondSub;
		}
		return milliSecondSub / 1000 / 60 / 60 / 24;
	}

	// 获取float指定位数
	public static float getFloatBit(double value, int bit) {
		BigDecimal b = new BigDecimal(value);
		return b.setScale(bit, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	// 格式化成两位小数，最后一位可以是0
	public static String getFloatBit(double value) {

		DecimalFormat df = new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(value);
	}

	// 转成实体对应的map
	public static Map<String, Object> getModelMap(Map<String, Object> map, Map<String, String> tempmap) {
		if (map != null) {
			// 转换数据
			Map<String, Object> temp = new HashMap<String, Object>();
			for (Map.Entry<String, String> entry : tempmap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (map.containsKey(value)) {
					temp.put(key, map.get(value));
				}

			}
			return temp;
		}
		return null;
	}

	// 字符串替换
	public static String repaceString(String template, Map<String, Object> parmes) {
		if (!isNullOrEmpty(template) || parmes == null)
			return "";
		for (Map.Entry<String, Object> entry : parmes.entrySet()) {
			template = template.replace(entry.getKey(), String.valueOf(entry.getValue()));
		}
		return template;
	}

	// 字符串替换
	public static String repaceString(String template, Map<String, Object> parmes, String splictString) {
		if (!isNullOrEmpty(template) || parmes == null)
			return "";
		for (Map.Entry<String, Object> entry : parmes.entrySet()) {
			String key = splictString + entry.getKey() + splictString;
			template = template.replace(key, String.valueOf(entry.getValue()));
		}
		return template;
	}

	// 获取今天星期几0周日，
	public static int getNowDay() {
		Date currDate = ModelUtils.getCurrTime();
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(currDate);
		return rightNow.get(Calendar.DAY_OF_WEEK) - 1;// 获取周几
	}

	// 添加或者减少日期
	public static Date getSubDateTime(int sub) {
		Date currDate = ModelUtils.getCurrTime();
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(currDate);
		rightNow.add(Calendar.DAY_OF_YEAR, sub);
		return rightNow.getTime();
	}

	// 比较两日期是否相等
	public static boolean compareToDate(Date begin, Date end) {
		if (begin.getYear() != end.getYear())
			return false;
		if (begin.getMonth() != end.getMonth())
			return false;
		if (begin.getDate() != end.getDate())
			return false;
		return true;
	}

	// 获取字符串中的数字
	public static BigDecimal getTextInDecimal(String value) {
		String REGEX = "[\\d|.|。]+";
		Pattern p = Pattern.compile(REGEX, Pattern.DOTALL); // 要想效率高点把它静态化
		Matcher m = p.matcher(value);
		BigDecimal total = new BigDecimal(0);
		while (m.find()) {
			String str = m.group();
			if (!isNullOrEmpty(str)) {
				str = str.replaceAll("。", ".");
				float fvalue = ModelUtils.getFloat(str);
				if (fvalue >= 0) {
					total=total.add(new BigDecimal(str));
				}
			}

		}
		return total;

	}

	// 判断字符串否是全数字
	public static boolean textIsNumber(String value) {
		String REGEX = "[0-9]+";
		Pattern p = Pattern.compile(REGEX, Pattern.DOTALL); // 要想效率高点把它静态化
		Matcher m = p.matcher(value);
		return m.matches();
	}

	// 字符串判空
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ss = "水费30块";
	 getTextInDecimal(ss);
	}
}
