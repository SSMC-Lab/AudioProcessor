package fruitbasket.com.audioprocessor.task;


import java.io.IOException;

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.sensor.AccSensor;
import fruitbasket.com.audioprocessor.ui.MainActivity;
import fruitbasket.com.audioprocessor.utilities.ExcelProcessor;

public class AccCollectionTask implements Runnable {

	private AccSensor[] accSensorDatas;
	private int length=0;//��������Ч���ݵĳ���
	
	public AccCollectionTask(AccSensor[] datas,int length){
		this.accSensorDatas=AccSensor.objectArrayDeepCopyOf(datas, length);
		this.length=length;
	}
	
	@Override
	public void run() {
		try {
			MainActivity.isready[1] =false;
			ExcelProcessor.appendDataQuickly(AppCondition.ACC_EXCEL, accSensorDatas,length);
			MainActivity.isready[1]=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
