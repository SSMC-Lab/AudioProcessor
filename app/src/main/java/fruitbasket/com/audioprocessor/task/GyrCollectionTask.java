package fruitbasket.com.audioprocessor.task;


import java.io.IOException;

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.sensor.GyrSensor;
import fruitbasket.com.audioprocessor.ui.MainActivity;
import fruitbasket.com.audioprocessor.utilities.ExcelProcessor;

public class GyrCollectionTask implements Runnable {

	private GyrSensor[] gyrSensorDatas;
	private int length=0;//????????งน????????
	
	public GyrCollectionTask(GyrSensor[] gyrSensorDatas,int length){
		this.gyrSensorDatas=GyrSensor.objectArrayDeepCopyOf(gyrSensorDatas, length);
		this.length=length;
	}
	
	@Override
	public void run() {
		try {
			MainActivity.isready[2] = false;
			ExcelProcessor.appendDataQuickly(AppCondition.GYR_EXCEL, gyrSensorDatas,length);
			MainActivity.isready[2] = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
