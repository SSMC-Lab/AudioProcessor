package fruitbasket.com.audioprocessor;

import java.text.SimpleDateFormat;

final public class DateHelper {
	private static final DateHelper dateHelper=new DateHelper();
	
	private DateHelper(){}
	
	public DateHelper getInstance(){
		return dateHelper;
	}


	/**
	 * get the current time with the format of "HH_mm_ss"
	 * @return
     */
	public static String getCurrentTime(){
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());
	}///
}
