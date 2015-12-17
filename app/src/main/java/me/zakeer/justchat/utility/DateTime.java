package me.zakeer.justchat.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import android.util.Log;

public class DateTime {
	
	private static final String TAG = "DateTime";
	
	public static final int DAY_MONTH_YEAR = 0;  //012
	public static final int DAY_YEAR_MONTH = 1;  //021
	public static final int MONTH_DAY_YEAR = 2;  //102
	public static final int MONTH_YEAR_DAY = 3;  //120
	public static final int YEAR_DAY_MONTH = 4;  //201
	public static final int YEAR_MONTH_DAY = 5;  //210
	
	public static final int HOUR_MIN_SEC = DAY_MONTH_YEAR;
	public static final int HOUR_SEC_MIN = DAY_YEAR_MONTH;
	public static final int MIN_HOUR_SEC = MONTH_DAY_YEAR;
	public static final int MIN_SEC_HOUR = MONTH_YEAR_DAY;	
	public static final int SEC_HOUR_MIN = YEAR_DAY_MONTH;
	public static final int SEC_MIN_HOUR = YEAR_MONTH_DAY;
	
	/**
	 * Mon , 25 Aug 2010
	 */
	public static final String WEEK_DAY_MONTH_YEAR = "EEE ',' dd MMM yyyy";
	/**
	 * 2010-08-25
	 */
	public static final String YEAR_MONTH_DAY_WITH_DASH = "yyyy-MM-dd";
	/**
	 * 29/09/2013
	 */
	public static final String DAY_MONTH_YEAR_WITH_SLASH = "dd/MM/yyyy";
		
	/**
	 * 11:12pm, 23/02/2014
	 */
	public static final String LAST_SEEN_FORMAT = "hh:mma, dd/MM/yyyy";
	
	/**
	 * 2014-02-23 23:12:59
	 */
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd hh:mm:ss";
		
	TimeZone timeZone = TimeZone.getTimeZone("GMT+5.30");
	
	public DateTime() {
		// Empty Constructor
	}
	
	public static long getDateTimeTimeStamp(String date, String time) {
		
		int day;
    	int month;
    	int year;
		
		String tempDate = date;	
    	String[] data = tempDate.split("/");
    	day = Integer.parseInt(data[0]);
		month = Integer.parseInt(data[1]) - 1;
		year = Integer.parseInt(data[2]);
    	Log.e(TAG, day+ " --- "+month+ " --- "+year);
    	
    	String tempTime = time;	
    	int hour , minute;
    	
    	if(tempTime.contains(".")) {
    		String[] dataTime = tempTime.split("\\.");
    		hour = Integer.parseInt(dataTime[0]);
        	minute = Integer.parseInt(dataTime[1] + "0");
    	}	
    	else
    	{
    		hour = Integer.parseInt(tempTime);
        	minute = 0;
    	}
    	
    	Log.e(TAG, hour + " "+minute);
    	
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(year, month, day, hour, minute, 00);
		return calendar.getTimeInMillis() / 1000L;
		//return calendar.getTimeInMillis();
	}
	
	//use in event list calculate function
	public static long getTimestampNow() {
		return new Date().getTime();
	}
	
	public static String convert(String date)
	{
		String year = "", month = "", day = "";
		String[] dateArray = null;
		String splitCharacter = "";
		splitCharacter = getSplitCharacter(date);
		if(!splitCharacter.equals(""))
		{	
			dateArray = date.split(splitCharacter);
			//dateArray = rearrange(dateArray, currentDateFormat);
		}
		day 	= dateArray[2];
		month 	= dateArray[1];
		year 	= dateArray[0];
		
		Log.v(TAG, "d:"+day+"-m:"+month+"y:"+year);
		
		date = day+"/"+month+"/"+year;
		
		return date;
	}
	
	public static String convertSlash(String date)
	{
		String year = "", month = "", day = "";
		String[] dateArray = null;
		String splitCharacter = "";
		splitCharacter = getSplitCharacter(date);
		if(!splitCharacter.equals(""))
		{	
			dateArray = date.split(splitCharacter);
			//dateArray = rearrange(dateArray, currentDateFormat);
		}
		day 	= dateArray[0];
		month 	= dateArray[1];
		year 	= dateArray[2];
		
		Log.v(TAG, "d:"+day+"-m:"+month+"y:"+year);
		
		date = year+"-"+month+"-"+day;
		
		return date;
	}
	
	public static String convertDate(String date, int currentDateFormat, String toDateFormat)
	{
		int year = 0, month = 0, day = 0;
		String[] dateArray = null;
		String splitCharacter = "";
		splitCharacter = getSplitCharacter(date);
		if(!splitCharacter.equals(""))
		{	
			dateArray = date.split(splitCharacter);
			dateArray = rearrange(dateArray, currentDateFormat);
		}
		day 	= Integer.parseInt(dateArray[0]);
		month 	= Integer.parseInt(dateArray[1]);
		year 	= Integer.parseInt(dateArray[2]);
		
		Log.v(TAG, "d:"+day+"-m:"+month+"y:"+year);
		
		return dateFormat(toDateFormat, day, month, year); 
	}
	
	/*
	public static long getTimestamp(String date, int dateFormat, String time, int timeFormat) {
    	
		int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
		
		String[] dateArray = null, timeArray = null;
		String splitCharacter = "";
		splitCharacter = getSplitCharacter(date);
		if(!splitCharacter.equals(""))
		{	
			dateArray = date.split(splitCharacter);
			dateArray = rearrange(dateArray, dateFormat);
		}
		
		splitCharacter = getSplitCharacter(time);
		if(!splitCharacter.equals("")) 
		{	
			timeArray = time.split(splitCharacter);
			timeArray = rearrange(timeArray, timeFormat);
		}
		
		day 	= Integer.parseInt(dateArray[0]);
		month 	= Integer.parseInt(dateArray[1])-1;
		year 	= Integer.parseInt(dateArray[2]);
		
		hour 	= Integer.parseInt(timeArray[0]);
		minute 	= Integer.parseInt(timeArray[1]);
		second 	= Integer.parseInt(timeArray[2]);
		
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, 0);
        
        return (long) (c.getTimeInMillis() / 1000L);
    }*/
	
	public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";
        
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());
        
        /*
        DateFormatSymbols symbols = new DateFormatSymbols();
        symbols.setAmPmStrings(new String[] { "am", "pm" });
        df_output.setDateFormatSymbols(symbols);
        */
        
        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) { 
            Log.e("TAG", "ParseException - dateFormat");
        } 
        
        return outputDate;
        
    }
	
	public static long getTimeStampFromCurrentTimeStampFormat(String dateTime) {
    	
		int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
		
		String format = "yyyy-MM-dd hh:mm:ss";
		
		String[] dateTimeArray = dateTime.split(" ");
		String date = dateTimeArray[0];
		String time = dateTimeArray[1];
		
		String[] dateArray = null, timeArray = null;
		String splitCharacterDate = "-";
		String splitCharacterTime = ":";
		
		dateArray = date.split(splitCharacterDate);
		timeArray = time.split(splitCharacterTime);
				
		year 	= Integer.parseInt(dateArray[0]);
		month 	= Integer.parseInt(dateArray[1])-1;
		day 	= Integer.parseInt(dateArray[2]);
		
		hour 	= Integer.parseInt(timeArray[0]);
		minute 	= Integer.parseInt(timeArray[1]);
		second 	= Integer.parseInt(timeArray[2]);
		
		Log.e(TAG, "month : "+month);
		Log.e(TAG, "hour : "+hour);
		
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, 0);
		
		return c.getTime().getTime();
		
    }
	
	public void getTimeStamp(String dateTime) {
		//startTime = "2013-02-27 21:06:30";
        StringTokenizer tk = new StringTokenizer(dateTime);
        String date = tk.nextToken();  
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
        Date dt;
        try {    
            dt = sdf.parse(dateTime);
            dt.getTime();
            System.out.println("Time Display: " + sdfs.format(dt)); // <-- I got result here
        } catch (ParseException e) {
            e.printStackTrace();
        }

	}
	
	public static long getTimestamp(String date, int dateFormat, String time, int timeFormat) {
    	
		int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
		
		String[] dateArray = null, timeArray = null;
		String splitCharacter = "";
		splitCharacter = getSplitCharacter(date);
		if(!splitCharacter.equals(""))
		{	
			dateArray = date.split(splitCharacter);
			dateArray = rearrange(dateArray, dateFormat);
		}
		
		splitCharacter = getSplitCharacter(time);
		if(!splitCharacter.equals("")) 
		{	
			timeArray = time.split(splitCharacter);
			timeArray = rearrange(timeArray, timeFormat);
		}
		
		day 	= Integer.parseInt(dateArray[0]);
		month 	= Integer.parseInt(dateArray[1])-1;
		year 	= Integer.parseInt(dateArray[2]);
		
		hour 	= Integer.parseInt(timeArray[0]);
		minute 	= Integer.parseInt(timeArray[1]);
		second 	= Integer.parseInt(timeArray[2]);
		
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, 0);
        
        return (long) (c.getTimeInMillis() / 1000L);
    }
	
	public static long getTimestamp(String date, int dateFormat) {
    	
		int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
		
		String[] dateArray = null;
		String splitCharacter = "";
		splitCharacter = getSplitCharacter(date);
		if(!splitCharacter.equals("")) 
		{	
			dateArray = date.split(splitCharacter);
			dateArray = rearrange(dateArray, dateFormat);
		}
		
		day 	= Integer.parseInt(dateArray[0]);
		month 	= Integer.parseInt(dateArray[1])-1;
		year 	= Integer.parseInt(dateArray[2]);
		
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, 0);
        
        return (long) (c.getTimeInMillis() / 1000L);
    }
	
	/*
	public static long getTimestamp(String date, int dateFormat) {
    	
		int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
		
		String[] dateArray = null;
		String splitCharacter = "";
		splitCharacter = getSplitCharacter(date);
		if(!splitCharacter.equals("")) 
		{	
			dateArray = date.split(splitCharacter);
			dateArray = rearrange(dateArray, dateFormat);
		}
		
		day 	= Integer.parseInt(dateArray[0]);
		month 	= Integer.parseInt(dateArray[1])-1;
		year 	= Integer.parseInt(dateArray[2]);
		
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, 0);
        
        return (long) (c.getTimeInMillis() / 1000L);
    }
	*/
	private static String[] rearrange(String[] array, int format) {
		
		String[] changedArray = array;
		
		if(format == 0) {
			changedArray = array;
		}
		else if(format == 1) {
			changedArray[0] = array[0];
			changedArray[1] = array[2];
			changedArray[2] = array[1];
		}
		else if(format == 2) {
			changedArray[0] = array[1];
			changedArray[1] = array[0];
			changedArray[2] = array[2];
		}
		else if(format == 3) {
			changedArray[0] = array[1];
			changedArray[1] = array[2];
			changedArray[2] = array[0];
		}
		else if(format == 4) {
			changedArray[0] = array[2];
			changedArray[1] = array[0];
			changedArray[2] = array[1];
		}
		else if(format == 5) {
			changedArray[0] = array[2];
			changedArray[1] = array[1];
			changedArray[2] = array[0];
		}
		
		return changedArray;
	}
	
	private static String getSplitCharacter(String string)
	{	
		String splitCharacter = "";
		if(string.contains("/"))
		{
			splitCharacter = "/";
		}
		else if(string.contains(" "))
		{
			splitCharacter = " ";
		}
		else if(string.contains("-"))
		{
			splitCharacter = "-";
		}
		else if(string.contains(":"))
		{
			splitCharacter = ":";
		}
		return splitCharacter;
	}	
	
	public static long getTimestamp(int year, int month, int day, int hour, int minute) {
    			
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        Log.e(TAG, day+ " --- "+c.getTime());
        
        Log.e(TAG, day+ " --- "+c.getTimeInMillis());
        
        return (long) (c.getTimeInMillis() / 1000L);
    }	
		
	public static long getTimeInMillis(int year, int month, int day, int hour, int minute) {
		
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        return c.getTimeInMillis();
    }	
	
	/*
	public static long getStartTimestamp(Date date) {
		
        Calendar c = dateToCalendar(date);
        c.set(Calendar.HOUR, -12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        return (long) (c.getTimeInMillis() / 1000L);
    }
	
	public static long getEndTimestamp(Date date) {
		
        Calendar c = dateToCalendar(date);
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        
        return (long) (c.getTimeInMillis() / 1000L);
    }
	*/
	
	public static long getStartTimestamp(long timeStamp) {
		
		Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeStamp);
        
        /*
        c.set(Calendar.HOUR, -12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        */
        
        c.set(Calendar.HOUR, 0);		// correct
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        return (long) (c.getTimeInMillis() / 1000L);
    }
	
	public static long getEndTimestamp(long timeStamp) {
		
		Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeStamp);
        
        /*
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        */
        
        c.set(Calendar.HOUR, 23); // correct
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        
        return (long) (c.getTimeInMillis() / 1000L);
    }
	
	public static long getTimestamp(long timeStamp) {
		
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeStamp);
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        
        return (long) (c.getTimeInMillis() / 1000L);
    }
	
	/**
	 * Get Calendar object from Timestamp
	 * 
	 * @param timeStamp
	 * @return
	 */
	public static Calendar getCalendarFromTimestamp(long timeStamp) {
		
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeStamp);
        return c;
    }
	
	public static long getCurrentTimestamp() {
		return new Date().getTime();
	}
	
	/**
	 * Get Date in date Format
	 * 
	 * @param date
	 * @param dateFormat Use constant Date Formats from DateTime class.
	 * @return
	 */
	public static String getDate(Date date, String dateFormat) {    	
    	DateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return sdf.format(date);
    }
	
	public Date getDateFromCalendar(Calendar calendar) {
		return calendar.getTime();
	}
	
	public static String dateFormat(String dateFormat, int day, int month, int year)
    {	
    	//String DATE_FORMAT = "EEE ',' dd MMM yyyy";
		String DATE_FORMAT = dateFormat;
    	Calendar c = Calendar.getInstance();
    	c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
    	Log.v("HomeScreen", "date  : "+c.getTime());
    	DateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    	String date = sdf.format(c.getTime());    	
    	return date;
    }
	
	public String getInDateFormat(String date, String dateFormat)
    {	
		String tempDate = date;	
    	String day = "";
    	String month = "";
    	String year = "";
    	if(tempDate.contains("/")) {
    		String[] data = tempDate.split("/");
    		day = data[0];
    		month = data[1];
    		year = data[2];
    	}
    	else if(tempDate.contains("-"))
    	{
    		String[] data = tempDate.split("-");
    		day = data[0];
    		month = data[1];
    		year = data[2];
    	}
    	date = dateFormat(dateFormat, Integer.parseInt(day), Integer.parseInt(month) - 1, Integer.parseInt(year));
    	return date;
    }
	
	public long getTimestampFromDate(String date)
    {	
		String tempDate = date;	
    	String day = "";
    	String month = "";
    	String year = "";
    	if(tempDate.contains("/")) {
    		String[] data = tempDate.split("/");
    		day = data[0];
    		month = data[1];
    		year = data[2];
    	}
    	else if(tempDate.contains("-"))
    	{
    		String[] data = tempDate.split("-");
    		day = data[0];
    		month = data[1];
    		year = data[2];
    	}
    	
    	Log.e(TAG, day+ " --- "+month+ " --- "+year);
    	
    	return getTimeInMillis(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), 0,0);
    }
	
	public String[] getDayMonthYear(String date)
    {
		String tempDate = date;	
    	String[] data = tempDate.split("-");
		return data;
    }
    
	public String timeFormat(int hour, int minutes)
    {	
    	String DATE_FORMAT = "h:mm";
    	Calendar c = Calendar.getInstance();
    	c.set(Calendar.HOUR, hour);
    	c.set(Calendar.MINUTE, minutes);        
    	//Log.v("HomeScreen", "date  : "+c.getTime());
    	DateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    	String time = sdf.format(c.getTime());
    	return time;
    }
	
	public static Calendar dateToCalendar(Date date) {
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(date);
		  return cal;
	}
	
	public String time12HrFormat(String time)
    {	
    	String tempTime = time;
    	String hour = time;
    	String minute = "00";
    	String timeOfDay = "am";
    	String[] data = {"", ""};
    	if(tempTime.contains(".")) {
    		data = tempTime.split("\\.");
    		hour = data[0];
    		minute = data[1];
    	}	
    	if(minute.length()<=1)
    		minute += "0";
    	//Log.e(TAG, hour + " "+minute);
    	
    	int hr = Integer.parseInt(hour);
    	if(hr>12) {
    		hr -= 12;
    		timeOfDay = "pm";
    	}	
    	
    	hour = String.valueOf(hr);
    	//time = hour + "." + minute;
    	//double value = Double.parseDouble(time);
    	//time = timeFormat(hr, min) + " " + timeOfDay;
    	  	
    	time = hour + ":" + minute +" " + timeOfDay;
    	
    	//DecimalFormat f = new DecimalFormat("##."+"00");
    	//time = f.format(value);
        //time = f.format(hour) + ":" + f.format(minute) +" " + timeOfDay;    	
    	return time;
    }
	
	public String getTime12HrFormat(String time)
    {	
    	String tempTime = time;	
    	String hour = time;
    	String minute = "00";
    	String timeOfDay = "am";
    	String[] data = {"", ""};
    	if(tempTime.contains(".")) {
    		data = tempTime.split("\\.");
    		Log.e(TAG, data[0] + " "+minute);
    		hour = data[0];
    		minute = data[1];
    	}
    	Log.e(TAG, hour + " "+minute);
    	
    	int hr = Integer.parseInt(hour);
    	int min = Integer.parseInt(minute);
    	if(hr>12) {
    		hr -= 12;
    		timeOfDay = "pm";
    	}	
    	
    	hour = String.valueOf(hr);
    	
    	time = timeFormat(hr, min) + " " + timeOfDay;
    	  	
    	//time = hour + ":" + minute +" " + timeOfDay;    
    	
    	//DecimalFormat f = new DecimalFormat("00");
        //time = f.format(hour) + ":" + f.format(minute) +" " + timeOfDay;    	
    	return time;
    }
    
    /*
    private String getDate(int day, int month, int year) {
    	
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        String DATE_FORMAT = "yyyy-MM-dd";
        Date date = c.getTime();
    	Log.v("HomeScreen", "date  : "+date);
    	DateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    	String start_time = sdf.format(date);
    	Log.v("HomeScreen", "start_time  : "+start_time);
        return start_time;
    }
    */
	
	public static String getDateTimeISOformat(Calendar calendar) {
		 	TimeZone z = calendar.getTimeZone(); // TimeZone.getTimeZone("GMT+0530"); // 
	        String timezonename= z.getDisplayName();
	        System.out.println("timezone: "+timezonename);
	        int offset = z.getRawOffset();
	        System.out.println("offset: "+offset);
	        Date date = calendar.getTime();
	        if(z.inDaylightTime(date))
	        {
	            offset = offset + z.getDSTSavings();
	        }
	        int offsetHrs = offset / 1000 / 60 / 60;
	        int offsetMins = offset / 1000 / 60 % 60;
	        String sign = "+";
	        System.out.println("offset: " + offsetHrs);
	        System.out.println("offset: " + offsetMins);
	        
	        //c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
	        //c.add(Calendar.MINUTE, (-offsetMins));	        
	        //System.out.println("GMT Time: "+c.getTime());
	         
	        //String DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss'+0530'";
	        String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'"+ sign + offsetHrs + offsetMins +"'";
	        
	    	Log.v("HomeScreen", "date  : "+date);
	    	DateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
	    	String start_time = sdf.format(date); 
	        return start_time;
	}
	
	// --------------
	
	/**
	 * Donot use yet.
	 * 
	 * @param dayNumber
	 * @return
	 */
	public long getDayFromToday(int dayNumber)
	{
		long today = getStartTimestamp(getCurrentTimestamp());
		long day = today * 1000;
		for (int i = 0; i < dayNumber; i++) {
			day += 1 * 36 * 60 * 60 * 1000;
		}
		return day;
	}
	
	public String getToday(String dateFormat) {
		String date = "";
		date = getDate(new Date(), dateFormat);
		return date;
	}
	
	public String getTomorrow(String dateFormat) {
		String date = "";
		long today = getStartTimestamp(getCurrentTimestamp());
		long tomorrow = today * 1000 + 1 * 36 * 60 * 60 * 1000;
		Calendar c = getCalendarFromTimestamp(tomorrow);		
		date = getDate(c.getTime(), dateFormat);
		return date;
	}
	
	public String getThisWeek(String dateFormat) {
		String date = "";
		//long today = getStartTimestamp(getCurrentTimestamp());
		Calendar c = getCalendarFromTimestamp(getCurrentTimestamp());	
		Log.i(TAG, "Calendar.SATURDAY : "+Calendar.SATURDAY);
		Log.i(TAG, "Calendar.SUNDAY : "+Calendar.SUNDAY + "DAY_OF_WEEK : "+c.get(Calendar.DAY_OF_WEEK));
		int jump = Calendar.SATURDAY - c.get(Calendar.DAY_OF_WEEK);
		long sunday = getDayFromToday(jump);
		Calendar calendar = getCalendarFromTimestamp(sunday);	
		date = getDate(calendar.getTime(), dateFormat);
		return date;
	}	
	
	
	/// new 
	
	
	public static String dateInFormat(Calendar calendar, String dateFormat)
    {	
    	//String DATE_FORMAT = "EEE ',' dd MMM yyyy";
		String DATE_FORMAT = dateFormat;
    	Calendar c = calendar;
    	DateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    	String date = sdf.format(c.getTime());    	
    	return date;
    }
}
