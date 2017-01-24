package com.raspberry.library.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {


    public final static String FORMAT_DATE = "yyyy-MM-dd";
    public final static String FORMAT_TIME = "hh:mm";
    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd hh:mm";
    public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日 hh:mm";
    private static SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_TIME, Locale.CHINA);
    private static final int YEAR = 365 * 24 * 60 * 60;// 年
    private static final int MONTH = 30 * 24 * 60 * 60;// 月
    private static final int DAY = 24 * 60 * 60;// 天
    private static final int HOUR = 60 * 60;// 小时
    private static final int MINUTE = 60;// 分钟

    /**
     * 自定义格式获取时间字符串
     *
     * @param format 格式
     * @return
     */
    public static String getDate(String format) {
        SimpleDateFormat formatBuilder = new SimpleDateFormat(format, Locale.CHINA);
        return formatBuilder.format(new Date());
    }

    /**
     * 获取hh:mm:ss格式时间字符串
     *
     * @return
     */
    public static String getDate() {
        return getDate("HH:mm:ss");
    }

    /**
     * 根据时间戳获取描述性时间，如3分钟前，1天前
     *
     * @param timestamp 时间戳 单位为毫秒
     * @return 时间字符串
     */
    public static String getDescriptionTimeFromTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
        String timeStr = null;
        if (timeGap > YEAR) {
            //   timeStr = timeGap / YEAR + "年前";
            timeStr = getFormatTimeFromTimestamp(timestamp, null);
        } else if (timeGap > MONTH) {
            timeStr = timeGap / MONTH + "个月前";
        } else if (timeGap > DAY) {// 1天以上
            timeStr = timeGap / DAY + "天前";
        } else if (timeGap > HOUR) {// 1小时-24小时
            timeStr = timeGap / HOUR + "小时前";
        } else if (timeGap > MINUTE) {// 1分钟-59分钟
            timeStr = timeGap / MINUTE + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    /**
     * 根据时间戳获取指定格式的时间，如2011-11-30 08:40
     *
     * @param timestamp 时间戳 单位为毫秒
     * @param format    指定格式 如果为null或空串则使用默认格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static String getFormatTimeFromTimestamp(long timestamp, String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int year = Integer.valueOf(sdf.format(new Date(timestamp)).substring(0, 4));
            System.out.println("currentYear: " + currentYear);
            System.out.println("year: " + year);
            if (currentYear == year) {// 如果为今年则不显示年份
                sdf.applyPattern(FORMAT_MONTH_DAY_TIME);
            } else {
                sdf.applyPattern(FORMAT_DATE_TIME);
            }
        } else {
            sdf.applyPattern(format);
        }
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    /**
     * 根据时间戳获取时间字符串，并根据指定的时间分割数partionSeconds来自动判断返回描述性时间还是指定格式的时间
     *
     * @param timestamp      时间戳 单位是毫秒
     * @param partionSeconds 时间分割线，当现在时间与指定的时间戳的秒数差大于这个分割线时则返回指定格式时间，否则返回描述性时间
     * @param format
     * @return
     */
    public static String getMixTimeFromTimestamp(long timestamp, long partionSeconds, String format) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
        if (timeGap <= partionSeconds) {
            return getDescriptionTimeFromTimestamp(timestamp);
        } else {
            return getFormatTimeFromTimestamp(timestamp, format);
        }
    }

    /**
     * 获取当前日期的指定格式的字符串
     *
     * @param format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static String getCurrentTime(String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(new Date());
    }

    /**
     * 将日期字符串以指定格式转换为Date
     * <p/>
     * //* @param time 日期字符串
     *
     * @param format 指定的日期格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static Date getTimeFromString(String timeStr, String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        try {
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 将Date以指定格式转换为日期时间字符串
     * <p/>
     * //* @param date 日期
     *
     * @param format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static String getStringFromTime(Date time, String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(time);
    }


    /**
     * 默认格式  yyyy-MM-dd
     * 通过日期String，返回Long毫秒
     * @param dateStr
     * @return
     */
    public static long String2Long(String dateStr){
        if (StringUtils.isEmpty(dateStr)){
            return 0;
        }
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_DATE);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }
    /**
     * 默认格式  yyyy-MM-dd
     * 通过日期String，返回Long毫秒
     * @param dateStr
     * @return
     */
    public static long String2Long(String dateStr,String formatStr){
        if (StringUtils.isEmpty(dateStr)){
            return 0;
        }
        SimpleDateFormat format = null;
        if (StringUtils.isEmpty(formatStr)){
            format = new SimpleDateFormat(FORMAT_DATE);
        }else {
            format = new SimpleDateFormat(formatStr);
        }
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 比较两个日期之间的大小
     *
     * @param d1
     * @param d2
     * @return 前者大于后者返回true 反之false
     */
    public static boolean compareDate(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        int result = c1.compareTo(c2);
        if (result >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        String timeStr = "2010-11-30 10:12:23";
        try {
            Date date = sdf.parse(timeStr);
            System.out.println(DateUtil.getDescriptionTimeFromTimestamp(date.getTime()));
            System.out.println(DateUtil.getDescriptionTimeFromTimestamp(new Date().getTime()));
            System.out.println(DateUtil.getFormatTimeFromTimestamp(date.getTime(), "yyyy年MM月dd日"));
            System.out.println(DateUtil.getFormatTimeFromTimestamp(date.getTime(), null));
            System.out.println(DateUtil.getFormatTimeFromTimestamp(new Date().getTime(), null));
            System.out.println(DateUtil.getMixTimeFromTimestamp(date.getTime(), 3 * 24 * 60 * 60, "yyyy年MM月dd日 hh:mm"));
            System.out.println(DateUtil.getMixTimeFromTimestamp(date.getTime(), 24 * 60 * 60, null));
            System.out.println(DateUtil.getMixTimeFromTimestamp(new Date().getTime(), 3 * 24 * 60 * 60, "yyyy年MM月dd日 " +
                    "" + "hh:mm"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * convert time str
     *
     * @param time
     * @return
     */
    public static String convertTime(int time) {

        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }


    //是不是今天
    public static boolean isToday(long timeMilliseconds) {
        Calendar calendar = Calendar.getInstance();
        int todayYear = calendar.get(Calendar.YEAR);
        int todayMonth = calendar.get(Calendar.MONTH);
        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(timeMilliseconds);
        return calendar.get(Calendar.YEAR) == todayYear &&
                calendar.get(Calendar.MONTH) == todayMonth &&
                calendar.get(Calendar.DAY_OF_MONTH) == todayDay;
    }

    public static int getMonth(long timeMilliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMilliseconds);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getDay(long timeMilliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMilliseconds);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String[] WEEK = {"星期日","星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 日期变量转成对应的星期字符串
     *
     * @param date
     * @return
     */
    public static String DateToWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > 7) {
            return null;
        }
        return WEEK[dayIndex - 1];
    }

    public static String getCNdate(int month){
        String[]cndates = {"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月",};
        switch (month){
            case 1:
                return cndates[0];
            case 2:
                return cndates[1];
            case 3:
                return cndates[2];
            case 4:
                return cndates[3];
            case 5:
                return cndates[4];
            case 6:
                return cndates[5];
            case 7:
                return cndates[6];
            case 8:
                return cndates[7];
            case 9:
                return cndates[8];
            case 10:
                return cndates[9];
            case 11:
                return cndates[10];
            default:
                return cndates[11];
        }
    }

    public static String getCurrentWeekDay() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return DateToWeek(date);
    }


    public static String getWeekDay(String time, String format) {
        Date date = getTimeFromString(time, format);
        return DateToWeek(date);
    }


    public static String getSpecifiedDayBefore(String specifiedDay, String format) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }

    public static String getSpecifiedDayAfter(String specifiedDay, String format) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);
        String dayAfter = new SimpleDateFormat(format).format(c.getTime());
        return dayAfter;
    }

    public static String getAgeByLong(long birthTime){
        int birthDay = Integer.parseInt(getFormatTimeFromTimestamp(birthTime,"yyyy"));
        int curDate = Integer.parseInt(DateUtil.getCurrentTime("yyyy"));
        Log.i("tag00","出生年月："+birthDay);
        int age = curDate - birthDay;
        String ageStr =age+"岁";
        if (age > 200 || age < 0){
            ageStr = "保密";
        }
        return ageStr;
    }

    public static String getAgeByDate(String date){
        int birthDay = Integer.parseInt(date.substring(0,4));
        int curDate = Integer.parseInt(DateUtil.getCurrentTime("yyyy"));
        int age = curDate - birthDay;
        String ageStr =age+"岁";
        if (age > 200 || age < 0){
            ageStr = "保密";
        }
        return ageStr;
    }
}
