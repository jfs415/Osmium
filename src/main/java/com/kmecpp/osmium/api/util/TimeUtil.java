package com.kmecpp.osmium.api.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {

	private static TimeZone timeZone;

	public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");

	public static TimeZone getTimeZone() {
		return timeZone;
	}

	public static void setTimeZone(String id) {
		timeZone = TimeZone.getTimeZone(id);
	}

	public static String formatTotalMillis(long time) {
		return formatTime(1000, time, "", "");
	}

	public static String formatTotalSeconds(long time) {
		return formatTime(1, time, "", "");
	}

	public static String formatTotalSeconds(long time, String amountPrefix, String unitPrefix) {
		return formatTime(1, time, amountPrefix, unitPrefix);
	}

	public static String formatEpoch(long epoch) {
		return formatEpoch(epoch, DEFAULT_FORMATTER);
	}

	public static String formatEpoch(long epoch, DateTimeFormatter formatter) {
		Instant instant = Instant.ofEpochMilli(epoch);
		return instant.atZone(ZoneId.of("America/New_York")).format(formatter);
	}

	private static String formatTime(int modifier, long time, String amountPrefix, String unitPrefix) {
		final long day = modifier * 86400;
		final long hour = modifier * 3600;
		final long minute = modifier * 60;
		final long second = modifier;
		if (time > day) {
			return StringUtil.plural(time / day, "day", amountPrefix, unitPrefix);
		} else if (time > hour) {
			return StringUtil.plural(time / hour, "hour", amountPrefix, unitPrefix);
		} else if (time > minute) {
			return StringUtil.plural(time / minute, "minute", amountPrefix, unitPrefix);
		} else if (time >= second) {
			return StringUtil.plural(time / second, "second", amountPrefix, unitPrefix);
		} else {
			return StringUtil.plural(time / second, "millisecond", amountPrefix, unitPrefix);
		}
	}

	public static Calendar getCalendar(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar;
	}

	public static Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(timeZone);
		return calendar;
	}

	public static int getDay() {
		return getCalendar().get(Calendar.DAY_OF_MONTH);
	}

	public static int getMonth() {
		return getCalendar().get(Calendar.MONTH) + 1;
	}

	public static int getYear() {
		return getCalendar().get(Calendar.YEAR);
	}

	public static long millis(long start) {
		return System.currentTimeMillis() - start;
	}

	public static double seconds(long start) {
		return (System.currentTimeMillis() - start) / 1000D;
	}

	public static double minutes(long start) {
		return (System.currentTimeMillis() - start) / 1000D / 60D;
	}

	public static double hours(long start) {
		return (System.currentTimeMillis() - start) / 1000D / 60D / 60D;
	}

	public static double days(long start) {
		return (System.currentTimeMillis() - start) / 1000D / 60D / 60D / 24D;
	}

	public static boolean isSameDay(long t1, long t2) {
		Calendar c1 = getCalendar(t1);
		Calendar c2 = getCalendar(t2);
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	}

}
