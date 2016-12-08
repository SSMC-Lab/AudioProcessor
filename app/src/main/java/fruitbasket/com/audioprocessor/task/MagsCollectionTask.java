package fruitbasket.com.audioprocessor.task;


import java.io.IOException;
import java.util.concurrent.locks.Condition;

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.sensor.MagsSensor;
import fruitbasket.com.audioprocessor.ui.MainActivity;
import fruitbasket.com.audioprocessor.utilities.ExcelProcessor;

public class MagsCollectionTask implements Runnable {

	private MagsSensor[] magsSensorDatas;
	private int length=0;//��������Ч���ݵĳ���
	
	public MagsCollectionTask(MagsSensor[] magsSensorDatas, int length){
		this.magsSensorDatas=MagsSensor.objectArrayDeepCopyOf(magsSensorDatas, length);
		this.length=length;
	}
	
	@Override
	public void run() {
		try {
			MainActivity.isready[3] =false;
			ExcelProcessor.appendDataQuickly(AppCondition.MAGS_EXCEL, magsSensorDatas,length);
			MainActivity.isready[3] = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
