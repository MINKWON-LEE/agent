
/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.utils.DateUtil.java
 * company : Mobigen
 * @author : Je Joong Lee
 * created at : 2015. 12. 4.
 * description : 
 */

package com.mobigen.snet.NeAgent.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	static Logger logger = LoggerFactory.getLogger(DateUtil.class);


	public static Date getCurTimeInDate(){
		return new Date(System.currentTimeMillis());	
	}
	
	/**
	 * return yyyyMMddhhmm
	 **/
	public static String getCurrDateByminute(){		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		String yyyymmddhhmm = format.format(getCurTimeInDate());		
		return yyyymmddhhmm;
	}
	
	/**
	 * return yyyyMMddhh
	 **/
	public static String getCurrDateByHour(){		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
		String yyyymmddhh = format.format(getCurTimeInDate());		
		return yyyymmddhh;
	}

	/**
	 * return yyyyMMddhh
	 **/
	public static String getCurrDateBySecond(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String yyyymmddhhmmss = format.format(getCurTimeInDate());
		return yyyymmddhhmmss;
	}
	
	public static String getNextHour(String yyyymmddhh){
		String strDate = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
		try {
			Date date = format.parse(yyyymmddhh);
			strDate = format.format(new Date(date.getTime()  + (60*60*1000)));
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("Parse Exception : "+ e.getMessage());
		}
		
		return strDate;
	}
	
	public static String getNextDay(String yyyymmddhh){
		String strDate = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
		try {
			Date date = format.parse(yyyymmddhh);
//			long miliTime = date.getTime() + (24*60*60*1000);
			strDate = format.format(new Date(date.getTime() + (24*60*60*1000)));
		} catch (ParseException e) {			
			e.printStackTrace();
			logger.error("Parse Exception : "+ e.getMessage());
		}
		return strDate;
	}

	public static String getDaysBefore(String yyyymmddhh, int days){
		String strDate = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
		try {
			Date date = format.parse(yyyymmddhh);
//			long miliTime = date.getTime() - (24*60*60*1000)*days;
			strDate = format.format(new Date(date.getTime() - (24*60*60*1000)*days));
		} catch (ParseException e) {			
			e.printStackTrace();
			logger.error("Parse Exception : "+ e.getMessage());
		}
		
		return strDate;
	}

	public static long getMiliTime(String yyyymmddhhmmss){
		long milli = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date date = format.parse(yyyymmddhhmmss);
			milli = date.getTime();

		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("Parse Exception : "+ e.getMessage());
		}

		return milli;
	}
	
	public static String getYear(String yyyymmddhh){

		String result = "";
		try{
		result = yyyymmddhh.substring(0, 4);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		return result;
		
	}
	
	public static String getMonth(String yyyymmddhh){

		String result = "";
		try{
		result = yyyymmddhh.substring(4, 6);
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		return result;
		
	}
	
	public static String getDay(String yyyymmddhh){

		String result = "";
		try{
		result = yyyymmddhh.substring(6, 8);
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		return result;		
	}
	
	public static String getHour(String yyyymmddhh){

		String result = "";
		try{
		result = yyyymmddhh.substring(8, 10);
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		return result;		
	}

	/**
	 * return yyyyMMdd
	 **/
	public static String getFirstDayOfWeek(String yyyymmddhh){
		Calendar cal = Calendar.getInstance();		
		int firstDay = Calendar.MONDAY; //if Sunday is the first day of week, then use Calendar.SUNDAY 
		
		String firstDateOfWeek = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		cal.clear(Calendar.YEAR);
		cal.clear(Calendar.MONTH);
		cal.clear(Calendar.DATE);
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		cal.set(Calendar.YEAR, Integer.parseInt(getYear(yyyymmddhh)));
		cal.set(Calendar.MONTH, Integer.parseInt(getMonth(yyyymmddhh)) -1);
		cal.set(Calendar.DATE, Integer.parseInt(getDay(yyyymmddhh)));
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		
		cal.getTime();

		cal.set(Calendar.DAY_OF_WEEK, firstDay);
		cal.getTime();
		
		firstDateOfWeek = format.format(new Date(cal.getTimeInMillis()));
		
		return firstDateOfWeek; //returns yyyyMMdd
	}

	public static long getFirstDayOfWeekInMilis(String yyyymmddhh){

	long returnTimeInMilis;
	int firstDay = Calendar.MONDAY; //if Sunday is the first day of week, then use Calendar.SUNDAY
	Calendar mcal = Calendar.getInstance();

	mcal.clear(Calendar.YEAR);
	mcal.clear(Calendar.MONTH);
	mcal.clear(Calendar.DATE);
	mcal.clear(Calendar.HOUR_OF_DAY);
	mcal.clear(Calendar.MINUTE);
	mcal.clear(Calendar.SECOND);
	mcal.clear(Calendar.MILLISECOND);
	
	mcal.set(Calendar.YEAR, Integer.parseInt(getYear(yyyymmddhh)));
	mcal.set(Calendar.MONTH, Integer.parseInt(getMonth(yyyymmddhh)) -1);
	mcal.set(Calendar.DATE, Integer.parseInt(getDay(yyyymmddhh)));
	mcal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !	
	mcal.getTime();	
	mcal.set(Calendar.DAY_OF_WEEK, firstDay);
	mcal.getTime();
	
	returnTimeInMilis = mcal.getTimeInMillis();
	
	return returnTimeInMilis;
}

	public static String[] getThisWeekDays(String batDate){
		long nextDay = 60*60*24*1000L;
		String[] fromFirstDay = new String[7];		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		long dayInMilis = getFirstDayOfWeekInMilis(batDate); 
		
		for(int i =0; i< 7;i++){
			fromFirstDay[i] = format.format(new Date(dayInMilis));
			dayInMilis = dayInMilis+nextDay;
		}
		
		return fromFirstDay;
		
	}

	/**
	 * return yyyyMMdd
	 **/
	public static String getFirstDayOfMonth(String yyyymmddhh){
		
		String firstDateOfMonth = "";	
		firstDateOfMonth = getYear(yyyymmddhh)+getMonth(yyyymmddhh) + "01";	
		return firstDateOfMonth; 
	}

	/**
	 * return LIST[yyyyMMdd,yyyyMMdd,yyyyMMdd,..]
	 **/
	public static String[] getFirstWeekDays(String yyyymmddhh){
	
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		long nextWeekDay = 60*60*24*7*1000L;
		String[] fromFirstDay = new String[5];
			
		String fdm = getFirstDayOfMonth(yyyymmddhh);
		
		String fdw = getFirstDayOfWeek(fdm);
		
		String startMonthOfFirstDay = getMonth(fdw+"00");
	
		String inputMonth = getMonth(yyyymmddhh);
		
		long startDayInMilis = 0;
		if(!inputMonth.equals(startMonthOfFirstDay)){		
			startDayInMilis = getFirstDayOfWeekInMilis(getFirstDayOfMonth(yyyymmddhh)) + nextWeekDay;		
		}
		else{
			startDayInMilis = getFirstDayOfWeekInMilis(fdw);		
		}
		
		String targetDate = "";
		for(int i =0; i< 5;i++){
			targetDate = format.format(new Date(startDayInMilis));			
			fromFirstDay[i] = targetDate;
			startDayInMilis = startDayInMilis + nextWeekDay;			
		}
		
		
		return fromFirstDay;
	
	}

}
