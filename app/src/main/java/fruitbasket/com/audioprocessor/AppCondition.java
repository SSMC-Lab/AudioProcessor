package fruitbasket.com.audioprocessor;

import android.os.Environment;
import java.io.File;
import java.util.Calendar;

final public class AppCondition {
	private static final AppCondition APP_CONDITION =new AppCondition();

	/*
	App的根目录
	 */
	public static final String APP_FILE_DIR=Environment.getExternalStorageDirectory()+ File.separator+"AudioProcessor";
	/*
	默认的声音样本频率
	 */
	public static final int DEFAULE_SIMPLE_RATE =44100;


	private static int year= Calendar.getInstance().get(Calendar.YEAR);
	private static int month=1+Calendar.getInstance().get(Calendar.MONTH);
	private static int date=Calendar.getInstance().get(Calendar.DATE);

	public static final String DATA_DIR=APP_FILE_DIR+"/"+year+"_"+month+"_"+date;
	public static final File ACC_EXCEL=new File(DATA_DIR+"/acc.xls");
	public static final File GYR_EXCEL=new File(DATA_DIR+"/gyr.xls");
	public static final File MAGS_EXCEL=new File(DATA_DIR+"/mags.xls");
	public static final int FAST_FLUSH_INTERVAL=100;//传感器每采集FAST_FLUSH_INTERVAL次数据，就将数据输出到文件
	public static final int MID_FLUSH_INTERVAL=50;
	public static final int SLOW_FLUSH_INTERVAL=5;

	private AppCondition(){}
	
	public AppCondition getInstance(){
		return APP_CONDITION;
	}
	
}
