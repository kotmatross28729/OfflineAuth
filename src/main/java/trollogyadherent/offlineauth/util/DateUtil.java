package trollogyadherent.offlineauth.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static final int YEAR = 1;
	public static final int MONTH = 2;
	public static final int WEEK = 4;
	public static final int DAY = 5;
	public static final int HOUR = 11;
	public static final int MINUTE = 12;
	public static final int SECOND = 13;
	
	public static Date addTime(Date date, int value, int timeField) {
		if(timeField == 0)
			return date;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(timeField, value);
		return calendar.getTime();
	}
	
	public static int convertString(String timeField) {
		switch (timeField) {
			case "YEAR" -> {return YEAR;}
			case "MONTH" -> {return MONTH;}
			case "WEEK" -> {return WEEK;}
			case "DAY" -> {return DAY;}
			case "HOUR" -> {return HOUR;}
			case "MINUTE" -> {return MINUTE;}
			case "SECOND" -> {return SECOND;}
			default -> {return 0;}
		}
	}
	
}
