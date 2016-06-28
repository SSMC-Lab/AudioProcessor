package fruitbasket.com.audioprocessor.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import fruitbasket.com.audioprocessor.DataIOHelper;

/**
 * 执行声音录制
 */
public class AudioRecordWrapper {
	private static final String TAG=AudioRecordWrapper.class.toString();
	
	private static final int DEFAULT_BUFFER_INCREASE_FACTOR=3;
	public static final int RECORDER_SAMPLERATE_CD=44100;//CD的采样频率
	
	private AudioRecord recorder1;
	//private AudioRecord recorder2;

	private boolean isContinueRecording; //state variable to control starting and stopping recording
	
	public AudioRecordWrapper(){}
	
	/**
	 * uses AudioRecord.getMinBufferSize() as the recording and the reading buffer size
	 * @return
	 */
	public boolean startRecording(){
		return startRecording(RECORDER_SAMPLERATE_CD,AudioFormat.ENCODING_PCM_16BIT);
	}
	
	/**
	 * 
	 * @param sampleRate : the number of  samples per second
	 * @param encoding: specific the quantity of bit of each voice frame
	 * @return
	 */
	public boolean startRecording(final int sampleRate,int encoding){
		boolean state = false;
		int bufferSize=determineMinimumBufferSize(sampleRate,encoding);
		try {
			state=doRecording(sampleRate,encoding,bufferSize,bufferSize,DEFAULT_BUFFER_INCREASE_FACTOR);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return state;
	}
	
	/**
	 * get the minimum audio buffer size, according to the sample rate and encording
	 * @param sampleRate
	 * @param encoding
	 * @return
	 */
	private int determineMinimumBufferSize(final int sampleRate,int encoding){
		return AudioRecord.getMinBufferSize(sampleRate,AudioFormat.CHANNEL_IN_MONO,encoding);
	}
	
	/**
	 * calculate audio buffer size such that it holds numSamplesInBuffer and is bigger than the minimum size, according to sample rate,encoding and numSamplesInBuffer
	 * @param sampleRate : the number of  samples per second
	 * @param encoding
	 * @param numSamplesInBuffer : the quantity of samples should be saved in the audio buffer
	 * @return
	 */
	private int determineCalculateBufferSize(final int sampleRate,int encoding,int numSamplesInBuffer){
		int minBufferSize=determineMinimumBufferSize(sampleRate,encoding);
		int bufferSize;
		if(encoding==AudioFormat.ENCODING_PCM_16BIT){
			bufferSize=numSamplesInBuffer*2;
		}
		else{
			bufferSize=numSamplesInBuffer;
		}
		///
		if(bufferSize<minBufferSize){
			bufferSize=minBufferSize;
		}
		return bufferSize;
	}
	
	/**
	 * 
	 * @param sampleRate
	 * @param encoding
	 * @param recordingBufferSize
	 * @param readBufferSize
	 * @param bufferIncreaseFactor
	 * @return
	 * @throws IOException 
	 */
	private boolean doRecording(final int sampleRate,int encoding,int recordingBufferSize,int readBufferSize,int bufferIncreaseFactor) throws IOException{
		String fileName= DataIOHelper.getRecordedFileName();
		File file1=new File(fileName);
		//File file2=new File("test");
		file1.createNewFile();
		//file2.createNewFile();

		OutputStream is1 = new FileOutputStream(file1);
        BufferedOutputStream bis1 = new BufferedOutputStream(is1);
        DataOutputStream output1 = new DataOutputStream(bis1);

		/*OutputStream is2=new FileOutputStream(file2);
		BufferedOutputStream bis2 = new BufferedOutputStream(is2);
		DataOutputStream output2 = new DataOutputStream(bis2);*/
		
		if(recordingBufferSize==AudioRecord.ERROR_BAD_VALUE){
			Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR_BAD_VALUE");
			return false;
		}
		else if(recordingBufferSize==AudioRecord.ERROR){
			Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR");
			return false;
		}
		//give it extra space to prevent overflow
		int increasedRecordingBufferSize=recordingBufferSize * bufferIncreaseFactor;
		recorder1 =
				new AudioRecord(AudioSource.MIC,
						sampleRate,
						AudioFormat.CHANNEL_IN_MONO,
						encoding,
						increasedRecordingBufferSize);
		/*recorder2=
				new AudioRecord(AudioSource.CAMCORDER,
						sampleRate,
						AudioFormat.CHANNEL_IN_MONO,
						encoding,
						increasedRecordingBufferSize);*/
		 final short[] readBuffer1=new short[readBufferSize];
		//final short[] readBuffer2=new short[readBufferSize];
		 isContinueRecording =true;
		 recorder1.startRecording();
		 //recorder2.startRecording();
		 while(isContinueRecording ==true){
			 //bufferReasult is a state code or the length of readBuffer

			 final int bufferResult1= recorder1.read(readBuffer1, 0,readBufferSize);
			 //final int bufferResult2=recorder2.read(readBuffer2, 0,readBufferSize);
			 if(isContinueRecording ==false){
				 break;
			 }
			 
			 if(bufferResult1==AudioRecord.ERROR_INVALID_OPERATION){
				 Log.e(TAG,"bufferResult1==AudioRecord.ERROR_INVALID_OPERATION");
			 }
			 /*else if(bufferResult2==AudioRecord.ERROR_INVALID_OPERATION){
				 Log.e(TAG,"bufferResult2==AudioRecord.ERROR_INVALID_OPERATION");
			 }*/
			 else if(bufferResult1==AudioRecord.ERROR_BAD_VALUE){
				 Log.e(TAG,"bufferResult1==AudioRecord.ERROR_BAD_VALUE");
			 }
			 /*else if(bufferResult2==AudioRecord.ERROR_BAD_VALUE){
				 Log.e(TAG,"bufferResult2==AudioRecord.ERROR_BAD_VALUE");
			 }*/
			 else{
				 //save the data
				 int i;
				 for(i=0;i<bufferResult1;++i){
					 output1.writeShort(readBuffer1[i]);
				 }
				 /*for(i=0;i<bufferResult2;++i){
					 output2.writeShort(readBuffer2[i]);
				 }*/
			 }
		 }
		 //output2.close();
		 output1.close();
		 recordingDone();

		return true;
	}
	
	public boolean isRecording(){
		return isContinueRecording;
	}
	
	public void stopRecording(){
		isContinueRecording =false;
	}
	
	public void recordingDone(){
		if(recorder1 !=null){
			recorder1.stop();
			recorder1.release();
			recorder1 =null;
		}
		/*if(recorder2!=null){
			recorder2.stop();
			recorder2.release();
			recorder2=null;
		}*/
	}
}
