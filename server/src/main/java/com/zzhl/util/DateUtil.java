package com.zzhl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期操作类
 **/
public class  DateUtil {
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private final static int INVALID_VALUE = 0xffff;
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    private static SimpleDateFormat defaultDateFormatter;

    public DateUtil() {

    }

    public static Date getDateFromString(String dateStr) {
        return getDateFromString(dateStr, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取指定时间对应的毫秒数
     *
     * @param time "HH:mm:ss"
     * @return
     */
    public static long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static Date getDateFromString(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date resDate = null;
        try {
            resDate = sdf.parse(dateStr);
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
        }
        return resDate;
    }

    public static String getDateStringByLongValue(long dateInMilliSeconds) {
        Date date = new Date(dateInMilliSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(getDefaultTimeZone());
        return sdf.format(date);
    }

    public static String getDateStringByLongValueCommon(long dateInMilliSeconds) {
        Date date = new Date(dateInMilliSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(getDefaultTimeZone());
        return sdf.format(date);
    }

    public static String getDateStringByLongValueFormat(long dateInMilliSeconds, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(dateInMilliSeconds);
        sdf.setTimeZone(getDefaultTimeZone());
        return sdf.format(date);
    }

    /**
     * Get simple date formatter with default time zone of GMT+8
     *
     * @return simple date formatter
     */
    public static SimpleDateFormat getDefaultDateFormatter() {
        if (defaultDateFormatter == null) {
            defaultDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            defaultDateFormatter.setTimeZone(getDefaultTimeZone());
        }
        return defaultDateFormatter;
    }

    /**
     * Get default time zone of China
     *
     * @return default time zone
     */
    public static TimeZone getDefaultTimeZone() {
        return TimeZone.getTimeZone("GMT+8");
    }

    /**
     * Get calendar instance with default time zone of GMT+8
     *
     * @return Calendar instance
     */
    public static Calendar getCalendar() {
        TimeZone.setDefault(getDefaultTimeZone());
        return Calendar.getInstance();
    }

    /**
     * Get day difference between two dates
     *
     * @param d1:date1
     * @param d2:date2
     * @return day difference between d1 and d2, and difference will be negative if d1 > d2
     */
    public static long getDaysBetween(String d1, String d2) {
        Calendar cal_start;
        Calendar cal_end;

        try {
            Date date_start = getDefaultDateFormatter().parse(d1);
            Date date_end = getDefaultDateFormatter().parse(d2);
            cal_start = Calendar.getInstance();
            cal_end = Calendar.getInstance();
            cal_start.setTime(date_start);
            cal_end.setTime(date_end);
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return INVALID_VALUE;
        }
        return getDaysBetween(cal_start, cal_end);
    }

    /**
     * Get day difference between two dates
     *
     * @param date_start:date1
     * @param date_end:date2
     * @return day difference between d1 and d2, and difference will be negative if d1 > d2
     */
    public static long getDaysBetween(Date date_start, Date date_end) {
        Calendar cal_start;
        Calendar cal_end;

        try {
            cal_start = Calendar.getInstance();
            cal_end = Calendar.getInstance();
            cal_start.setTime(date_start);
            cal_end.setTime(date_end);
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return INVALID_VALUE;
        }
        return getDaysBetween(cal_start, cal_end);
    }

    /**
     * Get day difference between two dates
     *
     * @param d1:date1
     * @param d2:date2
     * @return day difference between d1 and d2, and difference will be negative if d1 > d2
     */
    public static long getDaysBetween(Calendar d1, Calendar d2) {
        try {
            long sec1 = d2.getTimeInMillis() - d1.getTimeInMillis();
            long days = sec1 / 1000 / 3600 / 24;
            return days;
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return INVALID_VALUE;
        }
    }

    /**
     * Get date as format of "yyyy-MM-dd"
     *
     * @param date: string of date
     * @return String of date with format of "yyyy-MM-dd"
     */
    public static String getFormatedDate(String date) {
        return getFormatedDate(date, YYYY_MM_DD_HH_MM_SS);
    }


    public static String getFormatedDate(Date date) {
        return getFormatedDate(date, YYYY_MM_DD_HH_MM_SS);
    }

    public static String getFormatedDateWithNoSecond(Date date) {
        return getFormatedDate(date, YYYY_MM_DD_HH_MM);
    }

    /**
     * Get date as given date format
     *
     * @param date:   string of date
     * @param format: given date format, such as "yyyy-MM-dd"
     * @return String of date with given format
     */
    public static String getFormatedDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date dateTmp = sdf.parse(date);
            return sdf.format(dateTmp);
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return null;
        }
    }

    public static String getFormatedDate(Date date, String format) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return null;
        }
    }

    public static Date str2Date(String date) {
        try {
            return defaultDateFormatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Get day difference between given date and today
     *
     * @param date: given date
     * @return day difference
     */
    public static long getDaysFromToday(Date date) {
        Calendar cal_start;
        try {
            cal_start = getCalendar();
            cal_start.setTime(date);

            return getDaysBetween(cal_start, getCalendar());
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return INVALID_VALUE;
        }
    }

    public static String increaseDate(String startDate, int days) {
        Calendar cal_start;
        try {
            Date date_start = getDefaultDateFormatter().parse(startDate);
            cal_start = getCalendar();
            cal_start.setTime(date_start);
            cal_start.add(Calendar.DAY_OF_YEAR, days);
            return getDefaultDateFormatter().format(cal_start.getTime());
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return null;
        }
    }

    /**
     * Get day difference between given date and today
     *
     * @param date: given date
     * @return day difference
     */
    public static long getDaysFromToday(String date) {
        Calendar cal_start;
        try {
            Date date_start = getDefaultDateFormatter().parse(date);
            cal_start = getCalendar();
            cal_start.setTime(date_start);

            return getDaysBetween(cal_start, getCalendar());
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return INVALID_VALUE;
        }
    }

    /**
     * Get date object of current
     *
     * @return
     */
    public static Date getNowDate() {
        return getCalendar().getTime();
    }


    /**
     * Get date string as format of "yyyy-MM-dd HH:mm:ss"
     *
     * @return
     */
    public static String getNow() {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        sdf.setTimeZone(getDefaultTimeZone());
        return sdf.format(getCalendar().getTime());
    }

    /**
     * Get date string as format of "yyyy-MM-dd HH:mm:ss"
     *
     * @return
     */
    public static String getNow(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(getDefaultTimeZone());
        return sdf.format(getCalendar().getTime());
    }

    /**
     * Get date string as format of "yyyy-MM-dd"
     *
     * @return
     */
    public static String getToday() {
        return getDefaultDateFormatter().format(getCalendar().getTime());
    }

    /**
     * Get date string of backward from today by given days, in format of "yyyy-MM-dd"
     *
     * @param dayBack: backward days from today
     * @return Date string in format of "yyyy-MM-dd"
     */
    public static Date getDateByDayBack(int dayBack) {
        Calendar cal = getCalendar();
        cal.add(Calendar.DAY_OF_YEAR, -dayBack);
        return cal.getTime();
    }

    /**
     * Get date string of backward from today by given days, in format of "yyyy-MM-dd"
     *
     * @param dayBack: backward days from today
     * @return Date string in format of "yyyy-MM-dd"
     */
    public static String getDateStringByDayBack(int dayBack) {
        String now;
        Calendar cal = getCalendar();
        cal.add(Calendar.DAY_OF_YEAR, -dayBack);
        now = getDefaultDateFormatter().format(cal.getTime());
        return now;
    }

    /**
     * Get date of next time for triggering task
     *
     * @param taskTriggerHour: hour of triggering task
     * @return Date of next trigger
     */
    public static Date getTaskTrigger(int taskTriggerHour, boolean tomorrow) {
        return getTaskTrigger(taskTriggerHour, 0, tomorrow);
    }

    /**
     * Check if currently is time to trigger task
     *
     * @param taskTriggerHour: hour of triggering task
     * @return Return true if time is up, else return false
     */
    public static boolean isTimeToTrigerTask(int taskTriggerHour) {
        return isTimeToTrigerTask(taskTriggerHour, 0);
    }

    /**
     * Check if currently is time to trigger task
     *
     * @param taskTriggerHour:   hour of triggering task
     * @param taskTriggerMinute: minutes of triggering task
     * @return Return true if time is up, else return false
     */
    public static boolean isTimeToTrigerTask(int taskTriggerHour, int taskTriggerMinute) {
        Calendar cal = getCalendar();
        int curHour = cal.get(Calendar.HOUR_OF_DAY);
        int curMinute = cal.get(Calendar.MINUTE);

        if ((curHour > taskTriggerHour) ||
                ((curHour == taskTriggerHour) && (curMinute > taskTriggerMinute))) {
            return true;
        }

        return false;
    }

    /**
     * Get date of next time for triggering task
     *
     * @param taskTriggerHour:   hour of triggering task
     * @param taskTriggerMinute: minutes of triggering task
     * @return Date of next trigger
     */
    public static Date getTaskTrigger(int taskTriggerHour, int taskTriggerMinute, boolean tomorrow) {
        // change to start from given time
        Calendar cal = getCalendar();
        cal.set(Calendar.HOUR_OF_DAY, taskTriggerHour);
        cal.set(Calendar.MINUTE, taskTriggerMinute);


        if (tomorrow) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        } else {
            /*
			int curHour = cal.get(Calendar.HOUR_OF_DAY);
			int curMinute = cal.get(Calendar.MINUTE);
			if ((curHour > taskTriggerHour) ||
				((curHour == taskTriggerHour) && (curMinute > taskTriggerMinute)))
			{
				cal.add(Calendar.MINUTE, 2);
			}
			*/
        }
        return cal.getTime();
    }


    /**
     * return 2013-06-01 by "[6-01]����BT�ϼ�"
     *
     * @param title: title in web page
     * @return Formatted date string
     */
    public static String getDateByTitle(String title) {
        String outDate = null;
        int start = title.indexOf('[');
        boolean flag = true;
        if (start < 0) {
            flag = false;
        }

        int end = title.indexOf(']');
        if (end < 0) {
            flag = false;
        }

        String dateString = title.substring(start + 1, end);
        int mid = dateString.indexOf('-');
        if (mid < 0) {
            flag = false;
        }

        if (!flag) {
            logger.warn("Invalid date format found: ", title);
            return null;
        }
        outDate = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        outDate += "-";
        outDate += dateString;

        return getFormatedDate(outDate);
    }

    public static String getIncreaseDate(Date startDate, int days) {
        Calendar cal_start;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            cal_start = getCalendar();
            cal_start.setTime(startDate);
            cal_start.add(Calendar.DAY_OF_YEAR, days);
            return dateFormat.format(cal_start.getTime());
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return null;
        }
    }

    public static String getIncreaseDate(Date startDate, int days, String format) {
        Calendar cal_start;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            cal_start = getCalendar();
            cal_start.setTime(startDate);
            cal_start.add(Calendar.DAY_OF_YEAR, days);
            return dateFormat.format(cal_start.getTime());
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return null;
        }
    }

    public static Date getIncreaseTime(Date startDate, int days) {
        Calendar cal_start;
        try {
            cal_start = getCalendar();
            cal_start.setTime(startDate);
            cal_start.add(Calendar.DAY_OF_YEAR, days);
            return cal_start.getTime();
        } catch (Exception e) {
            logger.error("DateUtil Exception :", e);
            return null;
        }
    }

    public static String getYMDHMSFormatedDate(Date dateTime) {
        if (dateTime != null) {
            String format = getFormatedDate(dateTime, YYYY_MM_DD_HH_MM_SS);
            return format == null ? "" : format;
        }
        return "";
    }

    public static String getYMDFormatedDate(Date dateTime) {
        if (dateTime != null) {
            String format = getFormatedDate(dateTime, YYYY_MM_DD);
            return format == null ? "" : format;
        }
        return "";
    }

    /**
     * 获取指定时间是周几
     *
     * @param date
     * @return
     */
    public static String getDayOfWeek(Date date) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    /**
     * 获取和当前月份相差月份的月初时间
     *
     * @param month 和当前时间相差月份数
     * @return 毫秒数
     */
    public static Long getFirstDayOfMonth(int month) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取和当前月份相差月份的月末时间
     *
     * @param month 和当前时间相差月份数
     * @return 毫秒数
     */
    public static Long getLastDayOfMonth(int month) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取半年内的每周一,即最近26周的数据
     *
     * @return Set<Long>
     */
    public static List<Long> getEveryMondayWithinSixMonths() {
        List<Long> timeSet = new ArrayList();
        Calendar calendar = getCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        timeSet.add(calendar.getTimeInMillis());
        for (int i = 0; i < 25; i++) {
            calendar.add(Calendar.DATE, -7);
            timeSet.add(calendar.getTimeInMillis());
        }
        return timeSet;
    }

}
