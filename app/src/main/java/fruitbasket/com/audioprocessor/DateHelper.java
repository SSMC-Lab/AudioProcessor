package fruitbasket.com.audioprocessor;

import android.os.SystemClock;

import java.text.SimpleDateFormat;

final public class DateHelper {
	private static final DateHelper dateHelper=new DateHelper();
	
	private DateHelper(){}
	
	public DateHelper getInstance(){
		return dateHelper;
	}


	/**
	 * get the current time in secound
	 * @return
     */
	public static long getCurrentTime(){
		return System.currentTimeMillis();
	}
}
