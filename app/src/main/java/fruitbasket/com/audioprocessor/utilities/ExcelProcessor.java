package fruitbasket.com.audioprocessor.utilities;



import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import fruitbasket.com.audioprocessor.sensor.AccSensor;
import fruitbasket.com.audioprocessor.sensor.GyrSensor;
import fruitbasket.com.audioprocessor.sensor.MagsSensor;

public class ExcelProcessor {
	private static final ExcelProcessor mExcelProcesser=new ExcelProcessor();
	
	private ExcelProcessor(){}
	
	public ExcelProcessor getInstance(){
		return mExcelProcesser;
	}

	/**
	 * ��Excel���׷�Ӷ�������С���������Ҫ��ȷ
	 * @param excelFile
	 * @param accSensorDatas
	 * @return
	 * @throws IOException
	 */
	public synchronized static boolean appendDataQuickly(File excelFile, AccSensor accSensorDatas[], int length)
			throws IOException{
		RandomAccessFile randomAccessFile=new RandomAccessFile(excelFile,"rwd");
		randomAccessFile.seek(excelFile.length());
		for(int i=0;i<length;++i){
			randomAccessFile.writeBytes(accSensorDatas[i].time+"	");
			randomAccessFile.writeBytes(accSensorDatas[i].accels[0]+"	");
			randomAccessFile.writeBytes(accSensorDatas[i].accels[1]+"	");
			randomAccessFile.writeBytes(accSensorDatas[i].accels[2]+"	");
			randomAccessFile.write('\n');
		}
		randomAccessFile.close();
		return true;
	}
	
	public synchronized static boolean appendDataQuickly(File excelFile, GyrSensor gyrSensorDatas[], int length)
			throws IOException{
		RandomAccessFile randomAccessFile=new RandomAccessFile(excelFile,"rwd");
		randomAccessFile.seek(excelFile.length());
		for(int i=0;i<length;++i){
			randomAccessFile.writeBytes(gyrSensorDatas[i].time+"	");
			randomAccessFile.writeBytes(gyrSensorDatas[i].gyr[0]+"	");
			randomAccessFile.writeBytes(gyrSensorDatas[i].gyr[1]+"	");
			randomAccessFile.writeBytes(gyrSensorDatas[i].gyr[2]+"	");
			randomAccessFile.write('\n');
		}
		randomAccessFile.close();
		return true;
	}
	public synchronized static boolean appendDataQuickly(File excelFile, MagsSensor[] magsSensroDatas, int length)
			throws IOException{
		RandomAccessFile randomAccessFile=new RandomAccessFile(excelFile,"rwd");
		randomAccessFile.seek(excelFile.length());
		for(int i=0;i<length;++i){
			randomAccessFile.writeBytes(magsSensroDatas[i].time+"	");
			randomAccessFile.writeBytes(magsSensroDatas[i].mags[0]+"	");
			randomAccessFile.writeBytes(magsSensroDatas[i].mags[1]+"	");
			randomAccessFile.writeBytes(magsSensroDatas[i].mags[2]+"	");
			randomAccessFile.write('\n');
		}
		randomAccessFile.close();
		return true;
	}

	
	/**
	 * ������ؼ䲻���ڣ���������ͷ��Excel�ļ�
	 * ���̰߳�ȫ�����������ڴ����ļ�
	 * @param excelFile
	 * @param header
	 * @return true:�������ļ���false: û�д����ļ�
	 * @throws IOException
	 */
	public static boolean createFileWithHeader(File excelFile,String[] header) 
			throws IOException{
		if(excelFile!=null
				&&header!=null
				&&excelFile.exists()==false){
			excelFile.createNewFile();
			RandomAccessFile raf=new RandomAccessFile(excelFile,"rwd");
			raf.setLength(0);
			for(int list=0;list<header.length;++list){
				raf.writeBytes(header[list]+"	");
			}
			raf.write('\n');
			raf.close();
			return true;
		}
		else{
			return false;
		}
	}

}
